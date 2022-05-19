package spring.myapp.shoppingmall.service;

import java.util.List;

import spring.myapp.shoppingmall.dto.Shoppingbasket;
import spring.myapp.shoppingmall.dto.User;

public interface ShoppingBasketService { // ��ٱ��� ���� �������̽�
	//public void setshoppingbasket(int qty,int gid,int price,String User_ID,String name);
	public void setshoppingbasket(int qty,int gid,int price,String User_ID,String name);
	public List<Shoppingbasket> getshoppingbasketlist(String User_ID);
	public int checkcartspace(String Id);
	public void deleteshoppingbasket(int pnum);
	public void deleteall(String id);
}
