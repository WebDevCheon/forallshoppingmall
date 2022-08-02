package spring.myapp.shoppingmall.test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;

import spring.myapp.shoppingmall.service.ShoppingBasketImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class AppControllerTest {
	
	private static final Logger logger = LoggerFactory.getLogger(AppControllerTest.class);
	
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mock;
	
	@Mock
	private ShoppingBasketImpl shoppingBasketImpl;
	
	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
		mock = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Ignore
	@DisplayName("join 테스트")
	@Transactional
	public void joinTest() throws Exception {		// 회원가입 테스트
		mock.perform(post("/join")
				.param("id","testAdmin")
				.param("password","123456")
				.param("name","adminTest")
				.param("address","testAddress")
				.param("sex","M")
				.param("age","20")
				.param("phoneNumber","01012345678")
				.param("email","test@naver.com"))
	            .andDo(print())
	            .andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("shoppingbasket 테스트")
	@Transactional
	public void shoppingbasketTest() throws Exception {		// 장바구니에 넣기 테스트
		Map<String, Object> jsonObj = new HashMap<String,Object>();
		jsonObj.put("goods_id","34");
		jsonObj.put("price","100");
		jsonObj.put("name","후가는 유가");
		jsonObj.put("qty","5");
		jsonObj.put("userid","admin");
		jsonObj.put("thumbnail","https://localhost:8443/shoppingmall/...");
		Gson gson = new Gson();
		String jsonString = gson.toJson(jsonObj);
		
		doNothing().when(shoppingBasketImpl).setShoppingBasket(5, 34, 100, "admin", "후가는 유가");
		
		mock.perform(post("/shoppingbasket")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonString))
					.andDo(print())
					.andExpect(status().isCreated());
		shoppingBasketImpl.setShoppingBasket(5, 34, 100, "admin", "후가는 유가");
		verify(shoppingBasketImpl).setShoppingBasket(5, 34, 100, "admin", "후가는 유가");
	}
	
}
