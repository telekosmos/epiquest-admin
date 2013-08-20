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

    .box-form {
      background-color: #EEEEEE;
      padding: 1%;
      border-radius:5px;
    }  
    
    
    .typeahead {
			max-height: 200px;
			overflow-y: auto;
			overflow-x: hidden;
			margin-top: 0px;
		}
    
    
  </style>
  
  <title>EPIQUEST Admin</title>
</head>
<body>
<div id="wrap">


<%@include file="inc/navbar.jsp" %>

<div class="container-fluid">
  <div class="page-header page-title" >
    <h1>Delete interviews of patients</h1>
  </div>
  <!-- Intro -->
  <div class="row-fluid" style="padding-top: 2%;xpadding-bottom: 5%;">
    <div class="span6 description-list">
      <ul>
        <li><span class="text-info">Autocomplete</span>: just start typing the patient code</li>
        <li><span class="text-info">Autocomplete</span>: codes matching while typing will be showed. Choosing one of them set the current subject</li>
        <li>Click the search button (<i class="icon-search"></i>) to retrieve the interviews for the subject</li>
        <li>Select the interviews to be deleted and click on the Delete button</li>
      </ul>
    </div>
    <div class="span6 well" style="overflow-y: auto;height:140px;" id="responseDiv">
	  	No previous operation message	    
	  </div>
  </div> <!-- EO row-fluid -->
</div> <!-- EO container -->


<div class="container-fluid">
  <form id="frmCloning" name="frmCloning">
  <div class="row-fluid">
    <div class="span6 offset3">
      <div class="input-append" style="width:100%;">
        <input type="text" class="typeahead" id="type_code" style="width:50%;">
        <button type="button" class="btn btn-medium" style="padding-bottom:1.4%;" id="btnSearch">
        	<i class="icon-search"></i>
        </button>  
      </div>
    </div>
  </div>

  <div class="row-fluid">
	  <div class="span6 offset3 box-form">
	    <legend>Interviews</legend>
	    <select class="input-block-level" id="frmListIntrvs" name="frmListIntrvs" multiple="multiple" size="4">
	      <!-- 
	      <option value="-1">Identificación de paciente</option>
	      <option value="-1">QES_Español</option>
	      <option value="-1">Recogida_de_muestras</option>
	      <option value="-1">Family questionnaire</option>
	      -->
	    </select>
	    <div style="text-align:right;">
	    	
	    	<label class="checkbox inline" style="padding-right:2%">
					<input type="checkbox" id="chkSimulation" checked="checked"> Simulation
				</label>
				
	    	<a href="#" data-toggle="tooltip" data-placement="top" title="Delete selected interviews">
	    		<button type="button" class="btn btn-inverse" id="btnSubmit">Delete</button>
	    	</a>
	    	<a href="#" data-toggle="tooltip" data-placement="top" title="Clear search box and list">
	    		<button type="button" class="btn btn-inverse" id="btnClear">Clear</button>
	    	</a>
	    </div>
	  </div>
  </div>
  </form>
</div>


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

<script type="text/javascript" src="../js/delinterviews-ajaxresp.js"></script>
<script type="text/javascript" src="../js/delinterviews.js"></script>

<script type="text/javascript" src="../js/wz_tooltip.js"></script>

<script type="text/javascript" src="../js/lib/bootstrap.js"></script>
</body>
</html>