<%@
	page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
%>
        
<%@
	page import="org.hibernate.Session,	org.hibernate.Transaction,
				org.hibernate.HibernateException"
%>
<%@
	page import="org.cnio.appform.util.HibernateUtil,
				org.cnio.appform.util.HibController, org.cnio.appform.util.AppUserCtrl,
				org.cnio.appform.entity.*"
%>
<%@
	page import="java.util.Collection, java.util.Iterator, java.util.List,
				java.util.ArrayList, java.util.Vector, java.util.Enumeration,
				java.util.Hashtable, java.net.URLDecoder"
%>

<%
final String GRP_CODE = "GRP";
final String PRJ_CODE = "PRJ";
final String ROL_CODE = "ROL";
%>
<%
//out.print("{\"id\":"+hException.get("id")+",\"type\":\""+hException.get("type")+"\",\"message\":\""+hException.get("message")+"\"}");
String typeName = request.getParameter("what");
String msg = "", jsonStr = "", itemName = "";
int newId = 0;

Session hibSes = HibernateUtil.getSessionFactory().openSession();
AppUserCtrl usrCtrl = new AppUserCtrl (hibSes);

if (typeName == null) {
	msg = "You need specify the type of element to register";
	jsonStr = "{\"res\":0,\"msg\":\""+msg+"\"}";
}

else if (typeName.equalsIgnoreCase(GRP_CODE)) {
	String grpName = request.getParameter("frmGrpName");
	String grpTypeId = request.getParameter ("frmGrpType");
	String grpCode = request.getParameter ("frmGrpCode");
	String grpParent = request.getParameter("frmGrpParent");

	Integer grpParentId = grpParent == null? -1: Integer.decode(grpParent);
	itemName = URLDecoder.decode(grpName, "UTF-8");
	newId = usrCtrl.createGroup(itemName, grpCode, 
														Integer.decode(grpTypeId), grpParentId);
	
	if (newId > 0) 
		msg = "Group '"+grpName+"' was succesfully created";
	else
		msg = "Group '"+grpName+"' could not be added to the database";
}

else if (typeName.equalsIgnoreCase(PRJ_CODE)) {
	String prjName = request.getParameter("frmPrjName"),
				prjDesc = request.getParameter("frmPrjDesc"),
				prjCode = request.getParameter("frmPrjCode");
	
	itemName = URLDecoder.decode(prjName, "UTF-8");
	prjDesc = URLDecoder.decode(prjDesc, "UTF-8");
	prjCode = URLDecoder.decode(prjCode, "UTF-8");
	newId = HibernateUtil.createProj(hibSes, itemName, prjDesc, prjCode);
	
	if (newId > 0) 
		msg = "Project '"+prjName+"' was succesfully created";
	else
		msg = "Project '"+prjName+"' could not be added to the database";
}

else if (typeName.equalsIgnoreCase(ROL_CODE)) {
	String roleName = request.getParameter("frmRoleName"),
				roleDesc = request.getParameter("frmRoleDesc");
	
	roleName = URLDecoder.decode(roleName, "UTF-8");
	roleDesc = URLDecoder.decode(roleDesc, "UTF-8");
	newId = usrCtrl.createRole(roleName, roleDesc);
	
	if (newId > 0) 
		msg = "Role '"+roleName+"' was succesfully created";
	else
		msg = "Role '"+roleName+"' could not be added to the database";
}


hibSes.close();

if (newId > 0) {
	jsonStr = "{\"res\":"+1;
	jsonStr += ",\"id\":"+newId+",\"name\":\""+itemName;
	if (typeName.equalsIgnoreCase(PRJ_CODE))
		jsonStr += "\",\"code\":\""+request.getParameter("frmPrjCode");
	
	jsonStr += "\",\"msg\":\""+msg+"\"}";	
}
else
	jsonStr = "{\"res\":"+0+",\"msg\":\""+msg+"\"}";

System.out.println(jsonStr.replace('\n', ' '));

out.print(jsonStr.replace('\n', ' ').trim());
%>