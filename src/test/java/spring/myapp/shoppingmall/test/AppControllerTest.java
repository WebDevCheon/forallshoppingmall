package spring.myapp.shoppingmall.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import spring.myapp.shoppingmall.controller.MallRestController;
import spring.myapp.shoppingmall.controller.NaverLoginController;
import spring.myapp.shoppingmall.dto.Bookrecommend;
import spring.myapp.shoppingmall.dto.User;
import spring.myapp.shoppingmall.service.CouponServiceImpl;
import spring.myapp.shoppingmall.service.ProductServiceImpl;
import spring.myapp.shoppingmall.service.ShoppingBasketImpl;
import spring.myapp.shoppingmall.service.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class AppControllerTest {
	
	private MockMvc mock;
	private MockMvc naverMock;
	
	@Mock
	private ShoppingBasketImpl shoppingBasketImpl;
	
	@Mock
	private CouponServiceImpl couponServiceImpl;
	
	@Mock
	private ProductServiceImpl productServiceImpl;
	
	@Mock
	private UserServiceImpl userserviceImpl;
	
	@InjectMocks
	private MallRestController mallRestController;
	
	@InjectMocks
	private NaverLoginController naverLoginController;
	
	private MockHttpSession mockSession;
	private MockHttpSession naverLoginsession;
	
	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
		this.mock = MockMvcBuilders.standaloneSetup(mallRestController).build();
		this.naverMock = MockMvcBuilders.standaloneSetup(naverLoginController).build();
		mockSession = new MockHttpSession();
		mockSession.setAttribute("Userid","admin");
		naverLoginsession = new MockHttpSession();
		naverLoginsession.setAttribute("state","1234");
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
		verify(shoppingBasketImpl).setShoppingBasket(5, 34, 100, "admin", "후가는 유가");
	}

	@Test
	@DisplayName("deleteshoppingcart 테스트")
	@Transactional
	public void deleteshoppingcartTest() throws Exception {
		doNothing().when(shoppingBasketImpl).deleteShoppingBasket(5);
		mock.perform(post("/deleteshoppingcart")
				.param("pnum", "5"))
				.andExpect(status().isOk())
				.andDo(print());
		verify(shoppingBasketImpl).deleteShoppingBasket(5);
	}
	
	@Test
	@DisplayName("usecoupon 테스트")
	@Transactional
	public void usecouponTest() throws Exception {
		String cnumber = "1234";
		when(couponServiceImpl.useCoupon(cnumber)).thenReturn(5);
		mock.perform(post("/usecoupon")
				.param("cnumber", cnumber))
				.andExpect(status().isOk())
				.andDo(print());
		verify(couponServiceImpl).useCoupon(cnumber);
	}
	
	@Test
	@DisplayName("productrecommend 테스트")
	@Transactional
	public void productrecommendTest() throws Exception {
		Gson gson = new Gson();
		int bookId = 55;
		String Userid = "admin";
		Bookrecommend bookRecommend = new Bookrecommend();
		when(productServiceImpl.bookRecommend(bookRecommend, Userid, bookId)).thenReturn(0);
		mock.perform(post("/productrecommend")
				.session(mockSession)
				.contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(bookRecommend))
                .param("bookid",String.valueOf(bookId)))
				.andExpect(status().isOk())
				.andDo(print());
		verify(productServiceImpl).bookRecommend(any(Bookrecommend.class), refEq(Userid) , eq(bookId));
	}
	
	@Test
	@DisplayName("NaverLogin 테스트")
	@Transactional
	public void naverLoginControllerTest() throws Exception {
		String testId = "WebDevCheon";
		when(userserviceImpl.findUserById(testId)).thenReturn(new User());
		naverMock.perform(get("/naverlogincallback")
				.session(naverLoginsession)
				.param("state","1234")
				.param("code","1111"))
				.andExpect(status().is3xxRedirection())
				.andDo(print());
		verify(userserviceImpl).findUserById(testId);
	}
}
