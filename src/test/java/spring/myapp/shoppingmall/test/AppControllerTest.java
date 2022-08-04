package spring.myapp.shoppingmall.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import spring.myapp.shoppingmall.controller.PaymentAndRefundController;
import spring.myapp.shoppingmall.dto.Bookrecommend;
import spring.myapp.shoppingmall.dto.Order;
import spring.myapp.shoppingmall.dto.User;
import spring.myapp.shoppingmall.service.CouponServiceImpl;
import spring.myapp.shoppingmall.service.OrderServiceImpl;
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
	private MockMvc paymentMock;
	
	@Mock
	private ShoppingBasketImpl shoppingBasketImpl;
	
	@Mock
	private CouponServiceImpl couponServiceImpl;
	
	@Mock
	private ProductServiceImpl productServiceImpl;
	
	@Mock
	private UserServiceImpl userserviceImpl;
	
	@Mock
	private OrderServiceImpl orderServiceImpl;
	
	@InjectMocks
	private MallRestController mallRestController;
	
	@InjectMocks
	private NaverLoginController naverLoginController;
	
	@InjectMocks
	private PaymentAndRefundController paymentAndRefundController;
	
	private MockHttpSession mockSession;
	private MockHttpSession naverLoginsession;
	
	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
		this.mock = MockMvcBuilders.standaloneSetup(mallRestController).build();
		this.naverMock = MockMvcBuilders.standaloneSetup(naverLoginController).build();
		this.paymentMock = MockMvcBuilders.standaloneSetup(paymentAndRefundController).build();
		mockSession = new MockHttpSession();
		mockSession.setAttribute("Userid","admin");
		naverLoginsession = new MockHttpSession();
		naverLoginsession.setAttribute("state","1234");
	}

	@Ignore
	@DisplayName("shoppingbasket �׽�Ʈ")
	@Transactional
	public void shoppingbasketTest() throws Exception {		// ��ٱ��Ͽ� �ֱ� �׽�Ʈ
		Map<String, Object> jsonObj = new HashMap<String,Object>();
		jsonObj.put("goods_id","34");
		jsonObj.put("price","100");
		jsonObj.put("name","�İ��� ����");
		jsonObj.put("qty","5");
		jsonObj.put("userid","admin");
		jsonObj.put("thumbnail","https://localhost:8443/shoppingmall/...");
		Gson gson = new Gson();
		String jsonString = gson.toJson(jsonObj);
		
		doNothing().when(shoppingBasketImpl).setShoppingBasket(5, 34, 100, "admin", "�İ��� ����");
		
		mock.perform(post("/shoppingbasket")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonString))
					.andDo(print())
					.andExpect(status().isCreated());
		verify(shoppingBasketImpl).setShoppingBasket(5, 34, 100, "admin", "�İ��� ����");
	}

	@Ignore
	@DisplayName("deleteshoppingcart �׽�Ʈ")
	@Transactional
	public void deleteshoppingcartTest() throws Exception {
		doNothing().when(shoppingBasketImpl).deleteShoppingBasket(5);
		mock.perform(post("/deleteshoppingcart")
				.param("pnum", "5"))
				.andExpect(status().isOk())
				.andDo(print());
		verify(shoppingBasketImpl).deleteShoppingBasket(5);
	}
	
	@Ignore
	@DisplayName("usecoupon �׽�Ʈ")
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
	
	@Ignore
	@DisplayName("productrecommend �׽�Ʈ")
	@Transactional
	public void productrecommendTest() throws Exception {
		Gson gson = new Gson();
		int bookId = 55;
		String Userid = "admin";
		Bookrecommend bookRecommend = new Bookrecommend();
		bookRecommend.setUserid(Userid);
		when(productServiceImpl.bookRecommend(any(Bookrecommend.class), any(String.class), any(Integer.class))).thenReturn(0);
		String jsonString = gson.toJson(new Bookrecommend());
		mock.perform(post("/productrecommend")
				.session(mockSession)
				.contentType(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .param("bookid",String.valueOf(bookId)))
				.andExpect(status().isOk())
				.andDo(print());
		verify(productServiceImpl).bookRecommend(refEq(bookRecommend), refEq(Userid), eq(bookId));
	}
	
	@Ignore
	@DisplayName("Naver���� �α��� �׽�Ʈ")
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
	
	@Test
	@DisplayName("���� �׽�Ʈ")
	@Transactional
	public void completeTokenTest() throws Exception {
		String merchant_uid = "24242535532";
		String merchant_id = "24242535532";
		String imp_uid = "imp_3283728472";
		ArrayList<String> booknameList = new ArrayList<String>();
		ArrayList<Integer> bookqtyList = new ArrayList<Integer>();
		booknameList.add("�쳪���� �ļ���"); booknameList.add("�İ��� ����");
		bookqtyList.add(3);	bookqtyList.add(2);
		Map<String, Object> map = new HashMap<>();
		map.put("merchant_uid",merchant_uid);
		map.put("booknamelist",booknameList);
		map.put("bookqtylist",bookqtyList);
		map.put("imp_uid",imp_uid);
		map.put("merchant_id",merchant_id);
		map.put("price",1500);
		map.put("getTokenURL","https://96729ffb-0d4a-4a77-b9e2-c403aaa2cab1.mock.pstmn.io/importTestToken");	// POSTMAN Mock Server�κ��� TOKEN ��ȸ
		map.put("getPayResultURL","https://96729ffb-0d4a-4a77-b9e2-c403aaa2cab1.mock.pstmn.io/");	// POSTMAN Mock Server�κ��� ���� ��� ��ȸ
		Order order = new Order();
		order.setMerchant_id(merchant_uid);
		order.setImp_uid(imp_uid);
		order.setStatus("paid");
		order.setPaymethod("phone");
		order.setPrice(1500);
		order.setName("testId");
		
		String[] booknameArray = new String[booknameList.size()];
		Integer[] bookqtyArray = new Integer[bookqtyList.size()];
		for(int i = 0;i < booknameList.size();i++) {
			booknameArray[i] = booknameList.get(i);
			bookqtyArray[i] = bookqtyList.get(i);
		}
		
		when(orderServiceImpl.getPriceByMerchantId(merchant_uid)).thenReturn(1500);
		when(orderServiceImpl.updateStatusAndOrder(any(Order.class),any(String[].class),any(Integer[].class))).thenReturn(1);
		
	    paymentMock.perform(post("/completeToken")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new Gson().toJson(map)))
				.andExpect(status().isOk())
				.andDo(print());
	    
	    verify(orderServiceImpl).getPriceByMerchantId(merchant_uid);
	    verify(orderServiceImpl).updateStatusAndOrder(refEq(order), refEq(booknameArray), refEq(bookqtyArray));
	}
}
