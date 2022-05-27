package spring.myapp.shoppingmall.service;

import java.util.List;
import spring.myapp.shoppingmall.dto.Shoppingbasket;

public interface ShoppingBasketService { // ��ٱ��� ���� �������̽�
	public void setShoppingBasket(int qty,int gid,int price,String User_ID,String name);
	public List<Shoppingbasket> getShoppingBasketList(String User_ID);
	public int checkCartSpace(String Id);
	public void deleteShoppingBasket(int pnum);
	public void deleteAll(String id);
}
