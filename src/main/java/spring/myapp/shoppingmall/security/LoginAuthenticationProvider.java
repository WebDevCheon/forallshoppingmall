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

public class LoginAuthenticationProvider implements AuthenticationProvider {  // UserDetail�� ����ü�� UserDetails ��ü��
																			  // �����ͼ� ������ �Ͽ� ������ ���� ���� ����
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	
	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        //�Է��� ID, Password ��ȸ
        String userId = token.getName();
        String userPw = (String)token.getCredentials();

        //UserDetailsService�� ���� DB���� ��ȸ�� �����
        UserDetailsImpl dbUser = (UserDetailsImpl)userDetailsService.loadUserByUsername(userId);
        // ��й�ȣ ��Ī�Ǵ��� Ȯ��
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

