
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html;charset=UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%@ page import="org.hibernate.Session"%>

<%@ 
	page
	import="org.cnio.appform.util.HibernateUtil,
			org.cnio.appform.util.AppUserCtrl, org.cnio.appform.util.Singleton,
			org.cnio.appform.entity.AppuserRole, org.cnio.appform.entity.Role,
			org.cnio.appform.entity.AppUser, org.cnio.appform.entity.AppGroup, 
			org.cnio.appform.entity.GroupType, org.cnio.appform.entity.Project"%>
			
<%@ page import="java.util.List"%>

<%
	// Checking the users permissions
	String user = request.getUserPrincipal().getName();
System.out.println("Principal's name: "+user);

	List<Role> roleList = null;
	AppUserCtrl userCtrl = null;
	Session hibSes = null;
	
	hibSes = HibernateUtil.getSessionFactory().openSession();

	userCtrl = new AppUserCtrl(hibSes);
	roleList = userCtrl.getAllRoles();
/*	
	List<String> activeUsrs = Singleton.getInstance().getLoggedUsers();
	activeUsrs.remove(user);
	pageContext.setAttribute("activeUsrs", activeUsrs);
	pageContext.setAttribute ("numUsrs", activeUsrs.size());
*/	
%>

<html>
<head>
<title>Admin Form Tool</title>

<link rel="stylesheet" type="text/css" href="../css/portal_style.css"
	id="portalCss" />
<link rel="stylesheet" type="text/css" href="../css/admintool.css"
	id="adminCss" />
<link rel="stylesheet" type="text/css" href="../css/overlay.css" />

<link rel="shortcut icon" href="../img/favicon.ico" />
<script type="text/javascript" language="javascript" src="../js/mixed2b.js"></script>

<!-- yahoo event, dom and connection files -->
<script type="text/javascript" src="../js/yahoo/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../js/yahoo/connection-debug.js"></script>
<script type="text/javascript" src="../js/yahoo/json-debug.js"></script>

<script type="text/javascript" src="../js/jquery/jquery-1.2.6.js"></script>

<script type="text/javascript" src="../js/yahoo/ajaxreq.js"></script>
<script type="text/javascript" src="../js/overlay.js"></script>

<!-- ExtJs library files -->
<link rel="stylesheet" type="text/css" href="../js/lib/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="../js/lib/ext/resources/css/xtheme-gray.css" />

<script language="JavaScript1.2" type="application/javascript" 
			src="../js/lib/ext/adapter/ext/ext-base.js"></script>
<script language="JavaScript1.2" type="application/javascript" 
			src="../js/lib/ext/ext-all-debug.js"></script>
			
<!-- Configuration variables file -->
<script type="text/javascript" src="../js/admin-cfg.js"></script>
			
<!-- Admin tool scripts -->
<script type="text/javascript" src="../js/admin-ajaxresp.js"></script>
<script type="text/javascript" src="../js/adminctrl.js"></script>

<script type="text/javascript" src="../js/clon-ajaxresp.js"></script>
<script type="text/javascript" src="../js/cloningctrl.js"></script>

				
	<!--
	<script> 
    Ext.onReady(function(){ 
       Ext.Msg.alert('Hello', 'World'); 
    }); 
 	</script>
-->
</head>

<body xonload="onReady();" id="body">
<script type="text/javascript" src="../js/wz_tooltip.js"></script>

