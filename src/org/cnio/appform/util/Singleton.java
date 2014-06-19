package org.cnio.appform.util;

import java.util.List;
import java.util.ArrayList;

public class Singleton {
	private static Singleton instance = new Singleton ();

/**
 * The list of users currently connected
 */
	// private List<String> users;
  private List<Integer> users;

	
/**
 * This private constructor is defined so the compiler
 * won't generate a default public constructor.
 */
	private Singleton () {  
		users = new ArrayList<Integer> ();
	} 
	
	
/**
 * Return a reference to the only instance of this class.
 */
	public static Singleton getInstance() {
	  return instance;
	} // getInstance()
	
	
/**
 * Add an user to the list of logged users
 * @param userid
 */
	public void addUser (Integer userid) {
		users.add(userid);
System.out.println("Singleton.addUser: "+userid);
	}
	
	
/**
 * Checks if username is logged
 * @param userid, the user
 * @return true if the user username is logged; otherwise it returns false
 */
	public boolean isLogged (Integer userid) {
System.out.println("Singleton.islogged: "+userid+":"+users.contains(userid));
		return users.contains(userid);
	}
	
	
/**
 * Remove an element of the list based on their content
 * @param userid
 */
	public boolean rmvUser (Integer userid) {
    boolean removed = true;
    if (users.contains(userid))
		  users.remove(userid);
    else
      removed = false;

System.out.println("Singleton.rmvUser?: "+removed+" for "+userid);

    return removed;
	}
	
	
	
/**
 * Return the number of users
 * @return the number of users
 */
	public int numUsers () {
		return users.size();
	}
	
	
	
	public List<Integer> getLoggedUsers () {
		return users;
	}
	
	
	public void printUsrs() {
System.out.println("Users currently logged:");
		for (Integer user: users) {
			System.out.println(user);
		}
	}
	
	
}
