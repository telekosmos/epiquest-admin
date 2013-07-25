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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">


  <!-- Les styles -->
  <link rel="stylesheet" type="text/css" href="../css/overlay.css" />
  
  <link href="../css/bootstrap.min.css" rel="stylesheet">
  <link href="../css/bootstrap-responsive.min.css" rel="stylesheet">
  <style type="text/css">
  /*
    body {
      padding-top: 160px;
      padding-bottom: 40px;
    }
*/
    /* Sticky footer styles
      -------------------------------------------------- */

      html,
      body {
        height: 100%;
        /* The html and body elements cannot have any padding or margin. */
      }

      /* Wrapper for page content to push down footer */
      #wrap {
        min-height: 100%;
        height: auto !important;
        height: 100%;
        /* Negative indent footer by it's height */
        margin: 0 auto -60px;
      }

      /* Set the fixed height of the footer here */
      #push,
      #footer {
        height: 60px;
      }
      #footer {
        background-color: #f5f5f5;
        margin-top: 5%;
        border-top: 1px solid darkgray;
      }

      /* Lastly, apply responsive CSS fixes as necessary */
      @media (max-width: 767px) {
        #footer {
          margin-left: -20px;
          margin-right: -20px;
          padding-left: 20px;
          padding-right: 20px;
        }
      }

      /* Custom page CSS
      -------------------------------------------------- */
      /* Not required for template or sticky footer method. */

      #wrap > .container {
        padding-top: 100px;
      }

      #wrap > .container-fluid  {
        padding-top: 5%;

      }

      .container-fluid > hr {
        margin: 30px 0;
      }

      .container-fluid .intro-description {
        padding: 1% 3%;
        /* font-size: 24px; */
        line-height: 1.25;
      }

      .page-title {
        padding-left: 2%;
        text-align: center;
      }

      .page-title h1 {
        font-size: 42px
      }

      .section-row {
        padding-left: 12.5%;
      }

      .title-row {
        padding-left: 5%;
      }

      form .controls {
        padding: 1% 0%;
      }

      form select {
        padding: 1% 0%;
      }
      
  </style>
  
  <title>EPIQUEST Admin</title>
</head>
<body style="xoverflow-x: hidden;">
<div id="wrap">
<!-- 
<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container-fluid">
      <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        <span class="icon-bar">x</span>
        <span class="icon-bar">y</span>
        <span class="icon-bar">z</span>
      </button>
      <a class="brand" href="#">EPIQUEST Admin</a>
      <div class="nav-collapse collapse">
        
        <p class="navbar-text pull-right">
          Logged in as <a href="#" class="navbar-link">username (administrator)</a>
        </p>
      
        <ul class="nav">
          <li><a href="users.html">Users</a></li>
          <li class="active"><a href="cloning.html">Cloning</a></li>
          -- <li><a href="#contact">Contact</a></li> --
          <li><a href="dbmanage.html">DB Management</a></li>
        </ul>
      </div> --/.nav-collapse -
    </div>
  </div>
</div>
-->

<%@include file="inc/navbar.jsp" %>

<div class="container-fluid">
  <div class="page-header page-title">
    <h1>EPIQUEST admin tool - User management</h1>
  </div>
  <!-- Intro -->
  <div class="row-fluid">
    <div class="span12">
    <p class="intro-description text-center">
      This is the main page of the administration tool. From here, by clicking on the upper navigation bar links or using the green buttons below, you can reach the sections to manage the main parts of the application.
    </p>
    </div>
  </div>
  <hr>

  <!-- Admins -->
  <div class="row-fluid">
    <div class="span3 offset1">
      <p class="lead">Users</p>
      <select size="12" class="input-block-level" id="listUsrs" name="listUsrs">
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
      </select>
      <div class="accordion" id="accordionKey">
        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" xdata-parent="#accordionKey" href="#collapseOne">
              Role key
            </a>
          </div>
          <div id="collapseOne" class="accordion-body collapse in">
            <div class="accordion-inner">
              <small>
      <b>(A)</b> Admin, <b>(E)</b> Editor, <b>(I)</b> Interviewer, <b>(C)</b> Curator, <b>(DM)</b> Data Mngr, 
