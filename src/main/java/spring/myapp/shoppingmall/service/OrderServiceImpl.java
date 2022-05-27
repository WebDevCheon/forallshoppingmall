package spring.myapp.shoppingmall.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.myapp.shoppingmall.controller.MallController;
import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.dto.Coupon;
import spring.myapp.shoppingmall.dto.Order;
import spring.myapp.shoppingmall.dto.Ordergoods;
import spring.myapp.shoppingmall.dto.Shoppingbasket;
import spring.myapp.shoppingmall.dto.Vbank;

@Service
public class OrderServiceImpl implements OrderService {
	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private MallDao Malldao;
	
	@Override
	@Transactional
	public void deleteMerchantId(String merchant_id) {
		Malldao.deltemerchantid(merchant_id);
	}
	
	@Override
	@Transactional
	public String getImp_Uid(String merchant_uid){
		return Malldao.getimp_uid(merchant_uid);
	}
	
	@Override
	public Order getMerchantId(String merchant_id){
		return Malldao.getMerchantid(merchant_id);
	}
	
	@Override
	@Transactional
	public int getPriceByMerchantId(String merchant_uid){
		return Malldao.getfindprice(merchant_uid);
	}
	
	@Override
	public List<Ordergoods> getOrderGoods(String merchant_id){
		return Malldao.getordergoods(merchant_id);
	}
	
	@Override
	public List<Order> getOrderInfo(String id,int curPageNum){
		return Malldao.getorderinfo(id,curPageNum);
	}
	
	@Override
	@Transactional
	public Vbank getVbankInfo(String merchant_id){
		return Malldao.getvbankinfo(merchant_id);
	}
	
	@Override
	public void insertPrice(String price,String merchant_uid){
		Malldao.insertPrice(Integer.valueOf(price),merchant_uid);
	}
	
	@Override
	public void insertGoods(String merchant_id,String[] list,Integer[] glist){
		Malldao.insertgoods(merchant_id,list,glist);
	}
	
	@Override
	public void InsertVbank(String merchant_id,String vbanknum,String vbankname,String vbankdate,String vbankholder,String vbankperson,
			String vbankcode) {
		Malldao.insertvbank(merchant_id,vbanknum,vbankname,vbankdate,vbankholder,vbankperson,vbankcode);
	}
	
	@Override
	@Transactional
	public int InsertVbankAndUpdateStatus(Order order,Vbank vbank,String[] booknamelist, Integer[] bookqtylist) {
			try {
				Malldao.insertVbankgoods(order.getMerchant_id(),booknamelist,bookqtylist);
				Malldao.insertPrice(order.getPrice(),order.getMerchant_id());
				Malldao.insertvbank(order.getMerchant_id(),vbank.getVbanknum(),vbank.getVbankname(),vbank.getVbankdate(),vbank.getVbankholder(),order.getName(),vbank.getVbankcode());
				Malldao.statusupdate(order.getStatus(),order.getMerchant_id(),order.getImp_uid(),order.getPaymethod());
				if(!order.getCouponid().equals("")){
					Malldao.updatestatususecoupon(order.getCouponid(),order.getMerchant_id());
				}
				return 1;
			} catch(Exception e) {
				e.printStackTrace();
				Order failedorder = Malldao.getMerchantid(order.getMerchant_id());
				logger.info("주문 상태 : {}",failedorder.getStatus());
				if(failedorder.getStatus().equals("not paid")) {
					logger.info("주문 실패 번호 : {}",failedorder.getOrderid());
					logger.info("rollback 성공 :" + order.getMerchant_id());
					Malldao.rollbackdeletevbankmerchantid(failedorder.getMerchant_id());
				}
				return 0;
			}
	}
	
	@Override
	public void InsertMerchant(String id,String merchant_id,String phoneNumber,String address,String buyer_name,String memo,int price) {
		Malldao.insertMerchant(id,merchant_id,phoneNumber,address,buyer_name,memo,price);
	}
	
	@Override
	public boolean mobileCheckByMerchantUid(String merchant_uid) {
		return Malldao.mobilecheck(merchant_uid);
	}
	
	@Override
	@Transactional
	public void updateStatusWebhook(Order vbankorder,Vbank vbank){	
		try {
			Malldao.statusupdate(vbankorder.getStatus(),vbankorder.getMerchant_id(),vbankorder.getImp_uid(),vbankorder.getPaymethod());
			Malldao.subordergoodsVbank(vbankorder.getMerchant_id());
		} catch(Exception e) {
			communicateWithIMPORTServer(vbankorder,vbank);
			if(Malldao.getMerchantid(vbankorder.getMerchant_id()).getStatus().equals("ready")) {
				logger.info("주문 실패 번호 : {}",vbankorder.getOrderid());
				logger.info("rollback 성공 :" + vbankorder.getMerchant_id());
				logger.info("무통장 입금 웹훅 실패 : " + vbankorder);
				Malldao.rollbackdeletemerchantid(vbankorder.getMerchant_id());
			} 				
		}
	}
	
