<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="THIS_TITLE" value="Certificate Enrollment from CSR" />
<%@ include file="header.jsp" %>


<style>
code {
    padding: 2px 4px  !important;
       font-size: 16px !important;
    color: red !important;
    background-color: WHITE !important;
    white-space: nowrap !important;
    border-radius: 4px !important;
    font-family: "Helvetica Neue",Helvetica,Arial,sans-serif !important;
    font-weight: normal !important;
}
</style>
 <section id="annd-token" class="container-fuild">
 <div class="col-xs-12">
<h1 class="title">Certificate enrollment from a CSR</h1>
  <small>Please give your username and password, select a PEM- or DER-formated certification request file (CSR) for upload, 
or paste a PEM-formated request into the field below and click OK to fetch your certificate. 
  </small>

<p>A PEM-formatted request is a BASE64 encoded certificate request starting with<br />
  <code>-----BEGIN CERTIFICATE REQUEST-----</code><br />
  and ending with<br />
  <code>-----END CERTIFICATE REQUEST-----</code>
</p>

<form name="EJBCA" action="../certreq" method="post" enctype="multipart/form-data">
  <fieldset >
    <legend>Enroll</legend>
	<input type="text" size="40" name="user" id="user"  placeholder="Username" class="form-control" style="width: 20%"  accesskey="u" />
	<br />
	<input type="password" size="40"  placeholder="Password"  class="form-control" style="width: 20%" name="password" id="password" accesskey="p" />
	<br />
	<div style="clear: both; margin-bottom: 20px;">
	<label for="pkcs10file" style="float: left;">Request file</label>
	<input type="file" size="40" name="pkcs10file" id="pkcs10file"  style="float: left; margin-left: 10px;"></input>
  

   <div style="clear: both; margin-bottom: 20px;">
	<label for="pkcs10req" style="display: block;">or pasted request</label>
	<textarea rows="15" cols="66" name="pkcs10req" id="pkcs10req"></textarea>
 </div>
	<label for="resulttype">Result type</label>
	<select name="resulttype" id="resulttype">
		<option value="<%=org.ejbca.ui.web.RequestHelper.ENCODED_CERTIFICATE%>">PEM certificate</option> 
		<option value="<%=org.ejbca.ui.web.RequestHelper.ENCODED_PKCS7%>">PKCS#7 certificate</option>
	</select>
	<br />
	<label for="ok"></label>
	<input class="btn btn-default" type="submit" id="ok" value="OK" />
	<br />
    <br />
  </fieldset>
</form>
   </div>
    </section>


