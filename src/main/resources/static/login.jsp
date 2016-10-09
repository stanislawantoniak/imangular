<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>${__static__['login.title']}</title>
<%@ include file="/WEB-INF/views/common/cssinheader.jsp"%>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-md-4 col-md-offset-4">
				<div class="login-panel panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">${__static__['login.pleasesignin']}
						<!--
						does not worj - posibly it is new session at page reload 
							<div class="pull-rigth">
								<c:forEach items="${languages}" var="language">
									<c:if test="${! (user.languageSelected.name eq language.name)}">
										<c:url value="/selectLanguage" var="selectLanguage" />
										<div>
											<a href="${selectLanguage}/${language.acronym }"> <img
												src="<c:url value="/resources/${language.flag}"/>" />
											</a>
										</div>
									</c:if>
								</c:forEach>
							</div>
							-->
						</h3>
					</div>
					<div class="panel-body">

						<c:url var="loginUrl" value="/login" />
						<form action="${loginUrl}" method="post">
							<c:if test="${param.error != null}">
								<div class="alert alert-danger">
									<p>${__static__['login.error.message'] }</p>
								</div>
							</c:if>
							<c:if test="${param.logout != null}">
								<div class="alert alert-success">
									<p>You have been logged out successfully.</p>
								</div>
							</c:if>

							<div class="form-group">
								<input class="form-control"
									placeholder="${__static__['login.name.label']}" name="username"
									autofocus>
							</div>
							<div class="form-group">
								<input class="form-control"
									placeholder="${__static__['login.pass.label'] }"
									name="password" type="password" value="">
							</div>

							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" /> <input type="submit"
								class="btn btn-lg btn-success btn-block"
								value="${__static__['login.button.login']}">
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%@ include file="/WEB-INF/views/common/endingscripts.jsp"%>
</body>
</html>