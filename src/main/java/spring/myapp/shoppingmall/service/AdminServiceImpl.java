package spring.myapp.shoppingmall.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.dto.Goods;
import spring.myapp.shoppingmall.dto.Monthbook;
import spring.myapp.shoppingmall.dto.Refund;

@Service
public class AdminServiceImpl implements AdminService{
	@Autowired
	private MallDao Malldao;
	
	@Override
	public void register(Goods goods) {
		Malldao.uploadgoods(goods);
	}
	
	@Override
	public void updatestatuscancel(String merchant_id,String cancel) {
		Malldao.statusupdatecancel(merchant_id,cancel);
	}
	
	@Override
	public void uploadProflie(int goods_id,String uploadurl) {
		Malldao.uploadProfilegoods(goods_id,uploadurl);
	}
	
	@Override
	public Refund getrefund(String merchant_id) {
		return Malldao.getrefund(merchant_id);
	}
	
	@Override
	public List<Goods> getmonthbooklist(int curPageNum){
		return Malldao.getmonthbooklist(curPageNum);
	}
	
	@Override
	public void setmonthbooklist(List<String> selectedbooklist) {
		Malldao.setmonthbooklist(selectedbooklist);
	}
	
	@Override
	public void downmonthbooklist() {
		Malldao.downmonthbooklist();
	}

	@Override
	public Goods findbook(String name) {
		return Malldao.findbook(name);
	}
	
	@Override
	public void settodaybookselect(int bookid) {
		Malldao.settodaybookselect(bookid);
	}

	@Override
	public void purchasecancel(HashMap<String, Object> map) {
		Malldao.purchasecancel(map);
	}
}