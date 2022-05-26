package spring.myapp.shoppingmall.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import spring.myapp.shoppingmall.service.UserDetailsImpl;
import spring.myapp.shoppingmall.service.UserDetailsServiceImpl;

public class LoginAuthenticationProvider implements AuthenticationProvider {  // UserDetail의 구현체인 UserDetails 객체를
																			  // 가져와서 인증을 하여 권한을 줄지 말지 결정
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	
	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        //입력한 ID, Password 조회
        String userId = token.getName();
        String userPw = (String)token.getCredentials();

        //UserDetailsService를 통해 DB에서 조회한 사용자
        UserDetailsImpl dbUser = (UserDetailsImpl)userDetailsService.loadUserByUsername(userId);
        // 비밀번호 매칭되는지 확인
        if(!passwordEncoder.matches(userPw, dbUser.getPassword()))
        	throw new BadCredentialsException(dbUser.getUsername() + "Not Matched Password");
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new UsernamePasswordAuthenticationToken(dbUser, userPw, dbUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

