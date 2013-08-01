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
    <h1>EPIQUEST admin tool</h1>
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
  <div class="row-fluid adminrow">
    <div class="offset1 span3">
      <h2>User management</h2>
      <p>Check and update user properties and disable users</p>
      <p class="text-right"><a class="btn btn-info" href="users.jsp">Go &raquo;</a></p>
    </div>
    <div class="span4">
      <h2>Questinonaire cloning</h2>
      <p class="copy">Clone/copy questionnaires to replicate them from one project to another without effort</p>
      <p  class="text-right"><a class="btn btn-info" href="cloning.jsp">Go &raquo;</a></p>
   </div>
    <div class="span3">
      <h2>DB Management</h2>
      <p>Delete subjects, interviews for subjects or change subject ids upon monitors request</p>
      <!-- <p class="text-right"><a class="btn btn-info" href="dbmngment.jsp">Go &raquo;</a></p> -->
      <div class="btn-group" style="margin-left: 70%;">
	      <a class="btn btn-info dropdown-toggle" data-toggle="dropdown" href="#">Go &raquo;
	        <span class="caret"></span>
	      </a>
	      <ul class="dropdown-menu">
	        <li><a href="delpatients.jsp">Delete subjects</a></li>
	        <li><a href="delinterviews.jsp">Delete interviews</a></li>
	        <li><a href="changecodes.jsp">Change subjects code</a></li>
	        <li class="divider"></li>
	        <li style="background-color: #DEDEDE"><a href="dbdumps.jsp">Database dumps</a></li>
	      </ul>
      </div>
    </div>
  </div>

  
</div> <!-- EO container -->


</div> <!-- EO wrap -->

<div id="footer">
  <div class="container">
    <p class="muted credit">
      &copy; INB, CNIO - Epidemiology Group
    </p>
  </div>
</div>

<script type="text/javascript" src="../js/lib/jquery-1.9.1.js"></script>
<script type="text/javascript" src="../js/lib/bootstrap.js"></script>

</body>
</html>





