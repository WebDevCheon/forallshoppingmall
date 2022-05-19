package spring.myapp.shoppingmall.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import spring.myapp.shoppingmall.dto.Bookrecommend;
import spring.myapp.shoppingmall.dto.Order;
import spring.myapp.shoppingmall.dto.Reply;
import spring.myapp.shoppingmall.dto.ReviewReply;
import spring.myapp.shoppingmall.dto.Shoppingbasket;
import spring.myapp.shoppingmall.dto.User;
import spring.myapp.shoppingmall.dto.Vbank;
import spring.myapp.shoppingmall.restcontrollerexception.UserNotFindExceptionHandler;
import spring.myapp.shoppingmall.service.AdminServiceImpl;
import spring.myapp.shoppingmall.service.AwsServiceImpl;
import spring.myapp.shoppingmall.service.CouponServiceImpl;
import spring.myapp.shoppingmall.service.OrderServiceImpl;
import spring.myapp.shoppingmall.service.ProductServiceImpl;
import spring.myapp.shoppingmall.service.ReplyServiceImpl;
import spring.myapp.shoppingmall.service.RequestRefund;
import spring.myapp.shoppingmall.service.ShoppingBasketImpl;
import spring.myapp.shoppingmall.service.UserServiceImpl;

@RestController
public class ShoppingmallRestContoller {
	private static final Logger logger = LoggerFactory.getLogger(ShoppingmallRestContoller.class);
	@Autowired
	private ShoppingBasketImpl shoppingbasketimpl;
	
	@Autowired
	private UserServiceImpl userserviceimpl;
	
	@Autowired
	private ProductServiceImpl productserviceimpl; 
	
	@Autowired
	private OrderServiceImpl orderserviceimpl;
	
	@Autowired
	private RequestRefund requestrefund;
	
	@Autowired
	private AdminServiceImpl adminserviceimpl;
	
	@Autowired
	private AwsServiceImpl awsserviceimpl;
	
	@Autowired
	private CouponServiceImpl couponserviceimpl;
	
	@Autowired
	private ReplyServiceImpl replyserviceimpl;
	
	@PostMapping(value = "/cart")
	public void cart(Shoppingbasket cart,@RequestParam String userid,@RequestParam int goods_id){
		logger.info("{}",userid);
		logger.info("카트로 담은 책의 이름,카트로 담은 책의 수량 : {},{}",cart.getName(),cart.getQty());
		logger.info("책의 가격 : {}",cart.getPrice());
		shoppingbasketimpl.setshoppingbasket(cart.getQty(),goods_id,cart.getPrice(),userid,cart.getName());  //카트 담기
	}
	
	@RequestMapping(value = "/shoppingbasket",method = {RequestMethod.POST,RequestMethod.GET})
	public ResponseEntity<String> shoppingbasket(@RequestBody HashMap<String,Object> map,HttpSession session){
		   int qty;
		   String TypeCheck = map.get("qty").getClass().getName();
		   if(TypeCheck.equals("java.lang.String")){
			   qty = Integer.valueOf((String)map.get("qty"));
		   } else {
			   qty = ((Double)map.get("qty")).intValue();
		   }
		   String UserId = (String)(session.getAttribute("Userid"));
		   int gid = Integer.valueOf((String)map.get("goods_id"));
		   int price = Integer.valueOf((String)map.get("price"));
		   String name = (String)map.get("name");
		   logger.info("책의 이름 : {},유저의 이름 : {}",gid,UserId);
		   logger.info("책의 이름 - 책의 수량 : {},{}",name,qty);
		   logger.info("책의 가격 : {}",price);
		   shoppingbasketimpl.setshoppingbasket(qty, gid,price,UserId,name);  //카트 담기
		   return new ResponseEntity<String>("yes",HttpStatus.CREATED);
	}
	
