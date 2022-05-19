package spring.myapp.shoppingmall.service;

import java.util.HashMap;
import java.util.List;

import spring.myapp.shoppingmall.dto.Goods;
import spring.myapp.shoppingmall.dto.Monthbook;
import spring.myapp.shoppingmall.dto.Refund;

public interface AdminService {
	public void register(Goods goods);
	public void updatestatuscancel(String merchant_id,String cancel);
	public void uploadProflie(int goods_id,String uploadurl);
	public Refund getrefund(String merchant_id);
	public List<Goods> getmonthbooklist(int curPageNum);
	public void setmonthbooklist(List<String> selectedbooklist);
	public void downmonthbooklist();
	public Goods findbook(String name);
	public void settodaybookselect(int bookid);
	public void purchasecancel(HashMap<String, Object> map);
}