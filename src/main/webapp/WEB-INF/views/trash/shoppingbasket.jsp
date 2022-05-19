<%@ page import = "java.util.*,spring.myapp.shoppingmall.dto.Shoppingbasket" language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
  List<Shoppingbasket> goods = (ArrayList<Shoppingbasket>)request.getAttribute("list");
  List<String> goodsname = new ArrayList<String>();
  List<Integer> goodsqty = new ArrayList<Integer>();
  
  for(int i=0; i < goods.size(); i++){
	  goodsname.add("'"+((Shoppingbasket)goods.get(i)).getName()+"'");
	  //goodsname.add(((Shoppingbasket)goods.get(i)).getName());
	  goodsqty.add(((Shoppingbasket)goods.get(i)).getQty());
  	  System.out.println((String)goodsname.get(i));
  	  System.out.println((Integer)goodsqty.get(i));
  }
  int subtotal = 0;
  int total = 0;
  
  if(goods.size() > 0){
    subtotal = 0;
	total = 0;
	int sum = 0;
	
	for(int i = 0; i < goods.size(); i++)
		sum += goods.get(i).getPrice() * goods.get(i).getQty();
	subtotal = sum;
	total = sum;
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- The above 4 meta tags *must* come first in the head; any other head content must come *after* these tags -->

    <!-- Title  -->
    <title>장바구니</title>

    <!-- Favicon  -->
    <link rel="icon" href="/shoppingmall/resources/img/core-img/favicon.ico">

    <!-- Core Style CSS -->
    <link rel="stylesheet" href="/shoppingmall/resources/css/core-style.css">
    <link rel="stylesheet" href="/shoppingmall/resources/style.css">
    <script src="/shoppingmall/resources/js/order/shoppingbasket.js"></script>
<body>
    <!-- Search Wrapper Area Start -->
    <div class="search-wrapper section-padding-100">
        <div class="search-close">
            <i class="fa fa-close" aria-hidden="true"></i>
        </div>
        <div class="container">
            <div class="row">
                <div class="col-12">
                    <div class="search-content">
                      <form action="/shoppingmall/search" method="get">
                           <select name = "subject" class = "form-control">
                            	<option value = "name" style = "color:black;">서명</option>
                            	<option value = "writer" style = "color:black;">저자</option>
                            	<option value = "wcompany" style = "color:black;">출판사</option>
                            </select>
                            <br>
                            <br>
                            <input type="search" name="search" id="search" placeholder="검색어를 입력해주세요." style = "font-weight:bold;font-size:100%;">
                            <div>
                            	<button type="submit" style = "margin-left:1040px;margin-top:50px;"><img src="/shoppingmall/resources/img/core-img/search.png" alt=""></button>
                      		</div>
                      </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
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
                <a href="/shoppingmall/home"><img src="/shoppingmall/resources/img/core-img/bookstore.png" alt=""></a>
            </div>
              <br><br>
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
            <!-- Amado Nav -->
            <nav class="amado-nav">
                <ul>
                    <li><a href="/shoppingmall/home">홈페이지</a></li>
                    <li><a href="/shoppingmall/shop">상품</a></li>
                    <li class="active"><a href="/shoppingmall/showbasket">장바구니</a></li>
                    <c:if test = "${not empty sessionScope.Userid }">
							<li><a href="/shoppingmall/showorder">주문 정보</a></li>
					</c:if>
					<c:if test = "${sessionScope.Userid eq 'admin'}">
						<li><a href="/shoppingmall/admin/adminrefund">고객 환불</a>
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
                    <c:if test ="${not empty sessionScope.Userid}">
                    	<li><a href="/shoppingmall/logout">로그아웃</a></li>
                	</c:if>
                </ul>
            </nav>
            <!-- Button Group -->
            <!--  
            <div class="amado-btn-group mt-30 mb-100">
                <a href="#" class="btn amado-btn mb-15">%Discount%</a>
                <a href="#" class="btn amado-btn active">New this week</a>
            </div>
            -->
            <!-- Cart Menu -->
            <div class="cart-fav-search mb-100">
                <a href="/shoppingmall/showbasket" class="cart-nav"><img src="/shoppingmall/resources/img/core-img/cart.png" alt="">장바구니</a>
                <a href="#" class="search-nav"><img src="/shoppingmall/resources/img/core-img/search.png" alt="">상품 검색</a>
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

        <div class="cart-table-area section-padding-100">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-12 col-lg-8">
                        <div class="cart-title mt-50">
                            <h2>선택 품목</h2>
                        </div>
						 <div class="cart-table clearfix">
						  <div class = "container">
						   <div class = "row">
                            <table class="table table-responsive" style = "text-align:center;">
                                <thead>
                                    <tr>
                                    	<th>물품사진</th>
                                        <th>물품명</th>
                                        <th>가격</th>
                                        <th>개수</th>
                                        <th>삭제</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                 <c:when test = "${not empty list}">
                                  <c:forEach items = "${list}" var = "dto">
                                    <tr id = "${dto.pnum}">
                                        <td>
                                            <img src="${dto.thumbnail}" alt="Product">
                                        </td>
                                        <td>
                                            <span style = "font-weight:bold;">${dto.name}</span>
                                        </td>
                                        <td id = "tdprice">
                                            <span id = "price">${dto.price}</span>
                                        </td>
                                        <td id = "tdqty">
                                            <span id = "qty">${dto.qty}</span>
                                        </td>
                                        <td>
                                        	<span><a class="btn amado-btn" href = "#" onclick = "DeleteShoppingCart(${dto.pnum})">삭제</a></span>
                                        </td>
                                    </tr>
                                   </c:forEach>
                                 </c:when>
                                 <c:otherwise>
                               		<tr>
                               			<td colspan = "5" style = "font-weight:bold;margin-left:350px;">물품이 없습니다.</td>
                               		</tr>  
                                 </c:otherwise>
                               </c:choose>
                                </tbody>
                            </table>
                            </div>
                            <br>
                            <button class = "btn amado-btn" onclick = "DeleteAllShoppingCart('${sessionScope.Userid}')">모든 상품 삭제하기</button>
                            <br>
                            <br>
                            <br>
                            
                          <div class = "container">
						   <div class = "jumbotron">
							<c:if test = "${empty state}">
								<h6 style = "text-align:right;font-size:20px;"><button class = "btn amado-btn" onclick = "jeongbo('${sessionScope.Userid}')">회원 정보로 입력</button></h6>
						    </c:if>
						    <h3 style = "text-align:center;">배송 정보</h3>
                            <table class = "table table-responsive">
                            	<tr>
                            		<th><label>수령인</label></th>
                            		<td><input type = "text" name = "name" id = "sname" class="form-control" style = "width:600px;" placeholder = "홍길동"></td>
                            	</tr>
                            	<tr>
                            		<th><label>배송지 주소</label></th>
                            		<td><input type = "text" name = "address" id = "saddr" style = "width:720px;" class="form-control" readonly></td>
                            	</tr>     
                            	<tr style = "margin-left:40px;text-align:left">
                            		<td><button class = "btn amado-btn" onclick = "goPopup()">주소명 입력</button></td>
                            	</tr>    
                            	<tr>
                            		<!--<td><input type = "text" name ="phone" id = "sphone" class="form-control" style = "width:400px;"></td>-->
                            		<th><label>연락처</label></th>
                            		<td><input type = "text" name = "phone" id = "sphone" class = "form-control" style = "width:600px;" placeholder = "01012345678"></td>
                            	</tr>
                            	<tr>
                            		<th><label>이메일</label></th>
                            		<td><input type = "email" name ="email" id = "semail" class="form-control" style = "width:600px;" placeholder = "forallshoppingmall@naver.com"></td>
                            	</tr>
                            	<tr>
                            		<th><label>배송메모</label></th>
                            		<td><input type = "text" name ="memo" id = "smemo" class="form-control" style = "width:600px;" placeholder = "부재시 경비실에 맡겨주세요."></td>
                            	</tr>
                            	<tr>
                            		<th><label>쿠폰번호</label></th>
                            		<td><input type = "text" name ="coupon" id = "scoupon" class="form-control" style = "width:600px;"></td>
                            	</tr>
                            	<tr style = "margin-left:40px;text-align:left">            
									<td><button class = "btn amado-btn" onclick = "usecoupon(document.getElementById('scoupon').value)">쿠폰 사용</button></td>             
									<input type = "hidden" id = "CouponId">
								</tr>    
                            </table>
                          </div>
                         </div>
                        </div>
                       </div>
                    </div>
                    <div class="col-12 col-lg-4">
                        <div class="cart-summary">
                            <h5>주문 가격</h5>
                            <ul class="summary-table">
                                <li><span>세금 적용 이전</span> <span id = "subtotal"><%= subtotal %></span></li>
                                <li><span>세금</span> <span>무료</span></li>
                                <li><span>총 주문 가격</span> <span id = "total"><%= total %></span></li>
                            </ul>
                              <input type = "hidden" id = "tmoney" value = "<%=total%>">
                            <div class="cart-btn mt-100">
                                <button class="btn amado-btn w-100" onclick = "paymentPhone('${sessionScope.Userid}')">휴대폰결제</button><br>
                                <button class="btn amado-btn w-100" onclick = "paymentCard('${sessionScope.Userid}')">카드결제</button><br>
                                <button class="btn amado-btn w-100" onclick = "paymentWithOutDeposit('${sessionScope.Userid}')">무통장입금</button><br>
                                <button class="btn amado-btn w-100" onclick = "paymentWithDeposit('${sessionScope.Userid}')">실시간계좌이체</button><br>
                            </div>
                        </div>
                    </div>
                </div>
         	</div>
        </div>
    </div>
    <!-- ##### Main Content Wrapper End ##### -->

    <!-- ##### Newsletter Area Start ##### -->
    <section class="newsletter-area section-padding-100-0">
        <div class="container">
            <div class="row align-items-center">
                <!-- Newsletter Text -->
                <div class="col-12 col-lg-6 col-xl-7">
                    <div class="newsletter-text mb-100">
                        <h2>Subscribe for a <span>25% Discount</span></h2>
                        <p>Nulla ac convallis lorem, eget euismod nisl. Donec in libero sit amet mi vulputate consectetur. Donec auctor interdum purus, ac finibus massa bibendum nec.</p>
                    </div>
                </div>
                <!-- Newsletter Form -->
                <div class="col-12 col-lg-6 col-xl-5">
                    <div class="newsletter-form mb-100">
                        <form action="#" method="post">
                            <input type="email" name="email" class="nl-email" placeholder="Your E-mail">
                            <input type="submit" value="Subscribe">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <!-- ##### Newsletter Area End ##### -->

    <!-- ##### Footer Area Start ##### -->
    <footer class="footer_area clearfix">
        <div class="container">
            <div class="row align-items-center">
                <!-- Single Widget Area -->
                <div class="col-12 col-lg-4">
                    <div class="single_widget_area">
                        <!-- Logo -->
                        <div class="footer-logo mr-50">
                            <a href="/shoppingmall/resources/index.html"><img src="/shoppingmall/resources/img/core-img/logo2.png" alt=""></a>
                        </div>
                        <!-- Copywrite Text -->
                        <p class="copywrite"><!-- Link back to Colorlib can't be removed. Template is licensed under CC BY 3.0. -->
Copyright &copy;<script>document.write(new Date().getFullYear());</script> All rights reserved | This template is made with <i class="fa fa-heart-o" aria-hidden="true"></i> by <a href="https://colorlib.com" target="_blank">Colorlib</a>
<!-- Link back to Colorlib can't be removed. Template is licensed under CC BY 3.0. -->& Re-distributed by <a href="https://themewagon.com/" target="_blank">Themewagon</a>
</p>
                    </div>
                </div>
                <!-- Single Widget Area -->
                <div class="col-12 col-lg-8">
                    <div class="single_widget_area">
                        <!-- Footer Menu -->
                        <div class="footer_menu">
                            <nav class="navbar navbar-expand-lg justify-content-end">
                                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#footerNavContent" aria-controls="footerNavContent" aria-expanded="false" aria-label="Toggle navigation"><i class="fa fa-bars"></i></button>
                                <div class="collapse navbar-collapse" id="footerNavContent">
                                    <ul class="navbar-nav ml-auto">
                                        <li class="nav-item active">
                                            <a class="nav-link" href="/shoppingmall/home">홈페이지</a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" href="/shoppingmall/shop">상품</a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" href="/shoppingmall/product">상품 상세보기</a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" href="/shoppingmall/showbasket">장바구니</a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" href="/shoppingmall/logout">로그아웃</a>
                                        </li>
                                    </ul>
                                </div>
                            </nav>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <form id="form" name="form" method="post">
			<input type="hidden" id="confmKey" name="confmKey" value=""/>
			<input type="hidden" id="returnUrl" name="returnUrl" value=""/>
			<input type="hidden" id="resultType" name="resultType" value=""/>
		<!-- 해당시스템의 인코딩타입이 EUC-KR일경우에만 추가 START--> 
		<!-- 
		<input type="hidden" id="encodingType" name="encodingType" value="EUC-KR"/>
		 -->
		<!-- 해당시스템의 인코딩타입이 EUC-KR일경우에만 추가 END-->
		</form>
    </footer>
    <!-- ##### Footer Area End ##### -->

    <!-- ##### jQuery (Necessary for All JavaScript Plugins) ##### -->
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <!-- Popper js -->
    <script src="/shoppingmall/resources/js/popper.min.js"></script>
    <!-- Bootstrap js -->
    <script src="/shoppingmall/resources/js/bootstrap.min.js"></script>
    <!-- Plugins js -->
    <script src="/shoppingmall/resources/js/plugins.js"></script>
    <!-- Active js -->
    <script src="/shoppingmall/resources/js/active.js"></script>
    <script type="text/javascript" src="https://cdn.iamport.kr/js/iamport.payment-1.1.5.js"></script>
    <script type="text/javascript" src="http://www.turnjs.com/lib/turn.min.js"></script>
	<script>
	/*
		function test(){
			$.ajax({
				type : "GET",
				url:"/shoppingmall/test?param=" + list,
				success : function(){
					alert('성공');
				},
				error : function(){
					alert('실패');
				}
			});
		}
	*/
	function goPopup(){
		// 호출된 페이지(jusopopup.jsp)에서 실제 주소검색URL(http://www.juso.go.kr/addrlink/addrLinkUrl.do)를 호출하게 됩니다.
	    var pop = window.open("/shoppingmall/jusoPopup","pop","width=570,height=420, scrollbars=yes, resizable=yes"); 
	    
		// 모바일 웹인 경우, 호출된 페이지(jusopopup.jsp)에서 실제 주소검색URL(http://www.juso.go.kr/addrlink/addrMobileLinkUrl.do)를 호출하게 됩니다.
	    //var pop = window.open("/popup/jusoPopup.jsp","pop","scrollbars=yes, resizable=yes"); 
	}
	/** API 서비스 제공항목 확대 (2017.02) **/
	function jusoCallBack(roadFullAddr,roadAddrPart1,addrDetail,roadAddrPart2,engAddr, jibunAddr, zipNo, admCd, rnMgtSn, bdMgtSn
							, detBdNmList, bdNm, bdKdcd, siNm, sggNm, emdNm, liNm, rn, udrtYn, buldMnnm, buldSlno, mtYn, lnbrMnnm, lnbrSlno, emdNo){
		// 팝업페이지에서 주소입력한 정보를 받아서, 현 페이지에 정보를 등록합니다.
	/*	document.form.roadAddrPart1.value = roadAddrPart1;
		document.form.roadAddrPart2.value = roadAddrPart2;
		document.form.addrDetail.value = addrDetail;
		document.form.zipNo.value = zipNo;*/
		document.getElementById("saddr").value = roadFullAddr;
	}
		function DeleteShoppingCart(pnum){
			$.ajax({
		        type:"POST",
		        url:"/shoppingmall/deleteshoppingcart",
		        data : {pnum : pnum},
		        success: function(data){
		            alert("삭제하였습니다 " + data);
		            $("#subtotal").html($("#subtotal").html() - ($("#"+pnum).find('td#tdprice').find('span#price').html()) * ($("#"+pnum).find('td#tdqty').find('span#qty').html()));
		        	$("#total").html($("#total").html() - ($("#"+pnum).find('td#tdprice').find('span#price').html()) * ($("#"+pnum).find('td#tdqty').find('span#qty').html()));
		            $("#"+pnum).remove();
		            location.href = "/shoppingmall/showbasket";
		        },
		        error: function(xhr, status, error) {
		            alert(error);
		        }  
		    });
		}
		
		function DeleteAllShoppingCart(Id){
			$.ajax({
				type:"POST",
				url:"/shoppingmall/deleteallshoppingcart",
				data : {"Id" : Id},
				success : function(){
					alert("모든 상품 삭제");
					location.href = "/shoppingmall/showbasket";
				},
				error : function(){
					alert("오류 발생");
				}
			});
		}
	</script>
	<script>
	/*
	if("${alertsystem}") {
		alert('장바구니 담기에 실패했습니다.재고량을 확인해주세요.');
	}
	*/
		//var remaincheck = null;
		var generateRandom = function (min, max){
		  var ranNum = Math.floor(Math.random()*(max-min+1)) + min;
		  return ranNum;
		}
		var couponcheck = 0;
		var user_merchant = null;
		booknamelist = <%=goodsname%>
		bookqtylist = <%=goodsqty%>
		/*
				function paymentPhone()
				{
					 if("${sessionScope.Userid}" == "")
						 location.href = "/shoppingmall/loginForm";
					var sname = document.getElementById("sname").value;
					//var sphone = document.getElementById("sphone1").value.trim()+document.getElementById("sphone2").value.trim()+document.getElementById("sphone3").value.trim();
					var sphone = document.getElementById("sphone").value.trim();
					var saddr = document.getElementById("saddr").value;
					var semail = document.getElementById("semail").value;
					var smemo = document.getElementById("smemo").value; 
					var scoupon = document.getElementById("scoupon").value.trim();
					
					if(sname == "" || sphone == "" || saddr == "" || semail == "" || smemo == "")
					{
						alert("배송 정보를 모두 기입해주세요.");
						return;
					}
					
					if(document.getElementById('tmoney').value == 0) 
					{
						alert('물품을 장바구니에 담아주세요');
					}
					
					alert('성공');
					var rnum = generateRandom(10000,99999);
					var ordernum = rnum + String(new Date().getTime());
					var obj = {"id" : "${User.id}","merchant_id" : ordernum,"phoneNumber" : sphone,"address" : saddr,
							"buyer_name" : sname,"memo" : smemo,"price" : document.getElementById("tmoney").value,
							"coupon" : scoupon} //"${User.phoneNumber}" "${User.address}"
					var IMP = window.IMP; 
					IMP.init('imp79913276');
					$.ajax({
				        type:"POST",
				        url:"/shoppingmall/InsertMerchantId",
				        data : JSON.stringify(obj),
				        headers: { "Content-Type": "application/json" },
				        success: function(data){
				        	if(data == "formupdated"){
				        		alert("폼 값을 수정하셨습니다.다시 시도해주십시오.");
				        		return;
				        	}
				        	user_merchant = data;
				        	console.log("user_merchant : " + user_merchant);
				        	alert(user_merchant);
							alert('success');
							document.onkeydown = noEvent;
							IMP.request_pay({
							    pg : 'inicis', // version 1.1.0부터 지원.
							    pay_method : 'phone',
							    merchant_uid : user_merchant,
							    name : "forallshoppingmall",
							    amount : document.getElementById('tmoney').value,
							    buyer_name : sname, //"${User.name}"
							    buyer_tel : sphone, //"${User.phoneNumber}"
							    buyer_addr : saddr, //"${User.address}"
							    buyer_email : semail,
							    m_redirect_url: "https://www.forallshoppingmall.com/shoppingmall/mobile?booknamelist=" + booknamelist + "&bookqtylist=" + bookqtylist + "&amount=" + document.getElementById('tmoney').value + "&coupon=" + document.getElementById('CouponId').value
							    //m_redirect_url: "https://www.forallshoppingmall.com/shoppingmall/mobile?listparam=" + booknamelist + "&glistparam=" + bookqtylist + "&amount=" + document.getElementById('tmoney').value + "&coupon=" + document.getElementById('CouponId').value
							}, function(rsp) {
							    if ( rsp.success ) {
							    	console.log(rsp);
						   			console.log("user_merchant : " + user_merchant);
							    	console.log("merchant_uid : " + rsp.merchant_uid);
							    	var mydata = {
							    			"imp_uid" : rsp.imp_uid,
							    			"merchant_uid" : rsp.merchant_uid, 
							    			"booknamelist" : booknamelist,
							    			"bookqtylist" : bookqtylist,
							    			"merchant_id" : user_merchant,
							    			"price" : rsp.paid_amount,
							    			"coupon" : document.getElementById("CouponId").value
							    			};
							        var msg = '결제가 완료되었습니다.';
							        msg += '고유ID : ' + rsp.imp_uid;
							        msg += '상점 거래ID : ' + rsp.merchant_uid;
							        msg += '결제 금액 : ' + rsp.paid_amount;
							        alert(msg);
							        
							        $.ajax({
							            url: "/shoppingmall/completeToken", // 가맹점 서버
							            method: "POST",
							            headers: { "Content-Type": "application/json" },
							            data: JSON.stringify(mydata)
							        }).done(function (data) {
							        	console.log(data);
							        	  switch(data.check) {
							              	case 'success':
												alert('결제 성공');
												location.href = "/shoppingmall/OrderResult?merchant_id=" + rsp.merchant_uid;
							            }
							        });
							      } else {
							    	  var merchantdata = { "merchant_id" : user_merchant }
							    	  document.onkeydown = null;
							    	  $.ajax({
							    		url : "/shoppingmall/stop",
							    		method : "POST",
							    		headers : {"Content-Type" : "application/json"},
							    		data : JSON.stringify(merchantdata),
							    		success : function(){
							    			alert("결제에 실패하였습니다");
							    		}
							    	  });
							        }
						     });
				        },error: function(xhr, status, error) {
				            alert(error);
				        }  
				    });
				}
		*/
	</script>
	<script>
	/*
	var user2_merchant = null;	
	
	function paymentCard(){		//카드 결제
	 if("${sessionScope.Userid}" == "")
		 location.href = "https://www.forallshoppingmall.com:8443/shoppingmall/loginForm";
	var sname = document.getElementById("sname").value;
	//var sphone = document.getElementById("sphone1").value.trim()+document.getElementById("sphone2").value.trim()+document.getElementById("sphone3").value.trim();
	var sphone = document.getElementById("sphone").value.trim();
	var saddr = document.getElementById("saddr").value;
	var semail = document.getElementById("semail").value;
	var smemo = document.getElementById("smemo").value; 
	var scoupon = document.getElementById("scoupon").value;
	
	if(sname == "" || sphone == "" || saddr == "" || semail == "" || smemo == "")
	{
		alert("배송 정보를 모두 기입해주세요.");
		return;
	}
	if(document.getElementById('tmoney').value == 0) 
	{
		alert('물품을 장바구니에 담아주세요');
	}
		var rnum = generateRandom(10000,99999);
		var ordernum = rnum + String(new Date().getTime());
		var obj = {"id" : "${User.id}","merchant_id" : ordernum,"phoneNumber" : sphone,"address" : saddr,
				"buyer_name" : sname,"memo" : smemo,"price" : document.getElementById("tmoney").value,
				"coupon" : scoupon}
		var IMP = window.IMP; 
		IMP.init('imp79913276');
		$.ajax({
	        type:"POST",
	        url:"/shoppingmall/InsertMerchantId",
	        data : JSON.stringify(obj),
	        headers: { "Content-Type": "application/json" },
	        success: function(data){
	        	if(data == "formupdated"){
	        		alert("폼 값을 수정하셨습니다.다시 시도해주십시오.");
	        		return;
	        	}
	        	user2_merchant = data;
	        	console.log("user2_merchant : " + user2_merchant);
	        	alert(user2_merchant);
				alert('success');
				document.onkeydown = noEvent;
				IMP.request_pay({
				    pg : 'inicis', // version 1.1.0부터 지원.
				    pay_method : 'card',
				    merchant_uid : user2_merchant,
				    name : "forallshoppingmall",
				    amount : document.getElementById('tmoney').value,
				    buyer_name : sname,
				    buyer_tel : sphone,
				    buyer_addr : saddr,
				    buyer_email : semail,
				    m_redirect_url: "https://www.forallshoppingmall.com/shoppingmall/mobile?booknamelist=" + booknamelist + "&bookqtylist=" + bookqtylist + "&amount=" + document.getElementById('tmoney').value + "&coupon=" + document.getElementById('CouponId').value
				    //m_redirect_url: "https://www.forallshoppingmall.com/shoppingmall/mobile?listparam=" + list + "&glistparam=" + glist + "&amount=" + document.getElementById('tmoney').value
				}, function(rsp) {
				    if ( rsp.success ) {
				    	console.log("rsp : " + rsp);
				    	console.log("merchant_uid : " + rsp.merchant_uid);
				    	var mydata = {
				    			"imp_uid" : rsp.imp_uid ,
				    			"merchant_uid" : rsp.merchant_uid,
				    			"booknamelist" : booknamelist,
				    			"bookqtylist" : bookqtylist,
				    			"merchant_id" : user2_merchant,
				    			"price" : rsp.paid_amount};
				        var msg = '결제가 완료되었습니다.';
				        msg += '고유ID : ' + rsp.imp_uid;
				        msg += '상점 거래ID : ' + rsp.merchant_uid;
				        msg += '결제 금액 : ' + rsp.paid_amount;
				        alert(msg);
				        msg += '카드 승인번호 : ' + rsp.apply_num;
				        $.ajax({
				            url: "/shoppingmall/completeToken", // 가맹점 서버
				            method: "POST",
				            headers: { "Content-Type": "application/json" },
				            data: JSON.stringify(mydata),
				            dataType : "json"
				        }).done(function (data) {
				        	  switch(data.check) {
				              	case 'success':
									alert('결제 성공');
									location.href = "/shoppingmall/OrderResult?merchant_id=" + rsp.merchant_uid;
				              	case 'fail':
				              		alert('결제 실패');
				            }
				        });
				      } else {
				    	  var merchantdata = { "merchant_id" : user2_merchant }
				    	  document.onkeydown = null;
				    	  $.ajax({
				    		url : "/shoppingmall/stop",
				    		method : "POST",
				    		headers : {"Content-Type" : "application/json"},
				    		data : JSON.stringify(merchantdata),
				    		success : function(){
				    			alert("결제에 실패하였습니다");
				    		}
				    	  });
				        }
			     });
	        },
	        error: function(xhr, status, error) {
	            alert(error);
	        }  
	    });
   }
	*/
	</script>
	<script>
	/*
	var user3_merchant;
	
	function paymentWithOutDeposit(){
		alert('송금자명과 배송자 정보의 이름을 동일하게 입력해주세요.');
		 if("${sessionScope.Userid}" == "")
			 location.href = "https://www.forallshoppingmall.com/shoppingmall/loginForm";
		var sname = document.getElementById("sname").value;
		//var sphone = document.getElementById("sphone1").value.trim()+document.getElementById("sphone2").value.trim()+document.getElementById("sphone3").value.trim();
		var sphone = document.getElementById("sphone").value;
		var smemo = document.getElementById("smemo").value; 
		var saddr = document.getElementById("saddr").value;
		var semail = document.getElementById("semail").value;
		var scoupon = document.getElementById("scoupon").value;
		
		if(sname == "" || sphone == "" || saddr == "" || semail == "" || smemo == "")
		{
			alert("배송 정보를 모두 기입해주세요.");
			return;
		}
		
		if(document.getElementById('tmoney').value == 0) 
		{
			alert('물품을 장바구니에 담아주세요');
		}
		var rnum = generateRandom(10000,99999);
		var ordernum = rnum + String(new Date().getTime());
		var obj = {"id" : "${User.id}","merchant_id" : ordernum,"phoneNumber" : sphone,"address" : saddr,
				"buyer_name" : sname,"memo" : smemo,"price" : document.getElementById("tmoney").value,
				"coupon" : scoupon}
		var IMP = window.IMP; 
		IMP.init('imp79913276');
		$.ajax({
	        type:"POST",
	        url:"/shoppingmall/InsertMerchantId",
	        data : JSON.stringify(obj),
	        headers: { "Content-Type": "application/json" },
	        success: function(data){
	        	if(data == "formupdated"){
	        		alert("폼 값을 수정하셨습니다.다시 시도해주십시오.");
	        		return;
	        	}
	        	user3_merchant = data;
	        	console.log("user3_merchant : " + user3_merchant);
	        	alert(user3_merchant);
				alert('success');
				document.onkeydown = noEvent;
				IMP.request_pay({
				    pg : 'inicis', // version 1.1.0부터 지원.
				    pay_method : 'vbank',
				    merchant_uid : user3_merchant,
				    name : "forallshoppingmall",
				    amount : document.getElementById('tmoney').value,  //결제 금액
				    buyer_name : sname,
				    buyer_tel : sphone,
				    buyer_addr : saddr,
				    buyer_email : semail,
				    m_redirect_url: "https://www.forallshoppingmall.com/shoppingmall/mobile?booknamelist=" + booknamelist + "&bookqtylist=" + bookqtylist + "&amount=" + document.getElementById('tmoney').value + "&coupon=" + document.getElementById('CouponId').value
				}, function(rsp) {
				    if ( rsp.success ) {
				    	console.log(rsp);
				    	console.log(rsp.vbank_holder);
				    	console.log("merchant_uid : " + rsp.merchant_uid);
				    	var mydata = {"imp_uid" : rsp.imp_uid,
				    				  "merchant_uid" : rsp.merchant_uid,
						    		  "booknamelist" : booknamelist,
						    		  "bookqtylist" : bookqtylist,
						    		  "merchant_id" : user3_merchant,
						    		  "price" : rsp.paid_amount,
				    				  "vbanknum": rsp.vbank_num,
				    				  "vbankname": rsp.vbank_name,
				    				  "vbankdate": rsp.vbank_date,
				    				  "vbankholder" : rsp.vbank_holder 
				    				  }
				    	var msg = '결제가 완료되었습니다.';
				        msg += '고유ID : ' + rsp.imp_uid;
				        msg += '상점 거래ID : ' + rsp.merchant_uid;
				        msg += '결제 금액 : ' + rsp.paid_amount;
				        msg += '카드 승인번호 : ' + rsp.apply_num;
				        alert(msg);
				        $.ajax({
				            url: "/shoppingmall/completeToken", // 가맹점 서버
				            method: "POST",
				            headers: { "Content-Type": "application/json" },
				            dataType : "json",
				            data: JSON.stringify(mydata)
				        }).done(function (data) {
				        	  switch(data.check) {
				              	case 'vbankIssued':
				              		alert('무통장 입금 결제 성공');
				              		console.log(data.data);
									location.href = "/shoppingmall/OrderResult?merchant_id=" + rsp.merchant_uid + "&vbanknum=" + data.vbanknum + "&vbankname=" + rsp.vbank_name + "&vbankholder=" + data.data.buyer_name + "&vbankcode=" + data.data.vbank_code + "&vbankdate=" + rsp.vbank_date;
				              		break;
				              	case 'success':
									alert('결제 성공');
									location.href = "/shoppingmall/OrderResult?merchant_id=" + rsp.merchant_uid;
				            }
				        });
				      } else {
				    	  var merchantdata = { "merchant_id" : user3_merchant }
				    	  document.onkeydown = null;
				    	  $.ajax({
				    		url : "/shoppingmall/stop",
				    		method : "POST",
				    		headers : {"Content-Type" : "application/json"},
				    		data : JSON.stringify(merchantdata),
				    		success : function(){
				    			alert("결제에 실패하였습니다");
				    		}
				    	  });
				        }
			     });
	        },
	        error: function(xhr, status, error) {
	            alert(error);
	        }  
	    });
	}
	*/
	</script>
	<script>
	/*
		var user4_merchant = null;
		
		function paymentWithDeposit(){
					if("${sessionScope.Userid}" == "")
						location.href = "/shoppingmall/loginForm";
					var sname = document.getElementById("sname").value;
					//var sphone = document.getElementById("sphone1").value.trim()+document.getElementById("sphone2").value.trim()+document.getElementById("sphone3").value.trim();
					var sphone = document.getElementById("sphone").value.trim();
					var smemo = document.getElementById("smemo").value; 
					var saddr = document.getElementById("saddr").value;
					var semail = document.getElementById("semail").value;
					var scoupon = document.getElementById("scoupon").value;
					
					if(sname == "" || sphone == "" || saddr == "" || semail == "")
					{
						alert("배송 정보를 모두 기입해주세요.");
						return;
					}			
					
					if(document.getElementById('tmoney').value == 0) 
					{
						alert('물품을 장바구니에 담아주세요');
					}
						
			alert('성공');
			var rnum = generateRandom(10000,99999);
			var ordernum = rnum + String(new Date().getTime());
			var obj = {"id" : "${User.id}","merchant_id" : ordernum,"phoneNumber" : "${User.phoneNumber}",
					"address" : "${User.address}","buyer_name" : sname,"memo" : smemo,
					"price" : document.getElementById("tmoney").value,
					"coupon" : scoupon}
			var IMP = window.IMP; 
			IMP.init('imp79913276');
			$.ajax({
		        type:"POST",
		        url:"/shoppingmall/InsertMerchantId",
		        data : JSON.stringify(obj),
		        headers: { "Content-Type": "application/json" },
		        success: function(data){
		        	if(data == "formupdated"){
		        		alert("폼 값을 수정하셨습니다.다시 시도해주십시오.");
		        		return;
		        	}
		        	user4_merchant = data;
		        	console.log("user4_merchant : " + user4_merchant);
		        	alert(user4_merchant);
					alert('success');
					document.onkeydown = noEvent;
					IMP.request_pay({
					    pg : 'inicis', // version 1.1.0부터 지원.
					    pay_method : 'trans',
					    merchant_uid : user4_merchant,
					    name : "forallshoppingmall",
					    amount : document.getElementById('tmoney').value,
					    buyer_name : sname,
					    buyer_tel : sphone,
					    buyer_addr : saddr,
					    buyer_email : semail,
					    m_redirect_url: "https://www.forallshoppingmall.com/shoppingmall/mobile?booknamelist=" + booknamelist + "&bookqtylist=" + bookqtylist + "&amount=" + document.getElementById('tmoney').value + "&coupon=" + document.getElementById('CouponId').value
					}, function(rsp) {
					    if ( rsp.success ) {
					    	console.log(rsp);
					    	console.log("merchant_uid : " + rsp.merchant_uid);
					    	var mydata = {
					    			"imp_uid" : rsp.imp_uid ,
					    			"merchant_uid" : rsp.merchant_uid,
					    			"booknamelist" : booknamelist,
					    			"bookqtylist" : bookqtylist,
					    			"merchant_id" : user4_merchant,
					    			"price" : rsp.paid_amount
					    			};
					        var msg = '결제가 완료되었습니다.';
					        msg += '고유ID : ' + rsp.imp_uid;
					        msg += '상점 거래ID : ' + rsp.merchant_uid;
					        msg += '결제 금액 : ' + rsp.paid_amount;
					        alert(msg);
					        msg += '카드 승인번호 : ' + rsp.apply_num;
					        $.ajax({
					            url: "/shoppingmall/completeToken", // 가맹점 서버
					            method: "POST",
					            headers: { "Content-Type": "application/json" },
					            data: JSON.stringify(mydata)
					        }).done(function (data) {
					        	  switch(data.check) {
					              	case 'success':
										alert('결제 성공');
										location.href = "/shoppingmall/OrderResult?merchant_id=" + rsp.merchant_uid;
					            }
					        });
					      } else {
					    	  var merchantdata = { "merchant_id" : user4_merchant }
					    	  document.onkeydown = null;
					    	  $.ajax({
					    		url : "/shoppingmall/stop",
					    		method : "POST",
					    		headers : {"Content-Type" : "application/json"},
					    		data : JSON.stringify(merchantdata),
					    		success : function(){
					    			alert("결제에 실패하였습니다");
					    		}
					    	  });
					        }
				     });
		        },
		        error: function(xhr, status, error) {
		            alert(error);
		        }  
		    });
		}
	*/
	</script>
	<script>
		if("${check}" != ""){
			location.href = "/shoppingmall/showbasket";
		}
	</script>
	<script>
	
	function noEvent() {
		if (event.keyCode == 116) {
			event.keyCode= 2;
			return false;
		} else if(event.ctrlKey && (event.keyCode==78 || event.keyCode == 82)) {
		  return false;
		}
	}
	</script>
  	<script>
  	/*
  		function usecoupon(cnumber)
  		{
  			if(cnumber.trim() == "")
  			{
  				alert("쿠폰 번호를 입력해주세요");
  				return;
  			}
  			if(couponcheck == 1)
  			{
  				alert('쿠폰 중복 적용은 불가능합니다.');
  				return;
  			}
  			$.ajax({
  				url : "/shoppingmall/usedcouponcheck",
  				method : "POST",
  				data : {"cnumber" : cnumber.trim()},
  				success : function(data){
  					if(data == "1"){	//쿠폰 사용한 적 있음
  						alert("이미 사용한 쿠폰입니다.");
  						return;
  					}else {
  						
  						document.getElementById("CouponId").value = cnumber.trim();
  						$.ajax({
  							url : "/shoppingmall/usecoupon",
  							method: "POST",
  							data : {"cnumber" : cnumber.trim()},
  							success : function(data) 
  							{
  								if(data == 0 && couponcheck == 0)			//5% 할인
  								{	
  									alert('쿠폰을 적용했습니다.');
  									document.getElementById('tmoney').value = $("#subtotal").html() * 0.95;
  									$("#subtotal").html($("#subtotal").html() * 0.95);
  									$("#total").html($("#total").html() * 0.95);
  									couponcheck = 1;
  								}
  								else if(data == 1 && couponcheck ==0)		//10%할인
  								{
  									alert('쿠폰을 적용했습니다.');
  									document.getElementById('tmoney').value = $("#subtotal").html() * 0.9;
  									$("#subtotal").html($("#subtotal").html() * 0.9);
  									$("#total").html($("#total").html() * 0.9);
  									couponcheck = 1;
  								}
  								else if(data == 2 && couponcheck ==0) 		//15%할인
  								{
  									alert('쿠폰을 적용했습니다.');
  									document.getElementById('tmoney').value = $("#subtotal").html() * 0.85;
  									$("#subtotal").html($("#subtotal").html() * 0.85);
  									$("#total").html($("#total").html() * 0.85);
  									couponcheck = 1;
  								}
  								else if(data == 3 && couponcheck ==0)		//20%할인
  								{
  									alert('쿠폰을 적용했습니다.');
  									document.getElementById('tmoney').value = $("#subtotal").html() * 0.8;
  									$("#subtotal").html($("#subtotal").html() * 0.8);
  									$("#total").html($("#total").html() * 0.8);
  									couponcheck = 1;
  								}
  								else if(data == 10)
  									alert("쿠폰이 존재하지 않거나 이미 사용한 쿠폰입니다");
  							},
  							error : function(err) {
  								alert('에러가 발생하였습니다.');
  							}
  						});
  						
  					}
  				}
  			});
  		}
  		*/
  	</script>
  	
 <script>
	if("${sessionScope.Userid}" != "" && localStorage.getItem("Userid") == null)	
	{
		localStorage.setItem("Userid","${sessionScope.Userid}");
	}
	else if("${sessionScope.Userid}" == "" && localStorage.getItem("Userid") != null)
	{
		localStorage.removeItem("Userid");
	}
 </script>
 <script>/*
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
 function jeongbo()
 { 
	 $.ajax({
		 url : "/shoppingmall/jeongbo",
		 data : JSON.stringify({"Id" : "${sessionScope.Userid}"}),
		 headers: { "Content-Type": "application/json" },
		 method : "POST",
		 success : function(data) {
			document.getElementById("sname").value = data.name;
			document.getElementById("saddr").value = data.address;
			document.getElementById("sphone").value = data.phone;
			//document.getElementById("sphone1").value = data.phone.substring(0,3);
			//document.getElementById("sphone2").value = data.phone.substring(3,7);
			//document.getElementById("sphone3").value = data.phone.substring(7,11);
			//document.getElementById("sphone1").value = data.phone1;
			//document.getElementById("sphone2").value = data.phone2;
			//document.getElementById("sphone3").value = data.phone3;
			document.getElementById("semail").value = data.email;
		 },
		 error : function(err){
			 alert("에러 발생");
		}
	 })
 }
 */
 </script>
  	<script>
	 if("${sessionScope.Userid}" == "")
		 location.href = "/shoppingmall/loginForm";
  	</script>
  	</body>
</html>