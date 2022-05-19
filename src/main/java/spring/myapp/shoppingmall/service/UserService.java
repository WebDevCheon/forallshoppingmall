package spring.myapp.shoppingmall.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import spring.myapp.shoppingmall.dto.User;

public interface UserService {
	public boolean authId(String e_mail,String phoneNumber,String name);
	public boolean authPw(String cId,String e_mail,String phoneNumber,String name);
	public void joinuser(User user);
	public void joinnaveruser(User naveruser);
	public int execute(String id);
	public boolean checkNowPassword(String Userid,String Password);
	public void find(String name,String phoneNumber,Model model);
	public void find(String cId,Model model);
	public void update(String Userid,String Password);
	public void execute(String userId,String repassword);
	public User finduserbyid(String id);
	public void authorizingemailconfirm(String email);
	boolean checkalreadyexistemail(String email);
}
