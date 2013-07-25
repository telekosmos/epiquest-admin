<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container-fluid">
      <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        <span class="icon-bar">x</span>
        <span class="icon-bar">y</span>
        <span class="icon-bar">z</span>
      </button>
      <a class="brand" href="index.jsp">EPIQUEST Admin</a>
      <div class="nav-collapse collapse">
        <% if (user != null) { %>
        <p class="navbar-text pull-right">
          Logged in as <a href="#" class="navbar-link"><%=session.getAttribute("user")%> 
          (<%=session.getAttribute("roles")%>)</a>
        </p>
      	<% } %>
      	
        <ul class="nav nav-pills">
          <li><a href="users.jsp">Users</a></li>
          <li><a href="cloning.jsp">Cloning</a></li>
          <!-- <li><a href="#contact">Contact</a></li> -->
          <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#">DB Management
              <b class="caret"></b>
            </a>
            <ul class="dropdown-menu">
              <li><a href="delpatients.jsp">Delete subjects</a></li>
              <li><a href="delintrvs.jsp">Delete interviews</a></li>
              <li><a href="changecodes.jsp">Change subjects code</a></li>
              <li class="divider"></li>
              <li style="background-color: #DEDEDE"><a href="dbdumps.jsp">Database dumps</a></li>
            </ul>
          </li>
          <% if (user != null) { %>
          <li><a href="<%= response.encodeURL("../logout.jsp?adm=1") %>">Logout</a></li>
          <% } %>
        </ul>
      </div><!--/.nav-collapse -->
    </div>
  </div>
</div>