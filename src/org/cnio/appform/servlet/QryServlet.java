package org.cnio.appform.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import java.net.URLEncoder;


import org.cnio.appform.entity.Project;
import org.cnio.appform.entity.Interview;
import org.cnio.appform.entity.Section;
import org.cnio.appform.entity.AbstractItem;
import org.cnio.appform.entity.Question;
import org.cnio.appform.entity.Text;
import org.cnio.appform.entity.AppUser;
import org.cnio.appform.entity.AppGroup;

import org.cnio.appform.util.HibernateUtil;
import org.cnio.appform.util.HibController;
import org.cnio.appform.util.AppUserCtrl;
import org.cnio.appform.util.IntrvFormCtrl;
import org.cnio.appform.util.LogFile;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.Query;

/**
 * Servlet implementation class for Servlet: QryServlet
 *
 */
public class QryServlet extends javax.servlet.http.HttpServlet 
 												implements javax.servlet.Servlet {
   
	 static final long serialVersionUID = 1L;
	 private Session hibSes = null;
   
/* (non-Java-doc)
 * @see javax.servlet.http.HttpServlet#HttpServlet()
 */
	public QryServlet() {
		super();
	}
	
	
	
/* (non-Java-doc)
 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
 */
/*
 * select i.idinterview, s.idsection, s.name, s.section_order
from interview i, section s
where i.idinterview in (50,1650,1700,2000)
  and s.codinterview = i.idinterview
  and s.section_order = 3
order by 1, 4


select i.idinterview, s.name, it.item_order, it.content
from interview i, section s, item it
where i.idinterview in (50,1650,1700,2000)
  and s.codinterview = i.idinterview
  and it.idsection = s.idsection
  and s.section_order = 3
order by 1,3
 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
						throws ServletException, IOException {

		String what = request.getParameter("what"), jsonOut = "";
		List<?> list = null;
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
// Get the patient code for the current active secondary group
		if (what.equalsIgnoreCase("cod")) {
			String jsonMsg = "{\"codes\":[";
			
			List<String> patCodes = getPatCodes (request);
			boolean empty = (patCodes == null || patCodes.size() == 0)? true: false;
			if (!empty) {
				for (String code: patCodes) {
					jsonMsg += "\""+code+"\",";
				}
			}
			jsonMsg = empty? jsonMsg: jsonMsg.substring(0, jsonMsg.length()-1);
			jsonMsg += "]}";
			
			out.print(jsonMsg);
			return;
		}

		else if (what.equalsIgnoreCase("prj")) {
			list = getProjects();
		}
		
		else if (what.equalsIgnoreCase("search")) {
			String parentId = request.getParameter("parentid");
			String intrvName = request.getParameter("intrvname");
			
			Transaction tx = null;
			String strQry = "from Interview i where i.parentPrj=:prj and UPPER(i.name) like UPPER('%"+
						intrvName+"%') order by i.name";
			try {
				openHibSession ();
				tx = hibSes.beginTransaction();
				Project prj = (Project)hibSes.get(Project.class, Integer.parseInt(parentId));
				Query qry = hibSes.createQuery(strQry);
				qry.setEntity("prj", prj);
				
				list = qry.list();
				tx.commit();
			}
			catch (HibernateException hibEx) {
				if (tx != null)
					tx.rollback();
			}
			finally {
				closeHibSession ();
			}
		}
		
		else if (what.equalsIgnoreCase("intrv")) {
//			String parentId = request.getParameter("parentid");
//			String intrvName = request.getParameter("intrvname");
			String intrvIds = request.getParameter("intrvids");
			
//			list = getInterviews (Integer.parseInt(parentId), intrvName);
// List<Object[]> secs = getIntrvStruct (Integer.parseInt(parentId), intrvName);
//			list = getIntrvStruct (Integer.parseInt(parentId), intrvName);
			list = getSectionsIntrv (intrvIds);
		}
		
		
		// Retrieve interviews for patients
		else if (what.equalsIgnoreCase("intrv4pat")) {
			String patCode = request.getParameter("code");
			
			this.openHibSession();
			IntrvFormCtrl ifctrl = new IntrvFormCtrl(this.hibSes);
			list = ifctrl.getInterviews4Subject(patCode);
			
			this.closeHibSession();
		}

		else if (what.equalsIgnoreCase("sec")) {
			String intrvId = request.getParameter("parentid");
			
			list = getSections (Integer.parseInt(intrvId));
		}
		
		else if (what.equalsIgnoreCase("items")) {
			String secId = request.getParameter("secid");
			
			list = getSectionItems (Integer.parseInt(secId));
		}
		
		else {
			jsonOut = jsonOut.length() > 0? jsonOut: "{\"msg\":\"qryservlet: Nothing to retrieve\"}";
			// out.print(getServletInfo());
			out.print(jsonOut);
			return;
		}
		
		
		jsonOut = buildJson(list, what);
		out.print (jsonOut);
	}  	
	
	
	
	
/* (non-Java-doc)
 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
						throws ServletException, IOException {
		
	}
	
	
	
	
	
/**
 * Build the json string to send back to client. It will look like:
 * {"type":"typename","items":[{"id":anid,"name":"thename"}]}
 * @param list, the list of items
 * @param what, the type of the items (project, interview, section, ...)
 * @return an string with json format, alike at 
 * {"num":4,"intrvs":[{"name":"uno","id":325},{"name":"dos","id":4001}]}
 */
	private String buildJson (List<?> list, String what) throws java.io.UnsupportedEncodingException {
		String jsonAux = null;
		int numElems = (list != null)? list.size(): 0;
		
		jsonAux = "{\"num\":"+numElems+",";
		if (list == null || list.size() == 0) {
			jsonAux += "\"elems\":[]}";
			return jsonAux;
		}
		
		if (what.equalsIgnoreCase("prj")) {
			jsonAux += "prjs\":[";
			for (Object obj: list) {
				Project prj = (Project)obj;
				jsonAux += "{\"name\":\""+prj.getName()+"\",\"id\":"+prj.getId()+"},";
			}
		}
		else if (what.equalsIgnoreCase("intrv")) {

			int oldId = -1;
			jsonAux = "{\"intrvs\":[[";
			boolean first = true;
// Iterate over the row {idintrv, idsec, intrvname,secname, secorder}
// there will be several idsec, secname for each idintrv/intrvname
			for (Object item: list) {
				Object[] row = (Object[])item;
				Integer idIntrv = (Integer)row[0];
				
				if (idIntrv == oldId) { // add a section
					row[3] = URLEncoder.encode((String)row[3], "UTF-8");
					jsonAux += "{\"id\":"+row[1]+",\"name\":\""+row[3]+"\"},";
				}
				else { // new interview!!!
					oldId = idIntrv;
					jsonAux = jsonAux.substring (0, jsonAux.length()-1);
					if (!first) 
						jsonAux += "]},";
					else 
						first = false;
					
					jsonAux += "{\"id\":"+idIntrv+",\"name\":\""+
										row[2]+"\",\"secs\":[";
				}
			}
			jsonAux = jsonAux.substring(0, jsonAux.length()-1) + "]},"; // finish below
			
		}
		
		else if (what.equalsIgnoreCase("intrv4pat")) {
			jsonAux += "\"interviews\":[";
			boolean thereAreInterviews = false;
			for (Object item: list) {
				Object[] row = (Object[])item;
				String idintrv = ((Integer)row[0]).toString();
				String nameIntrv = (String)row[1];
				
				jsonAux += "{\"id\":"+idintrv+", \"name\":\""+nameIntrv+"\"},";
				thereAreInterviews = true;
			}
			jsonAux = thereAreInterviews? jsonAux: jsonAux+",";
			// jsonAux += "]}";
		}
		
		else if (what.equalsIgnoreCase("sec")) {
			jsonAux += "secs\":[";
			for (Object obj: list) {
				Section sec = (Section)obj;
				jsonAux += "{\"name\":\""+URLEncoder.encode(sec.getName(), "UTF-8")+
									"\",\"id\":"+sec.getId()+"},";
			}
		}
		else if (what.equalsIgnoreCase("items")) {
			jsonAux += "items\":[";
			for (Object obj: list) {
				AbstractItem ai = (AbstractItem)obj;
				jsonAux += "{\"id\":"+ai.getId()+",\"content\":\"";
				if (ai instanceof Question) {
					Question q = (Question)ai;
					String qCode = q.getCodquestion();
					jsonAux += "<b>[Q</b>, <i>("+qCode+")</i><b>]</b> ";
				}
				else
					jsonAux += "<b>[T]</b> ";
				
				jsonAux += URLEncoder.encode(ai.getContent().trim(), "UTF-8")+"\"},";
//				jsonAux += ai.getContent().trim()+"\"},";
			}
			
		}
		else if (what.equalsIgnoreCase("search")) {
			int num = list.size();
			jsonAux = "{\"num\":"+num+",\"intrv\":[";
			for (Object item: list) {
				Interview current = (Interview)item;
				jsonAux += "{\"name\":\""+current.getName()+"\",\"id\":"+current.getId()+"},";
			}
		}
		
		jsonAux = jsonAux.substring(0, jsonAux.length()-1);
		jsonAux += "]}";
		return jsonAux;
	}
	
	
	
	
/**
 * Get a list with the patient codes as strings.
 * @return the list of patient codes
 */
	private List<String> getPatCodes (HttpServletRequest request) {
		List<String> aux = null;
		HttpSession session = request.getSession();
		openHibSession ();
		
		Integer usrid = (Integer)session.getAttribute("usrid");
		Integer intrvid = (Integer)session.getAttribute ("intrvId");
		if (intrvid == null || usrid == null)
			return aux;
		
		AppUserCtrl usrCtrl = new AppUserCtrl (hibSes);
		AppUser usr = (AppUser)hibSes.get(AppUser.class, usrid);
		AppGroup actGrp = usrCtrl.getSecondaryActiveGroup (usr);
		Interview intrv = (Interview)hibSes.get(Interview.class, intrvid);
		

		if (usr != null && actGrp != null && intrv != null) {
			Transaction tx = null;
			String strQry = "select p.codpatient from Patient p join p.performances pf where " +
					"pf.interview=:intrv and pf.group=:grp and " +
					"p.codpatient <> '"+org.cnio.appform.util.IntrvFormCtrl.NULL_PATIENT+
					"' and p.codpatient <> '"+org.cnio.appform.util.IntrvFormCtrl.TEST_PATIENT+
					"' order by 1";
			
// System.out.println(strQry);

			try {
				tx = hibSes.beginTransaction();
				Query qry = hibSes.createQuery(strQry);
				qry.setEntity("intrv", intrv);
				qry.setEntity("grp", actGrp);
				
				aux = qry.list();
				tx.commit();
			}
			catch (HibernateException ex) {
				if (tx != null)
					tx.rollback();
			}
			finally {
				if (hibSes.isOpen())
					closeHibSession ();
			}
		}
		
		return aux;
	}
	
	
	
	public List<Project>	getProjects () {
		Transaction tx = null;
		List<Project> l = null;
		try {
			String strQry = "from Project";
			openHibSession ();
			
			tx = hibSes.beginTransaction();
			Query qry = hibSes.createQuery(strQry);
			l = qry.list();
			tx.commit();
		}
		catch (HibernateException hibEx) {
			if (tx != null)
				tx.rollback();
		}
		finally {
			if (hibSes.isOpen())
				closeHibSession ();
		}
		
		return l;
	}
	
	
	
	public List<Interview> getInterviews (int prjId, String partName) {
		
		Transaction tx = null;
		List<Interview> l = null;
		try {
			String strQry = "from Interview i where i.parentPrj=:prj and UPPER(i.name) " +
					"like UPPER('%"+partName+"%') order by i.name";
			openHibSession ();
System.out.println("strQry");
			tx = hibSes.beginTransaction();
			Project prj = (Project)hibSes.get(Project.class, prjId);
			
System.out.println("project: "+prj.getId()+":"+prj.getName());
			Query qry = hibSes.createQuery(strQry);
			qry.setEntity("prj", prj);
			l = qry.list();
			
			tx.commit();
		}
		catch (HibernateException hibEx) {
			if (tx != null)
				tx.rollback();
		}
		finally {
			if (hibSes.isOpen())
				closeHibSession ();
		}
		
		return l;
	}
	
	
	

/**
 * Gets the sections for the interviews represented by their id
 * @param intrvIds, a comma separated list of ids, ready to use in a sql in 
 * comparison
 * @return
 */
	public List<Object[]> getSectionsIntrv (String intrvIds) {
		
		String strQry = "select i.id, s.id, i.name, s.name, s.sectionOrder" +
		" from Interview i join i.sections s where " +
		"i.id in ("+intrvIds+")"+
		"order by 1, 5";

		Transaction tx= null;
		List<Object[]> lSecs = null;
		try {
			openHibSession ();
			tx = hibSes.beginTransaction();
			Query qry = hibSes.createQuery(strQry);
			
			lSecs = qry.list();
			tx.commit();
		}
		catch (HibernateException hibEx) {
			if (tx != null)
				tx.rollback();
		}
		finally {
			if (hibSes.isOpen())
				closeHibSession ();
		}
		
		return lSecs;
	}
	
	
	
	public List<Object[]> getIntrvStruct (int prjId, String intrvName) {
	/*	
		select i.idinterview, s.idsection, i.name, s.name, s.section_order
		from interview i, section s
		where upper(i.name) like upper('%qes%')
		  and i.idinterview = s.codinterview
		order by 1, 5
	*/	
		String strQry = "select i.id, s.id, i.name, s.name, s.sectionOrder" +
				" from Interview i join i.sections s where " +
				"UPPER(i.name) like UPPER ('%"+intrvName+"%') "+
				"and i.parentPrj=:prj "+
				"order by 1, 5";
		
		
		Transaction tx= null;
		List<Object[]> lSecs = null;
		try {
			openHibSession ();
			tx = hibSes.beginTransaction();
			Project prj = (Project)hibSes.get(Project.class, prjId);
			Query qry = hibSes.createQuery(strQry);
			qry.setEntity("prj", prj);
			lSecs = qry.list();
			tx.commit();
		}
		catch (HibernateException hibEx) {
			if (tx != null)
				tx.rollback();
		}
		finally {
			if (hibSes.isOpen())
				closeHibSession ();
		}
		
		return lSecs;
	}
	
	
	
/**
 * Gets the sections for a interview based on the interview id
 * @param intrvId, the id of the interview to get the sections
 * @return, a list of sections
 */
	public List<Section> getSections (int intrvId) {
		List<Section> lSec = null;
		try {
			openHibSession ();
			Interview intrv = (Interview)hibSes.get(Interview.class, new Integer(intrvId));
			lSec = HibController.SectionCtrl.getSectionsFromIntrv(hibSes, intrv);
		}
		catch (HibernateException hibEx) {
			LogFile.error("QryServlet: Fail to open hibernate session:\t");
			LogFile.error(hibEx.getLocalizedMessage());
			StackTraceElement[] stack = hibEx.getStackTrace();
			LogFile.logStackTrace(stack);
		}
		finally {
			if (hibSes.isOpen())
				closeHibSession ();
		}
		
		return lSec;
	}
	
	
	
/**
 * Get the items for this section using the hibernate utility class
 * @param idSec, the id of the section
 * @return
 */
	public List<AbstractItem> getSectionItems (int idSec) {
	
		List<AbstractItem> items = null;
		try {
			openHibSession ();
			items = HibernateUtil.getItems4Section(hibSes, idSec);
			
		}
		catch (HibernateException hibEx) {
			LogFile.error("QryServlet: Fail to open hibernate session:\t");
			LogFile.error(hibEx.getLocalizedMessage());
			StackTraceElement[] stack = hibEx.getStackTrace();
			LogFile.logStackTrace(stack);
		}
		finally {
			if (hibSes.isOpen()) 
				closeHibSession ();
		}
		return items;
	}
	
	
	
	
	public void openHibSession () throws HibernateException {
		if (hibSes == null || !hibSes.isOpen()) 
			hibSes = HibernateUtil.getSessionFactory().openSession();
		
	}
	
	
	public void closeHibSession () throws HibernateException {
		if (hibSes.isOpen())
			hibSes.close();
	}

}