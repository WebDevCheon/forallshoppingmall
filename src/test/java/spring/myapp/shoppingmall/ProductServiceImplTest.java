package spring.myapp.shoppingmall;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import spring.myapp.shoppingmall.controller.MallController;
import spring.myapp.shoppingmall.dto.Order;
import spring.myapp.shoppingmall.dto.Vbank;
import spring.myapp.shoppingmall.paging.Paging;
import spring.myapp.shoppingmall.service.AdminServiceImpl;
import spring.myapp.shoppingmall.service.CouponServiceImpl;
import spring.myapp.shoppingmall.service.FindJeongbo;
import spring.myapp.shoppingmall.service.OrderServiceImpl;
import spring.myapp.shoppingmall.service.ProductServiceImpl;
import spring.myapp.shoppingmall.service.ReplyServiceImpl;
import spring.myapp.shoppingmall.service.RequestRefund;
import spring.myapp.shoppingmall.service.ShoppingBasketImpl;
import spring.myapp.shoppingmall.service.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@WebAppConfiguration
public class ProductServiceImplTest {
	private static final Logger logger = LoggerFactory.getLogger(ProductServiceImplTest.class);
	
	@Autowired
	private ShoppingBasketImpl shoppingbasketimpl;
	
	@Autowired
	private CouponServiceImpl couponserviceimpl;
	
	@Autowired
	private ProductServiceImpl productserviceimpl; 
	
	@Autowired
	private OrderServiceImpl orderserviceimpl;
	
	@Autowired
	private ReplyServiceImpl replyserviceimpl;
	
	@Autowired
	private AdminServiceImpl adminserviceimpl;
	
	@Autowired
	private UserServiceImpl userserviceimpl;
	
	@Autowired
	private RequestRefund requestrefund;
	
	@Autowired
	private Paging paging;
	
	@Autowired
	private FindJeongbo findjeongbo;
	
	@Autowired
	private HttpSession session;
	private HashMap<String,Object> map = new HashMap<String,Object>();
	private String start = "productserviceimpl test start...";
	private String end = "productserviceimpl test end...";
	
	@Before
	public void start() { //테스트 케이스 작성
		logger.info("{}",start);
		session.setAttribute("Userid","admin");
		List<String> booknamelist = new ArrayList<String>();
		booknamelist.add("후가는 유가");
		List<Integer> bookqtylist = new ArrayList<Integer>();
		bookqtylist.add(2);
		map.put("imp_uid","imp_409075511555");
		map.put("merchant_uid","SkHXskIAkzyksDA");
		map.put("booknamelist",booknamelist);
		map.put("bookqtylist",bookqtylist);
		map.put("merchant_id","SkHXskIAkzyksDA");
		map.put("price",1200);
		map.put("coupon","");
	}
	
	
	/////////////////////////////////////////Test Case Written/////////////////////////////////////////
	
	/*
	@Transactional
	@Rollback(true)
	@Test
	public void testGetmonthbooklist() throws Exception{
		completeToken(map,session);  //사용자의 결제 정보 조회
	}
	*/
	
	public class testThread implements Runnable {
		 public void run(){
			 try {
				 logger.info("{}",productserviceimpl.getmonthbooklist());  
			 } catch(Exception e) {
				 e.printStackTrace();
			 }
		 }
	}
	
	 	@Transactional
		@Rollback(true)
		@Test
	    public void testRequest() throws Exception {
	 		for(int i = 0; i < 10000; i++) {
	 			Thread t = new Thread(new testThread());
	 			t.run();
	 		}
	    }
	
	/////////////////////////////////////////Test Case Written/////////////////////////////////////////
	
	@After
	public void end() {
		logger.info("{}",end);
	}
	
	public ResponseEntity<JSONObject> completeToken(@RequestBody HashMap<String,Object> map,HttpSession session) throws Exception{
		logger.info("사용자가 입력한 주문 정보 : {}",map);
		JSONObject json = new JSONObject();
		String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
		String imp_secret =	URLEncoder.encode("75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
		json.put("imp_key",imp_key);
		json.put("imp_secret", imp_secret);
		logger.info("결제 요청 JSON 객체 정보 : {}",json);
		logger.info("imp_key : {}",json.get("imp_key"));
		logger.info("imp_secret : {}",json.get("imp_secret"));
		try{
			String token = MallController.getToken(json,"https://api.iamport.kr/users/getToken");
			if(token != null)
				logger.info("iamport api token : {}",token);
			JSONObject getdata = null; //결제 정보
			JSONObject paymentjson = requestPaymentinfo(map,token,getdata,"https://api.iamport.kr/payments/" + (String)map.get("imp_uid"),session);
			return new ResponseEntity<JSONObject>(paymentjson,HttpStatus.OK);
		}catch(Exception e) {
			logger.info("token을 받아오던 도중 에러 발생했거나 다른 에러 발생");
			e.printStackTrace();
			JSONObject exceptionjson = new JSONObject();
			exceptionjson.put("check","failed");
			return new ResponseEntity<JSONObject>(exceptionjson,HttpStatus.OK);
		}
	}
	
	private int getamount(String merchant_uid){
		return orderserviceimpl.getpriceBymerchantid(merchant_uid);
	}
	
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
	
}
