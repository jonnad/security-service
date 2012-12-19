<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<html>
<head>
    <title>Sign On</title>
</head>

<body>
<h1>Service Configuration Edit</h1>
<s:form action="saveServiceDefinition">
    <s:hidden name="serviceDefinition.id"/>
    <s:textfield label="Name" name="serviceDefinition.name"/>
    <s:textfield label="Description" name="serviceDefinition.description" size="60"/>
    <s:checkbox label="Enabled" name="serviceDefinition.enabled"/>
    <s:checkbox label="Secured" name="serviceDefinition.secured"/>
    <s:checkbox label="Requires Authentication" name="serviceDefinition.requiresUserAuthentication"/>

    <s:submit label="Submit" align="left"/>
</s:form>

</body>
</html>