<%@ page import = "java.util.*,spring.myapp.shoppingmall.dto.Goods" language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>책 상세보기 : ${good.name}</title>
    <link rel="icon" href="img/core-img/favicon.ico">
	<link href="/shoppingmall/resources/fontawesome/css/all.css" rel="stylesheet">
    <link rel="stylesheet" href="/shoppingmall/resources/css/core-style.css">
    <link rel="stylesheet" href="/shoppingmall/resources/style.css">
	<style>
[contenteditable=true]:empty:before{
  content: attr(placeholder);
  display: block; /* For Firefox */
}
 
div[contenteditable=true] {
  border: 1px solid #ddd;
  color : gray;
  font-size: 12px;
  width: 300px;
  padding: 5px;
}
	</style>
 <link rel="stylesheet" href="/resources/fontawesome/css/all.css">
<%@ include file = "../common/head.jsp" %>
</head>
<body>
    <!-- Search Wrapper Area Start -->
    <jsp:include page = "../common/search_wrapper.jsp"/>
    <!-- Search Wrapper Area End -->

    <!-- ##### Main Content Wrapper Start ##### -->
    <div class="main-content-wrapper d-flex clearfix">

       <jsp:include page = "../common/first_menu.jsp"/>
        <!-- Header Area End -->

        <!-- Product Details Area Start -->
        <div class="single-product-area section-padding-100 clearfix">
            <div class="container-fluid">

                <div class="row">
                    <div class="col-12">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb mt-50">
                                <li class="breadcrumb-item"><a href="/shoppingmall/home">Home</a></li>
                                <li class="breadcrumb-item"><a href="/shoppingmall/shop?bigclass=${good.bigclass}&subclass=${good.subclass}">${good.kind}</a></li>
                                <li class="breadcrumb-item active" aria-current="page">${good.name}</li>
                            </ol>
                        </nav>
                    </div>
                </div>

                <div class="row">
                    <div class="col-12 col-lg-7">
                        <div class="single_product_thumb">
                            <div id="product_details_slider" class="carousel slide" data-ride="carousel">
                                <div class="carousel-inner">
                               <!-- 
                                <ol class="carousel-indicators">
                                    <li class="active" data-target="#product_details_slider" data-slide-to="0" style="background-image: url(${good.goodsprofile});">
                                    </li>
                                    <li data-target="#product_details_slider" data-slide-to="1" style="background-image: url(/shoppingmall/resources/img/product-img/pro-big-2.jpg);">
                                    </li>
                                    <li data-target="#product_details_slider" data-slide-to="2" style="background-image: url(/shoppingmall/resources/img/product-img/pro-big-3.jpg);">
                                    </li>
                                    <li data-target="#product_details_slider" data-slide-to="3" style="background-image: url(/shoppingmall/resources/img/product-img/pro-big-4.jpg);">
                                    </li>
                                </ol>
                            	-->
                                    <div class="carousel-item active">
                                        <a class="gallery_img" href="/shoppingmall/resources/img/product-img/pro-big-1.jpg">
                                            <img class="d-block w-100" src="${good.goodsprofile}" alt="First slide" style = "width:300px;height:935px">
                                        </a>
                                    </div>
                                    <!-- 
                                    <div class="carousel-item">
                                        <a class="gallery_img" href="/shoppingmall/resources/img/product-img/pro-big-2.jpg">
                                            <img class="d-block w-100" src="/shoppingmall/resources/img/product-img/pro-big-2.jpg" alt="Second slide">
                                        </a>
                                    </div>
                                    <div class="carousel-item">
                                        <a class="gallery_img" href="/shoppingmall/resources/img/product-img/pro-big-3.jpg">
                                            <img class="d-block w-100" src="/shoppingmall/resources/img/product-img/pro-big-3.jpg" alt="Third slide">
                                        </a>
                                    </div>
                                    <div class="carousel-item">
                                        <a class="gallery_img" href="/shoppingmall/resources/img/product-img/pro-big-4.jpg">
                                            <img class="d-block w-100" src="/shoppingmall/resources/img/product-img/pro-big-4.jpg" alt="Fourth slide">
                                        </a>
                                    </div>
                                     -->
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-12 col-lg-5">
                        <div class="single_product_desc">
                            <div class="product-meta-data">
                                <div class="line"></div>
                                <p class="product-price">${good.price}원</p>
                                <a href="#">
                                    <h6>${good.name}</h6>
                                </a>
                                
                                
                              <c:if test = "${good.remain ne 0}">
                                <p class="avaibility"><i class="fa fa-circle"></i><span style = "font-weight:bold;font-size:20px;">재고 : ${good.remain}</span></p>
                              </c:if>
                              <c:if test = "${good.remain eq 0}">
                              	<p>재고 없음 : ${good.remain}</p>
                              </c:if>
                            </div>
                            	<c:if test = "${good.kind eq 'book'}">
               	  					<div class="short_overview my-5">
                        				<h4>전체 쪽수</h4>
                        				<p>${good.page}&nbsp;쪽</p>
                  					</div>	
               					</c:if>
               					
               					<div class="ratings-review mb-15 d-flex align-items-center justify-content-between">
                                    <div class="ratings">
                                        <h4>추천</h4>
                                      <c:if test = "${good.recommend < 20}">
                                        <c:forEach begin = "1" end = "1">
                                        	<i class="fa fa-star" aria-hidden="true"></i>
                                       </c:forEach>
                                      </c:if>
                                      <c:if test = "${good.recommend >= 20 and good.recommend < 40}">
                                      	<c:forEach begin = "1" end = "2">
                                        	<i class="fa fa-star" aria-hidden="true"></i>
                                       </c:forEach>
                                      </c:if>
                                      <c:if test = "${good.recommend >= 40 and good.recommend < 60}">
                                      	<c:forEach begin = "1" end = "3">
                                        	<i class="fa fa-star" aria-hidden="true"></i>
                                       </c:forEach>
                                      </c:if>
                                      <c:if test = "${good.recommend >= 60 and good.recommend < 80}">
                                      	<c:forEach begin = "1" end = "4">
                                        	<i class="fa fa-star" aria-hidden="true"></i>
                                       </c:forEach>
                                      </c:if>
                                      <c:if test = "${good.recommend >= 80 and good.recommend < 100}">
                                      	<c:forEach begin = "1" end = "5">
                                        	<i class="fa fa-star" aria-hidden="true"></i>
                                       </c:forEach>
                                      </c:if>
                                      <c:if test = "${good.recommend >= 100}">
                                      	<c:forEach begin = "1" end = "6">
                                        	<i class="fa fa-star" aria-hidden="true"></i>
                                       </c:forEach>
                                      </c:if>
                                    </div>
                                   </div>
							<!--               				
               				<c:if test = "${not empty good.tcontent}">
							<div class="short_overview my-5">
                            	<h4>목차</h4>
                                <p>${good.tcontent}</p>
                            </div>
                            </c:if>
							-->               				
							<c:if test = "${not empty good.writer}">
							<div class="short_overview my-5">
                            	<h4>저자</h4>
                                <p>${good.writer}</p>
                            </div>
                            </c:if>
							<c:if test = "${not empty good.wcompany}">
							<div class="short_overview my-5">
                            	<h4>출판사</h4>
                                <p>${good.wcompany}</p>
                            </div>
                            </c:if>
                            
                            <c:if test = "${good.kind eq 'shoes'}">
                            <div class="short_overview my-5">
                            	<h4>성별</h4>
                              	<p>
                              		<select name="sex" style = "padding-bottom:3px;">
                              			<option value="man">남자</option>
                              			<option value="woman">여자</option>
                              		</select>
								</p>
                            </div>
                            <br>
                            </c:if>
                                <div class="cart-btn d-flex mb-50" style = "text-align:left;">
                                <c:if test = "${good.remain ne 0}">
                                    <p>주문량</p>
                                    <div class="quantity">
                                        <span class="qty-minus" onclick="minusfunc()"><i class="fa fa-caret-down" aria-hidden="true"></i></span>
                                        <input type="text" class="qty-text" id="qty" step="1" min="1" max="300" name="quantity" value="1" style = "width:45px;" >
                                        <span class="qty-plus" onclick="plusfunc()"><i class="fa fa-caret-up" aria-hidden="true"></i></span>
                                    	<input type = "hidden" name = "qty" id = "hqty">
                                    	<input type = "hidden" name = "goods_id" value = "${good.id}">
                                    	<input type = "hidden" name = "price" value = "${good.price}">
                                    	<input type = "hidden" name = "name" value = "${good.name}">
                                    	<input type = "hidden" name = "check" value = "cartcheck">
                                    </div>
                                </c:if>
                                </div>
                               
                                <c:if test = "${good.remain ne 0 and not empty sessionScope.Userid}">
                                	<button type="submit" style = "width:300px;height:90px;"name="addtocart" value="5" class="btn amado-btn" onclick = "cart('${good.id}','${good.price}','${good.name}',document.getElementById('qty').value,'${sessionScope.Userid}','${good.goodsprofile}')">주문하기</button>
                                	<br><br>
                                																											
                                </c:if>
                          
                          		<c:if test = "${good.remain ne 0 and not empty sessionScope.Userid }">
                             		<button class = "btn amado-btn" style = "width:300px;height:90px;" onclick = "shoppingbasket('${sessionScope.Userid}',${good.remain},${good.id},${good.price},'${good.name}',document.getElementById('qty').value,'${good.goodsprofile}')">장바구니</button>
                          		</c:if>
                          		<br>
                          		<br>
                        </div>
                    </div>
                </div>
                
                <!--------------------------------------- 코드 작성 --------------------------------------------->
                <!--  
                <div class = "row">
                	<div class = "col-12 col-lg-7">
                		<div class = "container">
                			<div style = "text-align:left;display:inline;">
                				<a href = "#" class = "btn amado-btn">좋아요</a>&nbsp;&nbsp;&nbsp;&nbsp;
							 <span style = "margin-left:400px;">
								<a href = "#" class = "btn amado-btn">싫어요</a>
							 </span>
							</div>
                		</div>
                	</div>
                </div>
                -->
                  
               	  <div class="row" style = "margin-top:30px;">
               	  	<c:if test = "${good.kind eq 'book' and not empty good.goodscontent}">
                        <h4>책소개</h4>
                    </c:if>
                    <c:if test = "${good.kind != 'book' and not empty good.goodscontent}">
                    	<h4>제품 소개</h4>
                    </c:if>
                        <p>${good.goodscontent}</p>
                  </div>	
               	
               	<c:if test = "${good.kind eq 'book' and not empty good.writerintroduction}">
               	  <div class="row" style = "margin-top:30px;">
                        <h4>저자소개</h4>
                        <p>${good.writerintroduction}</p>
                  </div>	
               	</c:if>
               	
               	<c:if test = "${good.kind eq 'book' and not empty good.review}">
               	  <div class="row" style = "margin-top:30px;">
                        <h4>출판사 서평</h4>
                        <p>${good.review}</p>
                  </div>	
               	</c:if>
             	
             	<c:if test = "${good.kind eq 'book' and not empty good.summary}">
               	  <div class="row" style = "margin-top:30px;">
                        <h4>줄거리</h4>
                        <p>${good.summary}</p>
                  </div>	
               	</c:if>
               	
               	<c:if test = "${good.kind eq 'book' and not empty good.tcontent}">
                  <div class="row" style = "margin-top:30px;">
                        <h4>목차</h4>
                        <p>${good.tcontent}</p>
                  </div>
                </c:if>
                 <div class="row" style = "margin-top:30px;">
						<h4>추천하기</h4>
                			<p style = "margin-left:30px;">
                				<a onclick = "productrecommend(${good.id},'${good.name}')"><i style="font-size:30px;" class="far fa-thumbs-up"></i></a>&nbsp;&nbsp;&nbsp;&nbsp;
							</p>
                 </div>
                
                  <div class="row">
                    <div class="col-12 col-lg-7">
                		  <div class = "container">
                		  <br>
                		  <br>
                		  	<div id="afterreply" style = "text-align:left">		
					<c:if test = "${empty list}">
						<div>
							<div>
								<table class='table'>
								  <tr>
								    <td>
										<h4 style = "text-align:center;"><strong>등록된 리뷰 댓글이 없습니다.</strong></h4>
								    </td>
								  </tr>
								</table>
							</div>
						</div>
					</c:if>
					<c:if test = "${not empty list}">
						<c:forEach items = "${list}" var = "dto">			
									<div style = "width:800px;border-top:1px solid gray;border-bottom:1px solid gray;margin:5px;">
   										<div class = "jumbotron"> 
   										 <span style = "color:red;">아이디</span>&nbsp;&nbsp;
   										 <div>
   										 <span style = "font-size:24px">
											${dto.user_id}
    									 </span>
    									    <hr>
    									 </div>
    									 <span style = "vertical-align:middle;color:red;font-size:17px;">리뷰 글</span>&nbsp;&nbsp;
     									<div id = "${dto.rid}" style = "text-align:left;font-size:20px;">
											<span id = "spancontent">${dto.content}</span>
     									</div>
     									</div>
							</div>
							<c:if test = "${sessionScope.Userid eq dto.user_id}">
								 <br><br>
								 <div style = "text-align:left;"> 
								  <a id = "modifier${dto.rid}" class = "btn amado-btn" onclick = "replymodify('${dto.rid}','${good.name}',${good.id})">수정</a>&nbsp;&nbsp;
								  <a id = "deleter${dto.rid}" class = "btn amado-btn" onclick = "replydelete('${dto.rid}','${good.name}',${good.id})">삭제</a>
								</div>
								
     						</c:if>
						</c:forEach>	
					 </c:if>			
  		  					</div> 
  		  					<br>
  		  					<br>
  		  					<br>
		  					<input type = "hidden" id = "userid" value = "${sessionScope.Userid}">
		  						<div style = "padding-top : 20px;">
									<span style = "color:red;margin-right:10px;">내 아이디 </span><span style = "font-weight:bold;">${sessionScope.Userid}</span>
		  						<div placeholder ="리뷰 댓글을 달아주세요." id = "reply" style = "font-size:20px;text-align:left;padding:20px;width:800px;border:1px solid black;" contentEditable = "true"></div><br><br>
  		   						<div style = "text-align:center;">
  		   							<input type = "button" style = "font-size:30px;" class = "btn amado-btn" onclick = "reply('${sessionScope.Userid}','${good.name}',${good.id})" value = "리뷰 등록">
		   						</div>
		   						<br>
		  						</div>
  						 	<input type = "hidden" id = "remain" value = "${good.remain}">
  						  
  						  	<nav aria-label="navigation">
                            <ul class="pagination justify-content-end mt-50">        
        						<c:if test="${ curPageNum > 5 }">
            						<li class="page-item"><a class="page-link" style = "color:black;" href="/shoppingmall/product?page=${ blockStartNum - 1 }&gId='${good.name}'">◀</a></li>
        						</c:if>
        
        						<c:forEach var="i" begin="${ blockStartNum }" end="${ blockLastNum }">
            						<c:choose>
            							<c:when test="${ i > lastPageNum }">
            								
            							</c:when>
                						<c:when test="${ i == curPageNum }">
                    						<li class="page-item active" style = "color:black;">${ i }</li>
                						</c:when>
                						<c:otherwise>
                    						<li class="page-item"><a class="page-link" style = "color:black;" href="/shoppingmall/product?goods_id=${good.id}&page=${ i }&gId='${good.name}'">${ i }</a></li>
                						</c:otherwise>
            						</c:choose>
            						&nbsp;&nbsp;
        						</c:forEach>
 
        						<c:if test="${ lastPageNum > blockLastNum }">
            						<li class="page-item"><a class="page-link" style = "color:black;" href="/shoppingmall/product?page=${ blockLastNum + 1 }&gId='${good.name}'>">▶</a></li>
        						</c:if>
    						</ul>
                     	</nav>
                        </div>
                  	</div>
                </div>
              <!--------------------------------------- 코드 작성 --------------------------------------------->  
            </div>
        </div>
        <!-- Product Details Area End -->
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
                            <a href="/shoppingmall/home"><img src="/shoppingmall/resources/img/core-img/logo2.png" alt=""></a>
                        </div>
                        <!-- Copywrite Text -->
                        <p class="copywrite"><!-- Link back to Colorlib can't be removed. Template is licensed under CC BY 3.0. -->
