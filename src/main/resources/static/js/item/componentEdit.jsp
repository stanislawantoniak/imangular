<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<title>Edit component</title>
<%@ include file="/WEB-INF/views/common/cssinheader.jsp"%>
</head>
<body>
	<div id="wrapper">
		<%@ include file="/WEB-INF/views/common/navi.jsp"%>
		<div id="page-wrapper" style="min-height: 618px;">

			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">Edit Component</h1>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-heading">Component of Item: ${item.name}
							(#${item.id})</div>
						<div class="panel-body">

							<c:url var="updateAction" value="/items/component/update"></c:url>
							<form:form id="component" role="form" action="${updateAction}"
								modelAttribute="itemComponent">
								<form:hidden path="id" />
								<form:hidden path="parent.id" />
								<div class="form-group">
									<form:label path="component">
										<spring:message text="Component name" />
									</form:label>
									<c:if test="${empty componentItem}">
										<form:select class="form-control" path="component">
											<option value="">--Select Item--</option>
											<c:forEach items="${allItems}" var="i">
												<c:choose>
													<c:when test="${i.key eq itemComponent.component.id}">
														<option value="${i.key}" selected="true">${i.value}</option>
													</c:when>
													<c:otherwise>
														<option value="${i.key}">${i.value}</option>
													</c:otherwise>
												</c:choose>
											</c:forEach>

										</form:select>
									</c:if>
									<form:errors path="component" />
								</div>
								<div class="form-group">
									<form:label path="remarks">
										<spring:message text="Remarks" />
									</form:label>
									<form:textarea class="form-control" path="remarks" rows="3" />
									<form:errors path="remarks" />
								</div>
								<div class="form-group">
									<form:label path="quantity">
										<spring:message text="Quantity" />
									</form:label>
									<form:input class="form-control" path="quantity" />
									<form:errors path="quantity" />
								</div>
								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
								<button class="${buttonOutlineClasses }" type="submit">
									<spring:message text="Save component" />
								</button>
								<c:url var="cancelAction" value="/items/item/edit/${item.id}"></c:url>
								<form action="${cancelAction}">
									<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" />
									<button class="${buttonDefaultClasses }" type="submit">${__static__['component.editform.button.cancel']}</button>
								</form>

							</form:form>
						</div>
					</div>

				</div>
			</div>
		</div>
	</div>

	<%@ include file="/WEB-INF/views/common/endingscripts.jsp"%>
</body>
</html>