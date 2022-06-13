package spring.myapp.shoppingmall.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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

import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	@Before
	public void start() {
		MockitoAnnotations.initMocks(this);
		mock = MockMvcBuilders.webAppContextSetup(wac).build();
		logger.info("Test Start...................");
	}

	@After
	public void end() {
		logger.info("Test End...................");
	}
	
	@Ignore
	@Transactional
	public void joinTest() {
		try {
			logger.info("==================== join Method Start ====================");
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
			logger.info("==================== join Method End ====================");
		} catch (Exception e) {
			logger.info("join Method Error : " + e);
		}
	}
	
	@Test
	@Transactional
	public void shoppingbasketTest() throws Exception {
		logger.info("==================== shoppingbasket Method Start ====================");
		Map<String, Object> jsonObj = new HashMap<String,Object>();
		jsonObj.put("goods_id","34");
		jsonObj.put("price","100");
		jsonObj.put("name","후가는 유가");
		jsonObj.put("qty","5");
		jsonObj.put("userid","admin");
		jsonObj.put("thumbnail","https://localhost:8443/shoppingmall/...");
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(jsonObj);
		
		mock.perform(post("/shoppingbasket")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonString))
					.andDo(print())
					.andExpect(status().isOk());
		logger.info("==================== shoppingbasket Method End ====================");
	}
	
}
