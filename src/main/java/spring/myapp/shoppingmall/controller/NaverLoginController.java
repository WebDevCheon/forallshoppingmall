package spring.myapp.shoppingmall.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.myapp.shoppingmall.dto.Goods;
import spring.myapp.shoppingmall.dto.User;
import spring.myapp.shoppingmall.security.LoginSocialAuthentication;
import spring.myapp.shoppingmall.service.UserServiceImpl;

@Controller
public class NaverLoginController {
	private final String  CLIENT_ID = "Ulrc1qEQrJH7h_kvtKrM";
	private final String CLIENT_SECRET_ID = "4t8KT7jF_p";
	private final String CALLBACK_URL = "https://localhost:8443/shoppingmall/naverlogincallback";
	//private final String CALLBACK_URL = "https://www.forallshoppingmall.com/naverlogincallback";
	private Logger logger = LoggerFactory.getLogger(NaverLoginController.class);
	private RequestCache requestCache = new HttpSessionRequestCache();
	private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();
	private String defaultUrl = "https://localhost:8443/shoppingmall/home";
	//private final String defaultUrl = "https://www.forallshoppingmall.com/home";
	
	@Autowired
	private UserServiceImpl userserviceimpl;
	
	@RequestMapping("/naverlogin")
	public void NaverLogin(HttpServletRequest request,HttpServletResponse response){
		logger.info("naverlogin url request accepted");
		String token = getToken(request);
		String loginurl = "https://nid.naver.com/oauth2.0/authorize?client_id=" + CLIENT_ID + "&response_type=code&"
				+ "redirect_uri=" + CALLBACK_URL + "&state=" + token;
		try {
			response.sendRedirect(loginurl);
		} catch(Exception e) {
			logger.info("naverlogin 장애 발생");
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/naverlogincallback")
	public String naverlogincallback(@RequestParam String state,@RequestParam String code,HttpSession session,
			HttpServletRequest request,HttpServletResponse response) {
		if(!statecheck(state,(String)(session.getAttribute("state")))) {
			logger.info("인증 토큰 에러 발생");
			return "home";
		} else {
			try {
				String tokenurl = "https://nid.naver.com/oauth2.0/token?client_id=" + CLIENT_ID + "&client_secret=" 
						+ CLIENT_SECRET_ID + "&grant_type=authorization_code&state=" + (String)session.getAttribute("state") + 
						"&code=" + code;
				URL url = new URL(tokenurl);
				HttpURLConnection con = (HttpURLConnection)url.openConnection();
				con.setDoOutput(true); 				
				con.setInstanceFollowRedirects(false);  
				con.setRequestMethod("GET");
				con.setRequestProperty("Accept-Charset","UTF-8");
				con.setRequestProperty("Content-Type","text/plain;charset=utf-8");
				OutputStream os= con.getOutputStream();
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
				logger.info("jsonObj : {}",jsonObj);
				String access_token = null;
				String token_type = null;
				access_token = (String)jsonObj.get("access_token");
				token_type = (String)jsonObj.get("token_type");
				try {
					String getprofileurl = "https://openapi.naver.com/v1/nid/me";
					URL url2 = new URL(getprofileurl);
					HttpURLConnection profileconnection = (HttpURLConnection)url2.openConnection();
					profileconnection.setDoOutput(true); 				
					profileconnection.setInstanceFollowRedirects(false);  
					profileconnection.setRequestMethod("GET");
					profileconnection.setRequestProperty("Authorization",token_type + " " + access_token);
					os = profileconnection.getOutputStream();
					profileconnection.connect();
					sb = new StringBuilder(); 
					if (profileconnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(new InputStreamReader(profileconnection.getInputStream(), "utf-8"));
						String line = null;  
						while ((line = br.readLine()) != null) {  
							sb.append(line + "\n");  
						}
						br.close();
						requestString = sb.toString();
					}
					os.flush();
					profileconnection.disconnect();
					logger.info("requestString : {}",requestString);
					jsonObj = (JSONObject) jsonParser.parse(requestString);
					logger.info("jsonObj : {}",jsonObj);
					JSONObject profilejson = (JSONObject)jsonObj.get("response");
					logger.info("profilejson : {}",profilejson);
					String navernickname = (String)profilejson.get("nickname");
					if(userserviceimpl.finduserbyid(navernickname) == null) {  //회원가입을 하지 않은 경우
						User naveruser = new User();
						naveruser.setId(navernickname);
						naveruser.setPassword("forallshoppingmallnaveruserpassword");  //관리자만 아는 비밀번호
						naveruser.setAuthorities("ROLE_USER");
						naveruser.setEmail("forallshoppingmallnaveremail");
						naveruser.setAddress("forallshoppingmallnaveraddress");
						naveruser.setPhoneNumber("forallshoppingmallphoneNumber");
						naveruser.setName((String)profilejson.get("name"));
						naveruser.setSex((String)profilejson.get("gender"));
						naveruser.setGrade("Bronze");
						naveruser.setEnabled(0);  //로그인폼으로 접속 금지
						naveruser.setNaver(1);  //네이버용 계정으로 로그인함 
						userserviceimpl.joinnaveruser(naveruser);
					}
					session.setAttribute("Userid",navernickname);
					User userinfo = new User();
					userinfo.setAuthorities("ROLE_USER");
					userinfo.setEnabled(1);
					userinfo.setPassword(null);
					LoginSocialAuthentication.Authentication(userinfo, session, request);
					
					SavedRequest savedRequest = requestCache.getRequest(request, response);
			    	logger.info("savedRequest: {}",savedRequest); 
			        if(savedRequest!=null){ //인증 구간을 누르고 로그인 폼으로 이동
			            String targetUrl = savedRequest.getRedirectUrl(); 
			            redirectStratgy.sendRedirect(request, response, targetUrl); 
			        } else {			//직접 브라우저 URL로 로그인 폼에 접근
			            redirectStratgy.sendRedirect(request, response, defaultUrl);
			        }
					
					return "home";
				} catch(Exception e) {
					logger.info("네이버 프로필 조회 장애 발생");
					session.setAttribute("getprofileerror","네이버 프로필 에러가 발생했습니다.");
					return "home";
				}
			} catch(Exception e) {
				logger.info("네이버 토큰 조회 장애 발생");
				session.setAttribute("gettokenerror","네이버 토큰 에러가 발생했습니다.");
				return "home";
			}
		}
	}
	
	private String generateState() {
	    SecureRandom random = new SecureRandom();
	    return new BigInteger(130, random).toString(32);
	}
	
	private String getToken(HttpServletRequest request) {
		String state = generateState();
		request.getSession().setAttribute("state", state);
		return state;
	}
	
	private boolean statecheck(String responsestate,String sessionstate) {
		if(responsestate.equals(sessionstate))
			return true;
		else
			return false;
	}
}
