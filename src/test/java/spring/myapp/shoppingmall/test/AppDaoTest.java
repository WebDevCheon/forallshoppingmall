package spring.myapp.shoppingmall.test;

import static org.junit.Assert.assertEquals;
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
import spring.myapp.shoppingmall.dto.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class AppDaoTest {
	
	@Mock
	private MallDao mallDao;
	
	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	@Transactional
	@DisplayName("getShoppingbasket 테스트")
	public void getShoppingbasketTest() { 	// 장바구니 담았던 물품 정보들 가져오기
		User user = new User();
		List<Shoppingbasket> carts = new ArrayList<Shoppingbasket>();
		Shoppingbasket b1 = new Shoppingbasket();
		Shoppingbasket b2 = new Shoppingbasket();
		Shoppingbasket b3 = new Shoppingbasket();
		b1.setPnum(1);
		b2.setPnum(2);
		b3.setPnum(3);
		carts.add(b1);
		carts.add(b2);
		carts.add(b3);
		user.setId("admin");
		user.setCarts(carts);
		List<Shoppingbasket> getCartList = mallDao.getShoppingbasket(user.getId());
		for(int i = 0;i < getCartList.size();i++)
			assertEquals(getCartList.get(i).getPnum(),carts.get(i).getPnum());
	}
}
