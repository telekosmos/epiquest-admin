package org.cnio.appform.servlet;

import org.cnio.appform.util.*;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.inb.dbtask.*;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cnio.appform.entity.*;

import org.cnio.appform.util.dump.*;

import org.hibernate.Session;

import org.json.simple.*;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Servlet implementation class for Servlet: AjaxUtil
 * This is a servlet which is used as server communication for the admintool
 *
 */
 public class AjaxUtilServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   static final String PRJS = "prj";
   static final String GRPS = "grp";
   static final String ROLES = "rol";
   
   static final String INTRV = "intrvs";
   static final String SECTION = "secs";
   static final String HOSPITALS = "hosp";
   static final String DEPARTMENT = "hosp"; // just to generalize the term
   static final String SUBJECT = "subj";
   
   static final String PATS_FROM_TEXT = "pats";
   
   static final String DBDUMP = "dump";
   static final String REPD = "repd";
   
   private String dbUser;
   private String dbPasswd;
   private String dbName;
   private String dbHost;
   private String dbPort;

   private AppDBLogger dbLog;


    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public AjaxUtilServlet() {
		super();
	}
	
	
	public void init (ServletConfig config) throws ServletException {
		super.init(config);
    ServletContext context = getServletContext();
    
    this.dbName = context.getInitParameter("dbName");
    this.dbPasswd = context.getInitParameter("dbPassword");
    this.dbUser = context.getInitParameter("dbUserName");
    this.dbHost = context.getInitParameter("dbServerName");
    this.dbPort = context.getInitParameter("dbPort");
		this.dbPort = this.dbPort == null? "5432": this.dbPort;
	}
	