	@PostMapping("/cartspace")
	public ResponseEntity<Integer> cartspace(@RequestBody HashMap<String,Object> map) {  //유저의 아이디 Serialization
		String Id = (String)(map.get("Id"));
		int cartspace = shoppingbasketimpl.checkcartspace(Id);  //카트를 담을 수 있는 공간 확인
		return new ResponseEntity<Integer>(cartspace,HttpStatus.OK);
	}
	
	@PostMapping("/jeongbo")
	public ResponseEntity<JSONObject> jeongbo(@RequestBody HashMap<String,Object> map){
		String Id = (String)(map.get("Id")); //(String)(map.get("Id"));
		logger.info("Id : {}",Id);
		JSONObject data = new JSONObject();
		User user = userserviceimpl.finduserbyid(Id);  //유저 객체 찾기
		if(user == null) {
			throw new UserNotFindExceptionHandler(Id);
		}
		data.put("name",user.getName());
		data.put("address",user.getAddress());
		data.put("phone",user.getPhoneNumber());
		data.put("email",user.getEmail());
		return new ResponseEntity<JSONObject>(data,HttpStatus.OK);
	}
	
	@GetMapping("/remaincheck")
	public ResponseEntity<Integer> remaincheck(@RequestParam String[] newname,@RequestParam Integer[] newqty){
		return new ResponseEntity<Integer>((Integer)(productserviceimpl.remaincheck(newname,newqty)),HttpStatus.OK);
	}

