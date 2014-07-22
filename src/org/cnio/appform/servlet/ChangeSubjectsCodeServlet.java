package org.cnio.appform.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

import org.json.simple.*;

import org.inb.dbtask.FixingTasksHub;

public class ChangeSubjectsCodeServlet extends HttpServlet {

  private String dbUrl, dbUser, dbPass, dbHost;
  private boolean simulation;

  public void init(ServletConfig config) throws ServletException {
    System.out.println("ChangeSubjectsCodeServlet initializing...");

    super.init(config);
    ServletContext context = getServletContext();

    String dbName = context.getInitParameter("dbName");
    this.dbHost = context.getInitParameter("dbServerName");
    String dbPort = context.getInitParameter("dbPort");
    dbPort = dbPort == null? "5432": dbPort;

    this.dbUrl = "jdbc:postgresql://"+dbHost+":"+dbPort+"/"+dbName;
    this.dbPass = context.getInitParameter("dbPassword");
    this.dbUser = context.getInitParameter("dbUserName");

    this.simulation = true;
  }


  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Check that we have a file upload request
    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
    // FixingTasksHub fth = new FixingTasksHub(this.dbUrl, this.dbUser, this.dbPass);
    JSONObject jsonObj = new JSONObject();
    InputStream filecontent = null;
    String filename = null;

    if (request.getParameter("single") == null) {
      // File upload with codes
      try {
        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
        for (FileItem item : items) {
          if (item.isFormField()) {
            // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
            // In fact, simulation checkbox value should be here
            String fieldname = item.getFieldName();
            String fieldvalue = item.getString();
            System.out.println("Regular field: "+fieldname+" -> "+fieldvalue);
            jsonObj.put("result", 1);
            jsonObj.put(fieldname, fieldvalue);
            if (fieldname.equalsIgnoreCase("sim"))
              this.simulation = Boolean.parseBoolean(fieldvalue);
          }
          else {
            // Process form file field (input type="file").
            String fieldname = item.getFieldName();
            filename = FilenameUtils.getName(item.getName());
            filecontent = item.getInputStream();
          }
        } // EO for

        jsonObj = changeSubjectsFromFile(filename, filecontent);
      }
      catch (FileUploadException e) {
        // throw new ServletException("Cannot parse multipart request.", e);
        jsonObj.put("result", 0);
        jsonObj.put("err", e.getLocalizedMessage());
      }
    }
    else { // got single parameter => single subject code change
      String oldCode = request.getParameter("old"),
        newCode = request.getParameter("new");
      this.simulation = Boolean.parseBoolean(request.getParameter("sim"));
      jsonObj = changeSingleSubjectCode(oldCode, newCode);
    }

    jsonObj.put("sim", this.simulation);

