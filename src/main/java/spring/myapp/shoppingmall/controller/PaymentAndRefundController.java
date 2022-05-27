package spring.myapp.shoppingmall.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import spring.myapp.shoppingmall.dto.Order;
import spring.myapp.shoppingmall.dto.Vbank;
import spring.myapp.shoppingmall.service.AdminServiceImpl;
import spring.myapp.shoppingmall.service.OrderServiceImpl;

@Controller
public class PaymentAndRefundController {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentAndRefundController.class);
	
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	
	@Autowired
	private AdminServiceImpl adminServiceImpl;
	
	@PostMapping("/completeToken")  // 결제 이후에 IMPORT서버로부터 온 결제 정보를 쇼핑몰 서버의 MySQL DB에 주문 정보를 동기화 과정
	public ResponseEntity<JSONObject> synchronizationOrderTable(@RequestBody HashMap<String,Object> map,HttpSession session) throws Exception{
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
	
	@Transactional
	private JSONObject requestPaymentinfo(HashMap<String,Object> map,String token,JSONObject getdata,String requestURL,
			HttpSession session) {		// 결제 메소드
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
				
				int vbankinsertcheck = orderServiceImpl.InsertVbankAndUpdateStatus(vbankorder,vbank,list,bookqtylist);
				if(vbankinsertcheck == 1) {	
					resjson.put("check","vbankIssued");
					resjson.put("data",getdata);
					resjson.put("vbanknum",orderServiceImpl.getVbankInfo(merchant_uid).getVbanknum());
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
				int paidcheck = orderServiceImpl.updateStatusAndOrder(order,list,bookqtylist);
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
	
	private int getamount(String merchant_uid){		// 주문의 가격
		return orderServiceImpl.getPriceByMerchantId(merchant_uid);
	}
	
	@PostMapping("/stop")		// 결제 도중 에러가 발생하면 DB의 주문 정보를 삭제	
	public void stopPayment(@RequestBody HashMap<String,Object> map){
		String merchant_id = (String)map.get("merchant_id");
		orderServiceImpl.deleteMerchantId(merchant_id);
	}
	
	@PostMapping("/cancel")		// 환불 메소드
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
		int amount = Integer.valueOf(orderServiceImpl.getPriceByMerchantId((String)map.get("merchant_uid")));		
		int cancel_request_amount = Integer.valueOf((String)map.get("cancel_request_amount")); //Integer.valueOf((String)map.get("cancel_request_amount"));				
		if((String)map.get("refund_holder") != null){  //환불할 결제 수단이 무통장 입금인 경우
			String imp_uid = orderServiceImpl.getImp_Uid((String)(map.get("merchant_uid")));
			obj.put("imp_uid",imp_uid);
			obj.put("merchant_uid",(String)(map.get("merchant_uid")));
			//obj.put("cancel_request_amount",(String)map.get("cancel_request_amount"));  (optional)
			obj.put("refund_holder",(String)map.get("refund_holder"));
			obj.put("refund_bank",(String)map.get("refund_bank"));
			obj.put("refund_account",(String)map.get("refund_account"));
			logger.info("환불할 json 객체 : {}",obj);
		}
		else{  //환불한 수단이 무통장 입금 이외인 경우
			obj.put("imp_uid",orderServiceImpl.getImp_Uid((String)(map.get("merchant_uid"))));
			//obj.put("cancel_request_amount",(String)map.get("cancel_request_amount"));  (optional)
			logger.info("환불 json 객체 : {}",obj);
		}
		adminServiceImpl.purchaseCancel(map);
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
	
	@PostMapping("/cancelstatus")		// 주문 정보를 환불 완료 상태로 바꾸는 메소드
	public void cancelstatus(@RequestBody HashMap<String,Object> map){
		String merchant_id = (String)map.get("merchant_id");
		String cancel = (String)map.get("cancel");
		logger.info("merchant_id : {},cancel : {}",merchant_id,cancel);
		adminServiceImpl.updateStatusCancel(merchant_id, cancel);
	}
}