Copyright &copy;<script>document.write(new Date().getFullYear());</script> All rights reserved | This template is made with <i class="fa fa-heart-o" aria-hidden="true"></i> by <a href="https://colorlib.com" target="_blank">Colorlib</a> & Re-distributed by <a href="https://themewagon.com/" target="_blank">Themewagon</a>
<!-- Link back to Colorlib can't be removed. Template is licensed under CC BY 3.0. --></p>
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
                                            <a class="nav-link" href="/shoppingmall/showbasket">장바구니</a>
                                        </li>
                                        <c:if test = "${not empty sessionScope.Userid }">
											<li class = "nav-item"><a class="nav-link" href="/shoppingmall/showorder">주문 정보</a></li>
										</c:if>
                                        <li class="nav-item">
                                             <c:if test ="${not empty sessionScope.Userid}">
                    							<a class="nav-link" href="/shoppingmall/logout">로그아웃</a>
                							</c:if>
                                        </li>
                                    </ul>
                                </div>
                            </nav>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </footer>
    <!-- ##### Footer Area End ##### -->
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
    <script src="/shoppingmall/resources/js/menu/product.js"></script>
    <script>
    	if("${sessionScope.Userid}" != "" && localStorage.getItem("Userid") == null){
			localStorage.setItem("Userid","${sessionScope.Userid}");
		}
		else if("${sessionScope.Userid}" == "" && localStorage.getItem("Userid") != null){
			localStorage.removeItem("Userid");
		}
    </script>
    <script>
  /*
	function getCommentList() // DB에서 글 다 빼오기
	{
				$.ajax({
					type : 'GET',
					url : "/shoppingmall/commentList",
					data : {
						gId : "${good.name}"
					},
					success : function(data) {
						var html = "";
						if (data.length > 0)
						{
							for (i = 0; i < data.length; i++) {
								html += "<div class = 'container'>" +"<span style = 'font-size:24px;'>"+ data[i].user_id +"</span>";
								html += "<div id = "
										+ "'"
										+ data[i].rId
										+ "'"
										+ " style = 'font-size:20px;padding:20px;border:1px solid black' class = 'container'>";
								html += data[i].content + "</div><br>";

								if ("${sessionScope.Userid}" == data[i].user_id) {
									html += "<a id = "
											+ "'"
											+ "modifier"
											+ data[i].rId
											+ "'"
											+ " class = 'btn amado-btn' onclick='replymodify("
											+ data[i].rId + ")'>수정</a>&nbsp;&nbsp;";
									html += "<a id = "
											+ "'"
											+ "deleter"
											+ data[i].rId
											+ "'"
											+ " class = 'btn amado-btn'  onclick='replydelete("
											+ data[i].rId + ")'>삭제</a>";
									html += "<br><br>";
								}
								html += "</div>";
							}
						} else {
							html += "<div>";
							html += "<div><table class='table'><h4 style = 'text-align:center;'><strong>등록된 리뷰 댓글이 없습니다.</strong></h4>";
							html += "</table></div>";
							html += "</div>";
						}
						$("#afterreply").html(html);
					},
					error : function(data) {
						alert('getCommentList 에러 발생');
					}

				});
	}
  */	
    </script>
  </body>
</html>