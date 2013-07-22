<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">

  <!-- Le styles -->
  <link href="../css/bootstrap.min.css" rel="stylesheet">
  <link href="../css/bootstrap-responsive.min.css" rel="stylesheet">
  <style type="text/css">
	.box-login {
	  background-color: #EEEEEE;
	  padding: 5% 15% 5% 15%;
	  border-radius:5px;
	}

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
  .container .credit {
    margin: 20px 0;
  }

  code {
    font-size: 80%;
  }
  </style>
  
  <title>EPIQUEST Admin</title>
</head>
<body>
<div id="wrap">

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
          <li class="active"><a href="users.html">Users</a></li>
          <li><a href="cloning.html">Cloning</a></li>
          <!-- <li><a href="#contact">Contact</a></li> -->
          <li><a href="dbmanage.html">DB Management</a></li>
        </ul>
      </div><!--/.nav-collapse -->
    </div>
  </div>
</div>

<div class="container">
  <div class="row-fluid">
    <div class="span4 offset4">
      <form class="box-login" method="POST"
						action='<%= response.encodeURL("j_security_check") %>'>
        <fieldset>
          <legend>Login</legend>
          <label>User</label>
          <input type="text" name="j_username" class="input-block-level"/>
          <label>Password</label>
          <input type="password" name="j_password" class="input-block-level"/>
          <p style="text-align: center; padding-top: 2%;">
            <button type="submit" class="btn">Submit</button>
            <button type="reset" class="btn">Clear</button>
          </p>
        </fieldset>
      </form>
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

<script type="text/javascript" src="../assets/js/bootstrap.js"></script>

</body>
</html>