	@PostMapping("/completeToken")  //MySQL DB에 주문 정보 동기화 과정
	public ResponseEntity<JSONObject> completeToken(@RequestBody HashMap<String,Object> map,HttpSession session) throws Exception{
		logger.info("클라이언트에서 넘어온 정보 로깅 : {}",map);
		JSONObject json = new JSONObject();
		String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
		String imp_secret =	URLEncoder.encode("75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
		json.put("imp_key",imp_key);
		json.put("imp_secret", imp_secret);
		logger.info("json 객체 확인 : {}",json);
		logger.info("imp_key : {}",json.get("imp_key"));
		logger.info("imp_secret : {}",json.get("imp_secret"));
		try{
			String token = MallController.getToken(json,"https://api.iamport.kr/users/getToken");
			if(token != null)
				logger.info("iamport api token : {}",token);
			JSONObject getdata = null; //아임포트 서버에서 받아올 json 객체 null로 초기화
			JSONObject paymentjson = requestPaymentinfo(map,token,getdata,"https://api.iamport.kr/payments/" + (String)map.get("imp_uid"),session);
			return new ResponseEntity<JSONObject>(paymentjson,HttpStatus.OK);
		}catch(Exception e) {
			logger.info("네트워크 장애 에러");
			e.printStackTrace();
			JSONObject exceptionjson = new JSONObject();
			exceptionjson.put("check","failed");
			return new ResponseEntity<JSONObject>(exceptionjson,HttpStatus.OK);
		}
	}
	
	private int getamount(String merchant_uid){
		return orderserviceimpl.getpriceBymerchantid(merchant_uid);
	}
	
	@PostMapping("/unitInStockShortageCheck")
	public boolean unitInStockShortageCheck(@RequestBody HashMap<String,Object> map) {
		List<String> booknamelist = (ArrayList<String>)(map.get("booknamelist"));
		List<Integer> bookqtylist = (ArrayList<Integer>)(map.get("bookqtylist"));
		logger.info("booknamelist : {}",booknamelist);
		String merchant_id = (String)map.get("merchant_uid");
		return orderserviceimpl.unitInStockCheck(booknamelist,bookqtylist,merchant_id); 
	}
	
	@Transactional
	private JSONObject requestPaymentinfo(HashMap<String,Object> map,String token,JSONObject getdata,String requestURL,
			HttpSession session) {
		try{
			String requestString = "";
			URL url = new URL(requestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true); 				
			connection.setInstanceFollowRedirects(false);  
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", token);
			OutputStream os= connection.getOutputStream();
			connection.connect();
			StringBuilder sb = new StringBuilder(); 
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String line = null;  
				while ((line = br.readLine()) != null) {  
					sb.append(line + "\n");  
				}
				br.close();
				requestString = sb.toString();
			}
			os.flush();
			connection.disconnect();
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(requestString);
			if((Long)jsonObj.get("code")  == 0){
				getdata = (JSONObject) jsonObj.get("response");
				if(getdata != null)
					logger.info("MySQL에서 관리하는 주문 정보 ID : {},Payment Info from IamportServer : {}",(String)(map.get("merchant_uid")),getdata);
			}
		}catch(Exception e){
			logger.info("유저 아이디 : {}",session.getAttribute("Userid"));
			e.printStackTrace();  
			JSONObject exceptionjson = new JSONObject();
			exceptionjson.put("check","failed");
			return exceptionjson;
		}
		String amount = String.valueOf(getdata.get("amount"));  
		String status = String.valueOf(getdata.get("status"));  
		logger.info("merchant_uid : {}",(String)(map.get("merchant_uid")));
		int bepaid = getamount((String)(map.get("merchant_uid")));  
		logger.info("bepaid : {}",bepaid);
		String couponid = (String)(map.get("coupon"));  
		ArrayList<String> booknamelistArraylist = (ArrayList<String>)map.get("booknamelist");
		String[] list = booknamelistArraylist.toArray(new String[booknamelistArraylist.size()]);
		logger.info("주문한 책의 이름 리스트 : {}",booknamelistArraylist);
		ArrayList<Integer> bookqtylistArraylist = (ArrayList<Integer>)map.get("bookqtylist");
		logger.info("주문한 책의 수량 리스트 : {}",bookqtylistArraylist);
		Integer[] bookqtylist = new Integer[bookqtylistArraylist.size()];
		for (int i=0; i < bookqtylist.length; i++)
		    bookqtylist[i] = bookqtylistArraylist.get(i); 
		String merchant_uid = (String)map.get("merchant_uid");
		String imp_uid = (String)map.get("imp_uid");
		String merchant_id = (String)map.get("merchant_id");
		int pricefromserver = (Integer)map.get("price");
		String price = Integer.toString(pricefromserver);
		String paymethod = String.valueOf(getdata.get("pay_method"));
		logger.info("고객이 지불한 액수 >= 지불되어야 할 액수 : {} >= {}",Integer.valueOf(amount),bepaid);
		logger.info("액수 : {}",amount);
		logger.info("주문 상태 : {}",status);
		logger.info("imp_uid - 아임포트 서버에서 관리하는 주문 ID : {}",imp_uid);
		logger.info("merchant_id : {}",merchant_id);
		logger.info("merchant_uid - 아임포트 서버에서 보내준 주문 아이디 : {}",merchant_uid);
		JSONObject resjson = new JSONObject();
		if(Integer.valueOf(amount) >= bepaid && merchant_uid.equals(merchant_id)){  //amount : 고객이 지불한 금액(아임포트 서버에서 거래 내역 조회) >= bepaid(지불 되어야할 금액)
																					// && merchant_uid == merchant_id를 확인하여 위조 방지
			logger.info("paymethod : {},buyer_name : {}",String.valueOf(getdata.get("pay_method")),
					String.valueOf(map.get("buyer_name")));
			switch(status) {
			case "ready":  //무통장 입금인 경우
				String vbanknum = (String)(map.get("vbanknum"));
				String vbankname = (String)(map.get("vbankname"));
				String vbankdate = (String)(map.get("vbankdate"));
				String vbankholder = (String)(map.get("vbankholder"));
				String buyer_name = String.valueOf(getdata.get("buyer_name"));
				String vbank_code = String.valueOf(getdata.get("vbank_code"));
				logger.info("vbankdate : {}",vbankdate);
				logger.info("쿠폰 아이디 - 무통장 입금 결제 : {}",couponid);
				
				Vbank vbank = new Vbank();
				vbank.setVbanknum(vbanknum);
				vbank.setVbankname(vbankname);
				vbank.setVbankdate(vbankdate);
				vbank.setVbankholder(vbankholder);
				vbank.setVbankcode(vbank_code);
				
				Order vbankorder = new Order();
				vbankorder.setMerchant_id(merchant_id);
				vbankorder.setName(buyer_name);
				vbankorder.setStatus(status);
				vbankorder.setImp_uid(imp_uid);
				vbankorder.setPaymethod(paymethod);
				vbankorder.setPrice(Integer.valueOf(price));
				vbankorder.setCouponid(couponid);
				
				int vbankinsertcheck = orderserviceimpl.InsertVbankAndUpdateStatus(vbankorder,vbank,list,bookqtylist);
				if(vbankinsertcheck == 1) {	
					resjson.put("check","vbankIssued");
					resjson.put("data",getdata);
					resjson.put("vbanknum",orderserviceimpl.getvbankinfo(merchant_uid).getVbanknum());
					logger.info("무통장 입금 완료");
				} else {
					resjson.put("check","failed");
					logger.info("무통장 입금 실패");
				}
				return resjson;
			case "paid":  //무통장 입금 이외의 결제 수단
				Order order = new Order();
				order.setMerchant_id(merchant_id);
				order.setName(String.valueOf(getdata.get("buyer_name")));
				order.setStatus(status);
				order.setImp_uid(imp_uid);
				order.setPaymethod(paymethod);
				order.setPrice(Integer.valueOf(price));
				order.setCouponid(couponid);
				
				logger.info("쿠폰 아이디 - 무통장 입금 이외 결제 수단 : {}",couponid);
				int paidcheck = orderserviceimpl.updatestatusandorder(order,list,bookqtylist);
				if(paidcheck == 1) {
					resjson.put("check","success");
					resjson.put("data",getdata);
					logger.info("결제 완료");
				} else {
					resjson.put("check","failed");
					logger.info("결제 실패");
				}
				return resjson;
			default :
				logger.info("결제 실패");
				resjson.put("check","failed");
				return resjson;
			}
		} else {
			resjson.put("check","failed");
			resjson.put("data",getdata);
			return resjson;
		}
	}
	
