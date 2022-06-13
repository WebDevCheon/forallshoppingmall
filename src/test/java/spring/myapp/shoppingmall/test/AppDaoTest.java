package spring.myapp.shoppingmall.test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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

import spring.myapp.shoppingmall.dao.MallDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class AppDaoTest {
	
private static final Logger logger = LoggerFactory.getLogger(AppDaoTest.class);
	
	@Before
	public void start() { // 테스트 케이스 작성
		MockitoAnnotations.initMocks(this);
		logger.info("Test Start...................");
	}
	
	@After
	public void end() {
		logger.info("Test End...................");
	}
	
	@Test
	public void setShoppingbasketTest() {
		logger.info("==================== setShoppingBasket Method Start ====================");
		MallDao mallDao = mock(MallDao.class);
		int qty = 8; int goods_id = 34; int price = 100; String User_ID = "admin"; String name = "후가는 유가";
		doNothing().when(mallDao).setShoppingbasket(anyInt(),anyString(),anyInt(),anyInt(),anyString());
		for(int i = 0;i < 3;i++)
			mallDao.setShoppingbasket(goods_id, User_ID, price, qty, name);
		verify(mallDao,times(3)).setShoppingbasket(anyInt(),anyString(),anyInt(),anyInt(),anyString());
		logger.info("==================== setShoppingBasket Method End ====================");
	}
}

