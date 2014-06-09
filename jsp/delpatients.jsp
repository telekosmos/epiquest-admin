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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">

  <!-- Le styles -->
  <link href="../css/bootstrap.min.css" rel="stylesheet">
  <link href="../css/bootstrap-responsive.min.css" rel="stylesheet">
  
  <link href="../css/overlay.css" rel="stylesheet">
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

      .container-fluid > hr {
        margin: 60px 0;
      }

      .container-fluid .intro-description {
        padding: 1% 3%;
        font-size: 24px;
        line-height: 1.25;
      }

      .container-fluid .adminrow {
        /* padding-left: 5%; */
      }

      .page-title {
        padding-left: 2%;
        text-align: center;
      }

      .page-title h1 {
        font-size: 42px
      }

      
  </style>
  
  <title>EPIQUEST Admin</title>
</head>
<body>
<div id="wrap">

<%-- 
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
      
        <ul class="nav nav-pills">
          <li><a href="users.jsp">Users</a></li>
          <li><a href="cloning.jsp">Cloning</a></li>
          <!-- <li><a href="#contact">Contact</a></li> -->
          <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#">DB Management
              <b class="caret"></b>
            </a>
            <ul class="dropdown-menu">
              <li><a href="delpatients.jsp">Delete subjects</a></li>
              <li><a href="delintrvs.jsp">Delete interviews</a></li>
              <li><a href="changecodes.jsp">Change subjects code</a></li>
              <li class="divider"></li>
              <li style="background-color: #DEDEDE"><a href="dbdumps.jsp">Database dumps</a></li>
            </ul>
          </li>
        </ul>
      </div> !--/.nav-collapse --
    </div>
  </div>
</div>
--%>

<%@include file="inc/navbar.jsp" %>





<div class="container-fluid">
  <div class="page-header page-title">
    <h1>Delete subjects</h1>
  </div>
  <div class="row-fluid">
	  <div class="span6 description-list" style="height:140px;">
	  	<br/>
	    <ul>
	      <li><span class="text-error">Mind what you are doing: deleted patients only can be retrieved from backup and it takes a bit</span></li>
	      <li>Filter the patients you want to delete by choosing project, groups and types</li>
	      <li>Then choose the filtered patients from the list just below the combo boxes</li>
	      <li>When you are done, just click 'Delete' button to proceed</li>
	      <li>When finished, the form is reset and the list of deletions is showed below the list boxes</li>
	    </ul>
	  </div>
	  <div class="span6 well" style="overflow-y: auto;height:140px;" id="responseDiv">
	  	No previous operation message	    
	  </div>
	</div>
</div> <!-- EO container -->

<div class="container-fluid">
	<form name="frmPatsDeletion" id="frmPatsDeletion">
	<input type="hidden" name="what" value="dp" />
	
  <div class="row-fluid">
    <div class="offset2 span2">
      <label>Project</label>
      <select class="input-block-level" id="frmPrj" name="frmPrj">
        <option value="-1" selected="selected">Choose</option>
        <%
					List<Project> projectList = userCtrl.getAllProjects();
					for (Project project : projectList) {
						out.println("<option value='" + project.getProjectCode() + "'>"
								+ project.getName() + "</option>");
					}
				%>
      </select>
    </div>

    <div class="span2">
      <label>Group/Country</label>
      <select class="input-block-level" id="frmCountry" name="frmCountry">
        <option value="-1" selected="selected">Choose</option>
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
    </div>
    
    <div class="span2">
      <label>Group/Hospital</label>
      <select class="input-block-level" id="frmHospital" name="frmHospital">
        <option value="-1" selected="selected">Choose</option>
      </select>
    </div>

    <div class="span2">
      <label>Case/Control</label>
      <select class="input-block-level" id="frmSubjType" name="frmSubjType">
        <option value="-1">All</option>
        <option value="1">Case</option>
        <option value="2">Control</option>
        <option value="3">Familiar</option>
      </select>
    </div>
  </div> <!-- EO row-fluid for combo boxes -->
  
  <hr/>

  <div class="row-fluid" xstyle="margin: 0% 0% 0% -4%;">
    <div class="offset2 span8 well">

      <div class="span5" style="margin-left:4%">
        <label>Retrieved subjects</label>
        <select size="8" class="input-block-level" id="frmListPats" name="frmListPats" multiple="multiple">
        </select>
      </div>
      <div class="span1" style="padding: 5% 0%;">
        <button class="btn btn-primary btn-block" id="add2DeleteBtn" type="button" style="margin-top: 50%;"><i class="icon-arrow-right icon-white"></i></button>
        <!-- <button class="btn btn-mini btn-primary btn-block" id="rmvDeleteBtn" type="button"><i class="icon-arrow-left icon-white"></i></button> -->
      </div>
      <div class="span5">
        <label>Selected subjects (for deletion)</label>
        <select size="8" class="input-block-level" id="frmDelPats" name="frmDelPats" multiple="multiple">
        </select>
      </div>
      
    </div>
  </div> <!-- EO row-fluid for listOfRetreived - buttons - listOfSelected -->

  <div class="row-fluid">
    <div class="offset2 span2">
      <button type="button" class="btn btn-inverse" id="btnReset"><i class="icon-refresh icon-white"></i> Reset</button>
    </div>

    <div class="offset2 span3" style="text-align: right;">
    	<label class="checkbox inline" style="padding-right:2%">
				<input type="checkbox" id="chkSimulation" checked="checked"> Simulation
			</label>
      <button type="button" class="btn btn-inverse" id="btnSend"><i class="icon-exclamation-sign icon-white"></i> Delete</button>
    </div>
    <div class="span2">
      <button type="button" class="btn btn-inverse" id="btnClr"><i class="icon-repeat icon-white"></i> Clear</button>
    </div>

  </div> <!-- EO row-fluid list patients -->
  
	</form>
</div> <!-- container fluid -->




</div> <!-- EO wrap -->

<div id="footer">
  <div class="container">
    <p class="muted credit">
      &copy; INB, CNIO - Epidemiology Group
    </p>
  </div>
</div>


<%-- this is to create the modal "dialog" to run the progress bar --%>
<div id="overlay" style="visibility: hidden;">
	<div>
		<p style="font-family: Arial, Helvetica, sans-serif; font-size: 14px; text-align:center;">
		Processing...<br/>
		<img src="../img/ajax-loader-trans.gif" alt="Processing..." />
		</p>
	</div>
</div>

<%--
	if (hibSes != null)
		hibSes.close();
--%>


<script type="text/javascript" src="../js/lib/jquery-1.9.1.js"></script>

<script type="text/javascript" src="../js/admin-cfg.js"></script>

<script type="text/javascript" src="../js/yahoo/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../js/yahoo/connection-debug.js"></script>
<script type="text/javascript" src="../js/yahoo/json-debug.js"></script>

<script type="text/javascript" src="../js/overlay.js"></script>
<script type="text/javascript" src="../js/yahoo/ajaxreq.js"></script>
<script type="text/javascript" src="../js/mixed2b.js"></script>

<script type="text/javascript" src="../js/delpatients-ajaxresp.js"></script>
<script type="text/javascript" src="../js/delpatients.js"></script>

<script type="text/javascript" src="../js/wz_tooltip.js"></script>

<script type="text/javascript" src="../js/lib/bootstrap.js"></script>
</body>
</html>