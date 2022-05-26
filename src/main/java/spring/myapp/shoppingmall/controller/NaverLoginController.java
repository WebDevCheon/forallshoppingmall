package spring.myapp.shoppingmall.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.myapp.shoppingmall.dto.CustomUserDetails;
import spring.myapp.shoppingmall.dto.User;
import spring.myapp.shoppingmall.security.LoginAuthenticationProvider;
import spring.myapp.shoppingmall.service.UserServiceImpl;

@Controller
public class NaverLoginController {
	private final String  CLIENT_ID = "Ulrc1qEQrJH7h_kvtKrM";
	private final String CLIENT_SECRET_ID = "4t8KT7jF_p";
	private final String CALLBACK_URL = "https://localhost:8443/shoppingmall/naverlogincallback";	// ���� ����
	//private final String CALLBACK_URL = "https://www.forallshoppingmall.com/naverlogincallback";	// ���� ����
	private Logger logger = LoggerFactory.getLogger(NaverLoginController.class);
	private RequestCache requestCache = new HttpSessionRequestCache();
	private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();
	private String defaultUrl = "https://localhost:8443/shoppingmall/";			// ���� ����
	//private final String defaultUrl = "https://www.forallshoppingmall.com/";	// ���� ����
	
	@Autowired
	private UserServiceImpl userserviceimpl;
	
	@RequestMapping("/naverlogin")	// ���̹� �α��� ��ư�� Ŭ���Ͽ��� �� �̵��� '���̹� �α���' URL�� �����Ͽ� Redirect
	public void NaverLogin(HttpServletRequest request,HttpServletResponse response){
		logger.info("NaverLogin url Request Accepted");
		String token = getToken(request);			// csrf ���� ������ ���� ��ū�� ����
		String loginurl = "https://nid.naver.com/oauth2.0/authorize?client_id=" + CLIENT_ID + "&response_type=code&"
				+ "redirect_uri=" + CALLBACK_URL + "&state=" + token;
		try {
			response.sendRedirect(loginurl);
		} catch(Exception e) {
			logger.info("naverlogin Redirect Exception �߻�");
			logger.info("Error Content : {}",e);
		}
	}
	
