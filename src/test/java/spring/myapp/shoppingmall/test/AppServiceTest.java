package spring.myapp.shoppingmall.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.dto.Shoppingbasket;
import spring.myapp.shoppingmall.service.ShoppingBasketImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class AppServiceTest {
	
	private ShoppingBasketImpl shoppingBasketImpl;
	@Mock
	private MallDao mallDao;
	
	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
		shoppingBasketImpl = new ShoppingBasketImpl();
		shoppingBasketImpl.setMalldao(mallDao);
	}
	
	@Test
	@Transactional
	@DisplayName("setShoppingBasket 테스트")
	public void setShoppingBasketTest() {
		int qty = 8; int goods_id = 34; int price = 100; String User_ID = "admin3"; String name = "후가는 유가";
		doNothing().when(mallDao).setShoppingbasket(goods_id, User_ID, price, qty, name);
		shoppingBasketImpl.setShoppingBasket(qty, goods_id, price, User_ID, name);
		verify(mallDao).setShoppingbasket(goods_id,User_ID, price, qty, name);
	}

	@Test
	@DisplayName("getShoppingBasket 테스트")
	public void getShoppingBasketListTest(){
		String UserId = "admin";
		when(mallDao.getShoppingbasket(UserId)).thenReturn(new ArrayList<Shoppingbasket>());
		List<Shoppingbasket> cartList = shoppingBasketImpl.getShoppingBasketList(UserId);
		assertEquals(cartList.size(),0);
		verify(mallDao).getShoppingbasket(UserId);
	}
	
	@Test
	@DisplayName("checkCartSpace 테스트")
	public void checkCartSpaceTest() {
		String UserId = "admin";
		when(mallDao.cartspace(UserId)).thenReturn(0);
		int cartSpace = shoppingBasketImpl.checkCartSpace(UserId);
		assertEquals(cartSpace,0);
		verify(mallDao).cartspace(UserId);
	}
}