<div id="portal-container">
<div id="sizer">
<div id="expander">
<table border="0" cellpadding="0" cellspacing="0"
	id="header-container-adm">
	<tr valign="bottom">
		<td align="center" valign="middle" id="header">

		<div id='dashboardnavAdm' align="left">User: <b><%=session.getAttribute("user")%></b><br />
		Roles: <b><%=session.getAttribute("roles")%></b><br />
		<a href="<%= response.encodeURL("../logout.jsp?adm=1") %> "
			style="text-decoration: none; color: darkblue;">Logout</a> <!-- p:region regionName='dashboardnav' regionID='dashboardnav'/> -
							&nbsp;&nbsp;
							<a href="http://localhost:8080/portal/auth/dashboard">Dashboard</a>
							&nbsp;&nbsp;|
							&nbsp;&nbsp;<a href="http://localhost:8080/portal/auth/portal/admin">Admin</a>&nbsp;&nbsp;| -
							<a href="http://localhost:8080/portal/signout">Logout</a>--></div>

		<span
			style="font-family: Arial, Helvetica, sans-serif; font-size: 24px; font-weight: bold;">Application
		Form Tool Administration</span></td>
	</tr>
</table>

<!-- HERE STARTS THE CENTRAL PART, BOTH THE MENU AND CONTENT AREAS -->
<div id="content-container"><!-- ****************** START MENU (CENTER) AREA (REGION D) ***************** -->
<div id='regionD'>
<div id='registerAdminForm'
	style="font-family: Arial, Helvetica, sans-serif; font-size: 12px;">
	<span style="color: red;visibility:hidden;" id="spanDis" name="spanDis">
	This user is <b>disabled</b>.
		<a href="javascript:admCtrl.switchUsr();" style="color: black">Click</a> to enable it
	</span>
<table border="0" cellspacing="5">
	<input type="hidden" name="frmUsrId" id="frmUsrId" value="" />
	<tr>
		<th align="right" width="10%">Username:</th>
		<td align="left">
		<table border="0">
			<tr>
				<td><input type="text" id="username" class="lesserInputRegionD">
				</td>
				<td align="right" width="100"><b>First name:</b></td>
				<td><input type="text" id="firstname" name="firstname"
					class="lesserInputRegionD"></td>
			</tr>
		</table>
		</td>
	</tr>
 	
	<tr>
		<th align="right">Password:</th>
		<td align="left">
		<table border="0">
			<tr>
				<td><input type="password" id="password" name="password"
					class="lesserInputRegionD"></td>
				<td align="right" width="100"><b>Last name:</b></td>
				<td><input type="text" id="lastname" name="lastname"
					class="lesserInputRegionD"></td>
			</tr>
		</table>

		</td>
	</tr>
	<tr>
		<th align="right">Password (confirm):</th>
		<td align="left">
		<table border="0">
			<tr>
				<td><input type="password" id="re-password" name="re-password"
					class="lesserInputRegionD"/><br/>
					
					</td>
				<td align="right" width="100"><b>Email:</b></td>
				<td><input type="text" id="email" name="email"
					class="lesserInputRegionD"></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="3" height="10px">
		<a href="javascript:admCtrl.resetPasswd();" style="color:red">Reset password</a>
		</td>
	</tr>
	<tr>
		<td colspan="3" height="10px">
		</td>
	</tr>
	<tr>
		<td colspan="3" align="left" class="itemstitle">Roles</td>
	</tr>
	
	<tr>
<!-- 	<th align="right">Role:</th> -->
		<td colspan="2" align="left" style="border: 3px solid darkgray"><!-- table for the role group of components -->
		<table border="0" cellpadding="5" width="100%">
			<tr>
				<td><select class="selectRegionD" id="registered_role"
					multiple="multiple" size="4">
					<%
						if (roleList != null) {
							for (Role role : roleList)
								out.println("<option value='" + role.getId() + "'>" + role.getName()
										+ "</option>");
						}
					%>
				</select></td>
				<td align="center" valign="middle">
				<div class='rightButtonList'
					onClick="moveOptions(document.getElementById('registered_role'), document.getElementById('selected_role'));"></div>
				<div class='leftButtonList'
					onClick="moveOptions(document.getElementById('selected_role'), document.getElementById('registered_role'));"></div>
				</td>
				<td><select class="selectRegionD" id="selected_role"
					name="selected_role" size="5" multiple="multiple"></select></td>
			</tr>
			<tr>
				<td colspan="3" height="2px"></td>
			</tr>
			<tr bgcolor="lightgray">
				<td>

				<table>
					<tr>
						<td class="paddingGrps"><input type="text" id="frmRoleName" name="frmRoleName"
							class="textInputRegionD" value="New role name"
							onclick="this.value=''" /></td>
					</tr>
					<tr>
						<td class="paddingGrps"><textarea rows="2" class="textInputRegionD" id="frmRoleDesc"
							name="frmRoleDesc">Role description</textarea></td>
					</tr>
				</table>

				</td>
				<td align="center" valign="top" width="80"><input type="button"
					id="btnNewGrp" name="btnNewRole" onclick="admCtrl.newRole();"
					value="New" /></td>
				<td></td>
				<!-- 
				<td valign="top"><input type="button" id="btnRmvRole"
					name="btnRmvRole" onclick="javascript:alert('rmv role');"
					value="Remove" /></td>
