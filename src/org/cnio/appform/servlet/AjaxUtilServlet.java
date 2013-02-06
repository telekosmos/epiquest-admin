package org.cnio.appform.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cnio.appform.entity.*;
import org.cnio.appform.util.AppUserCtrl;
import org.cnio.appform.util.HibernateUtil;
import org.cnio.appform.util.IntrvFormCtrl;
import org.cnio.appform.util.IntrvController;
import org.cnio.appform.util.Singleton;

import org.hibernate.Session;

import org.json.simple.*;

import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;

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
   
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public AjaxUtilServlet() {
		super();
	}   	
	
/* 
 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String what = request.getParameter("what");
		String usrId = request.getParameter("usrid");
		String jsonResp = "";
		List<AppGroup> groups = null;
		List<Project> prjs = null;
		List<Role> roles = null;
		boolean nothing = false;
		
		response.setHeader("Content-type", "application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Session hibSes = HibernateUtil.getSessionFactory().openSession();
		AppUser theUsr = null;
		if (usrId != null)
			theUsr = (AppUser)hibSes.get(AppUser.class, Integer.parseInt(usrId));
		
		AppUserCtrl usrCtrl = new AppUserCtrl (hibSes);
		what = (what==null)? "": what;

// GET GROUPS
		if (what.equals(AjaxUtilServlet.GRPS)) {
			groups = (theUsr == null)? usrCtrl.getAllGroups(): usrCtrl.getGroups(theUsr);
			if (groups.size() == 0){
				nothing = true;
				jsonResp = "{\"groups\":[]}";
			}
			else {
				jsonResp = "{\"groups\":[";
				for (AppGroup grp: groups) 
					jsonResp += "{\"name\":\""+URLEncoder.encode(grp.getName(), "UTF-8") +
												"\",\"id\":"+grp.getId()+", \"type\":"+grp.getType().getId()+"},";
			}
		}
		
// GET PROJECTS
		else if (what.equals(AjaxUtilServlet.PRJS)) {
			prjs = (theUsr == null)? usrCtrl.getAllProjects(): usrCtrl.getProjects(theUsr);
			if (prjs.size() == 0){
				nothing = true;
				jsonResp = "{\"prjs\":[]}";
			}
			else {
				jsonResp = "{\"prjs\":[";
				for (Project prj: prjs) 
					jsonResp += "{\"name\":\""+URLEncoder.encode(prj.getName(), "UTF-8")+
											"\",\"id\":"+prj.getId()+
											",\"code\":\""+URLEncoder.encode(prj.getProjectCode(),"UTF-8")+"\"},";
			}
		}

// GET ROLES		
		else if (what.equals(AjaxUtilServlet.ROLES)) {
			roles = (theUsr == null)? usrCtrl.getAllRoles(): usrCtrl.getRoleFromUser(theUsr);
			if (roles.size() == 0){
				nothing = true;
				jsonResp = "{\"roles\":[]}";
			}
			else {
				jsonResp = "{\"roles\":[";
				for (Role role: roles) 
					jsonResp += "{\"name\":\""+URLEncoder.encode(role.getName(), "UTF-8")+"\",\"id\":"+role.getId()+"},";
			}
		}
		
// POST PERFORMANCE
		else if (what.equalsIgnoreCase("perf")) {
			doPost (request, response);
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
			jsonResp = "{\"msg\":\"Nothing to retrieve\"}";
			out.print(getServletInfo());
		}
		
		if (!nothing) {
			jsonResp = (jsonResp.length()==0)? jsonResp: 
									jsonResp.substring(0, jsonResp.length()-1);
			jsonResp += "]}";
		}
		
		out.print (jsonResp);
	}  	
	
	
	
	
	
	
	
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String what = request.getParameter("what");
		String patId = request.getParameter("patid"), jsonStr;
		
		if (what.equalsIgnoreCase("rmv")) {
			String paramUsers = request.getParameter("users");
			String[] users = paramUsers.split(",");
			jsonStr = "{\"res\":1,\"msg\":\"";
			jsonStr += users.length+" users were kicked out\"}";
					
			for (String user: users)
				Singleton.getInstance().rmvUser(user);
			
			PrintWriter pwr = response.getWriter();
			pwr.print(jsonStr);
			
			return;
		}
		
		
		if (what.equalsIgnoreCase("clon")) {
			jsonStr = doClon (request);
			
//			response.setHeader("Content-type", "application/json; charset=UTF-8");
			response.setHeader("Content-type", "application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter pwr = response.getWriter();
			pwr.print(jsonStr);
			
			return;
		}
			
		
		
		Session ses = HibernateUtil.getSessionFactory().openSession();
		Patient pat = (Patient)ses.get(Patient.class, Integer.decode(patId));
		IntrvFormCtrl formCtrl = new IntrvFormCtrl(ses);
		
// the query string from the items on the interview is:
// what=perf&q=q-NNNN-OO-GG&val=XXX&patid=patId
		String quesId = request.getParameter("q"); 
		String paramVal = request.getParameter("val");
		paramVal = URLDecoder.decode (paramVal, "UTF-8");
		paramVal = (paramVal == "")? org.cnio.appform.util.RenderEng.MISSING_ANSWER:
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
	 		
	 	PrintWriter pwr = response.getWriter();
	 	pwr.print (jsonStr);
	}
	 	
	 	
	 	
	 	
/**
 * Private method to manage the clon management requests
 * @param req, 
 * @return a json String ready to be sent back to server
 */
	private String doClon (HttpServletRequest req) {
		String jsonStr = "";
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
		}
		
		
		return jsonStr;
	}
	 	
	
	 	
	
/* (non-Javadoc)
 * @see javax.servlet.Servlet#getServletInfo()
 */
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return super.getServletInfo();
	}     
}