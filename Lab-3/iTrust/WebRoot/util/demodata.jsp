<%@page import="edu.ncsu.csc.itrust.datagenerators.TestDataGenerator"%>
<%@page import="java.lang.reflect.Method"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.DBUtil"%>
<%@page import="edu.ncsu.csc.itrust.dao.ProductionConnectionDriver"%>
<%@page import="edu.ncsu.csc.itrust.DemoData"%><html>

<head>
<title>FOR TESTING PURPOSES ONLY</title>
<style type="text/css">
.message {
	border-style: solid;
	border-width: 1px;
	background-color: #AAFFAA;
}
</style>
</head>
<body>
<div align=center>
<h1>Test Utilities</h1>
<%
	if ("resetdb".equals(request.getParameter("reset"))) {
		DemoData.resetDemoData(out);
%><h2>Database has been reset to standard data</h2>
<%
	}
%> Please click this to reset all data in the database to the standard
data. <a href="demodata.jsp?reset=resetdb">Reset Database</a> <br>
<br>
<h1><a href="/iTrust">Back to iTrust</a></h1>
</div>
</body>
</html>