<b>(CC)</b> Country Coordinator,
<b>(HC)</b> Hospital Coordinator, <b>(PC)</b> Project Coordinator,<b>(NC)</b> Node Coordinator,
 <b>(G)</b> Guest
              </small>
            </div>
          </div>
        </div>
      </div> <!-- EO accordion -->
    </div>

    <form class="form-horizontal">
    	<input type="hidden" name="frmUsrId" id="frmUsrId" value="" />
      <div class="span7" style="border-left: 1px solid darkgray;">
        <!-- div class="container-fluid" -->
        <div class="row-fluid" style="padding-left: 5%;">
          <p class="lead" style="xmargin-left: 5%">Selected user</p>
          <div class="control-group">
          <div class="span6">
            <label class="control-label" for="username">Username</label>
            <div class="controls">
            <input type="text" id="username">
            </div>

            <label class="control-label" for="password">Password</label>
            <div class="controls">
            <input type="password" id="password">
            </div>

            <label class="control-label" for="re-password">Password (confirm)</label>
            <div class="controls">
            <input type="password" id="re-password">
            </div>

          </div>

          <div class="span6">
            
            <label class="control-label" for="firstname">First name</label>
            <div class="controls">
            <input type="password" id="firstname">
            </div>

            <label class="control-label" for="lastname">Last Name</label>
            <div class="controls">
            <input type="text" id="lastname">
            </div>

            <label class="control-label" for="email">Email</label>
            <div class="controls">
            <input type="text" id="email">
            </div>
              
          </div>
          </div>
        </div> <!-- EO row-fluid for textboxes -->
        <!-- /div -->

        <!-- COMPONENT FOR GROUPS -->
        <!-- div class="container-fluid" style="border: 1px solid black;" -->
          <div class="row-fluid title-row">
            <div class="span12">
              <p class="text-left lead" style="xmargin-left:10%;">Groups</p>
            </div>
          </div>

          <!-- div class="container-fluid" style="padding-left:12.5%" -->
          <div class="row-fluid section-row" style="text-align: center">
            <div class="span5">
              <select size="5" class="input-block-level" id="registered_group" multiple="multiple">
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
              </select>
            </div>
            
            <div class="span2">
              <button class="btn btn-medium btn-primary btn-block" type="button" style="margin-top: 20%"
              	onClick="moveOptions(document.getElementById('registered_group'), document.getElementById('selected_group'));">
              	<i class="icon-arrow-right icon-white"></i>
              </button>
              <button class="btn btn-medium btn-primary btn-block" type="button"
              	onClick="moveOptions(document.getElementById('selected_group'), document.getElementById('registered_group'));">
              	<i class="icon-arrow-left icon-white"></i>
              </button>
            </div>
            
            <div class="span5">
              <select size="5" class="input-block-level" id="selected_group"
								name="selected_group" multiple="multiple">
                </select>
            </div>
          </div> <!-- EO list - buttons - list -->

          <div class="row-fluid" style="padding: 2% 0% 0% 12.5%;">
            <div class="span12" style="padding-top: 1%;">
              <input class="input-medium" type="text" id="frmGrpName" name="frmGrpName" placeholder="Group name"/>
              <input class="input-small" type="text" id="frmGrpCode" name="frmGrpCode" placeholder="Group code"/>
              
              <select class="input-medium" id="frmGrpType" name="frmGrpType"
                onchange="admCtrl.watchType(this);">
								<option value="-1">Group Type</option>
								<%
									List<GroupType> grpTypes = userCtrl.getGroupTypes();
									for (GroupType grpType : grpTypes) {
										out.println("<option value=\"" + grpType.getId() + "\">"
												+ grpType.getName() + "</option>");
									}
								%>
	            </select>
              
              <select class="input-medium" id="frmGrpParent" name="frmGrpParent" disabled="disabled">
                <option value="-1" selected="selected">Parent group</option>
              </select>
              
              <button class="btn btn-small btn-primary" type="button" id="btnNewGrp" 
              	name="btnNewGrp" onclick="admCtrl.newGroup();"
              	style="margin-left:1%">New</button>
            </div>
          </div>
          <!--/div> <!-- container-fluid -->
        <!-- EO COMPONENTS FOR GROUPS -->

        <!-- COMPONENT FOR PROJECTS -->
        <!-- div class="container-fluid" style="border: 1px solid black; margin-left: 10%;" -->
        <div class="row-fluid title-row">
          <div class="span12">
            <p class="text-left lead" style="xmargin-left:10%;">Projects</p>
          </div>
        </div>

        <div class="row-fluid section-row" style="text-align: center;">
          <div class="span5">
            <select size="5" class="input-block-level" id="registered_project" 
            	name="registered_project" multiple="multiple">
              <%
								List<Project> projectList = userCtrl.getAllProjects();
								for (Project project : projectList) {
									out.println("<option value='" + project.getId() + "'>"
											+ project.getName() + "</option>");
								}
							%>
            </select>
          </div>
          <div class="span2">
            <button class="btn btn-medium btn-primary btn-block" type="button" style="margin-top: 20%" 
            onClick="moveOptions(document.getElementById('registered_project'), document.getElementById('selected_project'));">
            	<i class="icon-arrow-right icon-white"></i>
            </button>
            <button class="btn btn-medium btn-primary btn-block" type="button"
            	onClick="moveOptions(document.getElementById('selected_project'), document.getElementById('registered_project'));">
            	<i class="icon-arrow-left icon-white"></i>
            </button>
          </div>
          <div class="span5">
            <select size="5" class="input-block-level" id="selected_project" multiple="multiple">
            </select>
          </div>
        </div> <!-- EO list - buttons - list -->

        <div class="row-fluid section-row">
          <div class="span12" style="padding-top: 1%;">
            <input class="input-medium" type="text" id="frmPrjName" name="frmPrjName" placeholder="Project name"/>
            <input class="input-large" type="text" id="frmPrjDesc" name="frmPrjDesc" placeholder="Project description"/>
            <input class="input-medium" type="text" id="frmPrjCode" name="frmPrjCode" placeholder="Project code"/>
            <button class="btn btn-small btn-primary" type="button" style="margin-left: 1%;">New</button>
          </div>
        </div>
        <!-- /div -->
        <!-- EO COMPONENTS FOR PROJECTS -->
      

        <!-- COMPONENT FOR ROLES -->
        <div class="row-fluid title-row">
          <div class="span12">
            <p class="text-left lead">Roles</p>
          </div>
        </div>

        <div class="row-fluid section-row" style="text-align: center;">
          <div class="span5">
            <select size="5" class="input-block-level" id="registered_role" multiple="multiple">
              <%
								if (roleList != null) {
									for (Role role : roleList)
										out.println("<option value='" + role.getId() + "'>" + role.getName()
												+ "</option>");
								}
							%>
            </select>
          </div>
          
          <div class="span2">
            <button class="btn btn-medium btn-primary btn-block" type="button" style="margin-top: 20%"
            	onClick="moveOptions(document.getElementById('registered_role'), document.getElementById('selected_role'));">
            	<i class="icon-arrow-right icon-white"></i>
            </button>
            <button class="btn btn-medium btn-primary btn-block" type="button"
            	onClick="moveOptions(document.getElementById('selected_role'), document.getElementById('registered_role'));">
	            <i class="icon-arrow-left icon-white"></i>
	          </button>
          </div>
          
          <div class="span5">
            <select size="5" class="input-block-level" id="selected_role"
							name="selected_role" multiple="multiple">
            </select>
          </div>
        </div> <!-- EO list - buttons - list -->

        <div class="row-fluid section-row">
          <div class="span12" style="padding-top: 1%;">
            <input class="input-medium" type="text" id="frmRoleName" name="frmRoleName" 
            	placeholder="Role name"/>
            <input class="input-large" type="text" id="frmRoleDesc" name="frmRoleDesc" 
            	placeholder="Role description"/>
            <button class="btn btn-small btn-primary" type="button" style="margin-left:2.5%">New</button>
          </div>
        </div>
        <!-- EO COMPONENTS FOR ROLES -->

 
        <div class="row-fluid" style="padding: 5% 0% 0% 17.5%;">
          <div class="span4 offset8">
            <button type="button" id="btnSend" onclick="admCtrl.submitRegisterForm();" 
            	class="btn btn-inverse"><i class="icon-ok icon-white"></i> Register
            </button>
            <button type="reset" class="btn btn-inverse"><i class="icon-repeat icon-white"></i> Reset</button>
          </div>
        </div>

      </div> <!-- EO span7 div, content form area -->

      

    </form>
  </div> <!-- EO row-fluid -->
