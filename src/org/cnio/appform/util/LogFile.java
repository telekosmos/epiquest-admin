
package org.cnio.appform.util;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;

// import org.cnio.appform.entity.AppDBLogger;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class LogFile {
//    private static Logger logger = Logger.getLogger(LogClass.class);
	private static Logger logger = Logger.getLogger(LogFile.class);
    private static String appenderName;
    private static boolean initialized = false;
    
//    private static org.apache.log4j.Logger log = Logger.getLogger(LogClass.class);
/*
    static {
      try {
          logger = Logger.getLogger("Logger");
          logger.setLevel(Level.DEBUG);
          logger.addAppender(new FileAppender (new SimpleLayout(),
                             "appender.log", true));
          appenderName = "appender.log";
          
      }
      catch (IOException ex) {
          ex.printStackTrace();
      }
    }
*/
    
    public static boolean isInitialized () {
    	return initialized;
    }
  
 
/*
    public static void init (Class theClass) {
    	if (!initialized) {
    		try {
          logger = Logger.getLogger(theClass);
          logger.setLevel(Level.DEBUG);
          logger.addAppender(new FileAppender (new SimpleLayout(),
                             "appform.log", true));
          appenderName = "appform.log";
          
	      }
	      catch (IOException ex) {
	          ex.printStackTrace();
	      }
    	}
    	initialized = true;
    }
*/
    
    
    public static void logStackTrace (StackTraceElement[] exc) {
    	String logMsg = "Uncaught exception:\n";
    	for (int i=0; i<exc.length; i++) {
    		StackTraceElement elem = exc[i];
    		
    		logMsg += "\t at "+elem.getClassName()+"."+elem.getMethodName()+":"+
    						elem.getLineNumber()+"\n";
    	}
    	logger.error("["+LogFile.formattedDate()+"]: "+logMsg);
    }
    

    public static String formattedDate () {
      return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date());
    }
    
    public static Logger getLogger () {
    	return logger;
    }
    
    
    public static void debug (String data) {
        logger.debug("["+LogFile.formattedDate()+"]: "+data);
    }

    
    public static void info (String data) {
        logger.info("["+LogFile.formattedDate()+"]: "+data);
    }


    public static void error (String data) {
        logger.error("["+LogFile.formattedDate()+"]: "+data.toUpperCase());
    }


    public static void setLogfile (String newName) {
//    	logger.removeAllAppenders();
    	try {
    		if (logger != null) {
    				logger.removeAppender(appenderName);
            logger.addAppender(new FileAppender (new SimpleLayout(),
                                                 newName, true));
        }
    		else {
    			logger = Logger.getLogger(LogFile.class);
          logger.setLevel(Level.DEBUG);
          logger.addAppender(new FileAppender (new SimpleLayout(),
                             									newName, true));
    		}
    		appenderName = newName;
    	}
      catch (IOException ex) {
         ex.printStackTrace ();
      }
    }
    
    
/*    
    public static void log2db (Integer patid, Integer usrid, 
    										Integer intrvid, String logmsg, String sessionid) {
    	
    	AppLog dbLog = new AppLog ();
    	dbLog.setIntrvId(intrvid);
    	dbLog.setLogMsg(logmsg);
    	dbLog.setSessionId(sessionid);
    	dbLog.setPatId(patid);
    	dbLog.setUserId(usrid);
    }
*/
    
    public static void stderr (String msg) {
    	System.err.println("["+LogFile.formattedDate()+"]: "+msg);
    }
    
    public static void stdout (String msg) {
    	System.out.println("["+LogFile.formattedDate()+"]: "+msg);
//    	logger.info("stdout: "+msg);
    }
    
    public static void display (String msg) {
    	System.out.println("["+LogFile.formattedDate()+"]: "+msg.toUpperCase());
//    	logger.info(msg.toUpperCase());
    }
}

