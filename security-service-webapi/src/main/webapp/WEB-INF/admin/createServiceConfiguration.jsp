<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<html>
<head>
    <title>Sign On</title>
</head>

<body>
<h1>Service Configuration Edit</h1>
<s:form action="saveServiceConfiguration">
    <s:textfield label="Name" name="serviceConfiguration.name"/>
    <s:textfield label="Description" name="serviceConfiguration.description" size="60"/>

    <s:checkbox label="Enabled" name="enabled" />
    <s:checkbox label="Requires Security" name="secured" />
    <s:checkbox label="Requires User Authentication" name="requiresUserAuthentication" />

    <s:submit label="Submit" align="left"/>
</s:form>

</body>
</html>