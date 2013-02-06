package org.cnio.appform.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.*;

import org.cnio.appform.entity.AppUser;
import org.cnio.appform.util.AppUserCtrl;
import org.cnio.appform.util.HibernateUtil;

import org.hibernate.Session;


/**
 * Servlet implementation class for Servlet: MngUsersServlet
 *
 */
 public class MngUsersServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
   static final String ACT_RESETPASSWD = "resetpasswd";
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public MngUsersServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}  	
	
	
	
	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject jsonOut = new JSONObject ();
		String action = request.getParameter("act");
		
		if (action.equalsIgnoreCase(ACT_RESETPASSWD)) {
			String username = request.getParameter("usr");
			if (username == null || username.length() == 0) {
				jsonOut.put("res", -1);
				jsonOut.put("msg", "An user has to be selected");
			}
			else {
				Session hibSes = HibernateUtil.getSessionFactory().openSession();
				AppUserCtrl usrCtrl = new AppUserCtrl (hibSes);
				AppUser usr = usrCtrl.getUser(username);
				
				if (usr == null) {
					jsonOut.put("res", -2);
					jsonOut.put("msg", "The user '"+username+"' could not be found");
				}
				else {
					boolean res = usrCtrl.setNewPasswd (usr, request.getSession().getId(), 
																							request.getRemoteAddr());
					
					if (!res) {
						jsonOut.put("res", -3);
						jsonOut.put ("msg", "Password for user '"+usr.getUsername()+"' could not be reset");
					}
					else {
						jsonOut.put("res", 0);
						jsonOut.put ("msg", "Password for user '"+usr.getUsername()+"' was reset successfuly");
					}
				} // else usr is not null
					
			} // username is not null
				
		}
		
	
		response.setHeader("Content-type", "application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter pwr = response.getWriter();
		pwr.print(jsonOut);
	
	}   	  	    
}