</div> <!-- EO container -->

<div id="footer">
  <div class="container">
    <p class="muted credit">
      &copy; INB, CNIO - Epidemiology Group
    </p>
  </div>
</div>

</div> <!-- EO wrap -->

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

<!-- yahoo event, dom and connection files -->
<script type="text/javascript" src="../js/overlay.js"></script>
<script type="text/javascript" src="../js/yahoo/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../js/yahoo/connection-debug.js"></script>
<script type="text/javascript" src="../js/yahoo/json-debug.js"></script>

<script type="text/javascript" src="../js/jquery/jquery-1.2.6.js"></script>

<script type="text/javascript" src="../js/yahoo/ajaxreq.js"></script>
<script type="text/javascript" src="../js/overlay.js"></script>
<script type="text/javascript" src="../js/mixed2b.js"></script>
			
<!-- Configuration variables file -->
<script type="text/javascript" src="../js/admin-cfg.js"></script>
			
<!-- Admin tool scripts -->
<script type="text/javascript" src="../js/admin-ajaxresp.js"></script>
<script type="text/javascript" src="../js/adminctrl.js"></script>

<script type="text/javascript" src="../js/wz_tooltip.js"></script>

<script type="text/javascript" src="../assets/js/bootstrap.js"></script>

</body>
</html>






