<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<title>Item Maker Dashboard</title>
<%@ include file="/WEB-INF/views/common/cssinheader.jsp"%>
</head>
<body>
	<div id="wrapper">
		<%@ include file="/WEB-INF/views/common/navi.jsp"%>
		<div id="page-wrapper" style="min-height: 618px;">

			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">${__static__['dashboard.content']}</h1>
				</div>
			</div>
		</div>
	</div>

	<%@ include file="/WEB-INF/views/common/endingscripts.jsp"%>
</body>
</html>