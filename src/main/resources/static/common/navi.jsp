<nav class="navbar navbar-default navbar-static-top"
	style="margin-bottom: 0" role="navigation">
	<div class="navbar-header">
		<button class="navbar-toggle" data-target=".navbar-collapse"
			data-toggle="collapse" type="button">
			<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span>
			<span class="icon-bar"></span> <span class="icon-bar"></span>
		</button>
		<c:url value="/" var="main" />
		<a class="navbar-brand" href="${main}">${__static__['app.name']}</a>
	</div>

	<%@ include file="navbar-right.jsp"%>

	<%--<%@ include file="navbar-right.jsp"%>--%>
	<div class="navbar-default sidebar" role="navigation">
		<div class="sidebar-nav navbar-collapse">
			<ul id="side-menu" class="nav">
				<li class="sidebar-search">
					<div class="input-group custom-search-form">
						<input class="form-control" type="text"
							placeholder="${__static__['search.widget.text']}"> <span
							class="input-group-btn">
							<button class="btn btn-default" type="button">
								<i class="fa fa-search"></i>
							</button>
						</span>
					</div>
				</li>
				<c:url value="/items" var="items" />
				<li><a class="active" href="${items}"> <i
						class="fa fa-table fa-fw"></i>
						${__static__['leftpanel.menu.manageitems']}
				</a></li>
				<c:url value="/users" var="users" />
				<li><a class="active" href="${users}"> <i
						class="fa fa-wrench fa-fw"></i>
						${__static__['leftpanel.menu.manageUsers']}
				</a></li>
			</ul>
		</div>
	</div>
</nav>