	@RequestMapping("/naverlogincallback")	// ���̹� �α��ΰ� ���� ���� ���� ������ �Ϸ�Ǹ� CallBack URL(���� ���θ� Server)�� code���� state ���� URL ���ڿ��� ����
	public String naverlogincallback(@RequestParam String state,@RequestParam String code,@RequestParam(required = false) String error,
			@RequestParam(required = false) String error_description,HttpSession session,HttpServletRequest request,HttpServletResponse response) {
		if(error != null) {
			logger.info("Naver Login Callback Error : {}",error_description);
			return "home";
		}
		String ServerToken = (String)session.getAttribute("state");
		if(!stateCheck(state,ServerToken)) {	// ���̹� ������ ��ū���� ���� ���θ� �������� �߱����� ��ū���� ��
			logger.info("��ū ���� ���� �߻� , Request : {}",request);
			return "home";
		} else {
			try {
				String tokenurl = "https://nid.naver.com/oauth2.0/token?client_id=" + CLIENT_ID + "&client_secret=" 
						+ CLIENT_SECRET_ID + "&grant_type=authorization_code&state=" + (String)session.getAttribute("state") + 
						"&code=" + code;	// Callback���� ���޹��� ������ �̿��Ͽ� ���� ���Ŀ� ���� ��ū�� �߱� ��û�� URL,���� ��ū�� ����ڰ� ������ �Ϸ��ߴٴ� ���� ������ �� �ִ� ���� ����
				URL url = new URL(tokenurl);
				HttpURLConnection con = (HttpURLConnection)url.openConnection();
				con.setDoOutput(true); 				
				con.setInstanceFollowRedirects(false);  
				con.setRequestMethod("GET");
				con.setRequestProperty("Accept-Charset","UTF-8");
				con.setRequestProperty("Content-Type","text/plain;charset=utf-8");
				OutputStream os = con.getOutputStream();
				con.connect();
				StringBuilder sb = new StringBuilder(); 
				String requestString = null;
				if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
					String line = null;  
					while ((line = br.readLine()) != null) {  
						sb.append(line + "\n");  
					}
					br.close();
					requestString = sb.toString();
				}
				os.flush();
				con.disconnect();
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObj = (JSONObject) jsonParser.parse(requestString);
				logger.info("���̹� ���� ��ū ���� : {}",jsonObj);
				String access_token = null;
				String token_type = null;
				access_token = (String)jsonObj.get("access_token");
				token_type = (String)jsonObj.get("token_type");
				try {
					String getprofileurl = "https://openapi.naver.com/v1/nid/me";	// ���� ��ū�� �̿��Ͽ� ������ ���� ��ȸ API�� ȣ��
					url = new URL(getprofileurl);					
					HttpURLConnection GetProfileConnection = (HttpURLConnection)url.openConnection();
					GetProfileConnection.setDoOutput(true); 				
					GetProfileConnection.setInstanceFollowRedirects(false);  
					GetProfileConnection.setRequestMethod("GET");
					GetProfileConnection.setRequestProperty("Authorization",token_type + " " + access_token);
					os = GetProfileConnection.getOutputStream();
					GetProfileConnection.connect();
					sb = new StringBuilder(); 
					if (GetProfileConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(new InputStreamReader(GetProfileConnection.getInputStream(), "utf-8"));
						String line = null;  
						while ((line = br.readLine()) != null) {  
							sb.append(line + "\n");  
						}
						br.close();
						requestString = sb.toString();
					}
					os.flush();
					GetProfileConnection.disconnect();
					jsonObj = (JSONObject) jsonParser.parse(requestString);
					logger.info("���̹� ������ ���� ��ȸ ��� : {}",jsonObj);
					JSONObject profilejson = (JSONObject)jsonObj.get("response");
					logger.info("profilejson : {}",profilejson);
					String navernickname = (String)profilejson.get("nickname");
					if(userserviceimpl.finduserbyid(navernickname) == null) {  // ȸ�������� ���� ���� ���
						User naveruser = new User();
						naveruser.setId(navernickname);
						naveruser.setPassword("forallshoppingmallnaveruserpassword");  //�����ڸ� �ƴ� ��й�ȣ
						naveruser.setAuthorities("ROLE_USER");
						naveruser.setEmail("forallshoppingmallnaveremail");
						naveruser.setAddress("forallshoppingmallnaveraddress");
						naveruser.setPhoneNumber("forallshoppingmallphoneNumber");
						naveruser.setName((String)profilejson.get("name"));
						naveruser.setSex((String)profilejson.get("gender"));
						naveruser.setGrade("Bronze");
						naveruser.setEnabled(0);  //�α��������� ���� ����
						naveruser.setNaver(1);  //���̹��� �������� �α����� 
						userserviceimpl.joinnaveruser(naveruser);
					}
					session.setAttribute("Userid",navernickname);
					User userinfo = new User();
					userinfo.setAuthorities("ROLE_USER");
					userinfo.setEnabled(1);
					userinfo.setPassword(null);		// ���� ���θ� �������� �˾Ƽ� ���̹� ���̵� ���� �α��� ���� ���� Ư���ϰ� ����
					
					// ������ ��ť��Ƽ�� ���ؼ� ���� ���ε�
					SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
					List<SimpleGrantedAuthority> collection = new ArrayList<>();
					collection.add(simpleGrantedAuthority);
					CustomUserDetails customUserDetails = new CustomUserDetails(navernickname,null,collection);
				    Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails.getId(),null, customUserDetails.getAuthorities());
				    SecurityContextHolder.getContext().setAuthentication(authentication);
					
					SavedRequest savedRequest = requestCache.getRequest(request, response);
			    	logger.info("savedRequest: {}",savedRequest); 
			        if(savedRequest != null) { 		// ������ �ʿ��� URL�� ������ �α��� ������ �̵� �ߴٸ�,�ٽ� �� URL�� Redirect
			            String targetUrl = savedRequest.getRedirectUrl(); 
			            redirectStratgy.sendRedirect(request, response, targetUrl); 
			        } else			// ���� ������ URL�� �α��� ���� ����
			            redirectStratgy.sendRedirect(request, response, defaultUrl);
					return "home";
				} catch(Exception e) {
					logger.info("���� ��ū�� �̿��Ͽ� ������ ���� ��ȸ API�� ȣ�� ���� ���� �߻�");
					session.setAttribute("getprofileerror","���̹� ������ ������ �߻��߽��ϴ�.");
					return "home";
				}
			} catch(Exception e) {
				logger.info("���̹� ���̵� ���� ��ū�� �߱� �޴� ���� ���� �߻�");
				session.setAttribute("gettokenerror","���̹� ��ū ������ �߻��߽��ϴ�.");
				return "home";
			}
		}
	}
	
	private String generateState() {	// csrf ������ �����ϱ� ���� ���ø����̼ǿ��� ������ ���� ��ū������ URL���ڵ��� ������ ���� ���
	    SecureRandom random = new SecureRandom();
	    return new BigInteger(130, random).toString(32);
	}
	
	private String getToken(HttpServletRequest request) {	// ��ū�� ���� -> ���߿� ���̹� �������� �� ��ū���� ���� ��ū��(csrf ��� ����)
		String state = generateState();
		request.getSession().setAttribute("state", state);
		return state;
	}
	
	private boolean stateCheck(String ServerState,String sessionstate) {	// ���̹� ������ ��ū���� ���� ���θ� �������� �߱����� ��ū���� ��
		if(ServerState.equals(sessionstate))
			return true;
		else
			return false;
	}
}
