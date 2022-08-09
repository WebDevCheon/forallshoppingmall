package spring.myapp.shoppingmall.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import spring.myapp.shoppingmall.service.UserDetailsImpl;
import spring.myapp.shoppingmall.service.UserDetailsServiceImpl;

public class LoginAuthenticationProvider implements AuthenticationProvider {
	
	// UserDetail�� ����ü�� UserDetails ��ü�� �����ͼ� ������ �Ͽ� ������ ���� ���� ����
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;

	private SecurityContext securityContext = SecurityContextHolder.getContext();	// �׽�Ʈ �ڵ� �ۼ� ���ؼ� getContext �Ҵ� ��ġ �ٲ�
	
	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {	// authentication : Ŭ���̾�Ʈ�����κ��� ���� id,password ���� ���� ���� ��ü
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        
        //�Է��� ID, Password ��ȸ
        String userId = token.getName();
        String userPw = (String)token.getCredentials();
        
        //UserDetailsService�� ���� DB���� ��ȸ�� �����
        UserDetailsImpl dbUser = (UserDetailsImpl)userDetailsService.loadUserByUsername(userId);
        
        if(dbUser == null)	// ���� Ȯ�� �Ұ�
        	throw new UsernameNotFoundException("DB User Not Found"); 	
        else if(dbUser.getUser().getEmailconfirm() == 0)	// ���� �̸��� ���� �ȵ�
        	throw new LockedException("this User Locked Because of email auth");
        else if(dbUser.getUser().getEnabled() == 0)			// ���� ���
        	throw new DisabledException("this User Locked");
        else if(!passwordEncoder.matches(userPw, dbUser.getPassword()))	// ��й�ȣ ����ġ
        	throw new BadCredentialsException(dbUser.getUsername() + "Not Matched Password");	
        securityContext.setAuthentication(authentication);
        return new UsernamePasswordAuthenticationToken(dbUser, userPw, dbUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

