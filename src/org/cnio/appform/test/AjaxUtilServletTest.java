package org.cnio.appform.test;

import org.cnio.appform.servlet.AjaxUtilServlet;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AjaxUtilServletTest {

  String prjCode, grpId, intrvId, secNumber;
  @Before
  public void setUp () {
    prjCode = "157";
    grpId = "400";
    intrvId = "50";
    secNumber = "4";
    System.out.println("Testing org.cnio.appform.servlet.AjaxUtilServlet");
  }

  @Test
  public void testTransposedDumpReq () throws IOException, ServletException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("prjid")).thenReturn(prjCode);
    when(request.getParameter("grpid")).thenReturn(grpId);
    when(request.getParameter("intrvid")).thenReturn(intrvId);
    when(request.getParameter("secid")).thenReturn(secNumber);
    PrintWriter writer = new PrintWriter("ajaxutilservlet.txt");
    when(response.getWriter()).thenReturn(writer);

    when(request.getParameter("what")).thenReturn("dump");


    new AjaxUtilServlet().doGet(request, response);
  }

}