-->
			</tr>
		</table>

		</td>
	</tr>

	<tr>
		<td colspan="3" height="25px"></td>
	</tr>
	<tr>
		<td colspan="3" align="left" class="itemstitle">Groups</td>
	</tr>
	<tr style="border: 1px solid darkgray;">
		<td colspan="2" align="left" style="border: 3px solid darkgray">
		<table border="0" cellpadding="5" width="100%">
			<tr>
				<td><select class="selectRegionD" id="registered_group"
					size="5" multiple="multiple">
					<%
						List<AppGroup> groupList = userCtrl.getAllGroups();
						for (AppGroup group : groupList) {
							String idOpt = "id=\"g-"+group.getId()+"-"+group.getType().getId()+"\"";
							out.println("<option "+idOpt+" value=\"" + group.getId() + "\"" + 
									" onmouseover=\"Tip('"+ group.getName() + "');\" onmouseout=\"UnTip();\">" + 
									group.getName()
									+ "</option>");
						}
					%>
				</select></td>

				<td align="center" valign="middle">
				<div class='rightButtonList'
					onClick="moveOptions(document.getElementById('registered_group'), document.getElementById('selected_group'));"></div>
				<div class='leftButtonList'
					onClick="moveOptions(document.getElementById('selected_group'), document.getElementById('registered_group'));"></div>
				</td>
				<td><select class="selectRegionD" id="selected_group"
					name="selected_group" size="5" multiple="multiple"></select></td>
			</tr>
			<tr>
				<td colspan="3" height="2px"></td>
			</tr>
			<tr bgcolor="lightgray">
				<td>

				<table>
					<tr>
						<td class="paddingGrps"><input type="text" id="frmGrpName" name="frmGrpName"
							class="textInputRegionD" value="New group name"
							onclick="this.value=''" onmouseover="Tip('The name of the group');" 
							onmouseout="UnTip();" /></td>
					</tr>
					<tr>
						<td class="paddingGrps"><input type="text" id="frmGrpCode" name="frmGrpCode"
							class="textInputRegionD" value="New group code"
							onclick="this.value=''" onmouseover="Tip('The code for the group as described elsewhere');" 
							onmouseout="UnTip();"/></td>
					</tr>
					<tr>
						<td class="paddingGrps"><select class="textInputRegionD" id="frmGrpType"
							name="frmGrpType" 
							onmouseover="Tip('The type of the group (COUNTRY or HOSPITAL, i.e. primary or secondary)');" 
							onmouseout="UnTip();"
						 	onchange="admCtrl.watchType(this);">
							<option value="-1">Choose a Group Type</option>
							<%
								List<GroupType> grpTypes = userCtrl.getGroupTypes();
								for (GroupType grpType : grpTypes) {
									out.println("<option value=\"" + grpType.getId() + "\">"
											+ grpType.getName() + "</option>");
								}
							%>
						</select></td>
					</tr>
					<tr>
						<td class="paddingGrps"><select class="textInputRegionD" id="frmGrpParent"
							name="frmGrpParent" disabled="disabled" 
							onmouseover="Tip('Choose a parent group in the case of the new one is a secondary one');" 
							onmouseout="UnTip();">
							<option value="-1">Choose a Parent Group</option>
							
						</select></td>
					</tr>
				</table>

				</td>
				<td align="center" valign="top" width="80"><input type="button"
					id="btnNewGrp" name="btnNewGrp" onclick="admCtrl.newGroup();"
					value="New" /></td>

				<td valign="top"><input type="button" id="btnRmvGrp"
					name="btnRmvGrp" onclick="javascript:alert('rmv group');"
					value="Remove" disabled="disabled" /></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="3" height="25px"></td>
	</tr>
	<tr>
		<td colspan="3" align="left" class="itemstitle">Projects</td>
	</tr>
	<tr>
		<!-- <th align="right">Project:</th> -->
		<td colspan="2"  align="left" style="border: 3px solid darkgray">
		<table border="0" cellpadding="5" width="100%">
			<tr>
				<td><select class="selectRegionD" id="registered_project"
					size="5" multiple="multiple">
					<%
						List<Project> projectList = userCtrl.getAllProjects();
						for (Project project : projectList) {
							out.println("<option value='" + project.getId() + "'>"
									+ project.getName() + "</option>");
						}
					%>
				</select></td>
				<td align="center" valign="middle">
				<div class='rightButtonList'
					onClick="moveOptions(document.getElementById('registered_project'), document.getElementById('selected_project'));"></div>
				<div class='leftButtonList'
					onClick="moveOptions(document.getElementById('selected_project'), document.getElementById('registered_project'));"></div>
				</td>
				<td><select class="selectRegionD" id="selected_project"
					size="5" multiple="multiple"></select></td>
			</tr>

			<tr>
				<td colspan="3" height="2px"></td>
			</tr>

			<tr bgcolor="lightgray">
				<td>
				<table cellspacing="0">
					<tr>
						<td class="paddingGrps"><input type="text" id="frmPrjName" name="frmPrjName"
							class="textInputRegionD" value="New project name"
							onclick="this.value=''" /></td>
					</tr>
					<tr>
						<td class="paddingGrps"><textarea rows="2" class="textInputRegionD" id="frmPrjDesc"
							name="frmPrjDesc">Project description</textarea></td>
					</tr>
					<tr>
						<td class="paddingGrps"><input type="text" id="frmPrjCode" name="frmPrjCode"
							class="textInputRegionD" value="New project code"
							onclick="this.value=''" />
						</td>
					</tr>
				</table>
				</td>

				<td align="center" valign="top" width="80"><input type="button"
					id="btnNewPrj" name="btnNewPrj" onclick="admCtrl.newProject();"
					value="New" /></td>
				<td valign="top"><input type="button" id="btnRmvPrj"
					name="btnRmvPrj" onclick="alert('rmv prj');" value="Remove" disabled="disabled"/></td>
			</tr>
		</table>
		</td>
	</tr>
	
	<tr><td colspan="2" height="20"></td></tr>
	<tr>
		<td align="right"><input type="button" id="btnSend"
			value="register" onclick="admCtrl.submitRegisterForm()"></td>
		<td align="left"><input type="button" value="reset"
			onclick="admCtrl.resetRegisterForm()"></td>
	</tr>