/* 
 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String what = request.getParameter("what");
		// String usrId = request.getParameter("usrid");
    String usrId = request.getSession().getAttribute("usrid").toString();
		String jsonResp = "";

		List<Interview> intrvs;
		List<Section> sections;
		List<AppGroup> groups;
		List<Project> prjs;
		List<Role> roles;
		boolean nothing = false;
    String logMsg = "";

    what = (what==null)? "": what;
		// set Content Types
		if (what.equals(AjaxUtilServlet.DBDUMP))
			response.setHeader("Content-type", "text/x-csv");
    else
			response.setHeader("Content-type", "application/json");
		 
		response.setCharacterEncoding("UTF-8");
				
		PrintWriter out;
    Session hibSes = HibernateUtil.getSessionFactory().openSession();
    // if (hibSes == null || !hibSes.isOpen())
      // hibSes = HibernateUtil.getSessionFactory().openSession();

		AppUser theUsr = null;
		if (usrId != null)
			theUsr = (AppUser)hibSes.get(AppUser.class, Integer.parseInt(usrId));
		
		AppUserCtrl usrCtrl = new AppUserCtrl (hibSes);
		IntrvController intrvCtrl = new IntrvController(hibSes);

// DBDUMP issue
		if (what.equals(AjaxUtilServlet.DBDUMP)) {
			RepeatableRetriever dr = new RepeatableRetriever();
			String prjCode = request.getParameter("prjid");
			String intrvId = request.getParameter("intrvid");
			String grpId = request.getParameter("grpid");
			String orderSec = request.getParameter("secid"); // actually the section order

      // Get the groups in the case a country is requested
      AppGroup group = (AppGroup)hibSes.get(AppGroup.class, Integer.parseInt(grpId));
      groups = HibernateUtil.getSecondaryGroups(hibSes, group);
      String grpIds = "";
      if (!groups.isEmpty()) {
        for (int i=0; i < groups.size(); i++)
          grpIds += groups.get(i).getId()+",";

        grpIds = grpIds.substring(0, grpIds.length()-1);
      }
      else
        grpIds = grpId;

      System.out.println("the qString: "+request.getQueryString());

      String isRepDump = request.getParameter(AjaxUtilServlet.REPD); // rep dumps
      String dumpOut = "";
      if (isRepDump == null) { // Subject per line download
        dumpOut = dr.getAdminDump(prjCode, intrvId, grpId, Integer.valueOf(orderSec));

        logMsg = "Request download for project code '"+prjCode+"'; interview database id ";
        logMsg += intrvId +" and section "+orderSec+"; group(s) db id "+grpId;
        // logMsg += theUsr == null? "": " by user '"+theUsr.getUsername()+"'";
        this.logRequest(hibSes, usrId, request.getSession().getId(), logMsg, request.getRemoteAddr());

        out = response.getWriter();
        out.print(dumpOut);
        return;
      }
      else { // Repeating block per file/sheet download
        // response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // for xlsx format
        response.setContentType("application/vnd.ms-excel");
        String attachedFile = request.getPathInfo();
        attachedFile = attachedFile.substring(1, attachedFile.length());
        response.setHeader("Content-Disposition", "attachment; filename="+attachedFile);


        try {
          dr.getRepBlocksDump(prjCode, intrvId, grpIds, Integer.parseInt(orderSec));
          dr.getExcelWb().write(response.getOutputStream());
        }
        catch (SQLException sqlEx) {
          logMsg = "There was a error while trying to request a data download.";
          logMsg = DateFormat.getDateTimeInstance().format(new Date())+": "+logMsg;
          logMsg += "User id: "+usrId;
          LogFile.error(logMsg);
          return; // should have to manage an error
        }

        logMsg = "Request download for project code '"+prjCode+"'; interview database id ";
        logMsg += intrvId +" and section "+orderSec+"; group(s) db id "+grpId;
        // logMsg += theUsr == null? "": " by user '"+theUsr.getUsername()+"'";
        this.logRequest(hibSes, usrId, request.getSession().getId(), logMsg, request.getRemoteAddr());

        return;
      }
		}
		
// GET GROUPS
    out = response.getWriter();
		if (what.equals(AjaxUtilServlet.GRPS)) {
      System.out.println("Before getting groups..."+(theUsr != null? theUsr.getUsername(): " no user"));
			groups = (theUsr == null)? usrCtrl.getAllGroups(): usrCtrl.getGroups(theUsr);
			if (groups.size() == 0){
				nothing = true;
				jsonResp = "{\"totalCount\": 0, \"groups\":[]}";
			}
			else {
				jsonResp = "{\"totalCount\": "+groups.size()+", \"groups\":[";
				for (AppGroup grp: groups) 
					jsonResp += "{\"name\":\""+ grp.getName() +
												"\",\"id\":"+grp.getId()+", \"type\":"+grp.getType().getId()+"},";
			}

      logMsg = "Request to retrieve groups";
      // logMsg += (theUsr == null)? "": " for user '"+theUsr.getUsername()+"'";
		}
// SECONDARY GROUPS
		else if (what.equals(AjaxUtilServlet.HOSPITALS)) {
			String grpId = request.getParameter("grpCode"); // ("grpCode");
      grpId = grpId != null? grpId: request.getParameter("grpid");
			
			AppGroup group = (AppGroup)hibSes.get(AppGroup.class,	Integer.parseInt(grpId));
			// groups = group.getContainees();
      if (theUsr != null)
        groups = usrCtrl.getSecondaryGroups(theUsr, group); // have to update the jar file
      else
        groups = group.getContainees();
			
			if (groups.size() == 0){
				nothing = true;
				jsonResp = "{\"totalCount\": 0, \"groups\":[]}";
			}
			else {
				jsonResp = "{\"totalCount\": "+groups.size()+", \"groups\":[";
				for (AppGroup grp: groups) 
					jsonResp += "{\"name\":\""+grp.getName() +
												"\",\"id\":"+grp.getId()+", \"code\":\""+grp.getCodgroup()+"\"},";
			}
      logMsg = "Request to retrieve secondary groups";
      // logMsg += (theUsr == null)? "": " for user '"+theUsr.getUsername()+"'";
		}
		
// GET PROJECTS
		else if (what.equals(AjaxUtilServlet.PRJS)) {
			prjs = (theUsr == null)? usrCtrl.getAllProjects(): usrCtrl.getProjects(theUsr);
			if (prjs.size() == 0){
				nothing = true;
				jsonResp = "{\"totalCount\": 0, \"prjs\":[]}";
			}
			else {
				jsonResp = "{\"totalCount\": "+prjs.size()+", \"prjs\":[";
				for (Project prj: prjs) 
					jsonResp += "{\"name\":\""+URLEncoder.encode(prj.getName(), "UTF-8")+
											"\",\"id\":"+prj.getId()+
											",\"code\":\""+URLEncoder.encode(prj.getProjectCode(), "UTF-8")+"\"},";
			}
      logMsg = "Request to retrieve projects";
      // logMsg += (theUsr == null)? "": " for user '"+theUsr.getUsername()+"'";
		}

// GET ROLES		
		else if (what.equals(AjaxUtilServlet.ROLES)) {
			roles = (theUsr == null)? usrCtrl.getAllRoles(): usrCtrl.getRoleFromUser(theUsr);
			if (roles.size() == 0){
				nothing = true;
				jsonResp = "{\"totalCount\": 0, \"roles\":[]}";
			}
			else {
				jsonResp = "{\"totalCount\": "+roles.size()+", \"roles\":[";
				for (Role role: roles) 
					jsonResp += "{\"name\":\""+URLEncoder.encode(role.getName(), "UTF-8")+
												"\",\"id\":"+role.getId()+"},";
			}
      logMsg = "Request to retrieve roles";
      // logMsg += (theUsr == null)? "": " for user '"+theUsr.getUsername()+"'";
		}

// INTERVIEWS BASED on a prjid and a grpId 		
		else if (what.equals(AjaxUtilServlet.INTRV)) {
			String prjCode = request.getParameter("prjid");
			String grpId = request.getParameter("grpid");

			Project prj = HibernateUtil.getProjectByCode(hibSes, prjCode);
			Integer prjId = prj.getId();
			Integer grpIdInt = (grpId != null && grpId.equals("") == false)? Integer.parseInt(grpId): null;
			AppGroup grp = (AppGroup)hibSes.get(AppGroup.class, grpIdInt);

			intrvs = intrvCtrl.getInterviews(prjId, grpIdInt);
			if (intrvs.size() == 0) {
				nothing = true;
				jsonResp = "{\"totalCount\": 0, \"intrvs\":[]}";
			}
			else {
				jsonResp = "{\"totalCount\": "+intrvs.size()+", \"intrvs\":[";
				for (Interview intrv: intrvs) 
					jsonResp += "{\"name\":\""+intrv.getName()+
											"\",\"id\":"+intrv.getId()+"},";
			}
      logMsg = "Request to retrieve questionnaires for project '"+prj.getName()+"'";
      logMsg += (grp != null)? " and group '"+grp.getName()+"'": "";
      // logMsg += (theUsr == null)? "": " for user '"+theUsr.getUsername()+"'";
		}
		
// SECTIONS for a interview/questionnaire
		else if (what.equals(AjaxUtilServlet.SECTION)) {
			String intrvId = request.getParameter("intrvid");
			// String grpId = request.getParameter("grpid");
			Interview intrv = (Interview)hibSes.get(Interview.class, Integer.parseInt(intrvId));
			// sections = intrv.getSections();
      sections = HibController.SectionCtrl.getSectionsFromIntrv(hibSes, intrv);
			
			if (sections.size() == 0) {
				nothing = true;
				jsonResp = "{\"totalCount\": 0, \"sections\":[]}";
			}
			else {
				jsonResp = "{\"totalCount\": "+sections.size()+", \"sections\":[";
				for (Section sec: sections) 
					jsonResp += "{\"name\":\""+sec.getName()+
											"\",\"id\":"+sec.getId()+", \"order\":"+sec.getSectionOrder()+"},";
			}
      logMsg = "Request to retrieve sections for the questionnaire '"+intrv.getName()+"'";
		}
		
		
		else if (what.equals(AjaxUtilServlet.SUBJECT)) {
			String prjCode = request.getParameter("prjid");
			String hospCode = request.getParameter("grpCode");
			String typeCode = request.getParameter("subjType"); // "", 1, 2 or 3
			
      System.out.println("AjaxUtilServlet: when getting subjects, hibSes is " + hibSes.isOpen());
      if (hibSes.isOpen() == false)
        hibSes = HibernateUtil.getSessionFactory().openSession();
			List<Patient> pats = 
						HibernateUtil.getPatiens4ProjsGrps(hibSes, prjCode, hospCode, typeCode);
			
			if (pats.size() == 0) {
				nothing = true;
				jsonResp = "{\"totalCount\": 0, \"subjects\":[]}";
			}
			else {
				jsonResp = "{\"totalCount\": "+pats.size()+", \"subjects\":[";
				for (Patient pat: pats) 
					jsonResp += "{\"codpatient\":\""+pat.getCodpatient()+
											"\",\"id\":"+pat.getId()+"},";
			}
      logMsg = "Request to retrieve subjects based on the project with code '"+prjCode;
      logMsg += "' and the center with code '"+hospCode+"'";
		}
		
		
		// gets patient codes from the part of the code (autocomplete oriented)
		else if (what.equals(AjaxUtilServlet.PATS_FROM_TEXT)) {
			String patCode = request.getParameter("q");
      if (hibSes.isOpen() == false)
        hibSes = HibernateUtil.getSessionFactory().openSession();

			List<String> pats = HibernateUtil.getPatientsFromCode(hibSes, patCode);
			
			String[] patCodes = new String[pats.size()];
			pats.toArray(patCodes);
			jsonResp = "{\"subjects\":[";
			for (String subjcode: patCodes)
				jsonResp += "\""+subjcode+"\",";
			
		}

// POST PERFORMANCE
		else if (what.equalsIgnoreCase("perf")) {
			doPost (request, response);
			if (hibSes.isOpen())
				hibSes.close();

      // this.logRequest(hibSes, usrId, request.getSession().getId(), logMsg, request.getRemoteAddr());
      return;
		}
		
// this is to remove session attributes from the performance side
// when the user close the window without finishing the interview
		else if (what.equalsIgnoreCase("end")) {  
			HttpSession ses = request.getSession();
System.out.println("ending session in AjaxUtilServlet: "+ses);
			if (ses != null) {
				Enumeration<String> en = ses.getAttributeNames();
				for (String attr = en.nextElement(); en.hasMoreElements();) {
					ses.removeAttribute(attr);
				}
				jsonResp = "{\"msg\":\"Performance session attributes removed\"}";
				
			}
			else
				jsonResp = "{\"msg\":\"Sesssion is already null\"}";
			
			nothing = true;
		}
		else {
			nothing = true;
			jsonResp = jsonResp.length() > 0? jsonResp: "{\"msg\":\"Nothing to retrieve\"}";
			out.print(getServletInfo());
		}
		
		if (!nothing) {
			jsonResp = (jsonResp.length()==0)? jsonResp: 
									jsonResp.substring(0, jsonResp.length()-1);
			jsonResp += "]}";
		}
		
    this.logRequest(hibSes, usrId, request.getSession().getId(), logMsg, request.getRemoteAddr());
    if (hibSes.isOpen())
      hibSes.close();

    out.print (jsonResp);
	}  	
	

	
	
	
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String what = request.getParameter("what");
		String patId = request.getParameter("patid"), jsonStr;
		
		response.setCharacterEncoding("UTF-8");
		PrintWriter pwr = response.getWriter();
		
		/* This is to remove system users from the application or so (basically, not used!)
		if (what.equalsIgnoreCase("rmv")) {
			String paramUsers = request.getParameter("users");
			String[] users = paramUsers.split(",");
			jsonStr = "{\"res\":1,\"msg\":\"";
			jsonStr += users.length+" users were kicked out\"}";
					
			for (String user: users)
				Singleton.getInstance().rmvUser(user);
			
			pwr.print(jsonStr);
			
			return;
		}
		*/
		
		if (what.equalsIgnoreCase("clon")) {
			jsonStr = doClon (request);
			
//			response.setHeader("Content-type", "application/json; charset=UTF-8");
			response.setHeader("Content-type", "application/json");
			response.setCharacterEncoding("UTF-8");
			pwr.print(jsonStr);
			
			return;
		}
		
	// DELETE PATIENTS ////////////////////////////////////////////////////////
		else if (what.equals("dp")) { // delete patients
			String subjects = request.getParameter("pats");
			String simParam = request.getParameter("sim");
			String dbUrl = "jdbc:postgresql://"+this.dbHost+":"+this.dbPort+"/"+this.dbName;
			
			System.out.println("Deleting patients: "+subjects);			
			
			FixingTasksHub fth = new FixingTasksHub (dbUrl, this.dbUser, this.dbPasswd);
			ArrayList<String> listCodsPatient = new ArrayList<String>();
			String patsArray[] = subjects.split(",");
			boolean simulation = Boolean.parseBoolean(simParam);
			for (int i=0; i<patsArray.length; i++)
				listCodsPatient.add(patsArray[i]);
			
			HashMap<String, Object> deletions =
	      (HashMap<String, Object>)fth.deletePatients(this.dbHost, 
	      		this.dbUser, 
	      		this.dbPasswd, 
	      		simulation, listCodsPatient);
			ArrayList<String> noDeletions = fth.getNoDeletedPatients();
			String jsonOut = "";
			
			Integer deletedPats = (Integer)deletions.get("rows_affected");

	    HashMap<String, HashMap<String, Integer>> patientSamples =
	                          (HashMap)deletions.get("pats_with_samples");
	    Iterator itOne = patientSamples.entrySet().iterator();
	    String jsonPat = "";
	    while (itOne.hasNext()) {
	      Map.Entry pair = (Map.Entry)itOne.next();
	      String patientCode = (String)pair.getKey();

	      HashMap samples = (HashMap)pair.getValue();
	      Iterator sampleIt = samples.entrySet().iterator();
	      String jsonSamples = "\"samples\": [";
	      while (sampleIt.hasNext()) {
	        Map.Entry samplePair = (Map.Entry)sampleIt.next();
	        jsonSamples += "{\"sample_code\":\""+samplePair.getKey()+"\",";
	        jsonSamples += "\"num_of_answers\": "+samplePair.getValue()+"},";
	      }
	      jsonSamples = jsonSamples.substring(0, jsonSamples.length()-1)+"]";

	      jsonPat += "{\"patient_code\": \""+patientCode+"\", ";
	      jsonPat += jsonSamples+"},";
	    }
	    jsonPat = jsonPat.length() > 0? jsonPat.substring(0, jsonPat.length()-1): jsonPat;

	    ArrayList<String> patients_affected = (ArrayList)deletions.get("pats_affected");
	    String jsonPatsAff = "\"patients_deleted\": [";
	    if (patients_affected.size() == 0)
	    	jsonPatsAff += "]";
	    
	    else {
		    for (String patientCode: patients_affected)
		      jsonPatsAff += "\""+patientCode+"\",";
	
		    jsonPatsAff = jsonPatsAff.substring(0, jsonPatsAff.length()-1)+"]";
	    }

	    jsonOut = "{\"deletions\": "+deletedPats+", " + jsonPatsAff+","+
	      " \"pats_with_samples\":["+jsonPat+"], \"sim\":"+simParam+"}";
	    
	    System.out.println(jsonOut);
	    pwr.print(jsonOut);
	    
	    return;
		} 
		
		// DELETE INTERVIEWS //////////////////////////////////////////////////////
		else if (what.equals("di")) { // delete interviews for a patient
			String patCode = request.getParameter("pat");
			String intrvs = request.getParameter("intrvs");
			String simParam = request.getParameter("sim");
			boolean sim = Boolean.parseBoolean(simParam);
			String jsonOut = this.delInterviews(patCode, intrvs, sim);
			System.out.println(jsonOut);
			pwr.print(jsonOut);
			
			return;
		}

    else if (what.equals("user")) {
      String disableParam = request.getParameter("disable");
      boolean forDisabling = disableParam != null;

      Session ses = HibernateUtil.getSessionFactory().openSession();
      AppUserCtrl usrCtrl = new AppUserCtrl(ses);

      if (forDisabling) {
        String usrId = request.getParameter("usrid");
        AppUser theUser = (AppUser)ses.get(AppUser.class, Integer.parseInt(usrId));
        boolean res = usrCtrl.setUserDisabled(theUser, (disableParam.equals("1") ? true : false));

        String msg, jsonOut;
        if (res) {
          msg = "User '"+theUser.getUsername()+"' ("+theUser.getId()+") was ";
          msg += disableParam.equals("0")? "enabled": "disabled";
        }
        else {
          msg = "Unable to "+(disableParam.equals("0")? "enable": "disable");
          msg += " user '"+theUser.getUsername()+"' ("+theUser.getId()+")";
        }
        jsonOut = "{\"msg\": \""+msg+"\", \"res\": 1, \"action\": \"";
        jsonOut += (disableParam.equals("0")? "enabled": "disabled")+"\"}";

        System.out.println(jsonOut);
        pwr.print(jsonOut);
        ses.close();
        return;
      }

    }
			
		/*
		if (what.equals("dp")) { // delete patients
			String subjects = request.getParameter("pats");
			String dbUrl = "jdbc:postgresql://"+this.dbHost+":"+this.dbPort+"/"+this.dbName;
			
			FixingTasksHub fth = new FixingTasksHub(dbUrl, this.dbUser, this.dbPasswd);
			ArrayList<String> listCodsPatient = new ArrayList<String>();
			String patsArray[] = subjects.split(",");
			for (int i=0; i<patsArray.length; i++)
				listCodsPatient.add(patsArray[i]);
			
			HashMap<String, Object> deletions =
	      (HashMap<String, Object>)fth.deletePatients(this.dbHost, 
	      		this.dbUser, 
	      		this.dbPasswd, 
	      		true, listCodsPatient);
			ArrayList<String> noDeletions = fth.getNoDeletedPatients();
			String jsonOut = "";
			
			Integer deletedPats = (Integer)deletions.get("rows_affected");

	    HashMap<String, HashMap<String, Integer>> patientSamples =
	                          (HashMap)deletions.get("pats_with_samples");
	    Iterator itOne = patientSamples.entrySet().iterator();
	    String jsonPat = "";
	    while (itOne.hasNext()) {
	      Map.Entry pair = (Map.Entry)itOne.next();
	      String patientCode = (String)pair.getKey();

	      HashMap samples = (HashMap)pair.getValue();
	      Iterator sampleIt = samples.entrySet().iterator();
	      String jsonSamples = "\"samples\": [";
	      while (sampleIt.hasNext()) {
	        Map.Entry samplePair = (Map.Entry)sampleIt.next();
	        jsonSamples += "{\"sample_code\":\""+samplePair.getKey()+"\",";
	        jsonSamples += "\"num_of_answers\": "+samplePair.getValue()+"},";
	      }
	      jsonSamples = jsonSamples.substring(0, jsonSamples.length()-1)+"]";

	      jsonPat += "{\"patient_code\": \""+patientCode+"\", ";
	      jsonPat += jsonSamples+"},";
	    }
	    jsonPat = jsonPat.substring(0, jsonPat.length()-1);

	    ArrayList<String> patients_affected = (ArrayList)deletions.get("pats_affected");
	    String jsonPatsAff = "\"patients_deleted\": [";
	    for (String patientCode: patients_affected)
	      jsonPatsAff += "\""+patientCode+"\",";

	    jsonPatsAff = jsonPatsAff.substring(0, jsonPatsAff.length()-1)+"]";

	    jsonOut = "{\"deletions\": "+deletedPats+", " + jsonPatsAff+","+
	      " \"pats_with_samples\":["+jsonPat+"]}";
	    System.out.println("REAL JSON:");
	    System.out.println(jsonOut);
		}
		*/
		
		Session ses = HibernateUtil.getSessionFactory().openSession();
		Patient pat = (Patient)ses.get(Patient.class, Integer.decode(patId));
		IntrvFormCtrl formCtrl = new IntrvFormCtrl(ses);
		
