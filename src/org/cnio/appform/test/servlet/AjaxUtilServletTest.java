package org.cnio.appform.test.servlet;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.cnio.appform.servlet.AjaxUtilServlet;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.*;



public class AjaxUtilServletTest {

  String prjCode, grpId, intrvId, secNumber;
  HttpServletRequest request;
  HttpServletResponse response;
  PrintWriter writer;


  @Before
  public void setUp () throws IOException {
    prjCode = "157";
    grpId = "400";
    intrvId = "50";
    secNumber = "4";
    System.out.println("Testing org.cnio.appform.servlet.AjaxUtilServlet");

    // Mocking servlet main behaviour
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);

    // overwritten below
    when(request.getParameter("prjid")).thenReturn(prjCode);
    when(request.getParameter("grpid")).thenReturn(grpId);
    when(request.getParameter("intrvid")).thenReturn(intrvId);
    when(request.getParameter("secid")).thenReturn(secNumber);
    writer = new PrintWriter("ajaxutilservlet.txt");
    when(response.getWriter()).thenReturn(writer);
  }



  @Test
  public void testDumpReq () throws IOException, ServletException {

    when(request.getParameter("what")).thenReturn("dump");
    new AjaxUtilServlet().doGet(request, response);

    verify(request, atLeastOnce()).getParameter("what");
    writer.flush(); // it may not have been flushed yet...

    File file = new File("ajaxutilservlet.txt");
    assertThat(file, notNullValue());
    Long fileSize = new Long(FileUtils.sizeOf(file));
    assertThat(fileSize, greaterThan(new Long(2048)));

    List lines = FileUtils.readLines(file, "UTF-8");
    LineIterator it = FileUtils.lineIterator(file, "UTF-8");

    // header
    assertThat((String)lines.get(0), not(containsString("\""+prjCode)));
    // file contents
    it.nextLine();
    while (it.hasNext()) {
      String line = it.nextLine();
      assertThat(line, Matchers.startsWith("\""+prjCode));
      assertThat(line, containsString("|"));
    }
  }



  @Test
  public void testTransposedDumpReq () throws IOException, ServletException {
    when(request.getParameter("what")).thenReturn("dump");
    when(request.getParameter("aliq")).thenReturn("1");
    new AjaxUtilServlet().doGet(request, response);

    // verify(request, atLeastOnce()).getParameter("what");
    verify(request, atLeastOnce()).getParameter("aliq");
    writer.flush(); // it may not have been flushed yet...

    File file = new File("ajaxutilservlet.txt");
    assertThat(file, notNullValue());
    Long fileSize = new Long(FileUtils.sizeOf(file));
    assertThat(fileSize, greaterThan(new Long(2048)));

    List lines = FileUtils.readLines(file, "UTF-8");
    LineIterator it = FileUtils.lineIterator(file, "UTF-8");

    // header
    assertThat((String)lines.get(0), not(containsString("\""+prjCode)));
    List headerElems = Arrays.asList(((String) lines.get(0)).split("\\|"));
    assertThat(headerElems, Matchers.hasSize(4));

    // file contents
    it.nextLine(); // skip the header
    while (it.hasNext()) {
      String line = it.nextLine();
      assertThat(line, Matchers.startsWith("\""+prjCode));
      assertThat(line, containsString("|"));
      assertThat(Arrays.asList(line.split("\\|")), hasSize(3));
    }
  }


  @Test
  public void dumpServlet () throws IOException, ServletException {
    when(request.getParameter("what")).thenReturn("dump");
    when(request.getParameter("prjid")).thenReturn("188");
    when(request.getParameter("grpid")).thenReturn("4");
    when(request.getParameter("intrvid")).thenReturn("4250");
    when(request.getParameter("secid")).thenReturn("2");

    writer = new PrintWriter("testdump.txt");
    when(response.getWriter()).thenReturn(writer);
    new AjaxUtilServlet().doGet(request, response);
    writer.flush();

    File f = new File ("testdump.txt");
    assertThat(f, notNullValue());

  }
}
