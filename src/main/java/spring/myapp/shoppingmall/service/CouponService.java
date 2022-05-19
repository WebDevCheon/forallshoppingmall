package spring.myapp.shoppingmall.service;

import java.util.List;

import spring.myapp.shoppingmall.dto.Coupon;

public interface CouponService {
	public void updateusecouponservice(String yes,String cnumber);
	public Integer usecoupon(String cnumber);
	public Integer receivecoupon(String Id);
	public List<Coupon> getcouponsbyId(String id);
	public void updatediscountpercent(String couponId,String merchant_id);
	public int usedcouponcheckmethod(String couponid);
	public int getcouponscountbyuserId(String id);
	public List<Coupon> canreceive(String id);
}
