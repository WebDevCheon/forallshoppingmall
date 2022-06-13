package spring.myapp.shoppingmall.test;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
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
	
	@Before
	public void start() {
		MockitoAnnotations.initMocks(this);
		logger.info("Test Start...................");
	}
	
	@After
	public void end() {
		logger.info("Test End...................");
	}
	
	@Test
	public void shoppingbasketImplTest() {
		logger.info("==================== setShoppingBasket Method Start ====================");
		int qty = 8; int goods_id = 34; int price = 100; String User_ID = "admin3"; String name = "후가는 유가";
		ShoppingBasketImpl shoppingBasketImpl = mock(ShoppingBasketImpl.class);
		doNothing().when(shoppingBasketImpl).setShoppingBasket(qty, goods_id, price, User_ID, name);
		shoppingBasketImpl.setShoppingBasket(qty, goods_id, price, User_ID, name);
		shoppingBasketImpl.setShoppingBasket(qty, goods_id, price, User_ID, name);
		verify(shoppingBasketImpl,atMost(2)).setShoppingBasket(qty, goods_id, price, User_ID, name);
		logger.info("==================== setShoppingBasket Method End ====================");
	}
}
