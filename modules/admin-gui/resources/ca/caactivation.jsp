<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page pageEncoding="ISO-8859-1"%>
<% response.setContentType("text/html; charset="+org.ejbca.config.WebConfiguration.getWebContentEncoding()); %>
<%@page errorPage="/errorpage.jsp" import="org.ejbca.config.GlobalConfiguration,org.ejbca.ui.web.RequestHelper,
                                           org.ejbca.ui.web.admin.configuration.EjbcaJSFHelper,
                                           org.ejbca.core.model.authorization.AccessRulesConstants" %>
<jsp:useBean id="ejbcawebbean" scope="session" class="org.ejbca.ui.web.admin.configuration.EjbcaWebBean" />
<jsp:setProperty name="ejbcawebbean" property="*" /> 
<jsp:useBean id="cabean" scope="session" class="org.ejbca.ui.web.admin.cainterface.CAInterfaceBean" />
<jsp:setProperty name="cabean" property="*" />
<%   // Initialize environment
 GlobalConfiguration globalconfiguration = ejbcawebbean.initialize(request, AccessRulesConstants.REGULAR_ACTIVATECA); 
 EjbcaJSFHelper helpbean = EjbcaJSFHelper.getBean();
 helpbean.setEjbcaWebBean(ejbcawebbean);
%>
<html>
<head>
  <title><c:out value="<%= globalconfiguration.getEjbcaTitle() %>" /></title>
  <base href="<%= ejbcawebbean.getBaseUrl() %>" />
 	  <script src="adminweb/themes/libs/js/jquery.min.js"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/bootstrap.min.css">
  <script src="adminweb/themes/libs/js/bootstrap.min.js"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/font-awesome.min.css">
  <script src="adminweb/themes/libs/js/main.js" type="text/javascript"></script>
  <link rel="stylesheet" href="adminweb/themes/libs/css/styles.css">
  <meta http-equiv="Content-Type" content="text/html; charset=<%= org.ejbca.config.WebConfiguration.getWebContentEncoding() %>" />
  <style>
  img {

    margin-right: 3px;
}
label
{
    font-weight: 100 !important;
    margin-right: 6px;
    margin-left: 6px;
}
input[type=checkbox] {

    margin-right: 6px !important;
}

.formcontrol {
    display: block;
    height: 34px;
    padding: 6px 12px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #555;
    background-color: #fff;
    background-image: none;
    border: 1px solid #ccc;
    border-radius: 4px;
    -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
    box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
    -webkit-transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
    transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
	float: left; 
	width: 250px; 
	margin-right: 10px;
}
#cat1{
    text-decoration: none !important;
 
    background: #d00a07 !important;
    display: block !important;
}
#cat1 strong
{
   color: #fff !important;
}

  </style>
</head>

<f:view>



<body>
<jsp:include page="/adminmenu.jsp" />

<section id="content-one" class="container-fuild">
 <div class="col-xs-12">
       <div id="brc">
       <li><a href="/annd/adminweb/main.jsp" target="mainFrame">Home</a></li>
       <li><img src="/annd/adminweb/themes/libs/img/icon.png"></li>
		<li><a>CA Activation</a></li>
         </div>
      </div>
<div class="col-xs-12">
<h2><h:outputText value="#{web.text.ACTIVATECAS}"/></h2>
</div>
<div class="col-xs-8">

	<h:form>
	<h:dataTable border="0" value="#{cAActivationMBean.hasMessages}" var="item" style="right: auto; left: auto">
	     	<h:column>
	     		<td>
	     		<h:outputText value="#{web.text.MESSAGE}: "/>
	     		<h:outputText value="#{item.name}: "></h:outputText>
				</td>	     	
	     	</h:column>
	     	<h:column>
	     	<h:outputText value="#{item.CAActivationMessage}"></h:outputText>
	     	</h:column>
	     </h:dataTable>
	<div id="activation">
	<h:dataTable styleClass="table table-bordered" value="#{cAActivationMBean.authorizedCAWrappers}" var="item" style="border-collapse: collapse; right: auto; left: auto">
	  			<h:column>
	    			<f:facet name="header">
	    				<h:outputText value="#{web.text.CA}" />
	    			</f:facet>
	    			<h:outputText value="#{item.name}"></h:outputText>
	    		</h:column>
	  			<h:column>
	    			<f:facet name="header">
	    				<h:outputText value="#{web.text.CASTATUS}" />
	    			</f:facet>
	    			<h:graphicImage height="16" width="16" url="#{item.statusImg}" style="border-width:0"/>
	    			<h:outputText value="#{item.status}"></h:outputText>
		    		</h:column>
	    		<h:column>
	    			<f:facet name="header">
	    				<h:outputText value="#{web.text.CATOKENSTATUS}" />
	    			</f:facet>
	    			<h:graphicImage height="16" width="16" url="#{item.tokenStatusImg}" style="border-width:0"/>
	    			<h:outputText value="#{item.tokenStatus}"></h:outputText>
	    		</h:column>
	    		<h:column>
	    		<f:facet name="header">
	    	 		<h:outputText value="#{web.text.ACTIVATEORMAKEOFFLINE}" />
	    		</f:facet>
	    		<h:selectOneRadio id="align" value="#{item.activateOption}">
  					<f:selectItem itemLabel="#{web.text.ACTIVATE}" itemValue="#{cAActivationMBean.activate}"/>
  					<f:selectItem itemLabel="#{web.text.MAKEOFFLINE}" itemValue="#{cAActivationMBean.makeoffline}"/>
  					<f:selectItem itemLabel="#{web.text.NOCHANGE}" itemValue="#{cAActivationMBean.keepcurrent}"/>
				</h:selectOneRadio>
	    		</h:column>
	    		<h:column>
	    		<f:facet name="header">
	    	 		<h:outputText value="#{web.text.MONITORIFCAACTIVE}" />
	    		</f:facet>
	    		<h:selectBooleanCheckbox value="#{item.monitored}" />
	    		<h:outputText value="#{web.text.MONITORED}" />
	    		</h:column>
	         </h:dataTable>
			</div>

			<div id="code">
	          <label style="float: left; height: 34px; line-height: 34px; margin-right: 10px;"> <h:outputText value="#{web.text.AUTHENTICATIONCODE}"></h:outputText> </label>
	           <h:inputSecret styleClass="formcontrol" id="password" value="#{cAActivationMBean.authenticationCode}" />
	           <h:commandButton id="submit" styleClass="btn btn-danger" action="#{cAActivationMBean.apply}" value="#{web.text.APPLY}" />
			</div>

	 </h:form>
	 </div>
  </section>
</body>
</f:view>
</html>
