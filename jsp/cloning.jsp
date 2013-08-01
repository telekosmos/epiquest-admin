<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
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

<!DOCTYPE html>
<html lang="en">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">

  <!-- Le styles -->
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

      .container .credit {
        margin: 20px 0;
      }

      .container-fluid .credit {
        margin: 2% 0%;
      }

      .page-title {
        padding-left: 2%;
      }

      .description-list {
        padding-left: 2%;
      }

      .box-form {
        background-color: #EEEEEE;
        padding: 1%;
        border-radius:5px;
      }

      code {
        font-size: 80%;
      }
  </style>
  
  <title>EPIQUEST Admin</title>
</head>
<body>
<div id="wrap">

<%@include file="inc/navbar.jsp" %>

<%-- 
<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container-fluid">
      <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        <span class="icon-bar">x</span>
        <span class="icon-bar">y</span>
        <span class="icon-bar">z</span>
      </button>
      <a class="brand" href="index.jsp">EPIQUEST Admin</a>
      <div class="nav-collapse collapse">
        
        <p class="navbar-text pull-right">
          Logged in as <a href="#" class="navbar-link">username (administrator)</a>
        </p>
      
        <ul class="nav">
          <li><a href="users.jsp">Users</a></li>
          <li class="active"><a href="#">Cloning</a></li>
          <!-- <li><a href="#contact">Contact</a></li> -->
          <li><a href="dbmanage.jsp">DB Management</a></li>
        </ul>
      </div>  !--/.nav-collapse --
    </div>
  </div>
</div>
--%>


<div class="container-fluid">
  <div class="page-header page-title">
    <h1>Questionnaire cloning</h1>
  </div>
  <div class="description-list">
    <ul>
      <li>Choose at the left side the source questionnaire you want to clone by selecting project and group, and then clicking the questionnaire to copy</li>
      <li>
        At the right side, set the target project and group, type in the new questionnaire name</li>
      <li>Just click on 'Create' button to clone the source questionnaire into the target one</li>
    </ul>
  </div>
</div> <!-- EO container -->


<div class="container-fluid">
  <form id="frmCloning" name="frmCloning">
  <div class="row-fluid">
    <div class="span4 offset2 box-form">
      <legend>Source Questionnaire</legend>
      <select class="input-block-level" id="frmSrcPrj" name="frmSrcPrj">
        <option value="-1" selected="selected">Choose project...</option>
        <%
				List<Project> projectList = userCtrl.getAllProjects();
				for (Project project : projectList) {
					out.println("<option value='" + project.getId() + "'>"
							+ project.getName() + "</option>");
				}
				%>
				<!-- 
        <option value="-1">Pangen-EU</option>
        <option value="-1">IsBlac</option>
        <option value="-1">MamaBlac</option>
        -->
      </select>
      <select class="input-block-level" id="frmSrcGrp" name="frmSrcGrp">
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
        <!-- 
        <option value="-1">Asturias</option>
        <option value="-1">Hospital Universitario Nuestra Señoras de las Calzas de Elche</option>
        -->
      </select>
      <select class="input-block-level" id="frmSrcIntrvs" name="frmSrcIntrvs">
        <option value="-1">No interviews yet</option>
      </select>

      <!--
      <img src="http://placehold.it/480x340"/>
    -->
    </div>
    <!-- TARGET INTERVIEW TO CLONE -->
    <div class="span4 box-form">
      <legend>Target Questionnaire</legend>
      <select class="input-block-level" id="frmTargetPrj" name="frmTargetPrj">
        <option value="-1" selected="selected">Choose project...</option>
        <%
//						List<Project> projectList = userCtrl.getAllProjects();
				for (Project project : projectList) {
					out.println("<option value='" + project.getId() + "'>"
							+ project.getName() + "</option>");
				}
				%>
				<!-- 
        <option value="-1">Pangen-EU</option>
        <option value="-1">IsBlac</option>
        <option value="-1">MamaBlac</option>
        -->
      </select>
      <select class="input-block-level" id="frmTargetGrp" name="frmTargetGrp">
        <option value="-1" selected="selected">Choose group...</option>
        <%
				// List<AppGroup> primaryGrps = userCtrl.getPrimaryGroups();
				for (AppGroup group : primaryGrps) {
					out.println("<option value=\"" + group.getId() + "\"" + 
							" onmouseover=\"Tip('"+ group.getName() + "');\" onmouseout=\"UnTip();\">" + 
							group.getName()
							+ "</option>");
				}
				%>
      </select>
      <input type="text" class="input-block-level" id="frmTargetIntrv" name="frmTargetIntrv">
    </div>
  </div> <!-- EO selects row -->

  <div class="row-fluid" style="padding-top: 3%">
    <div class="span4 offset5">
      <button type="button" class="btn" name="btnOk" id="btnOk" style="margin-right:1%">Submit</button>
      <button type="reset" class="btn" name="btnClr" id="btnClr">Reset</button>
    </div>
  </div>

  <!-- in the case we want to set a message on the page -->
  <div class="span12" style="padding-top: 3%;visibility:hidden;">
    <p class="text-center text-success">
      Interview source questionnaire was successfully cloned into target questionnaire
    </p>
  </div>
  
  </form>
</div> <!-- EO form container-fluid -->


</div> <!-- EO wrap -->


<%-- this is to create the modal "dialog" to run the progress bar --%>
<div id="overlay" style="visibility: hidden;">
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

<div id="footer">
  <div class="container">
    <p class="muted credit">
      &copy; INB, CNIO - Epidemiology Group
    </p>
  </div>
</div>

<!-- yahoo event, dom and connection files -->
<script type="text/javascript" src="../js/yahoo/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../js/yahoo/connection-debug.js"></script>
<script type="text/javascript" src="../js/yahoo/json-debug.js"></script>

<script type="text/javascript" src="../js/lib/jquery-1.9.1.js"></script>
<!--
<script type="text/javascript" src="../js/jquery/jquery-1.2.6.js"></script>
-->
<script type="text/javascript" src="../js/yahoo/ajaxreq.js"></script>
<script type="text/javascript" src="../js/overlay.js"></script>
		
<!-- Configuration variables file -->
<script type="text/javascript" src="../js/admin-cfg.js"></script>
			
<!-- Admin tool scripts -->
<script type="text/javascript" src="../js/admin-ajaxresp.js"></script>
<script type="text/javascript" src="../js/adminctrl.js"></script>

<script type="text/javascript" src="../js/wz_tooltip.js"></script>

<script type="text/javascript" src="../js/clon-ajaxresp.js"></script>
<script type="text/javascript" src="../js/cloningctrl.js"></script>

<script type="text/javascript" src="../js/lib/bootstrap.min.js"></script>

</body>
</html>





