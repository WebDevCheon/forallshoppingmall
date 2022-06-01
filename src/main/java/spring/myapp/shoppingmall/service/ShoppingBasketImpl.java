package spring.myapp.shoppingmall.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.dto.Shoppingbasket;

@Service
@Transactional
public class ShoppingBasketImpl implements ShoppingBasketService{
	@Autowired
	private MallDao Malldao;
	
	@Override
	public void setShoppingBasket(int qty,int goods_id,int price,String User_ID,String name) {
		Malldao.setShoppingbasket(goods_id,User_ID, price, qty, name);  //gid ù��°
	}
	
	@Override
	public List<Shoppingbasket> getShoppingBasketList(String User_ID){
		return Malldao.getShoppingbasket(User_ID);  //��ٱ��� ��Ҵ� ��ǰ ������	-> �̰��� ��ٱ��� ���� ���� ���� ��ǰ�� ���� ������ ������..
	}
	
	@Override
	public int checkCartSpace(String Id) {
		return Malldao.cartspace(Id); //��ٱ��� ����Ʈ���� �������� �̰��� ��ٱ��Ͽ� ���� ���� ���� ���� ��ǰ�� ������..
	}
	
	@Override
	public void deleteShoppingBasket(int pnum) {  //��ٱ��� â���� ���� ��ǰ �����ϱ�
		Malldao.deletebasket(pnum);
	}
	
	@Override
	public void deleteAll(String id) { //��ٱ��� â���� ���� ��ǰ ��� �����ϱ�
		Malldao.deleteall(id);
	}
}
