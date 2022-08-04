package spring.myapp.shoppingmall.test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.dto.Shoppingbasket;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class AppDaoTest {
	
	@Autowired
	private MallDao mallDao;
	
	@Test
	@Transactional
	@DisplayName("getShoppingbasket �׽�Ʈ")
	public void getShoppingbasketTest() { 	// ��ٱ��� ��Ҵ� ��ǰ ������ ��������
		String userId = "admin";
		mallDao.setShoppingbasket(34, userId, 500, 5, "�İ��� ����");
		mallDao.setShoppingbasket(30, userId, 800, 3, "�쳪���� �ļ���");
		List<Shoppingbasket> getCartList = mallDao.getShoppingbasket(userId);
		assertEquals(getCartList.get(0).getName(),"�İ��� ����");
		assertEquals(getCartList.get(0).getQty(),5);
		assertEquals(getCartList.get(1).getName(),"�쳪���� �ļ���");
		assertEquals(getCartList.get(1).getQty(),3);
	}
}
