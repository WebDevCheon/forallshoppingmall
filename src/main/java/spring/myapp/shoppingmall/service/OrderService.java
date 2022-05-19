package spring.myapp.shoppingmall.service;

import java.util.List;

import spring.myapp.shoppingmall.dto.Order;
import spring.myapp.shoppingmall.dto.Ordergoods;
import spring.myapp.shoppingmall.dto.Vbank;

public interface OrderService {
	public void deletemerchantid(String merchant_id);
	public String getimp_uid(String merchant_uid);
	public Order getMerchantId(String merchant_id);
	public int getpriceBymerchantid(String merchant_uid);
	public List<Ordergoods> getordergoods(String merchant_id);
	public List<Order> getorderinfo(String id,int curPageNum);
	public Vbank getvbankinfo(String merchant_id);
	public void insertprice(String price,String merchant_uid);
	public void insertgoods(String merchant_id,String[] list,Integer[] glist);
	public void InsertVbank(String merchant_id,String vbanknum,String vbankname,String vbankdate,String vbankholder,String vbankperson,
			String vbankcode);
	/*public int InsertVbankAndUpdateStatus(String merchant_uid, String vbanknum, String vbankname, String vbankdate,
			String vbankholder, String buyer_name, String vbank_code, String status, String imp_uid, String paymethod,
			String merchant_id, String[] list, Integer[] integerilist, String price,String couponid);*/
	public int InsertVbankAndUpdateStatus(Order order,Vbank vbank,String[] booknamelist, Integer[] bookqtylist);
	public void InsertMerchant(String id,String merchant_id,String phoneNumber,String address,String buyer_name,String memo,int price);
	public boolean mobilecheckbymerchantuid(String merchant_uid);
	/*public void updatestatuswebhook(String updatestatus,String merchant_uid,String imp_uid,String paymethod,String price,
			String vbankholder,String vbanknum,String vbankcode);*/
	public void updatestatuswebhook(Order vbankorder,Vbank vbank);
	/* int updatestatusandorder(String status, String merchant_uid, String imp_uid, String paymethod,
			String merchant_id,String[] list, Integer[] integerilist,String price,String vbankholder,
			String vbanknum,String vbankcode,String couponid);*/
	public int updatestatusandorder(Order order,String[] booknamelist,Integer[] bookqtylist);
	public int orderpaycheck(String Userid,String price,String coupon);
	public boolean unitInStockCheck(List<String> booknamelist,List<Integer> bookqtylist,String merchant_id);
	public boolean unitInStockShortageMobileCheck(String[] booknamelist,Integer[] bookqtylist,String merchant_uid);
}
