package spring.myapp.shoppingmall.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class AppDaoTest {
	
	private static final Logger logger = LoggerFactory.getLogger(AppDaoTest.class);
	@Mock
	private MallDao mallDao;
	
	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	@DisplayName("setShoppingbasket 테스트")
	public void setShoppingbasketTest() {
		int qty = 8; int goods_id = 34; int price = 100; String User_ID = "admin"; String name = "후가는 유가";
		doNothing().when(mallDao).setShoppingbasket(anyInt(),anyString(),anyInt(),anyInt(),anyString());
		for(int i = 0;i < 3;i++)
			mallDao.setShoppingbasket(goods_id, User_ID, price, qty, name);
		verify(mallDao,times(3)).setShoppingbasket(anyInt(),anyString(),anyInt(),anyInt(),anyString());
	}
	
	@Test
	@DisplayName("getShoppingbasket 테스트")
	public void getShoppingbasketTest() { 	// 장바구니 담았던 물품 정보들 가져오기
		User user = new User();
		List<Shoppingbasket> carts = new ArrayList<Shoppingbasket>();
		Shoppingbasket b1 = new Shoppingbasket();
		Shoppingbasket b2 = new Shoppingbasket();
		Shoppingbasket b3 = new Shoppingbasket();
		carts.add(b1);
		carts.add(b2);
		carts.add(b3);
		user.setCarts(carts);
		List<Shoppingbasket> Shoppingbasketlist = new ArrayList<Shoppingbasket>();  
		for(int i = 0;i < user.getCarts().size();i++)
			Shoppingbasketlist.add(user.getCarts().get(i));
		List<Shoppingbasket> testResultList = new ArrayList<Shoppingbasket>();
		testResultList.add(b1); testResultList.add(b2); testResultList.add(b3);
		when(mallDao.getShoppingbasket("admin")).thenReturn(testResultList);
		assertTrue(mallDao.getShoppingbasket("admin").get(0) == b1);
		assertTrue(mallDao.getShoppingbasket("admin").get(1) == b2);
		assertTrue(mallDao.getShoppingbasket("admin").get(2) == b3);
	}
}