</table>
</div>
</div>
<!-- region D -->

<div id='regionE' align="center"><span
	style="text-align: left; margin: 10px 0px 0px 0px;"> <b>Registered
users</b></span><br>
<span style="text-align: left; margin: 10px 0px 0px 0px;font-size:10px"><b>(A)</b>Admin, 
<b>(E)</b>Editor, <b>(I)</b>Interviewer, <b>(C)</b>Curator, <b>(DM)</b>Data Mngr, 
<b>(CC)</b>Country Coordinator,
<b>(HC)</b>Hospital Coordinator, <b>(PC)</b>Project Coordinator,<b>(NC)</b>Node Coordinator,
 <b>(G)</b>Guest</span> <br>
<br>
<select id="listUsrs" name="listUsrs" multiple="multiple" size="10"
	class="selectRegionE" xonclick="admCtrl.displayUsr();">
	<%
		List<Object[]> userRoles = userCtrl.getAllUsers();
		int i = 0;
		AppUser old = null;
		String roleCode = "";
		
		while (i < userRoles.size()) {
			Object[] pair = userRoles.get(i);
			AppUser u = (AppUser) pair[0];
			AppuserRole ur = (AppuserRole) pair[1];

			String removed = u.wasRemoved()? " style=\"color:red;\" ": "";

			if (ur.getTheRole().getName().compareToIgnoreCase("admin") == 0)
				roleCode = "(A)";

			else if (ur.getTheRole().getName().compareToIgnoreCase("editor") == 0)
				roleCode = "(E)";

			else if (ur.getTheRole().getName().compareToIgnoreCase("interviewer") == 0)
				roleCode = "(I)";
			
			else if (ur.getTheRole().getName().compareToIgnoreCase(AppUserCtrl.GUEST_ROLE) == 0)
				roleCode = "(G)";
			
			else if (ur.getTheRole().getName().compareToIgnoreCase(AppUserCtrl.CURATOR_ROLE) == 0)
				roleCode = "(C)";
			
			else if (ur.getTheRole().getName().compareToIgnoreCase(AppUserCtrl.DATAMGR_ROLE) == 0)
				roleCode = "(DM)";
			
			else if (ur.getTheRole().getName().compareToIgnoreCase(AppUserCtrl.COUNTRY_MNGR_ROLE) == 0) 
				roleCode = "(CC)";
			
			else if (ur.getTheRole().getName().compareToIgnoreCase(AppUserCtrl.HOSP_MNGR_ROLE) == 0) 
				roleCode = "(HC)";

			else if (ur.getTheRole().getName().compareToIgnoreCase(AppUserCtrl.PRJ_MNGR_ROLE) == 0) 
				roleCode = "(PC)";
			
			else if (ur.getTheRole().getName().compareToIgnoreCase(AppUserCtrl.NODE_MNG_ROLE) == 0) 
				roleCode = "(NC)";

			else 
				roleCode = "(D)";
			
// System.out.println (ur.getTheRole().getName()+ " vs " +roleCode);
// this is because the user can have several roles					
			if (u == old)
				out.print(" " + roleCode);

			else { // the user is new...
				if (i > 0)
					out.println("</option>");

				out.print("<option value=\"" + u.getId()+"\""+ removed +"\">" + u.getUsername());
				out.print(" " + roleCode);
				old = u;
			}
			i++;
		}
		roleCode = null;
		old = null;
	%>