	@PostMapping("/InsertMerchantId")
	public ResponseEntity<String> InsertMerchantId(HttpSession session,@RequestBody HashMap<String,Object> map){
		if(orderserviceimpl.orderpaycheck((String)session.getAttribute("Userid"),(String)map.get("price"),
				(String)map.get("coupon")) == 1) {  //결제할 돈의 액수를 클라이언트에서 수정한 경우 체크
			return new ResponseEntity<String>("formupdated",HttpStatus.OK);
		}
		String Userid = (String)map.get("id");
		String merchant_id = (String)map.get("merchant_id");
		String phoneNumber = (String)map.get("phoneNumber");
		String address = (String)map.get("address");
		String buyer_name = (String)map.get("buyer_name");
		String memo = (String)map.get("memo");
		String price = (String)map.get("price");
		orderserviceimpl.InsertMerchant(Userid,merchant_id,phoneNumber,address,buyer_name,memo,Integer.valueOf(price)); 
		return new ResponseEntity<String>(merchant_id,HttpStatus.OK);
	}
	
	@PostMapping("/refundadmin")
	public String refundadmin(@RequestBody HashMap<String,Object> map){
		JSONObject overlapcheckjson = new JSONObject(); 
		String merchant_id = (String)map.get("merchant_uid");
		String amount = (String)map.get("cancel_request_amount");
		logger.info("환불할 금액 : {}",amount);
		String holder = null;
		String bank = null;
		String account = null;
		if((String)map.get("refund_holder") != null) {
			holder = (String)map.get("refund_holder");
			bank = (String)map.get("refund_bank");
			account = (String)map.get("refund_account");
		}
		if(requestrefund.requestrefundoverlappingcheck(merchant_id) == 1) {
			overlapcheckjson.put("check",1);
			return overlapcheckjson.toJSONString();
		} else {
			overlapcheckjson.put("result",requestrefund.requestrefund(merchant_id,Integer.valueOf(amount), holder, bank, account));
			return overlapcheckjson.toJSONString();
		}
	}
	
