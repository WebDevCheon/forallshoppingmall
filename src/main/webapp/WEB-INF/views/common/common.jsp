<%@ page import = "java.util.*,spring.myapp.shoppingmall.dto.Goods" language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<%
    	String url = request.getRequestURI().toString();
		String[] urlArr = url.split("/");
		String nowjsppage = urlArr[urlArr.length - 1];
		String nowpagename = nowjsppage.substring(0,nowjsppage.length()-4);
		System.out.println("nowpagename : " + nowpagename);
	%>
    <meta charset="UTF-8">
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon/books-16.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/core-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/style.css">
</head>
<body>
   	<jsp:include page = "./search_wrapper.jsp"/>
   	<div class="main-content-wrapper d-flex clearfix">
        <!-- Mobile Nav (max width 767px)-->
        <div class="mobile-nav">
            <!-- Navbar Brand -->
            <div class="amado-navbar-brand">
                <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/resources/img/core-img/bookstore.png" alt=""></a>
            </div>
            <!-- Navbar Toggler -->
            <div class="amado-navbar-toggler">
                <span></span><span></span><span></span>
            </div>
        </div>
    </div>
	    <div class="main-content-wrapper d-flex clearfix">
        <div class="mobile-nav">
            <div class="amado-navbar-brand">
                <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/resources/img/core-img/bookstore.png" alt=""></a>
            </div>
            <div class="amado-navbar-toggler">
                <span></span><span></span><span></span>
            </div>
        </div>

        <header class="header-area clearfix">
            <div class="nav-close">
                <i class="fa fa-close" aria-hidden="true"></i>
            </div>
            <div class="logo">
                <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/resources/img/core-img/bookstore.png" alt=""></a><br>
                <br>
                <c:if test = "${empty sessionScope.Userid}">
              		<a href = "${pageContext.request.contextPath}/loginForm" class = "btn amado-btn">?????????</a><br><br>
		    	</c:if>
		    	<c:if test = "${empty sessionScope.Userid}">
              		<a href = "${pageContext.request.contextPath}/joinform" class = "btn amado-btn">????????????</a><br><br>
		    	</c:if>
		    	<c:if test = "${empty sessionScope.Userid}">
		    		<a href = "${pageContext.request.contextPath}/findId" class = "btn amado-btn">????????? ??????</a><br><br>
		    	</c:if> 
		    	<c:if test = "${empty sessionScope.Userid}">
		    		<a href = "${pageContext.request.contextPath}/findPassword" class = "btn amado-btn">???????????? ??????</a>
		    	</c:if>
		    	<c:if test = "${not empty sessionScope.Userid}">
		    	<div class = "jumbotron" align = "center" style = "width : 160px;">
              	   <span>??????????????? <span style = "font-weight:bold;">${sessionScope.Userid}</span>???!</span>
              	   <br>
              	   <span>????????? ????????? ???????????????!</span>
              	   <br>
			    </div>
				 <div>
					<a href = "${pageContext.request.contextPath}/updatePasswordForm" class = "btn amado-btn">???????????? ??????</a>
			     </div>
			 	 <div>
			    </c:if>
            </div>
            <nav class="amado-nav">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/">????????????</a></li>
                    <li><a href="${pageContext.request.contextPath}/shopentrance">??????</a></li>
                   <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/showbasket">????????????</a></li>
                   <li><a href="${pageContext.request.contextPath}/showcoupon">?????? ??????</a></li>
                    <c:if test = "${not empty sessionScope.Userid}">
                    	<% if(nowpagename.equals("orderinfo")) {%>
								<li class = "active"><a href="${pageContext.request.contextPath}/showorder">?????? ??????</a></li>
							<%} else { %>
								<li><a href="${pageContext.request.contextPath}/showorder">?????? ??????</a></li>
							<% } %>
					</c:if>
					<c:if test = "${not empty sessionScope.admingrp}">
						<li><a href="${pageContext.request.contextPath}/admin/adminrefund">?????? ??????</a>
					</c:if>
					<c:if test = "${not empty sessionScope.admingrp}">
						<li><a href="${pageContext.request.contextPath}/admin/registerForm">?????? ??????</a></li>
					</c:if>
					<c:if test = "${not empty sessionScope.admingrp}">
						<li><a href="${pageContext.request.contextPath}/admin/monthbookselect">????????? ?????? ?????? ??????</a>
					</c:if>
					<c:if test = "${not empty sessionScope.admingrp}">
						<li><a href="${pageContext.request.contextPath}/admin/todaybookselect">????????? ?????? ??????</a>
					</c:if>
					<c:if test = "${not empty sessionScope.admingrp}">
						<li><a href="${pageContext.request.contextPath}/admin/bookselect">?????? ?????? ??????</a>
					</c:if>
                    <c:if test = "${not empty sessionScope.Userid}">
                    	<li><a style = "cursor : pointer" onclick="document.getElementById('logout-form').submit();">????????????</a></li>
                		<form id="logout-form" action="<c:url value="/logout"/>" method="post">
    						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						</form>
                    </c:if>
                </ul>
            </nav>
            
            <div class="cart-fav-search mb-100">
             <c:if test = "${not empty sessionScope.Userid}">
                <a href="${pageContext.request.contextPath}/showbasket" class="cart-nav"><img src="${pageContext.request.contextPath}/resources/img/core-img/cart.png" alt="">???????????? </a>
             </c:if>
                <a href="#" class="search-nav"><img src="${pageContext.request.contextPath}/resources/img/core-img/search.png" alt="">?????? ??????</a>
            </div>

            <div class="social-info d-flex justify-content-between">
                <a href="#"><i class="fa fa-pinterest" aria-hidden="true"></i></a>
                <a href="#"><i class="fa fa-instagram" aria-hidden="true"></i></a>
                <a href="#"><i class="fa fa-facebook" aria-hidden="true"></i></a>
                <a href="#"><i class="fa fa-twitter" aria-hidden="true"></i></a>
            </div>
        </header>
		
		<div class="amado_product_area section-padding-100">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-12">
                          <div class="product-topbar d-xl-flex align-items-end justify-content-between">
                          
                          </div>                  
                    </div>
        		</div>
    <input type="hidden" name="${_csrf.parameterName}" id = "csrftoken" value="${_csrf.token}"/>
    <script src="${pageContext.request.contextPath}/resources/js/jquery/jquery-2.2.4.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/popper.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/plugins.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/active.js"></script>
    <script>
	function makecoupon(Id){
		$.ajax({
			url : "${pageContext.request.contextPath}/makecoupon",
			data : {"Id" : Id},
			method : "POST",
			success : function(data){
				if(data == 0){
					alert('?????? ??? ?????? ????????? ????????????.');
				}
				else if(data == 1){
					alert('????????? ?????? ??????????????????.');
				}
			},
			error : function(err){
				alert("?????? ??????");
			}
		});
	}
    </script>
</body>
</html>