    response.setHeader("Content-type", "application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    System.out.println("ChangeCodesServlet: "+
      jsonObj.toJSONString()+" vs toString: "+jsonObj.toString());
    out.print(jsonObj.toString());

  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

  }


  /**
   * Perform the process to change the subjects code from the filecontent.
   * @param filename the name of the file uploaded with the subject codes
   * @param filecontent the file content as a InputStream
   * @return a JSONObject which will be the response to the client
   */
  private JSONObject changeSubjectsFromFile (String filename, InputStream filecontent) {
    System.out.println("Processing uploaded file '"+filename+"'");
    FixingTasksHub fth = new FixingTasksHub(this.dbUrl, this.dbUser, this.dbPass);
    Map codes = fth.parseSubjectCodesFile(filecontent);
    JSONObject jsonObj = new JSONObject();

    if (codes == null) {
      jsonObj.put("result", 0);
      jsonObj.put("msg", filename+": Malformed subject codes file");
    }
    else {
      Set oldOnes = codes.keySet();
      Iterator oldOnesIt = oldOnes.iterator();
      JSONArray jsonCodes = new JSONArray();
      while (oldOnesIt.hasNext()) {
        String oldOne = (String)oldOnesIt.next();
        JSONObject aCode = new JSONObject();
        aCode.put(oldOne, codes.get(oldOne));
        jsonCodes.add(aCode);
      }
      jsonObj.put("subjects", jsonCodes);

      Map taskResult = (Map)fth.changeSubjecsCode(this.dbHost, this.simulation, codes);
      jsonObj.put("rows_affected", taskResult.get("rows_affected"));

      // jsonObj.put("subjs_with_samples", taskResult.get("pats_with_samples"));
      jsonObj.put("subjs_with_samples",
        buildSubjectsWithSamples((Map)taskResult.get("pats_with_samples")));
      jsonObj.put("subjects_unchanged",
        buildSubjectsUnchanged((Map)taskResult.get("patients_unchanged")));
      System.out.println("Retrieved "+codes.size()+" codes to change");
      jsonObj.put("result", 1);
      jsonObj.put("filename", filename);
    }
    return jsonObj;
  }


  /**
   * Performs the change of a single subject code
   * @param oldCode
   * @param newCode
   * @return a JSONObject which will be returned to the client
   */
  private JSONObject changeSingleSubjectCode (String oldCode, String newCode) {
    FixingTasksHub fth = new FixingTasksHub(this.dbUrl, this.dbUser, this.dbPass);
    JSONObject jsonObj = new JSONObject();

    if (oldCode != null && newCode != null &&
      oldCode.length() >= 9 && newCode.length() >= 9) {
      // build up the json object response
      JSONArray jsonCodes = new JSONArray();
      JSONObject aCode = new JSONObject();
      aCode.put(oldCode, newCode);
      jsonCodes.add(aCode);
      jsonObj.put("subjects", jsonCodes);

      Map codes = new HashMap();
      codes.put(oldCode, newCode);
      Map taskResult = (Map)fth.changeSubjecsCode(this.dbHost, this.simulation, codes);
      jsonObj.put("rows_affected", taskResult.get("rows_affected"));

      // jsonObj.put("subjs_with_samples", taskResult.get("pats_with_samples"));
      jsonObj.put("subjs_with_samples",
        buildSubjectsWithSamples((Map)taskResult.get("pats_with_samples")));
      jsonObj.put("subjects_unchanged",
        buildSubjectsUnchanged((Map)taskResult.get("patients_unchanged")));
      System.out.println("(Single) Retrieved "+codes.size()+" codes to change");
      jsonObj.put("result", 1);
    }
    else {
      jsonObj.put("result", 0);
      jsonObj.put("msg", "Malformed subject codes file");
    }

    return jsonObj;
  }


  /**
   * Build a json array filled with objects with the form {subject: [samples]}
   * @param subjSamples A Map with keys the subject code and the value will be an array
   *                    with its samples
   * @return a json array
   */
  private JSONArray buildSubjectsWithSamples (Map subjSamples) {
    JSONArray subjSamplesArray = new JSONArray();
    Set subjKeys = subjSamples.keySet();
    Iterator subjIt = subjKeys.iterator();
    while (subjIt.hasNext()) {
      String subjCode = (String)subjIt.next();
      // List samplesList = (List)subjSamples.get(subjCode);
      List samplesList = new ArrayList();
      samplesList.addAll((Collection)subjSamples.get(subjCode));

      JSONArray jsonArray = new JSONArray();
      jsonArray.addAll(samplesList);
      JSONObject subjectJson = new JSONObject();
      subjectJson.put(subjCode, jsonArray);

      subjSamplesArray.add(subjectJson);
    }

    return subjSamplesArray;
  }


  /**
   * Builds up a json array filled with objects like oldSubjectCode:newSubjectCode
   * @param subjsUnchanged
   * @return
   */
  private JSONArray buildSubjectsUnchanged (Map subjsUnchanged) {
    Set oldOnes = subjsUnchanged.keySet();
    Iterator oldOnesIt = oldOnes.iterator();
    JSONArray jsonCodes = new JSONArray();
    while (oldOnesIt.hasNext()) {
      String oldOne = (String)oldOnesIt.next();
      JSONObject aCode = new JSONObject();
      aCode.put(oldOne, subjsUnchanged.get(oldOne));
      jsonCodes.add(aCode);
    }

    return jsonCodes;
  }
}
