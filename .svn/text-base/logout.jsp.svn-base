<%@page import="org.cnio.appform.util.AppUserCtrl"%>
<%@page import="org.cnio.appform.util.LogFile, 
							org.cnio.appform.entity.AppUser, org.cnio.appform.entity.AppGroup,
							org.cnio.appform.util.HibernateUtil"%>
							
<%@page import="org.hibernate.Session, org.hibernate.Transaction,
								org.hibernate.HibernateException" %>

<%
	String adm = request.getParameter("adm");
	String ipAddr = request.getRemoteAddr();
	Session hibSes;
	Transaction tx = null;
	
	try {
		hibSes = HibernateUtil.getSessionFactory().openSession();
		
		String username = (String)session.getAttribute("user");
		String roles = (String)session.getAttribute("roles");
		AppUserCtrl usrCtrl = new AppUserCtrl (hibSes);
		AppUser usr = usrCtrl.getUser(username);
		if (usr != null) {
			tx = hibSes.beginTransaction();
//			usr.setLoggedIn(new Integer (0));
			usr.setLoggedFrom(null);
			usr.setLoginAttempts(0);
			tx.commit();
/*			
			AppGroup active1ary = usrCtrl.getPrimaryActiveGroup(usr),
							active2ary = usrCtrl.getSecondaryActiveGroup(usr);
			
			if (active1ary != null)
				usrCtrl.setActiveGroup(usr, active1ary, 0);
			
			if (active2ary != null)
				usrCtrl.setActiveGroup(usr, active2ary, 0);
*/
		}	
		hibSes.close();
		
		LogFile.info("User '"+username+"' acting with roles '"+roles+
					"' has logged out from "+ipAddr+" with "+session.getId());
//		session.removeAttribute("logged");
		session.invalidate();
	}
	catch (IllegalStateException ex) {
		
	}
	catch (HibernateException ex) {
		if (tx != null)
			tx.rollback();
		
		LogFile.error("Unable to log to database user login when loging out");
		LogFile.error(ex.getLocalizedMessage());
		StackTraceElement[] stack = ex.getStackTrace();
		LogFile.logStackTrace(stack);
		
		session.removeAttribute("logged");
		session.invalidate();
	}
	
	if (adm == null)
  	response.sendRedirect ("jsp/index.jsp");
	else
		response.sendRedirect (request.getContextPath()+"/jsp/index.jsp");
%>
