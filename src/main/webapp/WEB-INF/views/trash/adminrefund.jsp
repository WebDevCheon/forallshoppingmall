<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
<script src="/shoppingmall/resources/js/jquery/jquery-2.2.4.min.js"></script>
<script src="/shoppingmall/resources/js/admin/adminrefund.js"></script>
<title>상품자 환불</title>
<style>
	th,td {
		padding : 30px
	}
</style>
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- The above 4 meta tags *must* come first in the head; any other head content must come *after* these tags -->

    <!-- Title  -->

    <!-- Favicon  -->
    <link rel="icon" href="img/core-img/favicon.ico">

    <!-- Core Style CSS -->
    <link rel="stylesheet" href="/shoppingmall/resources/css/core-style.css">
    <link rel="stylesheet" href="/shoppingmall/resources/style.css">
</head>
<body>
     <!-- Search Wrapper Area Start -->
    <jsp:include page = "../common/search_wrapper.jsp" />
    <!-- Search Wrapper Area End -->

    <!-- ##### Main Content Wrapper Start ##### -->
    <div class="main-content-wrapper d-flex clearfix">

        <!-- Mobile Nav (max width 767px)-->
        <div class="mobile-nav">
            <!-- Navbar Brand -->
            <div class="amado-navbar-brand">
                <a href="/shoppingmall/home"><img src="/shoppingmall/resources/img/core-img/bookstore.png" alt=""></a>
            </div>
            <!-- Navbar Toggler -->
            <div class="amado-navbar-toggler">
                <span></span><span></span><span></span>
            </div>
        </div>

        <!-- Header Area Start -->
        <header class="header-area clearfix">
            <!-- Close Icon -->
            <div class="nav-close">
                <i class="fa fa-close" aria-hidden="true"></i>
            </div>
            <!-- Logo -->
            <div class="logo">
                <a href="/shoppingmall/home"><img src="/shoppingmall/resources/img/core-img/bookstore.png" alt=""></a><br>
                <br>
                <c:if test = "${empty sessionScope.Userid}">
              		<a href = "/shoppingmall/loginForm" class = "btn amado-btn">로그인</a><br><br>
		    	</c:if>
		    	<c:if test = "${empty sessionScope.Userid}">
              		<a href = "/shoppingmall/joinform" class = "btn amado-btn">회원가입</a><br><br>
		    	</c:if>
		    	<c:if test = "${empty sessionScope.Userid}">
		    		<a href = "/shoppingmall/findId" class = "btn amado-btn">아이디 찾기</a><br><br>
		    	</c:if> 
		    	<c:if test = "${empty sessionScope.Userid}">
		    		<a href = "/shoppingmall/findPassword" class = "btn amado-btn">비밀번호 찾기</a>
		    	</c:if>
		    	<c:if test = "${not empty sessionScope.Userid}">
		    	<div class = "jumbotron" align = "center" style = "width : 160px;">
              	   <span>안녕하세요 <span style = "font-weight:bold;">${sessionScope.Userid}</span>님!</span><br>
              	   <span>오늘도 즐거운 쇼핑하세요!</span>
              	   <br>
			    </div>
				 <div>
					<a href = "/shoppingmall/updatePasswordForm" class = "btn amado-btn">비밀번호 변경</a>
			     </div>
			 	 <div>
			 	 	<br>
					<button onclick = "makecoupon('${sessionScope.Userid}')" class = "btn amado-btn">쿠폰 받기</button>
			     </div>
			    </c:if>
            </div>
            <!-- Amado Nav -->
            <nav class="amado-nav">
                <ul>
                    <li><a href="/shoppingmall/home">홈페이지</a></li>
                    <li><a href="/shoppingmall/shop">상품</a></li>
                    <li><a href="/shoppingmall/product">상품 상세보기</a></li>
                   <c:if test = "${not empty sessionScope.Userid }">
                   <li class="nav-item">
                       <a class="nav-link" href="/shoppingmall/showbasket">장바구니</a>
                   </li>
                   </c:if>
                    <c:if test = "${not empty sessionScope.Userid }">
								<li><a href="/shoppingmall/showorder">주문 정보</a></li>
					</c:if>
					<c:if test = "${sessionScope.Userid eq 'admin'}">
						<li  class = "active"><a href="/shoppingmall/admin/adminrefund">고객 환불</a>
					</c:if>
					<c:if test = "${sessionScope.Userid eq 'admin'}">
						<li><a href="/shoppingmall/admin/registerForm">상품 등록</a>
					</c:if>
					<c:if test = "${sessionScope.Userid eq 'admin'}">
						<li><a href="/shoppingmall/admin/monthbookselect">이달의 인기 서적 등록</a>
					</c:if>
					<c:if test = "${sessionScope.Userid eq 'admin'}">
						<li><a href="/shoppingmall/admin/todaybookselect">오늘의 서적 등록</a>
					</c:if>
					<c:if test = "${sessionScope.Userid eq 'admin'}">
						<li><a href="/shoppingmall/admin/bookselect">대표 서적 등록</a>
					</c:if>
					<li><a href="/shoppingmall/board/list">문의 게시판</a></li>
					<li><a href="/shoppingmall/resources/jsp/introduce.jsp">회사 소개</a></li>
					<li><a href="/shoppingmall/resources/jsp/question.jsp">자주 묻는 질문</a></li>
                    <c:if test = "${not empty sessionScope.Userid}">
                    	<li><a href="/shoppingmall/logout">로그아웃</a></li>
                    </c:if>
                </ul>
            </nav>
            <!-- Button Group -->
           <!-- 
            <div class="amado-btn-group mt-30 mb-100">
                <a href="#" class="btn amado-btn mb-15">%Discount%</a>
                <a href="#" class="btn amado-btn active">New this week</a>
            </div-->
            
            <!-- Cart Menu -->
            <div class="cart-fav-search mb-100">
             <c:if test = "${not empty sessionScope.Userid}">
                <a href="/shoppingmall/showbasket" class="cart-nav"><img src="/shoppingmall/resources/img/core-img/cart.png" alt="">장바구니 </a>
             </c:if>
                <a href="#" class="search-nav"><img src="/shoppingmall/resources/img/core-img/search.png" alt="">상품 찾기</a>
            </div>
            <!-- Social Button -->
            <div class="social-info d-flex justify-content-between">
                <a href="#"><i class="fa fa-pinterest" aria-hidden="true"></i></a>
                <a href="#"><i class="fa fa-instagram" aria-hidden="true"></i></a>
                <a href="#"><i class="fa fa-facebook" aria-hidden="true"></i></a>
                <a href="#"><i class="fa fa-twitter" aria-hidden="true"></i></a>
            </div>
        </header>
        <!-- Header Area End -->
			 <!--
		   <div class="shop_sidebar_area">
            <div class="widget catagory mb-50">
                <h6 class="widget-title mb-30">상품 종류</h6>
                <div class="catagories-menu">
                    <ul>
                     	<li><a href = "/shoppingmall/shop?kind=chair">의자</a></li>
                        <li><a href = "/shoppingmall/shop?kind=shoes">신발</a></li>
                    	<li><a href = "/shoppingmall/shop?kind=game">게임</a></li>
						<li><a href = "/shoppingmall/shop?kind=plantpot">화분</a></li>
                    	<li><a href = "/shoppingmall/shop?kind=homedeco">장식품</a></li>
                    	<li><a href = "/shoppingmall/shop?kind=book">책</a></li>
                    	<li><a href = "/shoppingmall/shop?kind=smalltable">책상</a></li>
                        <li><a href = "/shoppingmall/shop?kind=computer">컴퓨터</a></li>
                    </ul>
                </div>
            </div>
          </div>
           -->
          <div class="amado_product_area section-padding-100">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-12">
                        <div class="product-topbar d-xl-flex align-items-end justify-content-between">
                            
                            <!-- 
                            <div class="total-products">
                                <p>Showing 1-8 0f 25</p>
                                <div class="view d-flex">
                                    <a href="#"><i class="fa fa-th-large" aria-hidden="true"></i></a>
                                    <a href="#"><i class="fa fa-bars" aria-hidden="true"></i></a>
                                </div>
                            </div>
                            <div class="product-sorting d-flex">
                                <div class="sort-by-date d-flex align-items-center mr-15">
                                    <p>Sort by</p>
                                    <form action="#" method="get">
                                        <select name="select" id="sortBydate">
                                            <option value="value">Date</option>
                                            <option value="value">Newest</option>
                                            <option value="value">Popular</option>
                                        </select>
                                    </form>
                                </div>
                                <div class="view-product d-flex align-items-center">
                                    <p>View</p>
                                    <form action="#" method="get">
                                        <select name="select" id="viewProduct">
                                            <option value="value">12</option>
                                            <option value="value">24</option>
                                            <option value="value">48</option>
                                            <option value="value">96</option>
                                        </select>
                                    </form>
                                </div>
                            </div>
                             -->
                        </div>
                    </div>
                </div>
        </div>
    
    
	<div class = "container" style = "margin-left:100px;">
	  <div>
	  <h3 style="margin-left:50px;">
		<span>주문번호</span> <input type = "text" id = "orderid" style = "margin:10px;"><a href = "#" onclick = "check(document.getElementById('orderid').value)" class = "btn amado-btn">조회</a><br> 
	  </h3>
		<table class = "table table-bordered" style = "text-align:center;padding:100px;border-spacing:10px;margin:10px" border = "1">
			<tr>
				<th>금액</th>
				<th>예금주</th>
				<th>은행</th>
				<th>계좌번호</th>
				<th>환불버튼</th>
			</tr>
			<tr>
				<td class="align-middle" id = "amount"></td>
				<td class="align-middle" id = "holder"></td>
				<td class="align-middle" id = "bank"></td>
				<td class="align-middle" id = "account"></td>
				<td><button class = "btn amado-btn" id = "rbtn">환불</button></td>
			</tr>
		</table>
	  </div>
	</div>
	
	<script>
		function check(orderid)
		{
			if(orderid == null)
			{
				alert('주문번호를 입력해주세요.');
				return;
			}
			else {
				$.ajax({
					"url" : "/shoppingmall/admin/findrefund",
					"type" : "POST",
					"contentType": "application/json",
					"data" : JSON.stringify({"orderid" : orderid}),
					"dataType" : "json",
					success : function(result)
					{
						alert('조회성공');
						if(result.amount == null) {
							alert("주문 번호 존재 하지 않음");
							return;
						}
						$("#amount").html(result.amount);
						$("#holder").html(result.holder);
						$("#bank").html(result.bank);
						$("#account").html(result.account);
						if(!result.holder)
							document.getElementById("rbtn").setAttribute("onclick","cancelPay()");
						else
							document.getElementById("rbtn").setAttribute("onclick","cancelPayVbank()");
					},
					error : function(){
						alert('오류 장애');
					}
				});
			}
		}
		
		function cancelPay() {
		      $.ajax({
		        "url": "/shoppingmall/cancel",
		        "type": "POST",
		        "contentType": "application/json",
		        "data": JSON.stringify({
		          "merchant_uid": document.getElementById("orderid").value, // 주문번호
		          "cancel_request_amount": document.getElementById("amount").innerHTML, // 환불금액
		        }),
		        success : function(){
		        	alert('요청 성공');
		        },
		        error : function(){
		        	alert('이미 환불 요청을 하셨습니다.');
		        },
		        "dataType": "json"
		      }).done(function(result){
		    	  console.log(result);
		    	  alert(result);
		    	  var cancelstatus = result.status;
		    	  console.log("cancelstatus : " + cancelstatus);
		    	  var canceldata = {"cancel" : cancelstatus, "merchant_id" : document.getElementById('orderid').value}
		    	  
		    	  $.ajax({
					"url" : "/shoppingmall/cancelstatus",
					"type" : "POST",
					headers: { "Content-Type": "application/json" },
					"data" : JSON.stringify(canceldata),
					success : function(){
						alert('환불 처리 성공');
						alert('status 변화 성공');
					},
					error : function(){
						alert('error');
					}
		    	  });
		    	  alert('환불 성공');
		      }).fail(function(error){
		    	  console.log(error);
		    	  alert('환불 실패'); 
		      });
		    } 
		
		function cancelPayVbank(){
		      $.ajax({
		        "url": "/shoppingmall/cancel",
		        "type": "POST",
		        "contentType": "application/json",
		        "data": JSON.stringify({
		          "merchant_uid": document.getElementById("orderid").value, // 주문번호
		          "cancel_request_amount": document.getElementById("amount").innerHTML, // 환불금액
		          "refund_holder": document.getElementById("holder").innerHTML, // [가상계좌 환불시 필수입력] 환불 가상계좌 예금주
		          "refund_bank": document.getElementById("bank").innerHTML, // [가상계좌 환불시 필수입력] 환불 가상계좌 은행코드(ex. KG이니시스의 경우 신한은행은 88번)
		          "refund_account": document.getElementById("account").innerHTML // [가상계좌 환불시 필수입력] 환불 가상계좌 번호
		        }),
		        success : function(){
		        	alert('환불 요청 성공');
		        },
		        error : function(){
		        	alert('이미 환불 요청을 하셨습니다.');
		        },
				"dataType" : "json"
		      }).done(function(result){
		    	  console.log(result);
		    	  var cancelstatus = result.status;
		    	  console.log("cancelstatus : " + cancelstatus);
		    	  var canceldata = {"cancel" : cancelstatus, "merchant_id" : document.getElementById('orderid').value}
		    	  $.ajax({
					url : "/shoppingmall/cancelstatus",
					type : "POST",
					headers: { "Content-Type": "application/json" },
					data : JSON.stringify(canceldata),
					success : function(){
						alert('환불 처리 성공');
						alert('status 변화 성공');
					},
					error : function(){
						alert('error');
					}
		    	  });
		    	  alert('환불 성공');
		      }).fail(function(error){
		    	  console.log(error);
		    	  alert('환불 실패'); 
		      });
		    }
	</script>
	       <!-- ##### jQuery (Necessary for All JavaScript Plugins) ##### -->
    <script src="/shoppingmall/resources/js/jquery/jquery-2.2.4.min.js"></script>
    <!-- Popper js -->
    <script src="/shoppingmall/resources/js/popper.min.js"></script>
    <!-- Bootstrap js -->
    <script src="/shoppingmall/resources/js/bootstrap.min.js"></script>
    <!-- Plugins js -->
    <script src="/shoppingmall/resources/js/plugins.js"></script>
    <!-- Active js -->
    <script src="/shoppingmall/resources/js/active.js"></script>
    <script>
	function makecoupon(Id)
	{
		$.ajax({
			url : "/shoppingmall/makecoupon",
			data : {"Id" : Id},
			method : "POST",
			success : function(data){
				if(data == 0)
				{
					alert('받을 수 있는 쿠폰이 없습니다.');
				}
				else if(data == 1)
				{
					alert('쿠폰을 모두 받으셨습니다.');
				}
			},
			error : function(err){
				alert("에러 발생");
			}
		});
	}
 </script>
</body>
</html>