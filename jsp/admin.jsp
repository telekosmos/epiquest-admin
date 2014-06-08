<%@
	page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
%>
        
<%@
	page import="org.hibernate.Session,
				org.hibernate.Transaction,
				org.hibernate.HibernateException"
%>
<%@
	page import="org.cnio.appform.util.HibernateUtil,
				org.cnio.appform.util.HibController,
				org.cnio.appform.util.AppUserCtrl,
				org.cnio.appform.entity.*"
%>
<%@
	page import="java.util.Collection, 
				java.util.Iterator,
				java.util.List,
				java.util.ArrayList,
				java.util.Vector, 
				java.util.Enumeration,
				java.util.Hashtable,
				java.net.URLDecoder"
%>
	
<%							
final String USER_ID = new String ("userId");
final String USERNAME = new String("username");
final String PWD = new String("pwd");
final String ROLE = new String("roles");
final String GROUP = new String("group");
final String PROJECT = new String("project");
final String FIRSTNAME = new String("firstname");
final String LASTNAME = new String ("lastname");
final String EMAIL = new String ("email");
%>

<%
String sUsername = new String ();
String sPwd = new String ();
String sFirstName = "", sLastName = "", usrId="", email="";
ArrayList <Integer> aGroup = new ArrayList<Integer> ();
ArrayList <Integer> aProject = new ArrayList<Integer> ();
ArrayList<Integer> aRole = new ArrayList<Integer> ();
	
Hashtable <String,String> hException = new Hashtable <String,String> ();

// Parameter retrieving and assignment
Enumeration<String> params = request.getParameterNames();
while (params.hasMoreElements())
{
	String sParamName = (String) params.nextElement();
	
	if (sParamName.equals(FIRSTNAME)) {
		sFirstName = request.getParameter(sParamName);
		continue;
	}
	
	if (sParamName.equals(LASTNAME)) {
		sLastName = request.getParameter (sParamName);
		continue;
	}

	if (sParamName.equals(EMAIL)) {
		email = request.getParameter(sParamName);
		continue;
	}
	
	if (sParamName.equals(USERNAME)) {
		String sParamValue[] = request.getParameterValues(sParamName);
		
		if (sParamValue[0] != "") {
			sUsername = sParamValue[0];			
		}
		else {
			hException.put("id","502");
			hException.put("type","error");
			hException.put("message","Error in "+sParamName+" parameter");
		}
	}
	else {
		if(sParamName.equals(PWD)) {
			String sParamValue[] = request.getParameterValues(sParamName);
			
			if (sParamValue[0] != "") {
				sPwd = sParamValue[0];			
			}
			else {
				hException.put("id","503");
				hException.put("type","error");
				hException.put("message","Error in "+sParamName+" parameter");
			}
		}
		else {
			if(sParamName.equals(ROLE))	{
				String aParameterValues[] = request.getParameterValues(sParamName)[0].split(";");
				try {
					if (aParameterValues.length != 0)	{
						 for (String sParameterValue : aParameterValues)
							 aRole.add(Integer.parseInt(sParameterValue));
					}
					
					else {
						hException.put("id","504");
						hException.put("type","error");
						hException.put("message","Error in "+sParamName+" parameter");
					}
				}
				catch (NumberFormatException nfEx) {
					hException.put("id","504");
					hException.put("type","error");
					hException.put("message","Error in "+sParamName+" parameter");
				}
			}
			else
			{
				if(sParamName.equals(GROUP)) {
					String aParameterValues[] = request.getParameterValues(sParamName)[0].split(";");
					try{
						if (aParameterValues.length != 0)	{
							 for (String sParameterValue : aParameterValues)
								 aGroup.add(Integer.parseInt(sParameterValue));
						}
						else {
							hException.put("id","505");
							hException.put("type","error");
							hException.put("message","Error in "+sParamName+" parameter");
						}
					}
					catch (NumberFormatException nfEx) {
						hException.put("id","505");
						hException.put("type","error");
						hException.put("message","Error in "+sParamName+" parameter");
					}
				}
				else {
					if(sParamName.equals(PROJECT)) {
						System.out.println("admin.js projects param: "+ request.getParameterValues(sParamName)[0]);
						String aParameterValues[] = request.getParameterValues(sParamName)[0].split(";");
						try {
							if (aParameterValues.length != 0)	{
								 for (String sParameterValue : aParameterValues)
									 aProject.add(Integer.parseInt(sParameterValue));					 
							}
							else {
								hException.put("id","506");
								hException.put("type","error");
								hException.put("message","Error in "+sParamName+" parameter");						
							}
						}
						catch (NumberFormatException nfEx) {
							hException.put("id","506");
							hException.put("type","error");
							hException.put("message","Error in "+sParamName+" parameter");
						}
					}
					else if (sParamName.equals(USER_ID)) {
						usrId = request.getParameter(USER_ID);
					}
					else {
						hException.put("id","501");
						hException.put("type","error");
						hException.put("message","Wrong parameters");
					}
				}
			}
		}
	}
}


