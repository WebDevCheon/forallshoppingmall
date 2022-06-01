package spring.myapp.shoppingmall.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@RequestMapping(value = "/iamport-webhook", method = RequestMethod.POST) // ���� ������ �Ա����� ������ �ϰų�,���� ���¿� �ݾ��� �־����� �߻�
	public void webhook(@RequestParam(required = false) String imp_uid,		 // �ٸ� ���� ������ ���� ��� ����
			@RequestParam(required = false) String merchant_uid, HttpServletRequest request,
			HttpServletResponse response, @RequestParam(required = false) String status, Model model) {
		if(status != null && !status.equals("paid")) // ������¿� �Ա��߰ų� ���� �Ϸ�
			return;
		int webhookflag = 0;
		Order order = orderServiceImpl.getMerchantId(merchant_uid);
		if (!order.getPaymethod().equals("vbank")) {
			webhookflag = 0;
			logger.info("������Ʈ ���� ��û stopping.");
			return;
		} else
			webhookflag = 1;
		
		if (webhookflag == 1) {
			try {
				JSONObject json = new JSONObject();
				String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
				String imp_secret = URLEncoder.encode(
						"75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
				json.put("imp_key", imp_key);
				json.put("imp_secret", imp_secret);
				String token = getToken(json, "https://api.iamport.kr/users/getToken");
				JSONObject getdata = null;
				try {
					String requestString = "";
					URL url = new URL("https://api.iamport.kr/payments/" + imp_uid);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Authorization", token);
					OutputStream os = connection.getOutputStream();
					connection.connect();
					StringBuilder sb = new StringBuilder();
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(connection.getInputStream(), "utf-8"));
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
					logger.info("��ȯ�� JSON��ü -> {}" + jsonObj);
					if ((Long) jsonObj.get("code") == 0) {
						getdata = (JSONObject) jsonObj.get("response"); // ������ å�� �ֹ� �Ҷ�,������ �Ա� ��û�� �ߴ� ������ �ٽ� ������
						logger.info("������Ʈ �����κ��� ������ ���� ����(�ٸ� ���� ��İ� ������ �Ա� ���� ����� ���� �����鵵 ����) -> {}",getdata);
						int bepaid = getamount(merchant_uid);
						String amount = String.valueOf(getdata.get("amount"));
						String mystatus = String.valueOf(getdata.get("status"));
						if (String.valueOf(getdata.get("vbank_num")) == null) // ������ �Ա��� �ƴϸ� webhook�� �����Ų��. -> ������ �Աݸ�
							return;											  // ������ ����� ���̱� �����̴�.
						String vbankholder = String.valueOf(getdata.get("vbank_holder"));
						String vbanknum = String.valueOf(getdata.get("vbank_num"));
						String vbankcode = String.valueOf(getdata.get("vbank_code"));
						String paymethod = String.valueOf(getdata.get("pay_method"));

						Vbank vbank = new Vbank();
						vbank.setVbanknum(vbanknum);
						vbank.setVbankholder(vbankholder);
						vbank.setVbankcode(vbankcode);

						Order vbankorder = new Order();
						vbankorder.setMerchant_id(merchant_uid);
						vbankorder.setStatus(mystatus);
						vbankorder.setImp_uid(imp_uid);
						vbankorder.setPaymethod(paymethod);
						vbankorder.setPrice(bepaid);
						
						if (bepaid <= Integer.valueOf(amount) && vbanknum != null) {
							switch (mystatus) {
								case "paid": // ���� ���� ���¿� ���� �Ա� ������
									orderServiceImpl.updateStatusWebhook(vbankorder, vbank); // ���� �Ϸ�� �ٲٱ�
							}
						}
					}
				} catch (Exception e) {
					logger.info("���� �߻� In webhook Method -> " + e,e);
				}
			} catch (Exception e) {
				logger.info("���� �߻� In webhook Method -> " + e,e);
			}
		}
	}

	@RequestMapping("/mobile")	// IMPORT �������� ī��� ������ ������ �Ϸ�� ���Ŀ� ���θ� ������ ���� ������ �����ְ� Redirect �� URL(Mobile���� ������������)
	public String mobile(@RequestParam String coupon, @RequestParam String imp_uid, @RequestParam String merchant_uid,
			HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam String[] booknamelist,
			@RequestParam Integer[] bookqtylist, @RequestParam String amount, HttpSession session) {
		String referer = (String) request.getHeader("REFERER");		// REFERER ��� ���� : ����ڰ� ���࿡ �ֹ� ���߿� �ڷ� ���� ��ư�� ������ �Ǹ�,
																	// ��Ȯ�� �ֹ��� DB�� ������ �ȵɼ� �ֱ� ������ ������ �����ϱ� ����
		try {
			JSONObject json = new JSONObject();
			orderServiceImpl.insertPrice(amount, merchant_uid);
			String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
			String imp_secret = URLEncoder.encode(
					"75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
			json.put("imp_key", imp_key);
			json.put("imp_secret", imp_secret);
			String token = getToken(json, "https://api.iamport.kr/users/getToken");
			JSONObject getdata = null;
			try {
				String requestString = "";
				URL url = new URL("https://api.iamport.kr/payments/" + imp_uid);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Authorization", token);
				OutputStream os = connection.getOutputStream();
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
				
				if ((Long) jsonObj.get("code") == 0) {
					getdata = (JSONObject) jsonObj.get("response");
					logger.info("����� ���� ��� -> {}", getdata);
					int bepaid = getamount(merchant_uid);
					String getamount = String.valueOf(getdata.get("amount"));
					String mystatus = String.valueOf(getdata.get("status"));
					String paymethod = String.valueOf(getdata.get("pay_method"));

					if (mystatus.equals("paid")
							&& referer.contains("https://ksmobile.inicis.com/smart/mobileAcsCancel")) {
						return "redirect:/showbasket";
					}
					if (bepaid <= Integer.valueOf(getamount) && merchant_uid.equals(getdata.get("merchant_uid"))) {
						switch (mystatus) {
						case "ready":
							SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Calendar time = Calendar.getInstance();
							String vbank_num = String.valueOf(getdata.get("vbank_num"));
							String vbank_code = String.valueOf(getdata.get("vbank_code"));
							String vbank_date = timeformat.format(time.getTime());
							String vbank_name = String.valueOf(getdata.get("vbank_name"));
							String vbank_holder = String.valueOf(getdata.get("vbank_holder"));
							String vbank_person = String.valueOf(getdata.get("buyer_name"));

							Vbank vbank = new Vbank();
							vbank.setVbanknum(vbank_num);
							vbank.setVbankname(vbank_name);
							vbank.setVbankdate(vbank_date);
							vbank.setVbankholder(vbank_holder);
							vbank.setVbankcode(vbank_code);

							Order vbankorder = new Order();
							vbankorder.setMerchant_id(merchant_uid);
							vbankorder.setName((String) getdata.get("buyer_name"));
							vbankorder.setStatus((String) getdata.get("status"));
							vbankorder.setImp_uid(imp_uid);
							vbankorder.setPaymethod(paymethod);
							vbankorder.setPrice(Integer.valueOf(getamount));
							vbankorder.setCouponid(coupon);

							if (orderServiceImpl.mobileCheckByMerchantUid(merchant_uid)) {
								model.addAttribute("vbank_date", vbank_date);
								model.addAttribute("vbank_holder", vbank_holder); // ������
								model.addAttribute("vbank_num",
										orderServiceImpl.getVbankInfo(merchant_uid).getVbanknum()); // ���� ���¹�ȣ
								model.addAttribute("vbank_name", vbank_name); // ���� �̸�
								model.addAttribute("vbank_code", vbank_code);
								model.addAttribute("Order", orderServiceImpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderServiceImpl.getOrderGoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							}
							
							int vbankinsertcheck = orderServiceImpl.InsertVbankAndUpdateStatus(vbankorder, vbank,
									booknamelist, bookqtylist);
							
							if (vbankinsertcheck == 1) {
								model.addAttribute("vbank_date", vbank_date);
								model.addAttribute("vbank_holder", vbank_holder); // ������
								model.addAttribute("vbank_num",
										orderServiceImpl.getVbankInfo(merchant_uid).getVbanknum()); // ���� ���¹�ȣ
								model.addAttribute("vbank_name", vbank_name); // ���� �̸�
								model.addAttribute("vbank_code", vbank_code);
								model.addAttribute("Order", orderServiceImpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderServiceImpl.getOrderGoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							} else
								return "/error/404code";
							
						case "paid":
							Order order = new Order();
							order.setMerchant_id(merchant_uid);
							order.setName((String) getdata.get("buyer_name"));
							order.setStatus((String) getdata.get("status"));
							order.setImp_uid(imp_uid);
							order.setPaymethod(paymethod);
							order.setPrice(Integer.valueOf(getamount));
							order.setCouponid(coupon);

							if (orderServiceImpl.mobileCheckByMerchantUid(merchant_uid)) {
								model.addAttribute("Order", orderServiceImpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderServiceImpl.getOrderGoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							}
							
							int paidcheck = orderServiceImpl.updateStatusAndOrder(order, booknamelist, bookqtylist);	// �ֹ� ������ ����
							
							if (paidcheck == 1) {
								model.addAttribute("Order", orderServiceImpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderServiceImpl.getOrderGoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							} else {
								return "/error/404code";
							}
						case "failed":	// ���� ����
							orderServiceImpl.deleteMerchantId((String) getdata.get("merchant_uid"));
						}
					} else
						return "mobilefailed";
				}
			} catch (Exception e) {
				logger.info("���� �߻� In mobile Method -> " + e,e);
			}
		} catch (Exception e) {
			logger.info("���� �߻� In mobile Method -> " + e,e);
		}
		return null;
	}
	
	@PostMapping("/completeToken")  // ���� ���Ŀ� IMPORT�����κ��� �� ���� ������ ���θ� ������ MySQL DB�� �ֹ� ������ ����ȭ ����
	public ResponseEntity<JSONObject> synchronizationOrderTable(@RequestBody HashMap<String,Object> map,HttpSession session) throws Exception{
		JSONObject json = new JSONObject();
		String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
		String imp_secret =	URLEncoder.encode("75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
		json.put("imp_key",imp_key);
		json.put("imp_secret", imp_secret);
		try {
			String token = getToken(json,"https://api.iamport.kr/users/getToken");	// ������Ʈ ������ ���� ���� ��ū �߱� ��û
			JSONObject getdata = null; //������Ʈ �������� �޾ƿ� json ��ü null�� �ʱ�ȭ
			JSONObject paymentjson = requestPaymentinfo(map,token,getdata,"https://api.iamport.kr/payments/" + (String)map.get("imp_uid"),session);
			return new ResponseEntity<JSONObject>(paymentjson,HttpStatus.OK);
		} catch(Exception e) {
			logger.info("���� �߻� In synchronizationOrderTable Method -> " + e,e);
			JSONObject exceptionjson = new JSONObject();
			exceptionjson.put("check","failed");
			return new ResponseEntity<JSONObject>(exceptionjson,HttpStatus.OK);
		}
	}
	
	public static String getToken(JSONObject json, String requestURL) {
		String _token = "";
		try {
			String requestString = "";
			URL url = new URL(requestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			OutputStream os = connection.getOutputStream();
			os.write(json.toString().getBytes());
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
				if ((Long) jsonObj.get("code") == 0) {
					JSONObject getToken = (JSONObject) jsonObj.get("response");
					_token = (String) getToken.get("access_token");
				}
			} catch (Exception e) {
				logger.info("���� �߻� In getToken Method -> " + e,e);
			}
		} catch (Exception e) {
			logger.info("���� �߻� In getToken Method -> " + e,e);
			_token = "";
		}
		return _token;
	}
	
	private JSONObject requestPaymentinfo(HashMap<String,Object> map,String token,JSONObject getdata,String requestURL,
			HttpSession session) {		// ���� �޼ҵ�
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
		} catch(Exception e){
			logger.info("���� ���̵� : {}",session.getAttribute("Userid"));
			logger.info("���� �߻� In requestPaymentinfo Method -> " + e,e);
			JSONObject exceptionjson = new JSONObject();
			exceptionjson.put("check","failed");
			return exceptionjson;
		}
		String amount = String.valueOf(getdata.get("amount"));  
		String status = String.valueOf(getdata.get("status"));  
		int bepaid = getamount((String)(map.get("merchant_uid")));  
		String couponid = (String)(map.get("coupon"));  
		ArrayList<String> booknamelistArraylist = (ArrayList<String>)map.get("booknamelist");
		String[] list = booknamelistArraylist.toArray(new String[booknamelistArraylist.size()]);
		ArrayList<Integer> bookqtylistArraylist = (ArrayList<Integer>)map.get("bookqtylist");
		Integer[] bookqtylist = new Integer[bookqtylistArraylist.size()];
		for (int i = 0; i < bookqtylist.length; i++)
		    bookqtylist[i] = bookqtylistArraylist.get(i); 
		String merchant_uid = (String)map.get("merchant_uid");
		String imp_uid = (String)map.get("imp_uid");
		String merchant_id = (String)map.get("merchant_id");
		int pricefromserver = (Integer)map.get("price");
		String price = Integer.toString(pricefromserver);
		String paymethod = String.valueOf(getdata.get("pay_method"));
		logger.info("���� ������ �׼� -> {},�����ؾ��� �׼� -> {}",Integer.valueOf(amount),bepaid);
		logger.info("�ֹ� ���� -> {}",status);
		logger.info("������Ʈ �������� �����ϴ� �ֹ� ID : {}",imp_uid);
		logger.info("å ���θ� DB���� �����Ǵ� �ֹ� ��ȣ -> {}",merchant_id);
		logger.info("������Ʈ �������� ������ �ֹ� ���̵� -> {}",merchant_uid);
		JSONObject resjson = new JSONObject();
		if(Integer.valueOf(amount) >= bepaid && merchant_uid.equals(merchant_id)){  //amount : ���� ������ �ݾ�(������Ʈ �������� �ŷ� ���� ��ȸ) >= bepaid(���� �Ǿ���� �ݾ�)
																					// && merchant_uid == merchant_id�� Ȯ���Ͽ� ���� ����
			logger.info("���� ��� -> {},������ �̸� -> {}",String.valueOf(getdata.get("pay_method")),
					String.valueOf(map.get("buyer_name")));
			switch(status) {
			case "ready":  //������ �Ա��� ���
				String vbanknum = (String)(map.get("vbanknum"));
				String vbankname = (String)(map.get("vbankname"));
				String vbankdate = (String)(map.get("vbankdate"));
				String vbankholder = (String)(map.get("vbankholder"));
				String buyer_name = String.valueOf(getdata.get("buyer_name"));
				String vbank_code = String.valueOf(getdata.get("vbank_code"));
				
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
				} else
					resjson.put("check","failed");		// ���� �߻�
				return resjson;

			case "paid":  // ������ �Ա� �̿��� ���� ����
				Order order = new Order();
				order.setMerchant_id(merchant_id);
				order.setName(String.valueOf(getdata.get("buyer_name")));
				order.setStatus(status);
				order.setImp_uid(imp_uid);
				order.setPaymethod(paymethod);
				order.setPrice(Integer.valueOf(price));
				order.setCouponid(couponid);
				
				int paidcheck = orderServiceImpl.updateStatusAndOrder(order,list,bookqtylist);
				
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
	
	private int getamount(String merchant_uid){		// �ֹ��� ����
		return orderServiceImpl.getPriceByMerchantId(merchant_uid);
	}
	
	@PostMapping("/stop")		// ���� ���� ������ �߻��ϸ� DB�� �ֹ� ������ ����	
	public void stopPayment(@RequestBody HashMap<String,Object> map){
		String merchant_id = (String)map.get("merchant_id");
		orderServiceImpl.deleteMerchantId(merchant_id);
	}
	
	@PostMapping("/cancel")		// ȯ�� �޼ҵ�
	public Map<String,Object> cancel(HttpServletRequest request,HttpServletResponse response,
			@RequestBody HashMap<String,Object> map) throws Exception{
		JSONObject json = new JSONObject();
		String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
		String imp_secret =	URLEncoder.encode("75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
		json.put("imp_key",imp_key);
		json.put("imp_secret", imp_secret);
		logger.info("json ��ü Ȯ�� : {}",json);
		logger.info("imp_key : {}",json.get("imp_key"));
		logger.info("imp_secret : {}",json.get("imp_secret"));
		JSONObject obj = new JSONObject();
		String token = getToken(json,"https://api.iamport.kr/users/getToken");
		String reason;
		if((String)map.get("reason") != null){
			reason = (String)map.get("reason");
			obj.put("reason",reason);
		}

		int amount = Integer.valueOf(orderServiceImpl.getPriceByMerchantId((String)map.get("merchant_uid")));		
		int cancel_request_amount = Integer.valueOf((String)map.get("cancel_request_amount")); //Integer.valueOf((String)map.get("cancel_request_amount"));				
		
		if((String)map.get("refund_holder") != null){  // ȯ���� ���� ������ ������ �Ա��� ���
			String imp_uid = orderServiceImpl.getImp_Uid((String)(map.get("merchant_uid")));
			obj.put("imp_uid",imp_uid);
			obj.put("merchant_uid",(String)(map.get("merchant_uid")));
			//obj.put("cancel_request_amount",(String)map.get("cancel_request_amount"));  (optional)
			obj.put("refund_holder",(String)map.get("refund_holder"));
			obj.put("refund_bank",(String)map.get("refund_bank"));
			obj.put("refund_account",(String)map.get("refund_account"));
			logger.info("ȯ���� JSON ��ü : {}",obj);
		} else {  // ȯ���� ������ ������ �Ա� �̿��� ���
			obj.put("imp_uid",orderServiceImpl.getImp_Uid((String)(map.get("merchant_uid"))));
			//obj.put("cancel_request_amount",(String)map.get("cancel_request_amount"));  (optional)
			logger.info("ȯ�� JSON ��ü : {}",obj);
		}
		adminServiceImpl.purchaseCancel(map);
		JSONObject getcanceldata = null;		// ������Ʈ �������� �޾ƿ� ȯ�� ������ json ��ü
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
				logger.info("������Ʈ �������� �޾ƿ� ȯ�� json ��ü : {}",jsonObj);
				if((Long)jsonObj.get("code")  == 0) {
					getcanceldata = (JSONObject)jsonObj.get("response");
					logger.info("getcanceldata(ȯ�� ����) : {}",getcanceldata);
				}
			} catch(Exception e) {
				logger.info("ȯ�� json ��ü ���� �Ľ� ���� -> " + e,e);
			}
		} catch(Exception e) {
			logger.info("������Ʈ ������ ȯ�� ���� ��Ʈ��ũ ��� ���� -> " + e,e);
		}
		return getcanceldata;
	}
	
	@PostMapping("/cancelstatus")		// �ֹ� ������ ȯ�� �Ϸ� ���·� �ٲٴ� �޼ҵ�
	public void cancelstatus(@RequestBody HashMap<String,Object> map){
		String merchant_id = (String)map.get("merchant_id");
		String cancel = (String)map.get("cancel");
		adminServiceImpl.updateStatusCancel(merchant_id, cancel);
	}
}
