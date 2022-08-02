package spring.myapp.shoppingmall.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atMost;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.dto.Shoppingbasket;
import spring.myapp.shoppingmall.dto.User;
import spring.myapp.shoppingmall.service.ShoppingBasketImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class AppServiceTest {
	
	private static final Logger logger = LoggerFactory.getLogger(AppServiceTest.class);
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
		Shoppingbasket b1 = new Shoppingbasket();
		Shoppingbasket b2 = new Shoppingbasket();
		Shoppingbasket b3 = new Shoppingbasket();
		List<Shoppingbasket> testResultList = new ArrayList<Shoppingbasket>();
		testResultList.add(b1); 
		testResultList.add(b2); 
		testResultList.add(b3);
		when(mallDao.getShoppingbasket("admin")).thenReturn(testResultList);
		List<Shoppingbasket> result = shoppingBasketImpl.getShoppingBasketList("admin");
		assertTrue(result.get(0) == b1);
		assertTrue(result.get(1) == b2);
		assertTrue(result.get(2) == b3);
		verify(mallDao).getShoppingbasket("admin");
	}
	
	@Test
	@DisplayName("checkCartSpace 테스트")
	public void checkCartSpaceTest() {
		User user = new User();
		user.setCarts(new ArrayList<Shoppingbasket>());
		user.setCarts(new ArrayList<Shoppingbasket>());
		user.setCarts(new ArrayList<Shoppingbasket>());
		when(shoppingBasketImpl.checkCartSpace("admin")).thenReturn(3);
		assertTrue(shoppingBasketImpl.checkCartSpace("admin") == 3);
	}
}
