<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<title>Edit item</title>
<%@ include file="/WEB-INF/views/common/cssinheader.jsp"%>
</head>
<body>
	<div id="wrapper">
		<%@ include file="/WEB-INF/views/common/navi.jsp"%>
		<div id="page-wrapper" style="min-height: 618px;">

			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">
						<c:if test="${item.id != 0}">
						${__static__['edituser.heading.edituser'] }
						</c:if>
						<c:if test="${item.id == 0}">
						${__static__['edituser.heading.additem'] }
						</c:if>
					</h1>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-heading">
							<c:if test="${item.id != 0}">
						${__static__['edituser.heading.edituser'] }
						</c:if>
							<c:if test="${item.id == 0}">
						${__static__['edituser.heading.additem'] }
						</c:if>
						</div>
						<div class="panel-body">
							<div class="col-lg-6">
								<c:url var="updateAction" value="/users/user/update"></c:url>
								<form:form role="form" action="${updateAction}"
									modelAttribute="user">
									<form:hidden path="id" />
									<div class="form-group">
										<form:label path="username">
											${__static__['edituser.label.name'] }
										</form:label>
										<form:input class="form-control" path="username" type="email" />
										<p class="help-block">${__static__['edituser.help.username'] }</p>
										<form:errors path="username" />
									</div>

									<div class="form-group">
										<form:label path="password">
											${__static__['edituser.label.password'] }
										</form:label>
										<form:input class="form-control" path="password"
											type="password" autocomplete="new-password" />
										<p class="help-block">${__static__['edituser.help.password'] }</p>
										<form:errors path="password" />
									</div>

									<div class="form-group">
										<form:label path="enabled">
											${__static__['edituser.label.enabled'] }
										</form:label>

										<c:if test="${user.enabled}">
											<label class="radio-inline"> <input type="radio"
												path="enabled" name="enabled" value="true" checked>
												${__static__['boolean.yes'] }
											</label>
											<label class="radio-inline"> <input type="radio"
												path="enabled" name="enabled" value="false">
												${__static__['boolean.no'] }
											</label>
										</c:if>

										<c:if test="${!user.enabled}">
											<label class="radio-inline"> <input type="radio"
												path="enabled" name="enabled" value="true">${__static__['boolean.yes'] }
											</label>
											<label class="radio-inline"> <input type="radio"
												path="enabled" name="enabled" value="false" checked>
												${__static__['boolean.no'] }
											</label>
										</c:if>
										<p class="help-block">${__static__['edituser.help.enabled'] }</p>
										<form:errors path="enabled" />
									</div>

									<div class="form-group">
										<form:label path="rolesSelected">${__static__['edituser.label.roles'] }</form:label>
										<form:select class="form-control" path="rolesSelected" multiple="multiple">
											<c:forEach items="${user.allRoles}" var="role">
												<c:choose>
													<c:when test="${role eq ''}">
														<option value="${role}" selected="true" type="checkbox">${role}</option>
													</c:when>
													<c:otherwise>
														<option value="${role}" type="checkbox">${role}</option>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</form:select>
										<form:errors path="rolesSelected" />
									</div>

									<button class="${buttonOutlineClasses }" type="submit">
										${__static__['edituser.button.save'] }</button>
									<c:url var="goToUsers" value="/users"></c:url>
									<a href="${goToUsers}" class="${buttonDefaultClasses }">
										${__static__['edituser.button.cancel'] } </a>
								</form:form>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>
	</div>

	<%@ include file="/WEB-INF/views/common/endingscripts.jsp"%>
</body>
</html>