</select> <br>
<input type="button" id="btnSwitchUsr" name="btnSwitchUsr"
	onclick="admCtrl.switchUsr();" value=" Disable " />
	


<%-- 
LIST OF ACTIVE USERS --	
<div id="divActiveUsrs" style="margin-top:30px;" class="default-font">
<span style="font-weight:bold;font-color:darkblue;">
Users currently using the application</span>
<br>
<c:if test="${numUsrs eq 0}">
	No users curretnly logged in
</c:if>
<c:forEach items="${activeUsrs}" var="aUsr">
	<input type="checkbox" name="${aUsr}" style="border:1px solid darkblue;">
		${aUsr}</input>
</c:forEach>
<br>
<c:if test="${numUsrs gt 0}">
<a href="" onclick="admCtrl.rmvActiveUsrs();">Remove selected</a>
</c:if>
<span id="rmvUsrsSpan"></span>

</div> <!-- divActiveUsrs 
--%>

</div> <!-- regionE -->
<!--  region E --> 
<!-- ****************** END LEFT MENU AREA (REGION D) ***************** -->


<!-- ********************* START CLONING REGION DIV ************************ -->
<div id="divCloning" name="divCloning" 
		style="background:silver; margin-top:5px; 
		font-family: Arial, Helvetica, sans-serif; font-size: 12px;">
		<span class="itemstitle">Questionnaire Cloning</span><br/>
		Choose the source and target params in order to clone the interview for a new
		group andp project.<br/><br/>
		<div align="center">
		<form id="frmCloning" name="frmCloning">
			<span class="itemstitle">Source Questionnaire</span><br/>
			<select class="selectCloning" id="frmSrcPrj" name="frmSrcPrj">
				<option value="-1" selected="selected">Choose project...</option>
			<%