	@PostMapping("/cancel")
	@Transactional
	public Map<String,Object> cancel(HttpServletRequest request,HttpServletResponse response,
			@RequestBody HashMap<String,Object> map) throws Exception{
		JSONObject json = new JSONObject();
		String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
		String imp_secret =	URLEncoder.encode("75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
		json.put("imp_key",imp_key);
		json.put("imp_secret", imp_secret);
		logger.info("json 객체 확인 : {}",json);
		logger.info("imp_key : {}",json.get("imp_key"));
		logger.info("imp_secret : {}",json.get("imp_secret"));
		JSONObject obj = new JSONObject();
		String token = MallController.getToken(json,"https://api.iamport.kr/users/getToken");
		String reason;
		if((String)map.get("reason") != null){
			reason = (String)map.get("reason");
			obj.put("reason",reason);
		}
		logger.info("merchant_uid typecheck : {}",((String)map.get("merchant_uid")).getClass().getName());
		logger.info("amount typecheck : {}",((String)map.get("cancel_request_amount")).getClass().getName());
		int amount = Integer.valueOf(orderserviceimpl.getpriceBymerchantid((String)map.get("merchant_uid")));		
		int cancel_request_amount = Integer.valueOf((String)map.get("cancel_request_amount")); //Integer.valueOf((String)map.get("cancel_request_amount"));				
		if((String)map.get("refund_holder") != null){  //환불할 결제 수단이 무통장 입금인 경우
			String imp_uid = orderserviceimpl.getimp_uid((String)(map.get("merchant_uid")));
			obj.put("imp_uid",imp_uid);
			obj.put("merchant_uid",(String)(map.get("merchant_uid")));
			//obj.put("cancel_request_amount",(String)map.get("cancel_request_amount"));  (optional)
			obj.put("refund_holder",(String)map.get("refund_holder"));
			obj.put("refund_bank",(String)map.get("refund_bank"));
			obj.put("refund_account",(String)map.get("refund_account"));
			logger.info("환불할 json 객체 : {}",obj);
		}
		else{  //환불한 수단이 무통장 입금 이외인 경우
			obj.put("imp_uid",orderserviceimpl.getimp_uid((String)(map.get("merchant_uid"))));
			//obj.put("cancel_request_amount",(String)map.get("cancel_request_amount"));  (optional)
			logger.info("환불 json 객체 : {}",obj);
		}
		adminserviceimpl.purchasecancel(map);
		JSONObject getcanceldata = null;		//아임포트 서버에서 받아올 환불 이후의 json 객체
		try{
			String requestString = "";
			URL url = new URL("https://api.iamport.kr/payments/cancel");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true); 				
			connection.setInstanceFollowRedirects(false);  
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", token);
			OutputStream os= connection.getOutputStream();
			os.write(obj.toString().getBytes());
			connection.connect();
			StringBuilder sb = new StringBuilder(); 
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String line = null;  
				while ((line = br.readLine()) != null) {  
					sb.append(line + "\n");  
				}
				br.close();
				requestString = sb.toString();
			}
			os.flush();
			connection.disconnect();
			try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(requestString);
			logger.info("아임포트 서버에서 받아온 환불 json 객체 : {}",jsonObj);
			if((Long)jsonObj.get("code")  == 0){
				getcanceldata = (JSONObject)jsonObj.get("response");
				logger.info("getcanceldata(환불 정보) : {}",getcanceldata );
			}
		}catch(Exception e) {
			logger.info("환불 json 객체 정보 파싱 실패");
			e.printStackTrace();
		}
		}catch(Exception e){
			logger.info("아임포트 서버와 환불 도중 네트워크 장애 오류");
			e.printStackTrace();
		}
		return getcanceldata;
	}
	
	@PostMapping("/cancelstatus")
	public void cancelstatus(@RequestBody HashMap<String,Object> map){
		String merchant_id = (String)map.get("merchant_id");
		String cancel = (String)map.get("cancel");
		logger.info("merchant_id : {},cancel : {}",merchant_id,cancel);
		adminserviceimpl.updatestatuscancel(merchant_id, cancel);
	}
	
	@PostMapping("/stop")
	public void stop(@RequestBody HashMap<String,Object> map){
		String merchant_id = (String)map.get("merchant_id");
		orderserviceimpl.deletemerchantid(merchant_id);
	}
	
	@PostMapping("/mobilemaintainsession")
	public void mobilemaintainsession(@RequestParam String Id,HttpSession session){
		if(session.getAttribute("Userid") == null)
			session.setAttribute("Userid",Id);
	}
	
	@PostMapping("/deleteshoppingcart")
	public int deleteshoppingcart(int pnum) {
		shoppingbasketimpl.deleteshoppingbasket(pnum);
		return pnum;
	}
	
	@PostMapping(value = "/upload",produces = "text/plain;charset=utf-8")
	public String upload(MultipartFile file,@RequestParam(required = false) Integer goods_id,
			@RequestParam(required = false) Integer reviewimgflag) throws Exception {	   
		String realFolder = null;
		JSONObject json = new JSONObject();
		UUID uuid = UUID.randomUUID();
		String org_filename = file.getOriginalFilename();
		String str_filename = uuid.toString() + org_filename;
		logger.info("占쏙옙占쏙옙 占쏙옙占싹몌옙 : {}",org_filename); //.........photo.jpg
		logger.info("占쏙옙占쏙옙占쏙옙 占쏙옙占싹몌옙 : {}",str_filename); //ad340234fsdfbdfvfd0924309..........photo.jpg
		if(goods_id != null) {   //책의 이미지인 경우
			byte[] data = file.getBytes();
			//FileOutputStream fos = new FileOutputStream("C:\\SpringShoppingmall\\workplace\\ShoppingApp\\src\\main\\webapp\\goodsimgUpload\\" +  str_filename); //개발자 PC 파일 시스템
			FileOutputStream fos = new FileOutputStream("/opt/tomcat/webapps/ROOT/upload/" +  str_filename);  //-> aws 파일 시스템
			fos.write(data);
			fos.close();
			logger.info("bookimagefile str_filename : {}",str_filename);
			File multitofile = convertfrommultiparttofile(str_filename,"book");
			awsserviceimpl.s3FileUpload(multitofile,"book");  //관리자가 등록할 책 이미지 aws s3에 업로드
			json.put("url","https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/bookimage/" + str_filename);
			return json.toString();
		} else if(reviewimgflag != null){  //리뷰 댓글 이미지인 경우
			try {
				byte[] data = file.getBytes();
				//FileOutputStream fos = new FileOutputStream("C:\\SpringShoppingmall\\workplace\\ShoppingApp\\src\\main\\webapp\\reviewUpload\\" +  str_filename); //개발자 PC 파일 시스템
				FileOutputStream fos = new FileOutputStream("/opt/tomcat/webapps/ROOT/reviewupload/" +  str_filename);  // aws 파일 시스템
				fos.write(data);
				fos.close();
				logger.info("reivewimage str_filename : {}",str_filename);
				File multitofile = convertfrommultiparttofile(str_filename,"review");
				awsserviceimpl.s3FileUpload(multitofile,"review");  //리뷰 댓글에 파일 업로드 이미지 aws s3에 업로드
				json.put("url","https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/reviewimage/" + str_filename);
				return json.toString();
			} catch(Exception e) {
				logger.info("파일 업로드중 오류 발생");
				json.put("thisiserror","error");
				return json.toString();
			}
		}
		else {  //게시판인 경우
			byte[] data = file.getBytes();
			//FileOutputStream fos = new FileOutputStream("C:\\SpringShoppingmall\\workplace\\ShoppingApp\\src\\main\\webapp\\boardUpload\\" +  str_filename); //개발자 PC 파일 시스템
			FileOutputStream fos = new FileOutputStream("/opt/tomcat/webapps/ROOT/boardupload/" +  str_filename);  // aws 파일 시스템
			fos.write(data);
			fos.close();
			logger.info("boardfile str_filename : {}",str_filename);
			File multitofile = convertfrommultiparttofile(str_filename,"board");
			awsserviceimpl.s3FileUpload(multitofile,"board");  //게시판에 파일 업로드 이미지 aws s3에 업로드
			json.put("url","https://shoppingmallbucket.s3.ap-northeast-2.amazonaws.com/boardimage/" + str_filename);
			return json.toString();
		}
	}
	
	private File convertfrommultiparttofile(String filename,String whatupload) throws Exception {
		if(whatupload.equals("book")) {
			//File file = new File("C:\\SpringShoppingmall\\workplace\\ShoppingApp\\src\\main\\webapp\\goodsimgUpload\\" + filename);
			File file = new File("/opt/tomcat/webapps/ROOT/upload/" +  filename);
			logger.info("File upload : {}",filename);
			return file;
		} else {
			//File file = new File("C:\\SpringShoppingmall\\workplace\\ShoppingApp\\src\\main\\webapp\\reviewUpload\\" + filename);
			File file = new File("/opt/tomcat/webapps/ROOT/reviewupload/" +  filename);
			logger.info("File reviewimag : {}",filename);
			return file;
		}
	}
	
	@PostMapping("/usecoupon")
	public Integer usecoupon(@RequestParam String cnumber){
		Integer data = couponserviceimpl.usecoupon(cnumber);
	    return data;
	}
	
	@PostMapping("/makecoupon")  //쿠폰 받기
	public Integer makecoupon(@RequestParam String Id,HttpSession session){
		if(!session.getAttribute("Userid").equals(Id))
			return 3;
		else
			return couponserviceimpl.receivecoupon(Id);
	}
	 
	@PostMapping("/deleteallshoppingcart")
	public void deleteallshoppingmall(@RequestParam String Id){
		shoppingbasketimpl.deleteall(Id);
	}
	
	@GetMapping("/addComment")
	public boolean addComment(@RequestParam String reply,@RequestParam String bookname,@RequestParam String user_id,
			@RequestParam(required = false) Integer reviewpoint){
		return replyserviceimpl.addComment(bookname,user_id,reply);
	}
	
	@PostMapping("/usedcouponcheck")  //사용된 쿠폰인지 체크
	public int usedcouponcheck(@RequestParam String cnumber){
		return couponserviceimpl.usedcouponcheckmethod(cnumber);
	}
	
	@PostMapping("/productrecommend")  //책 추천하기
	public int productrecommend(Bookrecommend bookrecommend,@RequestParam int bookid,HttpSession session) {
		String userid = (String)session.getAttribute("Userid");
		logger.info("recommend bookid : {}",bookrecommend.getBook_id());
		logger.info("int bookid : {}",bookid);
		bookrecommend.setUserid(userid);
		return productserviceimpl.bookrecommend(bookrecommend,userid,bookid);
	}
	
	@PostMapping("/getnoticecoupon")
	public int getnoticecoupon(@RequestParam String id){
		return couponserviceimpl.getcouponscountbyuserId(id);
	}
	
	@PostMapping(value = "/addreview",produces = "application/text; charset=utf8")
	public String addreview(@RequestBody HashMap<String,Object> map) {
		int reviewRating = (Integer)map.get("reviewRating");
		String reviewimgurl = (String)map.get("reviewimgurl");
		String user_id = (String)map.get("user_id");
		String reviewcontent = (String)map.get("reviewContent");
		int tag = Integer.valueOf((String)map.get("feelTagRating"));
		logger.info("tag : {}",tag);
		logger.info("reviewcontent : {}",reviewcontent);
		String bookname = (String)map.get("bookname");
		Reply userreview = new Reply();
		userreview.setContent(reviewcontent);
		userreview.setReviewpoint(reviewRating);
		userreview.setUser_id(user_id);
		userreview.setImgfileurl(reviewimgurl);
		userreview.setGid(bookname);
		userreview.setTag(tag);
		if(replyserviceimpl.addreview(userreview)) {
			return "리뷰 업로드 성공";
		} else {
			return "리뷰 업로드 실패";
		}
	}
	
	@PostMapping("/addreviewreply")
	public void addreviewreply(@RequestBody HashMap<String,Object> map) {
		String user_id = (String)map.get("user_id");
		String bookname = (String)map.get("bookname");
		String reviewContent = (String)map.get("reviewContent");
		int rid = (Integer)map.get("rid");  //rid는 리뷰의 아이디
		logger.info("content : {}",reviewContent);
		ReviewReply userreview = new ReviewReply();
		userreview.setUser_id(user_id);
		userreview.setBookname(bookname);
		userreview.setContent(reviewContent);
		replyserviceimpl.addreviewreply(userreview,rid);  //리뷰에 대한 답변 달기
	}
	
	@PostMapping(value = "/reviewmodify",produces = "application/text; charset=utf8")
	public String reviewmodify(@RequestParam String content,@RequestParam int reviewid) {
		String modifycontent = content;
		replyserviceimpl.reviewmodify(content,reviewid);
		return modifycontent;
	}
	
	@PostMapping(value = "/reviewdelete")
	public void reviewdelete(@RequestParam int reviewid,HttpServletRequest request,HttpServletResponse response){
		String[] arr = replyserviceimpl.getreviewbyrid(reviewid).getImgfileurl().split("/");
		logger.info("{}",(replyserviceimpl.getreviewbyrid(reviewid).getImgfileurl().split("/"))[arr.length-1]);
		awsserviceimpl.s3FileDelete((replyserviceimpl.getreviewbyrid(reviewid).getImgfileurl().split("/"))[arr.length-1]);
		File file = new File("C:\\SpringShoppingmall\\workplace\\ShoppingApp\\src\\main\\webapp\\reviewUpload\\" + (replyserviceimpl.getreviewbyrid(reviewid).getImgfileurl().split("/"))[arr.length-1]);
		//File file = new File("/opt/tomcat/webapps/ROOT/reviewupload/" + (replyserviceimpl.getreviewbyrid(reviewid).getImgfileurl().split("/"))[arr.length-1]);
		if(file.delete())
			logger.info("reviewupload file 삭제");
		else
			logger.info("reviewupload file 파일 삭제 실패");
		replyserviceimpl.reviewdelete(reviewid);
	}
	
	@PostMapping("/reviewreplydelete")
	public void reviewreplydelete(@RequestParam int reviewreplyid,HttpServletRequest request,HttpServletResponse response) {
		replyserviceimpl.reviewreplydelete(reviewreplyid);
	}
	
	@PostMapping("/reviewrecommend")
	public void reviewrecommend(@RequestParam int reviewid,@RequestParam String userid) {
		replyserviceimpl.reviewrecommend(reviewid,userid);
	}
	
	@PostMapping("/reviewreplyrecommend")
	public void reviewreplyrecommend(@RequestParam int reviewreplyid,@RequestParam String userid) {
		replyserviceimpl.reviewreplyrecommend(reviewreplyid,userid);
	}

	@PostMapping("/reviewrecommendcheck")
	public boolean reviewrecommendcheck(@RequestParam int reviewid,@RequestParam String userid) {
		return replyserviceimpl.reviewrecommendcheck(reviewid,userid);
	}
	
	@PostMapping("/reviewreplyrecommendcheck")
	public boolean reviewreplyrecommendcheck(@RequestParam int reviewreplyid,@RequestParam String userid) {
		return replyserviceimpl.reviewreplyrecommendcheck(reviewreplyid,userid);
	}
	
	@GetMapping("/pastreviewcheck")
	public int pastreviewcheck(HttpSession session,@RequestParam String bookname) {
		return replyserviceimpl.pastreviewcheck((String)(session.getAttribute("Userid")),bookname);
	}
}