// the query string from the items on the interview is:
// what=perf&q=q-NNNN-OO-GG&val=XXX&patid=patId
		String quesId = request.getParameter("q");
		String paramVal = request.getParameter("val");
		paramVal = URLDecoder.decode (paramVal, "UTF-8");
		paramVal = (paramVal.equalsIgnoreCase(""))? org.cnio.appform.util.RenderEng.MISSING_ANSWER:
																paramVal;
		
		String ansParams[] = quesId.split("-"); // i have {145,1,2,g2}
		String qId = ansParams[0].substring(1);
		Integer ansNumber = Integer.decode(ansParams[1]), 
						ansOrder = Integer.decode(ansParams[2]), ansGroup = null;
		boolean res = false;
		
		if (ansParams.length > 3) { // we have a group
			if (ansParams[3] != null) {
				String aux = ansParams[3].substring(1);
				ansGroup = Integer.decode(aux);
			}
		}

// decoupled? objects... lets see if this works
		Question q = (Question)ses.get(Question.class, Long.decode(qId));
		List<AnswerItem> ansTypes = HibernateUtil.getAnswerTypes4Question(ses, q);
		AnswerItem ansType = ansTypes.get(ansOrder.intValue()-1);
// out.print("ansType: " + ansType.getName()+"<br>");

	 	Object[] ans = 
	 		formCtrl.getAnswer4Question(Integer.decode(qId), Integer.decode(patId),
	 																ansNumber,ansOrder);
	 	if (ans == null) {
			res = formCtrl.saveAnswer(q, pat, ansNumber, ansOrder, ansGroup, paramVal, ansType);
// System.out.println("AjaxUtilServlet saving answer w/ result: "+res);			
		
	 	}	
	 	else {
// an update of the answer must be done ONLY if values are different
// this is to optimize unuseful database accesses
			if (((String)ans[1]).equalsIgnoreCase(paramVal) == false)
				res = formCtrl.updateAnswer((Integer)ans[0], paramVal);
			else
				res = true;
	 	}
	 	
		ses.close();
		
