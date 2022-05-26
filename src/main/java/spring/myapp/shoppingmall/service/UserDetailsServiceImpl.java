package spring.myapp.shoppingmall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import spring.myapp.shoppingmall.dao.UserDao;
import spring.myapp.shoppingmall.dto.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
    private UserDao userDao;

    @Override
    public UserDetailsImpl loadUserByUsername(String id) {
        User user =  userDao.getUser(id);
        if (user == null)
            return null;
        return new UserDetailsImpl(user);
    }
}