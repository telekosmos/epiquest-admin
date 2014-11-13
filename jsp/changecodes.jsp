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

  <!-- Le styles -
  <link href="../css/bootstrap.min.css" rel="stylesheet">
  <link href="../css/bootstrap-responsive.min.css" rel="stylesheet"> -->
  <link href="../css/jasny-bootstrap.css" rel="stylesheet">
  <link href="../css/jasny-bootstrap-responsive.css" rel="stylesheet">
  
  <link href="../css/overlay.css" rel="stylesheet">
<link href="../css/admintool.css" rel="stylesheet">
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

      /* Custom page CSS */
      #wrap > .container {
        padding-top: 100px;
      }
      #wrap > .container-fluid  {
        padding-top: 2em;
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

      .page-header {
        margin: 20px 0 10px;
      }
      .page-title {
        padding-left: 2%;
        text-align: center;
      }
      .page-title h1 {
        font-size: 42px
      }

      .border-separator {
        border-bottom: 1px solid #eeeeee;
      }
  </style>
  
  <title>EPIQUEST Admin</title>
</head>

<body>
<div id="wrap">

<%@include file="inc/navbar.jsp" %>

<div class="container-fluid">
  <div class="page-header page-title">
    <h1>Change subjects code</h1>
  </div>
  <div class="row-fluid border-separator">
    <div class="span6 description-list">
      <span class="text-error">Mind what you are doing: changing code patients means the old ones only can be retrieved from backup and it takes a bit</span><br/>

      <h4>Two options to change subjects code:</h4>
      <ul><strong>Upload a file</strong>
        <li>Create a <strong>text file</strong> with one line for each subject you want to change</li>
        <li>Each line must be as <code>old_subject_code:new_subject_code</code></li>
        <li>Then <strong>Select file</strong> and click the <strong>Process</strong> button</li>
      </ul>
      <ul><strong>Use the form</strong>
        <li>Filter the patients you want to update by choosing project, groups and types</li>
        <li>Then choose the filtered patients from the list just below the combo boxes</li>
        <li>When a subject is selected, you can change the code in the textbox</li>
        <li>Click the <strong>Change</strong> button to change that code only</li>
      </ul>
    </div>
    <div class="span6 well" style="overflow-y: auto;height:240px;" id="responseDiv">
      No previous operation message<hr style="border-color: black">
    </div>
  </div>
</div> <!-- EO container -->


<!-- upload file -->
<div class="container-fluid">
  <div class="row-fluid border-separator">
    <div class="offset2 span2" style="text-align: right;">
      <h4 style="line-height: 10px;">Upload a file</h4>
    </div>

    <div class="span4">
      <form id="uploadform" enctype="multipart/form-data">
      <div class="fileupload fileupload-new" data-provides="fileupload">
        <div class="input-append">

          <div class="uneditable-input span3">
            <i class="icon-file fileupload-exists"></i>
            <span class="fileupload-preview"></span>
          </div>
          <span class="btn btn-file">
            <span class="fileupload-new">Select file</span>
            <span class="fileupload-exists">Change</span><input type="file" />
          </span>
          <a href="#" class="btn fileupload-exists" data-dismiss="fileupload">Remove</a>
        </div>
      </div>
      </form>
    </div>

    <div class="span1">
      <button class="btn btn-inverse" type="button" id="btnUpl" name="btnUpl">Process</button>
    </div>
  </div>
</div>


<!-- form container fluid -->
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

    <div class="row-fluid border-separator">
      <div class="offset2 span8 well">

        <div class="span5" style="margin-left:4%">
          <label>Retrieved subjects</label>
          <select size="8" class="input-block-level" id="frmListPats" name="frmListPats">
          </select>
        </div>
        <!--
        <div class="span1" style="padding: 5% 0%;"></div>
        -->
        <div class="span6">
          <label>New code for selected subject</label>
          <input type="text" placeholder="Selected subject"
                 name="selSubject" id="selSubject" style="margin-bottom: 0"/>
          <button class="btn btn-inverse" type="button" id="btnChange">Change</button>
        </div>
      </div>
    </div> <!-- EO row-fluid for listOfRetreived - buttons - listOfSelected -->

    <div class="row-fluid" style="margin-top: 1em;">
      <div class="offset2 span2">
        <button type="button" class="btn btn-inverse" id="btnReset"><i class="icon-refresh icon-white"></i> Reset</button>
      </div>

      <div class="offset3 span3" style="text-align: right;">
        <label class="checkbox inline" style="padding-right:2%">
          <input type="checkbox" id="chkSimulation" checked="checked"> Simulation
        </label>
      </div>
    </div> <!-- EO row-fluid list patients -->

  </form>
</div> <!-- container fluid -->

<div id="footer">
  <div class="container">
    <p class="muted credit">
      &copy; INB, CNIO - Epidemiology Group
    </p>
  </div>
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





<script type="text/javascript" src="../js/admin-cfg.js"></script>

<script type="text/javascript" src="../js/yahoo/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../js/yahoo/connection-debug.js"></script>
<script type="text/javascript" src="../js/yahoo/json-debug.js"></script>

<script type="text/javascript" src="../js/overlay.js"></script>
<script type="text/javascript" src="../js/yahoo/ajaxreq.js"></script>
<script type="text/javascript" src="../js/mixed2b.js"></script>

<script type="text/javascript" src="../js/lib/twitter.widgets.js"></script>
<script type="text/javascript" src="../js/lib/jquery-jasny-1.9.1.js"></script>
<script type="text/javascript" src="../js/lib/bootstrap.js"></script>
<script type="text/javascript" src="../js/lib/bootstrap-fileupload.js"></script>

<script type="text/javascript" src="../js/overlay.js"></script>

<script type="text/javascript" src="../js/uploadfile-ajaxresp.js"></script>
<script type="text/javascript" src="../js/uploadfile.js"></script>
<script type="text/javascript" src="../js/changecodes-ajaxresp.js"></script>
<script type="text/javascript" src="../js/changecodes.js"></script>

</body>
</html>





