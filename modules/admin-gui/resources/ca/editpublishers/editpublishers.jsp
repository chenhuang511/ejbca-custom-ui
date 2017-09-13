<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page pageEncoding="ISO-8859-1"%>
<% response.setContentType("text/html; charset="+org.ejbca.config.WebConfiguration.getWebContentEncoding()); %>
<%@page errorPage="/errorpage.jsp" import="java.util.*, org.ejbca.ui.web.admin.configuration.EjbcaWebBean,org.ejbca.config.GlobalConfiguration, org.ejbca.core.model.SecConst, 
              org.ejbca.core.model.authorization.AuthorizationDeniedException, org.ejbca.core.model.authorization.AccessRulesConstants,
               org.ejbca.ui.web.admin.cainterface.CAInterfaceBean, org.ejbca.core.model.ca.publisher.*, org.ejbca.ui.web.admin.cainterface.EditPublisherJSPHelper, 
               org.ejbca.core.model.ca.publisher.PublisherExistsException, org.ejbca.util.dn.DNFieldExtractor, org.ejbca.util.dn.DnComponents"%>

<html>
<jsp:useBean id="ejbcawebbean" scope="session" class="org.ejbca.ui.web.admin.configuration.EjbcaWebBean" />
<jsp:useBean id="cabean" scope="session" class="org.ejbca.ui.web.admin.cainterface.CAInterfaceBean" />
<jsp:useBean id="publisherhelper" scope="session" class="org.ejbca.ui.web.admin.cainterface.EditPublisherJSPHelper" />

<% 

  // Initialize environment
  String includefile = "publisherspage.jspf"; 


  GlobalConfiguration globalconfiguration = ejbcawebbean.initialize(request, AccessRulesConstants.ROLE_SUPERADMINISTRATOR); 
                                            cabean.initialize(request, ejbcawebbean); 
                                            publisherhelper.initialize(request,ejbcawebbean, cabean);
  String THIS_FILENAME            =  globalconfiguration.getCaPath()  + "/editpublishers/editpublishers.jsp";
  
%>
 
<head>
  <title><c:out value="<%= globalconfiguration.getEjbcaTitle() %>" /></title>
  <base href="<%= ejbcawebbean.getBaseUrl() %>" />
   <script src="adminweb/themes/libs/js/jquery.min.js"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/bootstrap.min.css">
  <script src="adminweb/themes/libs/js/bootstrap.min.js"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/font-awesome.min.css">
  <link rel="stylesheet" href="adminweb/themes/libs/css/styles.css">
  <script type="text/javascript" src="<%= globalconfiguration .getAdminWebPath() %>ejbcajslib.js"></script>
</head>

<body>
<jsp:include page="//adminmenu.jsp" />
<%  // Determine action 

  includefile = publisherhelper.parseRequest(request);

 // Include page
  if( includefile.equals("publisherpage.jspf")){ 
%>
   <%@ include file="publisherpage.jspf" %>
<%}
  if( includefile.equals("publisherspage.jspf")){ %>
   <%@ include file="publisherspage.jspf" %> 
<%} 

   %>
</body>
</html>
