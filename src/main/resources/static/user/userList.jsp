<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<title>${__static__['userslist.title']}</title>
<%@ include file="/WEB-INF/views/common//cssinheader.jsp"%>
</head>
<body>
	<div id="wrapper">
		<%@ include file="/WEB-INF/views/common//navi.jsp"%>
		<div id="page-wrapper" style="min-height: 618px;">

			<div class="row">
				<div class="col-lg-6">
					<h1 class="page-header">${__static__['userslist.heading']}</h1>

					<c:url var="addAction" value="/users/user/edit/0"></c:url>
					<form action="${addAction}">
						<button class="${buttonOutlineClasses }" type="submit">
							${__static__['userslist.heading.button.user.add']}</button>
					</form>

				</div>
			</div>

			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="table-responsive">
							<c:if test="${!empty usersList}">
								<table class="table table-striped table-bordered table-hover">
									<thead>
										<tr>
											<th width="10%">${__static__['userslist.userstable.heading.id']}</th>
											<th width="60%">${__static__['userslist.userstable.heading.name']}</th>
											<th width="10%">${__static__['userslist.userstable.heading.enabled']}</th>
											<th width="20%">${__static__['userslist.userstable.heading.actions']}</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${usersList}" var="user">
											<tr>
												<td>${user.id}</td>
												<td>${user.username}</td>
												<td>${user.enabled}</td>
												<td><c:url var="editAction"
														value="/users/user/edit/${user.id}"></c:url>
													<div class="col-lg-6">
														<form action="${editAction}">
															<input type="hidden" name="${_csrf.parameterName}"
																value="${_csrf.token}" />
															<button class="${buttonDefaultClasses }" type="submit">${__static__['userslist.userstable.button.edit']}</button>
														</form>
													</div></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:if>
						</div>
					</div>
				</div>

			</div>
		</div>
	</div>

	<%@ include file="/WEB-INF/views/common/endingscripts.jsp"%>
</body>
</html>