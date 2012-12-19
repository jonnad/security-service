<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Sign On</title>
</head>

<body>
<s:form action="login">
    <s:textfield key="username" label="Username"/>
    <s:password key="password" label="Password" />
    <s:hidden key="loginAttempt" value="true" />
    <s:submit/>
</s:form>
</body>
</html>