//						List<Project> projectList = userCtrl.getAllProjects();
				for (Project project : projectList) {
					out.println("<option value='" + project.getId() + "'>"
							+ project.getName() + "</option>");
				}
			%>
			</select>
			<select class="selectCloning" id="frmSrcGrp" name="frmSrcGrp">
				<option value="-1" selected="selected">Choose group...</option>
			<%
				List<AppGroup> primaryGrps = userCtrl.getPrimaryGroups();
				for (AppGroup group : primaryGrps) {
					out.println("<option value=\"" + group.getId() + "\"" + 
							" onmouseover=\"Tip('"+ group.getName() + "');\" onmouseout=\"UnTip();\">" + 
							group.getName()
							+ "</option>");
				}
			%>
			</select>
			<select class="selectCloning" id="frmSrcIntrvs" name="frmSrcIntrvs">
			</select>
			
			<hr/>
			<span class="itemstitle">Target Questionnaire</span><br/>
			<select class="selectCloning" id="frmTargetPrj" name="frmTargetPrj" 
						disabled="disabled">
				<option value="-1" selected="selected">Choose project...</option>
			<%
//						List<Project> projectList = userCtrl.getAllProjects();
				for (Project project : projectList) {
					out.println("<option value='" + project.getId() + "'>"
							+ project.getName() + "</option>");
				}
			%>
			</select>
			<select class="selectCloning" id="frmTargetGrp" name="frmTargetGrp" 
						disabled="disabled">
				<option value="-1" selected="selected">Choose group...</option>
			<%
//				List<AppGroup> primaryGrps = userCtrl.getPrimaryGroups();
				for (AppGroup group : primaryGrps) {
					out.println("<option value=\"" + group.getId() + "\"" + 
							" onmouseover=\"Tip('"+ group.getName() + "');\" onmouseout=\"UnTip();\">" + 
							group.getName()
							+ "</option>");
				}
			%>
			</select>
			<span class="itemstitle">New questionnaire name</span>
			<input type="text" name="frmTargetIntrv" id="frmTargetIntrv" 
			 class="lesserInputRegionD" style="width:175px" disabled="disabled" />
			<br/><br/>
			<p align="right">
			<input type="button" name="btnOk" id="btnOk" value=" Create " />&nbsp;&nbsp;
			<input type="button" name="btnClr" id="btnClr" value=" Reset " />	
			</p>
			
		</form>
		</div>
</div>
<!-- ********************* END CLONING REGION DIV ************************** -->


</div>
<!-- content-container --></div>
<!-- expander --></div>
<!-- sizer --></div>
<!-- portal-container -->


<!-- FOOTER AND END OF PAGE -->
<div id="footer-container-adm" class="portal-copyright-adm">Developed
at <a class="portal-copyright" href="http://www.inab.org">CNIO/INB</a><br />
</div>

<%-- this is to create the modal "dialog" to run the progress bar --%>
<div id="overlay">
<div>
<p style="font-family: Arial, Helvetica, sans-serif; Font-size: 12px;">
Processing...</p>
<p><img src="../img/ajax-loader-trans.gif" alt="Processing..." /></p>
</div>
</div>

<%
	if (hibSes != null)
		hibSes.close();
%>
</body>
</html>