// the returned json string is as:
// {"res":[0|1],"msg":[""|"could not save the value"]}
	 	if (res)
	 		jsonStr = "{\"res\":1,\"msg\":\"\"}";
	 	
	 	else
	 		jsonStr = "{\"res\":0,\"itemname\":\""+quesId+"\",\"msg\":\"Value '"+paramVal+"' could not be saved\"}";
	 		
	 	// PrintWriter pwr = response.getWriter();
	 	pwr.print (jsonStr);
	}
	 	
	 	
	
	 	
	/**
	 * Performs an operation to delete interviews for a patient. Several checks
	 * are done in order to be sure no wrong things are deleted
	 * @param patCode, the patient code
	 * @param listIntrvs, the list of the names of interviews to be deleted
	 * @return a json string with the values returned by the operation
	 */
	private String delInterviews (String patCode, String listIntrvs, boolean sim) {
		String dbUrl = "jdbc:postgresql://"+this.dbHost+":"+this.dbPort+"/"+this.dbName;
		
		System.out.println("Deleting interviews: "+listIntrvs);			
		
		HashMap hashMap = new HashMap();
		String[] intrvs = listIntrvs.split(",");
		List<String> theIntrvs = Arrays.asList(intrvs);
		hashMap.put(patCode, theIntrvs);
		
    
		boolean simulation = sim;
		FixingTasksHub fs = new FixingTasksHub (dbUrl, this.dbUser, this.dbPasswd);
    HashMap<String, Object> jsonMap =
      (HashMap<String, Object>)fs.deleteInterviews(this.dbHost, 
      		this.dbUser, 
      		this.dbPasswd,
        simulation, hashMap);

    String jsonOut = "{\"rows_affected\": "+jsonMap.get("rows_affected").toString()+",";
    jsonOut += "\"samples\": ";

    HashMap patSamples = (HashMap)jsonMap.get("pats_with_samples");
    List samples = new ArrayList();
    samples.addAll(patSamples.values());


    Iterator sampleIt = samples.iterator();
    while (sampleIt.hasNext()) {
      jsonOut += sampleIt.next().toString()+",";
    }
    jsonOut = samples.size()>0? jsonOut.substring(0, jsonOut.length()-1): jsonOut;
    jsonOut += ", \"interviews_deleted\":[";


    List deletedOnes = (List)jsonMap.get("interviews_deleted");
    Iterator deletedIt = deletedOnes.iterator();
    while (deletedIt.hasNext()) {
      // jsonOut += "\""+deletedIt.next().toString()+"\",";
    	List pair = (List)deletedIt.next();
      jsonOut += "{\"codpat\":\""+pair.get(0)+"\", \"intrv\":\""+pair.get(1)+"\"},";
    }
    jsonOut = deletedOnes.size()>0? jsonOut.substring(0, jsonOut.length()-1):jsonOut;

    jsonOut += "], \"sim\":"+sim+"}";
    
    return jsonOut;
	}
	
	
	 	
