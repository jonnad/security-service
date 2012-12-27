<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<html>
<head>
    <title>Sign On</title>
</head>

<body>
<h1>Service Configuration Listing</h1>

<display:table name="${serviceConfigurations}" id="serviceConfiguration">

    <display:column title="Edit">
        <a href="editServiceConfiguration.action?serviceConfigurationId=${serviceConfiguration.id}">Edit</a>
    </display:column>
    <display:column property="name"/>
    <display:column property="description"/>
    <display:column property="enabled"/>
    <display:column property="secured"/>
    <display:column property="requiresUserAuthentication"/>
</display:table>

<INPUT TYPE="button" onClick="parent.location='createServiceConfiguration.action'">


</body>
</html>