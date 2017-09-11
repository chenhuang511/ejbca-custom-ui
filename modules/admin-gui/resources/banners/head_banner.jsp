<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page pageEncoding="ISO-8859-1"%>
<% response.setContentType("text/html; charset="+org.ejbca.config.WebConfiguration.getWebContentEncoding()); %>
<%@page errorPage="../errorpage.jsp" import="org.ejbca.config.GlobalConfiguration"%>
<html>
<jsp:useBean id="ejbcawebbean" scope="session" class="org.ejbca.ui.web.admin.configuration.EjbcaWebBean" />
<jsp:setProperty name="ejbcawebbean" property="*" /> 
<%	// Initialize environment
	GlobalConfiguration  globalconfiguration = ejbcawebbean.initialize(request,"/administrator"); 
%>
<head>
    <title><c:out value="<%= globalconfiguration.getEjbcaTitle() %>" /></title>
	<base href="<%= ejbcawebbean.getBaseUrl() %>" />
	  <script src="adminweb/themes/libs/js/jquery.min.js"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/bootstrap.min.css">
  <script src="adminweb/themes/libs/js/bootstrap.min.js"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/font-awesome.min.css">
  <script src="adminweb/themes/libs/js/main.js" type="text/javascript"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/styles.css">
</head>

<body>
<header class="container-fuild">
        <div class="col-xs-5 logo">
            <a href="#"><img src="adminweb/themes/libs/img/logo.png" alt=""></a>
        </div>
        <div class="col-xs-7 nav">
            <a href="<%=ejbcawebbean.getBaseUrl() + globalconfiguration.getAdminWebPath() + "logout" %>">Logout</a>
        </div>
    </header>
</body>
</html>
