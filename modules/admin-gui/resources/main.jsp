<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page pageEncoding="ISO-8859-1"%>
<% response.setContentType("text/html; charset="+org.ejbca.config.WebConfiguration.getWebContentEncoding()); %>
<%@page errorPage="errorpage.jsp" import="org.ejbca.config.GlobalConfiguration,org.ejbca.ui.web.RequestHelper,java.net.InetAddress,java.net.UnknownHostException" %>
<html>
<jsp:useBean id="ejbcawebbean" scope="session" class="org.ejbca.ui.web.admin.configuration.EjbcaWebBean" />
<jsp:useBean id="cabean" scope="session" class="org.ejbca.ui.web.admin.cainterface.CAInterfaceBean" />
<jsp:setProperty name="ejbcawebbean" property="*" /> 
<%   // Initialize environment
  GlobalConfiguration globalconfiguration = ejbcawebbean.initialize(request,"/administrator"); 
%>
<head>
  <title><c:out value="<%= globalconfiguration.getEjbcaTitle() %>" /></title>
  <base href="<%= ejbcawebbean.getBaseUrl() %>" />
  <script src="adminweb/themes/libs/js/jquery.min.js"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/bootstrap.min.css">
  <script src="adminweb/themes/libs/js/bootstrap.min.js"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/font-awesome.min.css">
  <script src="adminweb/themes/libs/js/main.js" type="text/javascript"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/styles.css">  <meta http-equiv="Content-Type" content="text/html; charset=<%= org.ejbca.config.WebConfiguration.getWebContentEncoding() %>" />

</head>

<body id="header">
 <jsp:include page="adminmenu.jsp" />

<section id="content-one" class="container-fuild">
<div class="col-xs-12">
<h2>Dashboard</h2> 
</div>
<div class="col-xs-8">
	


<div id="crls-item">
	<div><%= ejbcawebbean.getText("NODEHOSTNAME") + " : "%><code><c:out value="<%= ejbcawebbean.getHostName()%>"/></code></div> 
	<div><%= ejbcawebbean.getText("SERVERTIME") + " : "%><code><c:out value="<%= ejbcawebbean.getServerTime()%>"/></code></div>
   <table  class="table table-bordered">
   <tr>
   <td>
<%@ include file="statuspages/cacrlstatuses.jspf" %>
    </td>
   <td>
<%@ include file="statuspages/publisherqueuestatuses.jspf" %>
    </td>
    </tr>
    </table>
</div>
</div>
</section>
</body>
</html>