	@Override
	@Transactional
	public int updateStatusAndOrder(Order order,String[] booknamelist,Integer[] bookqtylist) {
			try {
				Malldao.insertgoods(order.getMerchant_id(),booknamelist,bookqtylist);
				Malldao.insertPrice(order.getPrice(),order.getMerchant_id());
				Malldao.statusupdate(order.getStatus(),order.getMerchant_id(),order.getImp_uid(),order.getPaymethod());
				if(!order.getCouponid().equals(""))
					Malldao.updatestatususecoupon(order.getCouponid(),order.getMerchant_id());
				return 1;
			} catch(Exception e) {
				e.printStackTrace();
				communicateWithIMPORTServer(order,null);
				Order failedorder = Malldao.getMerchantid(order.getMerchant_id());
				if(failedorder.getStatus().equals("not paid")) {
					logger.info("주문 실패 번호 : {}",failedorder.getOrderid());
					logger.info("rollback 성공 :" + order.getMerchant_id());
					Malldao.rollbackdeletemerchantid(failedorder.getMerchant_id());
				}
				return 0;
			}
	}
	
	@Override
	@Transactional
	public int orderPayCheck(String Userid,String price,String couponid) {
		List<Shoppingbasket> shoppingbasketlist = Malldao.getShoppingbasket(Userid);
		int sum = 0;
		if(couponid.equals("")) {
			for(Shoppingbasket goods : shoppingbasketlist)
				sum += goods.getPrice() * goods.getQty();
			if(sum != Integer.valueOf(price))	// 가격이 다름
				return 1;
			else
				return 0;
		} else {
			Coupon coupon = Malldao.getcoupon(couponid);
			for(Shoppingbasket goods : shoppingbasketlist)
				sum += goods.getPrice() * goods.getQty();
			
			if(coupon.getType() == 0)
				sum = sum - (int)(0.05 * sum);
			else if(coupon.getType() == 1)
				sum = sum - (int)(0.1 * sum);
			else if(coupon.getType() == 2)
				sum = sum - (int)(0.15 * sum);
			else if(coupon.getType() == 3)
				sum = sum - (int)(0.2 * sum);
			
			if(sum != Integer.valueOf(price))	//가격이 다름
				return 1;
			else
				return 0;
		}
	}
	
	private void communicateWithIMPORTServer(Order order,Vbank vbank) {
		String cancelstatus = null;
		try {
			JSONObject json = new JSONObject();
			String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
			String imp_secret =	URLEncoder.encode("75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
			json.put("imp_key",imp_key);
			json.put("imp_secret", imp_secret);
			logger.info(String.valueOf(json));
			logger.info("imp_key : " + json.get("imp_key"));
			logger.info("imp_secret : " + json.get("imp_secret"));
			JSONObject obj = new JSONObject();
			String token = MallController.getToken(json,"https://api.iamport.kr/users/getToken");
			String reason;
			int amount = order.getPrice();
			int cancel_request_amount = order.getPrice();
			if(vbank != null) {
				obj.put("refund_holder",vbank.getVbankholder());
				obj.put("refund_bank",vbank.getVbankcode());
				obj.put("refund_account",vbank.getVbanknum());
			}
			if(amount - cancel_request_amount <= 0)
				logger.info("이미 전액환불된 상품입니다.");
			obj.put("imp_uid",order.getImp_uid());
			obj.put("cancel_request_amount",order.getPrice());
			JSONObject getcanceldata = null;		//아임포트 서버에서 환불 받은 정보
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
					logger.info("jsonObj " + jsonObj);
					if((Long)jsonObj.get("code")  == 0) {
						getcanceldata = (JSONObject) jsonObj.get("response");
						logger.info("getcanceldata==>>"+getcanceldata );
					}
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			} catch(Exception exception){
				exception.printStackTrace();
			}
			logger.info("환불 정보 : {}",getcanceldata);
			cancelstatus = String.valueOf(getcanceldata.get("status"));
			logger.info("cancelstatus : " + cancelstatus);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		logger.info("{}","merchant_id in Rollback : " + order.getMerchant_id());
  	}
  
  	@Override
  	public boolean unitInStockCheck(List<String> booknamelist,List<Integer> bookqtylist,String merchant_id) {
  		return Malldao.unitInStockCheck(booknamelist,bookqtylist,merchant_id);
  	}
  	
  	@Override
  	public boolean unitInStockShortageMobileCheck(String[] booknamelist,Integer[] bookqtylist,String merchant_uid) {
  		return Malldao.unitInStockShortageMobileCheck(booknamelist,bookqtylist,merchant_uid);
  	}
}