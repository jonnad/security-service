<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<html>
<head>
    <title>Sign On</title>
</head>

<body>
<h1>Service Configuration Listing</h1>

<display:table name="${serviceDefinitions}" id="serviceDefinition">

    <display:column title="Edit">
        <a href="editServiceDefinition.action?serviceDefinitionId=${serviceDefinition.id}">Edit</a>
    </display:column>
    <display:column property="name"/>
    <display:column property="description"/>
    <display:column property="enabled"/>
    <display:column property="secured"/>
    <display:column property="requiresUserAuthentication"/>
</display:table>

<s:url action="add"/>

</body>
</html>