/**
 * 
 */
package org.cnio.appform.servlet.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cnio.appform.entity.AppUser;
import org.cnio.appform.entity.AppGroup;
import org.cnio.appform.entity.Role;
import org.cnio.appform.util.AppUserCtrl;
import org.cnio.appform.util.HibernateUtil;
import org.cnio.appform.util.LogFile;
import org.cnio.appform.util.Singleton;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import javax.servlet.http.Cookie;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @author bioinfo
 *
 */
public class AuthenticationFilter implements Filter {

	private FilterConfig cfg;
	
	public static final String INITPARAM_DOCROOT = "docroot";
	
	private String docRoot = "";
	
	private final int MAX_COOKIE_AGE = 24*60*60;
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	
	
	private String cookieExpirationFormat () {
		
		String pattern = "EEE, d MMM yyyy HH:mm:ss";
		Date now = new Date ();
		SimpleDateFormat sdf = new SimpleDateFormat (pattern);
		
		String formatDate = sdf.format(now);
		formatDate += " GMT";
		
		return formatDate;
	}

	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp,
											FilterChain chain) throws IOException, ServletException {

		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpReq = (HttpServletRequest) req;
			HttpServletResponse httpResp = (HttpServletResponse) resp;
			String username = httpReq.getRemoteUser();
			Principal myPpal = httpReq.getUserPrincipal();
			HttpSession session = httpReq.getSession(false);

			String ipAddr = req.getRemoteAddr();
// System.out.println("session: "+session.getId());		
			if (session == null) {
//			System.out.println ("Authentication filter: session is null");
				String contextPath = httpReq.getContextPath();
// System.out.println ("redirect: "+contextPath+"/web/jsp/index.jsp");
				httpResp.sendRedirect (contextPath+docRoot);
				return;
			}

// This condition prevents from get into the snippet more than once
			if (myPpal != null && session.getAttribute("logged") == null) {

				String sessionId = session.getId();
				Session hibSes = HibernateUtil.getSessionFactory().openSession();
				
				AppUserCtrl userCtrl = new AppUserCtrl (hibSes);
				AppUser appUsr = userCtrl.getUser(username);
				
				if (appUsr.wasRemoved()) {
					httpResp.sendRedirect("../logout.jsp");
					return;
				}
				
// This piece of code avoids one user logs in concurrently from DIFFERENT ips
/*				if (appUsr.getLoggedIn() == 1) {
					LogFile.info ("user ALREADY logged: '"+appUsr.getUsername()+
							"' from "+ipAddr+" (vs "+appUsr.getLoggedFrom()+") with "+
							session.getId()+" and redirecting...");
					
					userCtrl.logSessionInit(appUsr.getId(), username, "", "", ipAddr,
																	AppUserCtrl.LOGIN_CONCURRENT);
					session.invalidate();
					httpResp.sendRedirect("../nologged.jsp");
					return;
				}
				appUsr.setLoggedIn(1);
*/				
				session.setAttribute ("user", username);
				session.setAttribute ("usrid", appUsr.getId());
				session.setAttribute ("logged", 1);
				if (Singleton.getInstance().isLogged(username) == false)
					Singleton.getInstance().addUser(username);
				
// System.out.println("Set user to '"+username+"' and usrid to '"+appUsr.getId()+"'");
				
// Roles stuff
				List<Role> roles = userCtrl.getRoleFromUser(appUsr);
				int numRoles = roles.size();
				String strRoles = "";
				for (Role r: roles) {
					strRoles += r.getName()+",";
				}
				
System.out.println("user: "+username+"; roles: "+strRoles);
				if (strRoles.length() > 0)
					strRoles = strRoles.substring(0, strRoles.length()-1);
				else {
					httpResp.sendRedirect("../logout.jsp");
					return;
				}
				session.setAttribute ("roles", strRoles);
				
// cookie stuff
// a cookie is left on client side to prevent inconsistencies if the browser
// crash unexpectedly
/*
				Cookie myCookie = new Cookie ("appform", username);
				myCookie.setDomain("epi.bioinfo.cnio.es");
				myCookie.setPath("/appform/jsp/");
				myCookie.setMaxAge(MAX_COOKIE_AGE);
				((HttpServletResponse)resp).addCookie(myCookie);
*/
				
				((HttpServletResponse)resp).setHeader("Set-Cookie", "JSESSIONID=" 
						+ session.getId()
						+ "; Expires=" + cookieExpirationFormat ()
						+ "; Path=/");
				
// This conditional, which seems to be redundant, is here as the previous snippet
// can fail by raising an exception during database update
//				if (session != null && appUsr.getLoggedIn() == 1) {
				if (session != null && Singleton.getInstance().isLogged(username) == true) {
					userCtrl.logSessionInit(appUsr.getId(), username, strRoles, 
																	sessionId, ipAddr, AppUserCtrl.LOGIN_SUCCESS);
			LogFile.info("User '"+username+"' logged in with role(s) '"+strRoles+"'");
// System.out.println("User '"+username+"' logged in with role(s) '"+strRoles+"'");
				}

				hibSes.close();
			} // myPpal != null && session.getAttribute("logged") == null 
		}
		
		chain.doFilter(req, resp);
	}
	
	

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		cfg = arg0;
		
// get the docRoot parameter in order to do the redirection upon de beginning of the tool
		docRoot = cfg.getInitParameter(INITPARAM_DOCROOT);
	}

}
