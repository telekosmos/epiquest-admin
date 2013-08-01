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

<div class="container-fluid">
  <div class="container-fluid">
    <div class="page-header page-title text-center" >
      <h1>EPIQUEST admin tool  - Change patient codes</h1>
    </div>
    <!-- Intro -->
    <div class="row-fluid" style="padding-top: 10%;">
      <div class="span12">
        <h3 style="text-align:center;">Work in progress</h3>
      </div>
    </div>
  </div> <!-- EO container -->
</div>


</div> <!-- EO wrap -->


<%-- this is to create the modal "dialog" to run the progress bar --%>
<div id="overlay" style="visibility: hidden;">
	<div>
		<p style="font-family: Arial, Helvetica, sans-serif; font-size: 14px; text-align:center;">
		Processing...<br/>
		<img src="../img/ajax-loader-trans.gif" alt="Processing..." />
		</p>
	</div>
</div>


<div id="footer">
  <div class="container">
    <p class="muted credit">
      &copy; INB, CNIO - Epidemiology Group
    </p>
  </div>
</div>

<script type="text/javascript" src="../js/lib/jquery-1.9.1.js"></script>
<script type="text/javascript" src="../js/overlay.js"></script>

<script type="text/javascript" src="../js/lib/bootstrap.js"></script>

</body>
</html>





