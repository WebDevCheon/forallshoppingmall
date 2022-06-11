package spring.myapp.shoppingmall.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/security-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/service-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/dao-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
@WebAppConfiguration
public class AppTest {
	
	private static final Logger logger = LoggerFactory.getLogger(AppTest.class);
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mock;
	
	@Before
	public void start() { // 테스트 케이스 작성
		mock = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@After
	public void end() {
		logger.info("Test End...................");
	}
	
	@Test
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
}
