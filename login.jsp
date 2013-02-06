<html>
<head>
<title>Application Form Adminstration Tool</title>

<!--   <link rel="stylesheet" type="text/css" 
						href="../../css/portal_style.css" id="portalCss" /> -
		<link rel="stylesheet" type="text/css" href="../css/portal_style.css" id="portalCss" />
 <link rel="stylesheet" type="text/css" href="css/portal_style.css" id="portalCss" />
   <link rel="shortcut icon" href="../img/favicon.ico"/> -->
<link rel="stylesheet" type="text/css" href="../css/portal_style.css"
	id="portalCss" />

</head>

<body id="body">

<div id="portal-container">
<div id="sizer">
<div id="expander">
<table border="0" cellpadding="" cellspacing="5"
	id="header-container-adm">
	<tr>
		<td align="left" valign="top" id="header" width="220px" style="margin-left:20px"><a
			href="http://www.inab.org" target="_blank"
			style="text-decoration: none"> <img src="../img/inblogo.jpg"
			height="110" border="0" /></a></td>
			
		<td align="left" valign="bottom"><a href="http://www.inab.org"
			target="_blank" style="text-decoration: none"> <span
			class="inblogo">Instituto Nacional de Bioinform&aacute;tica</span> </a></td>
	</tr>
</table>



<!-- HERE STARTS THE CENTRAL PART, BOTH THE MENU AND CONTENT AREAS -->
<div id="content-container"><!-- **************** START CONTENT AREA (REGION b)**************** -->
<div id='regionAdmB'>
<table width="100%" height="30%" cellpadding="1" cellspacing="1"
	border="0">
	<tr>
		<td align="center" valign="middle">
		<h1>Application Form Construction and Development Tool<br><br>Administration</h1>
		</td>
	</tr>
	<!-- 
           	<tr><td align="center" valign="middle" class="textRegionAdmB">
           	Build and perform interview by:<br>
           	<ul>
           	<li>Login as registered user (previous request)</li>
           	<li>Choose a project to create the new interview in</li>
           	<li>Build the interview: create sections, questions, texts</li>
           	<li>Perform the interview to the patients you choose</li>
           	</ul>
           	</td>
           	</tr>-->
</table>

</div>
<!-- regionB --> <!-- ****************** END CONTENT AREA (REGION B) ***************** -->

<!-- ****************** START MENU (LEFT) AREA (REGION A) ***************** -->
<div id='regionAdmA' align="center">

<form method="POST"
	action='<%= response.encodeURL("j_security_check") %>'>
<table border="0" cellspacing="5">
	<tr>
		<td align="left">Username:</td>
	</tr>
	<tr>
		<td align="left"><input type="text" name="j_username" size="15"></td>
	</tr>
	<tr>
		<td align="left">Password:</td>
	</tr>
	<tr>
		<td align="left"><input type="password" name="j_password"
			size="15"></td>
	</tr>
	<tr align="left">
		<td><input type="submit" value="Log In">&nbsp; <input
			type="reset" value="Clear"></td>
	</tr>
</table>
</form>

</div>
<!-- region A --> <!-- ****************** END LEFT MENU AREA (REGION A) ***************** -->


</div>
<!-- content-container --></div>
<!-- expander --></div>
<!-- sizer --></div>
<!-- portal-container -->


<!-- FOOTER AND END OF PAGE -->
<div id="footer-container-adm" class="portal-copyright-adm">Developed
at <a class="portal-copyright-adm" href="mailto:gcomesana@cnio.es">CNIO/INB</a><br />
</div>

</body>
</html>
