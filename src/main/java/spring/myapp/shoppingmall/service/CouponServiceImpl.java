package spring.myapp.shoppingmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.dto.Coupon;

@Service
public class CouponServiceImpl implements CouponService{
	@Autowired
	private MallDao Malldao;
	
	@Override
	public void updateusecouponservice(String yes,String cnumber){
		Malldao.updateusecouponservice(yes,cnumber);
	}
	
	@Override
	public Integer usecoupon(String cnumber){
		return Malldao.usecoupon(cnumber);
	}
	
	@Override
	public Integer receivecoupon(String Id){
		return Malldao.receivecoupon(Id);
	}
	
	@Override
	public List<Coupon> getcouponsbyId(String id) {
		return Malldao.getcoupons(id);
	}
	
	@Override
	public void updatediscountpercent(String couponId,String merchant_id){
		Malldao.updatediscountpercent(couponId,merchant_id);
	}
	
	@Override
	public int usedcouponcheckmethod(String couponid) {
		return Malldao.usedcouponcheckmethod(couponid);
	}
	
	@Override
	public int getcouponscountbyuserId(String id) {
		return Malldao.getcouponscountbyuserId(id);
	}
	
	@Override
	public List<Coupon> canreceive(String id) {
		return Malldao.canreceive(id);
	}
}
