package spring.myapp.shoppingmall.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;

@Service
public class LoginSuccessHandler implements AuthenticationSuccessHandler {		// ����Ʈ���� �α��� ������ ����
	private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);
    private String loginidname;
	private String defaultUrl;
    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();

    protected void resultRedirectStrategy(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
         SavedRequest savedRequest = requestCache.getRequest(request, response);
    	 logger.info("savedRequest: {}",savedRequest); 
        if(savedRequest != null) { 	//���� ������ ������ �α��� ������ �̵�
            String targetUrl = savedRequest.getRedirectUrl(); 
            redirectStratgy.sendRedirect(request, response, targetUrl); 
        } else			//���� ������ URL�� �α��� ���� ����
            redirectStratgy.sendRedirect(request, response, defaultUrl);
    }
    
    protected void clearAuthenticationAttributes(HttpServletRequest request) {		// �α��� ���� ����
        HttpSession session = request.getSession(false);
        if(session==null) 
        	return;
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    		Authentication authentication) throws IOException, ServletException {  //�α��� �����Ŀ� ȣ��� �޼ҵ�
    	clearAuthenticationAttributes(request);
    	request.getSession().setAttribute("Userid",request.getParameter("id"));		// ���� Userid �Ӽ����� ������ ���̵� ����
    	if(request.getParameter("id").substring(0,5).equals("admin"))		// �������� ���̵��� ��쿡 admingrp��� ���� �Ӽ����� ���� ����
    		request.getSession().setAttribute("admingrp","admingrp");
    	if(request.getParameter("isRemember") != null && request.getParameter("isRemember").equals("on")){	// ���̵� ����ϱ� ��ư�� ���� ���
    		Cookie cookie = new Cookie("UserId",request.getParameter("id"));		// UserId��� ��Ű���� ���̵� ������Ѽ� Ŭ���̾�Ʈ���� ����
    		cookie.setMaxAge(60*60*24*30);		// ��Ű���� ���� ����
    		response.addCookie(cookie);
    	}
    	resultRedirectStrategy(request, response, authentication);		// ó���� ���Ϳ� ���Ͽ� intercept �Ǿ��� URL�� �ٽ� ������
    }
 
    public String getLoginidname() {
        return loginidname;
    }
 
    public void setLoginidname(String loginidname) {
        this.loginidname = loginidname;
    }
 
    public String getDefaultUrl() {
        return defaultUrl;
    }
 
    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }
}
