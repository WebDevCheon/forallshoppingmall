package spring.myapp.shoppingmall.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import spring.myapp.shoppingmall.dto.CustomUserDetails;
import spring.myapp.shoppingmall.dto.User;

public class LoginSocialAuthentication {
	private static final Logger logger = LoggerFactory.getLogger(LoginSocialAuthentication.class);
	
	public static void Authentication(User userinfo,HttpSession session,HttpServletRequest request){
		CustomUserDetails customdetails = new CustomUserDetails();
		customdetails.setId(userinfo.getId());
		customdetails.setPw(userinfo.getPassword());
		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));
		UsernamePasswordAuthenticationToken authentication  = new UsernamePasswordAuthenticationToken(customdetails,customdetails.getPassword(),roles);
		authentication.setDetails(customdetails);
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authentication);
		session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT",securityContext); 
		logger.info("authentication2 : {}",authentication.getCredentials());
		logger.info("principal : {}",authentication.getPrincipal());
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		logger.info("authorities : {}",authorities);
	}
}