/**
 * Private method to manage the clon management requests
 * @param req the servlet request
 * @return a json String ready to be sent back to server
 */
	private String doClon (HttpServletRequest req) {
		String jsonStr = "", logMsg = "";
		String action = req.getParameter("action");
		Session hibSes = HibernateUtil.getSessionFactory().openSession();
		IntrvController intrvCtrl = new IntrvController (hibSes);
		
		JSONObject jsonOut = new JSONObject ();
		JSONArray arrIntrvs = new JSONArray ();
		
		
// get interviews from project and group
		if (action.equalsIgnoreCase("intrvs")) { 
			String grp = req.getParameter("grpid"), prj = req.getParameter("prjid");
			
			
			List<Interview> intrvs = 
				intrvCtrl.getInterviews (Integer.decode(prj), Integer.decode(grp));
			
			for (Interview intrv: intrvs) {
				JSONObject aux = new JSONObject ();
				aux.put("id", intrv.getId());
				aux.put("name", intrv.getName());
				
				arrIntrvs.add(aux);
			}
			jsonOut.put("res", 1);
			jsonOut.put("intrvs", arrIntrvs);
			jsonStr = jsonOut.toJSONString();
		}
		
		if (action.equalsIgnoreCase("clon")) {
			String newName = req.getParameter("name"), grp = req.getParameter("grpid"),
					prj = req.getParameter("prjid"), intrv=req.getParameter("intrvid");
			
			Integer intrvId = Integer.decode(intrv), grpId = Integer.decode(grp),
				prjId = Integer.decode(prj);
				
      Interview intrv4Name = (Interview)hibSes.get(Interview.class, intrvId);
      Project prj4Name = (Project)hibSes.get(Project.class, prjId);

      AppGroup trgGrp = (AppGroup)hibSes.get(AppGroup.class, grpId);
			Project trgPrj = (Project)hibSes.get(Project.class, prjId);
			Interview newIntrv = intrvCtrl.replicateIntrv(intrvId, trgGrp, trgPrj, newName);

			JSONObject out = new JSONObject ();
			if (newIntrv != null) {
				out.put("res", 1);
				out.put("intrvName", newIntrv.getName());
				out.put("msg", "The interview was replicated successfully");
			}
			else {
				out.put("res", 0);
				out.put("msg", "Interview could not be created. Check the logs to get more information");
			}
			jsonStr = out.toJSONString();
      logMsg = "Request to clone the interview '"+intrv4Name.getName()+"'";
      logMsg += (prj4Name != null)? " from project '"+prj4Name.getName()+"'": "";
      logMsg += " into a new one named '"+newName+"' for project '"+trgPrj.getName()+"'";
		}

    // LOGGING
    this.logRequest(hibSes, req.getSession().getAttribute("usrid").toString(),
                    req.getSession().toString(), logMsg, req.getRemoteAddr());
		
		return jsonStr;
	}


  /**
   * Handy method to log the request through this servlet into the database log
   * @param theSess, the hibernate session
   * @param userId, the user database id
   * @param sessionId, the session id
   * @param msgLog, the message to log in the database
   * @param ipAddr, the ipAddr
   */
  private void logRequest (Session theSess, String userId, String sessionId,
                           String msgLog, String ipAddr) {

    Transaction tx = null;
    try {
      tx = theSess.beginTransaction();
      dbLog = new AppDBLogger();
      dbLog.setUserId(Integer.parseInt(userId));
      dbLog.setSessionId(sessionId);
      dbLog.setMessage(msgLog);
      dbLog.setLastIp(ipAddr);


      theSess.save(dbLog);
      tx.commit();
    }
    catch (HibernateException ex) {
      if (tx != null) {
        tx.rollback();
      }
      LogFile.error("Fail to log user session init:\t");
      LogFile.error("userId=" + userId + "; sessionId=" + sessionId);
      LogFile.error(ex.getLocalizedMessage());
      StackTraceElement[] stack = ex.getStackTrace();
      LogFile.logStackTrace(stack);
    }
  }

	 	
	
/* (non-Javadoc)
 * @see javax.servlet.Servlet#getServletInfo()
 */
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return super.getServletInfo();
	}     
}