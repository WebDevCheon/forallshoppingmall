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
	public void start() { //�׽�Ʈ ���̽� �ۼ�
		logger.info("{}",start);
		session.setAttribute("Userid","admin");
		List<String> booknamelist = new ArrayList<String>();
		booknamelist.add("�İ��� ����");
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
		completeToken(map,session);  //������� ���� ���� ��ȸ
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
		logger.info("����ڰ� �Է��� �ֹ� ���� : {}",map);
		JSONObject json = new JSONObject();
		String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
		String imp_secret =	URLEncoder.encode("75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
		json.put("imp_key",imp_key);
		json.put("imp_secret", imp_secret);
		logger.info("���� ��û JSON ��ü ���� : {}",json);
		logger.info("imp_key : {}",json.get("imp_key"));
		logger.info("imp_secret : {}",json.get("imp_secret"));
		try{
			String token = MallController.getToken(json,"https://api.iamport.kr/users/getToken");
			if(token != null)
				logger.info("iamport api token : {}",token);
			JSONObject getdata = null; //���� ����
			JSONObject paymentjson = requestPaymentinfo(map,token,getdata,"https://api.iamport.kr/payments/" + (String)map.get("imp_uid"),session);
			return new ResponseEntity<JSONObject>(paymentjson,HttpStatus.OK);
		}catch(Exception e) {
			logger.info("token�� �޾ƿ��� ���� ���� �߻��߰ų� �ٸ� ���� �߻�");
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
					logger.info("MySQL���� �����ϴ� �ֹ� ���� ID : {},Payment Info from IamportServer : {}",(String)(map.get("merchant_uid")),getdata);
			}
		}catch(Exception e){
			logger.info("���� ���̵� : {}",session.getAttribute("Userid"));
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
		logger.info("�ֹ��� å�� �̸� ����Ʈ : {}",booknamelistArraylist);
		ArrayList<Integer> bookqtylistArraylist = (ArrayList<Integer>)map.get("bookqtylist");
		logger.info("�ֹ��� å�� ���� ����Ʈ : {}",bookqtylistArraylist);
		Integer[] bookqtylist = new Integer[bookqtylistArraylist.size()];
		for (int i=0; i < bookqtylist.length; i++)
		    bookqtylist[i] = bookqtylistArraylist.get(i); 
		String merchant_uid = (String)map.get("merchant_uid");
		String imp_uid = (String)map.get("imp_uid");
		String merchant_id = (String)map.get("merchant_id");
		int pricefromserver = (Integer)map.get("price");
		String price = Integer.toString(pricefromserver);
		String paymethod = String.valueOf(getdata.get("pay_method"));
		logger.info("���� ������ �׼� >= ���ҵǾ�� �� �׼� : {} >= {}",Integer.valueOf(amount),bepaid);
		logger.info("�׼� : {}",amount);
		logger.info("�ֹ� ���� : {}",status);
		logger.info("imp_uid - ������Ʈ �������� �����ϴ� �ֹ� ID : {}",imp_uid);
		logger.info("merchant_id : {}",merchant_id);
		logger.info("merchant_uid - ������Ʈ �������� ������ �ֹ� ���̵� : {}",merchant_uid);
		JSONObject resjson = new JSONObject();
		if(Integer.valueOf(amount) >= bepaid && merchant_uid.equals(merchant_id)){  //amount : ���� ������ �ݾ�(������Ʈ �������� �ŷ� ���� ��ȸ) >= bepaid(���� �Ǿ���� �ݾ�)
																					// && merchant_uid == merchant_id�� Ȯ���Ͽ� ���� ����
			logger.info("paymethod : {},buyer_name : {}",String.valueOf(getdata.get("pay_method")),
					String.valueOf(map.get("buyer_name")));
			switch(status) {
			case "ready":  //������ �Ա��� ���
				String vbanknum = (String)(map.get("vbanknum"));
				String vbankname = (String)(map.get("vbankname"));
				String vbankdate = (String)(map.get("vbankdate"));
				String vbankholder = (String)(map.get("vbankholder"));
				String buyer_name = String.valueOf(getdata.get("buyer_name"));
				String vbank_code = String.valueOf(getdata.get("vbank_code"));
				logger.info("vbankdate : {}",vbankdate);
				logger.info("���� ���̵� - ������ �Ա� ���� : {}",couponid);
				
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
					logger.info("������ �Ա� �Ϸ�");
				} else {
					resjson.put("check","failed");
					logger.info("������ �Ա� ����");
				}
				return resjson;
			case "paid":  //������ �Ա� �̿��� ���� ����
				Order order = new Order();
				order.setMerchant_id(merchant_id);
				order.setName(String.valueOf(getdata.get("buyer_name")));
				order.setStatus(status);
				order.setImp_uid(imp_uid);
				order.setPaymethod(paymethod);
				order.setPrice(Integer.valueOf(price));
				order.setCouponid(couponid);
				
				logger.info("���� ���̵� - ������ �Ա� �̿� ���� ���� : {}",couponid);
				int paidcheck = orderserviceimpl.updatestatusandorder(order,list,bookqtylist);
				if(paidcheck == 1) {
					resjson.put("check","success");
					resjson.put("data",getdata);
					logger.info("���� �Ϸ�");
				} else {
					resjson.put("check","failed");
					logger.info("���� ����");
				}
				return resjson;
			default :
				logger.info("���� ����");
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
