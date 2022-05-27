
## 프로젝트명
Forallshoppingmall BookStore

<hr>

## 사용된 기술
> Programming Language : Java 1.8
> <br><br>
> DBMS : MySQL(4.0 Version)
> <br><br>
> ORM : Hibernate
> <br><br>
> FrameWork : SpringFrameWork(5.1.5 Version)
> <br><br>
> Pattern : MVC (View : JSP)
> <br><br>
> 배포 환경 : AWS EC2 Linux (t3.xlarge) <br>

## 데이터 입출력 구조
> <img src = "https://user-images.githubusercontent.com/57096651/170730567-eeb186fe-27ba-4dbf-ad2b-4b326ed2ba1e.PNG" width = "80%">

## 동영상 시연
> * SpringSecurity에 의해 Request Filtering -> LoginForm
> <img src = "https://user-images.githubusercontent.com/57096651/169265448-c6c36d2c-78b4-4b17-9e85-cc30674fb393.gif" width = "50%">

> * 책 상세보기 / 리뷰 댓글
> <img src = "https://user-images.githubusercontent.com/57096651/169265770-fe255588-834c-4c4c-b6b2-706f266e2659.gif" width = "50%">

> * 장바구니 클릭 / 주문 페이지 이동
> <img src = "https://user-images.githubusercontent.com/57096651/169266055-ba8ded04-2730-4884-acc5-b0f5b44d08cd.gif" width = "50%">

> * 결제
> <img src = "https://user-images.githubusercontent.com/57096651/169266261-ba2ae107-167d-4d8a-a7ad-c2c8edad60ed.gif" width = "50%">

> * 결제 결과(네이버 영수증)
> <img src = "https://user-images.githubusercontent.com/57096651/170526844-0b5ec4c2-8a7b-40a2-9365-b71d3e834fa8.PNG" width = "50%">

> * 네이버 로그인 연동
> <img src = "https://user-images.githubusercontent.com/57096651/169266585-7883dd3e-54a4-4a8b-81c3-4fdf756f2da4.gif" width = "50%">


## 프로젝트 기능

### 1. 결제
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%95%84%EC%9E%84%ED%8F%AC%ED%8A%B8%EC%84%9C%EB%B2%84.png" width = "60%">
<br>

> * 휴대폰 결제 / 카드 결제 / 무통장 입금 / 실시간 이체
> * 아임포트 서버 API 연동
> * 주문 정보(Client) -> 아임포트 서버 -> PG 서버 -> 카드사 서버 (요청의 흐름)

### 2. REST
> * AJAX Request -> ResponseEntity Response
> * Development Tool : PostMan

### 3. 보안
> * Spring Security FrameWork 적용
> * 권한 : 유저(ROLE_USER in MySQL) / 관리자(ROLE_ADMIN in MySQL)
> * CSRF ATTACK 방어
> * AWS의 HTTPS ssl 인증서 적용

### 4.네이버 로그인 연동
> * 네아로(Naver Login API) 적용
> * 네이버 로그인 -> Naver Developers에서 발급해준 토큰값이 맞다면,CallBack URL Redirect(스프링 시큐리티 권한 승인) 

### 5. 배포 환경
> * AWS EC2(Tomcat 설치)
> * 도서의 이미지 파일은 S3 FileSystem 업로드 / 저장
> * LoadBalancer 적용(확인 방법 : chrome dev tool에서 접속 IP 확인)

## DB 설계
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/forallshoppingmall+%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4.PNG" width = "60%">



## AWS 배포 사진
### PC

<h4>홈페이지</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(6).png" width = "800" height = "400">
<br>

<h4>홈페이지 목록</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(7).png" width = "800" height = "400">
<br>

<h4>상품탭</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(8).png" width = "800" height = "400">
<br>

<h4>책 목록</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(12).png" width = "800" height = "400">

<h4>책 상세보기1</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(13).png" width = "800" height = "400">

<h4>책 상세보기2</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(14).png" width = "800" height = "400">

<h4>리뷰</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(15).png" width = "800" height = "400">

<h4>장바구니</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(16).png" width = "800" height = "400">

<h4>KG 이니시스 결제창</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(17).png" width = "800" height = "400">

<h4>결제 메일</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(29).png" width = "800" height = "400">

<h4>결제 정보1</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(25).png" width = "800" height = "400">

<h4>결제 정보2</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(20).png" width = "800" height = "400">

<h4>환불 페이지(관리자 권한)</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(21).png" width = "800" height = "400">

<h4>책 등록(관리자 권한)</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(22).png" width = "800" height = "400">

<h4>로그인 창</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(24).png" width = "800" height = "400">

<h4>주문 테이블</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(27).png" width = "800" height = "400">

<h4>주문책 테이블</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7(28).png" width = "800" height = "400">


### Mobile

<h4>홈페이지</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-01-08.png" width = "400" height = "600">
<br>

<h4>로그인</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-01-35.png" width = "400" height = "600">
<br>

<h4>로그인 이후</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-01-54.png" width = "400" height = "600">
<br>

<h4>상품탭</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-02-29.png" width = "400" height = "600">
<br>

<h4>책 목록</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-03-07.png" width = "400" height = "600">
<br>

<h4>책 상세보기(1)</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-04-03.png" width = "400" height = "600">
<br>

<h4>책 상세보기(2)</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-03-55.png" width = "400" height = "600">
<br>

<h4>리뷰 댓글</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-16-37.png" width = "400" height = "600">
<br>

<h4>리뷰 작성</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-17-37.png" width = "400" height = "600">
<br>

<h4>장바구니</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-25-34.png" width = "400" height = "600">
<br>

<h4>결제창</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-27-35.png" width = "400" height = "600">
<br>

<h4>주문 정보</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-28-16.png" width = "400" height = "600">
<br>

<h4>고객 환불</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-29-11.png" width = "400" height = "600">
<br>

<h4>책 등록</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-29-26.png" width = "400" height = "600">
<br>

<h4>이달의 책 등록</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-29-38.png" width = "400" height = "600">
<br>

<h4>오늘의 책 등록</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-29-48.png" width = "400" height = "600">
<br>

<h4>책 검색</h4>
<img src = "https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/githubimage/mobile/Capture%2B_2020-07-03-17-30-07.png" width = "400" height = "600">
<br>

<hr>

<h3>API 웹사이트</h3>
아임포트 DOCS : https://docs.iamport.kr <br>
Naver Developers : https://developers.naver.com/main <br>
스윗 트랙커(택배 조회 API) : https://tracking.sweettracker.co.kr