boolean res = false;
if( hException.isEmpty()) // Error getting input
{
	Session hibSess = HibernateUtil.getSessionFactory().openSession();
	Transaction tx = null;
	AppUserCtrl usrCtrl = new AppUserCtrl (hibSess);
	
	try {
		AppUser theUser;
		Integer newUsrId;
		boolean newOne;
		
		if (usrId.equals("")) {
			tx = hibSess.beginTransaction();
			theUser = new AppUser (sUsername, sPwd);
		  newUsrId = (Integer)hibSess.save(theUser);
		  newOne = true;
//		  tx.commit();
		}
		else {
			tx = hibSess.beginTransaction();
			newUsrId = Integer.parseInt(usrId);
			theUser = (AppUser)hibSess.get(AppUser.class, newUsrId);
			newOne = false;
		}
		
		theUser.setFirstName(sFirstName);
		theUser.setLastName(sLastName);
		if (newOne)
			theUser.setPasswd(sPwd);
		
		theUser.setEmail(email);
		tx.commit();
		
// For the role, group and project lists, we get the set of roles, projects and
// groups from the user and add just the new ones from the admin, and remove
// those ones which were deleted by using the admin
		List<Role> usrRoles = usrCtrl.getRoleFromUser(theUser);
		String theRoles = "";
// adding Roles
		tx = hibSess.beginTransaction();
		for (Integer iRoleId: aRole) {
			Role oRole = (Role)hibSess.get(Role.class, iRoleId);
			theRoles += oRole.getName().charAt(0)+",";
			if (usrRoles == null || !usrRoles.contains(oRole)) {
				AppuserRole oUserRole = new AppuserRole (theUser, oRole);
				hibSess.save(oUserRole);
			}
		}
		tx.commit();

// removing Roles
		for (Role auxRole: usrRoles) {
			Integer roleId = auxRole.getId();
			if (aRole == null || !aRole.contains(roleId))
				usrCtrl.rmvRoleFromUser(theUser, auxRole);
		}
		theRoles = (theRoles.equals(""))? theRoles: 
																		theRoles.substring(0, theRoles.length()-1);

		
// usrGroups is the set of groups currently associated with theUser
		List<AppGroup> usrGroups = usrCtrl.getGroups(theUser);
		String theGroups = "";
// adding groups
		tx.begin();
    System.out.println("Start to add groups for user admin? "+theUser.isAdmin());
		if (theUser.isAdmin()) {
			if (usrGroups == null ||
					usrGroups.size() < usrCtrl.getAllGroups().size()) { // to avoid duplicates on update
				List<AppGroup> allGroups = usrCtrl.getAllGroups();
				for (AppGroup aGrp: allGroups) {
					if (!usrGroups.contains(aGrp)) {
						RelGrpAppuser rel = new RelGrpAppuser (aGrp, theUser);
						hibSess.save(rel);
System.out.println("admin.jsp adding groups: "+aGrp.getName()+" & "+theUser.getUsername()+" with id "+rel.getId());
					}
				}
			}
		}
		else { // the user has any other role
			for (Integer iGroupId : aGroup) { // aGroup is the group ids from client
				AppGroup oGroup = (AppGroup)hibSess.get(AppGroup.class, iGroupId);
				theGroups += oGroup.getName()+",";
				if (usrGroups == null || !usrGroups.contains(oGroup)) {
					RelGrpAppuser oRelGrpUser = new RelGrpAppuser (oGroup, theUser);
					hibSess.save(oRelGrpUser);
System.out.println("admin.jsp adding groups: "+oGroup.getName()+" & "+theUser.getUsername()+" with id "+oRelGrpUser.getId());
				}
			}
		}
		tx.commit();

// removing groups from usrGroups, the set of groups the user had previously
// if admin role, do nothing
		if (!theUser.isAdmin()) {
			for (AppGroup auxGrp: usrGroups) {
				Integer grpId = auxGrp.getId();
				if (aGroup == null || !aGroup.contains(grpId))
					usrCtrl.rmvUserFromGroup(theUser, auxGrp);
			}
			theGroups = (theGroups.equals(""))? theGroups: 
												theGroups.substring(0, theGroups.length()-1);
		}
		
		
// adding prjects to theUser
		List<Project> usrPrjs = usrCtrl.getProjects(theUser);
		String thePrjs = "";
		tx.begin();
		for (Integer iProjectId : aProject) {
/*
RelPrjAppusers oRelPrjUser = new RelPrjAppusers ();
oRelPrjUser.setProject(oProject);
oRelPrjUser.setAppuser(theUser);
*/
			Project oProject = (Project)hibSess.get(Project.class, iProjectId);
			thePrjs += oProject.getName()+",";
			if (usrPrjs == null || !usrPrjs.contains(oProject)) {
				RelPrjAppusers rpu = new RelPrjAppusers (oProject, theUser);
				hibSess.save(rpu);
			}
		}
		tx.commit();
		
// removing projects for the user
// if admin role, do nothing
		if (!theUser.isAdmin()) {
			for (Project auxPrj: usrPrjs) {
				Integer prjId = auxPrj.getId();
				System.out.println("admin.jsp: user project: "+auxPrj.getName() +"("
						+prjId+")");
				if (aProject == null || !aProject.contains(prjId))
					usrCtrl.rmvUserFromPrj(theUser, auxPrj);
			}
			thePrjs = (thePrjs.equals(""))? thePrjs: 
										thePrjs.substring(0, thePrjs.length()-1);
		}

//		tx.commit();
		res = true;

		hException.put("id", newUsrId.toString());
		hException.put("name", sUsername);
		hException.put("firstname", sFirstName);
		hException.put("lastname", sLastName);
		hException.put("type","info");
		hException.put("message","User was register successfully");
		hException.put("roles", theRoles);
	}
	catch (HibernateException hibEx) {
		if (tx == null)
			tx.rollback();
		
		res = false;
		hibEx.printStackTrace();
		hException.put("id","500");
		hException.put("type","error");
		hException.put("message",hibEx.getMessage());
	}
	hibSess.close();
}

// This has to be converted to JSON
if (res)
	out.print("{\"id\":"+hException.get("id")+
		",\"type\":\""+hException.get("type")+
		"\",\"message\":\""+hException.get("message")+
		"\",\"roles\":\""+hException.get("roles")+
		"\",\"firstname\":\""+hException.get("firstname")+
		"\",\"lastname\":\""+hException.get("lastname")+
		"\",\"name\":\""+hException.get("name")+"\"}");
else
	out.print("{\"id\":"+hException.get("id")+
			",\"type\":\""+hException.get("type")+
			"\",\"message\":\""+hException.get("message")+"\"}");


%>


