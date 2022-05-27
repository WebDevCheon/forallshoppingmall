package spring.myapp.shoppingmall.controller;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.myapp.shoppingmall.dto.User;
import spring.myapp.shoppingmall.service.UserServiceImpl;

@Controller
public class UserController 
{
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@RequestMapping("/joinform")	// ȸ������â���� �̵��ϴ� ������
	public String JoinForm(Model model) {
		model.addAttribute("user",new User());
		return "/join/joinform";
	}
	
	@RequestMapping("/loginForm") // �α��� ������ �̵��ϴ� ������
	public String loginForm(@RequestParam(value = "error",required = false) String error,
			@RequestParam(value = "logout",required = false) String logout,Model model,
			@CookieValue(value = "UserId",required = false) Cookie cookie,
			@RequestParam(value = "duplsession",required = false) String duplsession){			
		if(cookie != null){
			User user = new User();
			user.setId(cookie.getValue());
			model.addAttribute("User",user);
		}
		if(error != null)
			model.addAttribute("error","���̵� �Ǵ� ��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
		if(logout != null)
			model.addAttribute("logout","�α׾ƿ� �Ǿ����ϴ�.");
		if(duplsession != null)
			model.addAttribute("duplsession","�ߺ� �α����� �Ͽ����ϴ�.");
		
		return "/login/loginForm";
	}
	
	@RequestMapping(value = "/join",method = RequestMethod.POST)  // ����ڰ� ȸ������ ��ư�� ��������
	public String join(HttpServletRequest request,@Valid User user,BindingResult result,Model model) {
		if(result.hasErrors()) {
			List<ObjectError> errors = result.getAllErrors();
			for(ObjectError error : errors) 
				logger.info("{}",error.getDefaultMessage());
			return "/join/joinform";
		}
		else if(userServiceImpl.checkAlreadyExistEmail(user.getEmail())) {
			return "/join/joinfailed";
		}
		else {
			try {
				joinbyemail(user.getEmail());  // ����ڰ� ȸ������ ���� �Է��� �̸��Ϸ� ȸ������ ���� ��ȣ�� �߼�
				model.addAttribute("user",user);
				userServiceImpl.joinUser(user);
				return "/join/beforejoinconfirm";
			} catch(Exception e) {
				e.printStackTrace();
				return "/error/404code";
			}
		}
	}
	
	@RequestMapping("/afterjoinconfirm")
	public String afterjoinconfirm(@RequestParam String email) {		// ȸ���� �������Ϸκ��� ȸ�� ���� �Ϸ�
		logger.info("email : {} �������� ������ �Ϸ�Ǿ����ϴ�.",email);
		userServiceImpl.authorizingEmailConfirm(email);
		return "/join/afterjoinconfirm";
	}
	
	private void joinbyemail(String email) throws IOException {
		String setfrom = "1692078@hansung.ac.kr";  // 1692078@gmail.com -> �������� �̸��Ͽ��� ������� �̸��Ϸ� ���� ��ȣ�� �߼�
		String title = "forallshoppingmall ȸ�� ���� ���� �̸��� �Դϴ�."; 	// ���� ����
		String content = //���� ����
				System.getProperty("line.separator") + // ���پ� �ٰ����� �α����� �ۼ�
				System.getProperty("line.separator") +
				"�ȳ��ϼ��� ȸ���� ���� Ȩ�������� ã���ּż� �����մϴ�" +
				System.getProperty("line.separator") +
				System.getProperty("line.separator") +
				//"<a href = 'https://www.forallshoppingmall.com/afterjoinconfirm?email=" + email + "'>ȸ������ �̸��� ���� �Ϸ�</a>" +		// ���� ����
				"<a href = 'https://localhost:8443/shoppingmall/afterjoinconfirm?email=" + email + "'>ȸ������ �̸��� ���� �Ϸ�</a>" +		// ���� ����
				System.getProperty("line.separator") +
				System.getProperty("line.separator");
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			messageHelper.setFrom(setfrom); // �����»�� �����ϸ� �����۵��� ����
			messageHelper.setTo(email);
			messageHelper.setSubject(title); // ���������� ������ �����ϴ�
			messageHelper.setText(content,true); // ���� ����
			mailSender.send(message);
		} catch (Exception e) {
			logger.info("ȸ������ �̸��� �߼� ���� ���� �߻� : {}",e);
		}
	}
	
	@RequestMapping("findId")  //���̵� ã�� ������ �̵�
	public String findId(HttpServletRequest request){
		return "/finduser/email_findId";
	}
	
	@RequestMapping("findPassword")  //��й�ȣ ã�� ������ �̵�
	public String findPassword(HttpServletRequest request) {
		return "/finduser/email_findPw";
	}
	
	@RequestMapping(value = "/authid",method = RequestMethod.POST)  // ���̵� ã�� ���ؼ� ����ڿ��� ���� ��ȣ �̸����� �߼�
	@ResponseBody
	public Integer authFindUserId(HttpServletResponse response_email,@RequestParam String e_mail,
			@RequestParam String phoneNumber,@RequestParam String name,Model model) throws IOException {//(DB�� �ִ� �̸�,�ڵ�����ȣ,�̸��Ϸ� ��ȸ)���� DB�� ����Ǿ� �ִ� �̸����� �´ٸ�,String �̸����� ������,�̸��Ͽ� ���� ��ȣ�� ������.���� DB�� ������ ��ġ���� ������,�׷��� ������ null���� ������
		if(userServiceImpl.authId(e_mail, phoneNumber, name)) {
			Random r = new Random();
			int dice = r.nextInt(4589362) + 49311; // �̸��Ϸ� �޴� �����ڵ� �κ� (����)
			String setfrom = "1692078@hansung.ac.kr";  //1692078@gmail.com
			String title = "���̵� ã�� ���� �̸��� �Դϴ�."; // ����
			String content = //���� ����
					System.getProperty("line.separator") + // ���پ� �ٰ����� �α����� �ۼ�
					System.getProperty("line.separator") +
					"�ȳ��ϼ��� ȸ���� ���� Ȩ�������� ã���ּż� �����մϴ�" +
					System.getProperty("line.separator") +
					System.getProperty("line.separator") +
					" ������ȣ�� " + dice + " �Դϴ�. " +
					System.getProperty("line.separator") +
					System.getProperty("line.separator") +
					"������ ������ȣ�� Ȩ�������� �Է��� �ֽø� �������� �Ѿ�ϴ�."; // ����
			try{
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

				messageHelper.setFrom(setfrom); // �����»�� �����ϸ� �����۵��� ����
				messageHelper.setTo(e_mail);
				messageHelper.setSubject(title); // ���������� ������ �����ϴ�
				messageHelper.setText(content); // ���� ����
				mailSender.send(message);
			} catch (Exception e) {
				logger.info("���̵� ã�� �̸����� �߼� ���� ���� �߻� : " + e);
			}
			model.addAttribute("datacheck","������ �����Ͽ����ϴ�.");
			return dice; //�̸��� ���� ����
		}
		else
			return 0;
	}
	
	@RequestMapping(value = "/authpw",method = RequestMethod.POST)  // ��й�ȣ ã�� ���ؼ� ����ڿ��� ���� ��ȣ �̸����� �߼�
	@ResponseBody
	public Integer authFindUserPassword(HttpServletResponse response_email,@RequestParam String e_mail,@RequestParam String phoneNumber,
			@RequestParam String name,@RequestParam String cId,Model model) throws IOException { //(DB�� �ִ� �̸�,�ڵ�����ȣ,�̸��Ϸ� ��ȸ)���� DB�� ����Ǿ� �ִ� �̸����� �´ٸ�,String �̸����� ������,�̸��Ͽ� ���� ��ȣ�� ������.���� DB�� ������ ��ġ���� ������,�׷��� ������ null���� ������
		if(userServiceImpl.authPw(cId,e_mail, phoneNumber, name)){
			Random r = new Random();
			int dice = r.nextInt(4589362) + 49311; // �̸��Ϸ� �޴� �����ڵ� �κ� (����)

			String setfrom = "1692078@hansung.ac.kr";  //1692078@gmail.com
			String title = "forallshoppingmall ��й�ȣ ã�� �̸��� �Դϴ�."; // ����
			String content = //���� ����
					System.getProperty("line.separator") + // ���پ� �ٰ����� �α����� �ۼ�
					System.getProperty("line.separator") +
					"�ȳ��ϼ��� ȸ���� ���� Ȩ�������� ã���ּż� �����մϴ�" +
					System.getProperty("line.separator") +
					System.getProperty("line.separator") +
					" ������ȣ�� " + dice + " �Դϴ�. " +
					System.getProperty("line.separator") +
					System.getProperty("line.separator") +
					"������ ������ȣ�� Ȩ�������� �Է��� �ֽø� �������� �Ѿ�ϴ�."; // ����
			try {
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
				messageHelper.setFrom(setfrom); // �����»�� �����ϸ� �����۵��� ����
				messageHelper.setTo(e_mail);
				messageHelper.setSubject(title); // ���������� ������ �����ϴ�
				messageHelper.setText(content); // ���� ����
				mailSender.send(message);
			} catch (Exception e) {
				logger.info("��й�ȣ ã�� �̸����� �߼� ���� ���� �߻� : " + e);
			}
			return dice; // �̸��� ���� ����
		}
		else {
			model.addAttribute("error","������ ��ȿ���� �ʽ��ϴ�.�ٽ� �Է����ֽʽÿ�.");
			return 0; //�̸��� ���� ����
		}
	}
	
	@RequestMapping(value = "/findUserId",method = RequestMethod.POST)	// ���̵� ã�� ������ ���� ������ �������� ���̵� ã��
	public String findUserId(@RequestParam String name,@RequestParam String phoneNumber,Model model,HttpServletRequest request){
		logger.info("�̸� : {},�ڵ��� ��ȣ : {} ������ ���̵� ã�⸦ �õ�",name,phoneNumber);
		userServiceImpl.find(name,phoneNumber,model);
		return "/finduser/findIdResult";
	}
	
	@RequestMapping(value = "/findUserPw",method = RequestMethod.POST)	// ��й�ȣ ã�� �����κ��� ���� ������ �������� ��й�ȣ ã��
	public String findUserPw(@RequestParam String cId,Model model){
		logger.info("���̵� : {} ������ ��й�ȣ ã�⸦ �õ�",cId);
		userServiceImpl.find(cId,model);
		return "/finduser/findPwResult";
	}

	@RequestMapping(value = "/repassword",method = RequestMethod.POST) //��й�ȣ ã�� ��� ��й�ȣ �ٲٱ�
	public String repassword(@RequestParam String userId,@RequestParam String repw){
		logger.info("��й�ȣ ã�⿡�� ��й�ȣ ������Ʈ - ���� ���̵� : {},���� ��й�ȣ : {}",userId,repw);
		userServiceImpl.execute(userId,repw);
		return "redirect:/home";
	}
	
	@RequestMapping("/check")
	@ResponseBody
	public int check(HttpServletRequest request,@RequestParam String id){
		int count = 0;
		count = userServiceImpl.execute(id); 
		return count;
	}
	
	@RequestMapping("/updatePasswordForm")	// ��й�ȣ �ٲٱ� ������ �̵�
	public String updatePasswordForm(){
		return "updatePasswordForm";
	}
	
	@RequestMapping(value = "/updatePassword",method = RequestMethod.POST)	// ������� ��й�ȣ�� ����
	public ModelAndView updatePassword(@RequestParam String Userid,@RequestParam String password,
			@RequestParam String nowPassword,RedirectAttributes redirectAttributes){		
		logger.info("���̵� : {},��й�ȣ : {} ������ ��й�ȣ ������Ʈ �õ�",Userid,password);
		if(!userServiceImpl.checkNowPassword(Userid, nowPassword)){
			ModelAndView mv = new ModelAndView(); 
			mv.setViewName("redirect:/updatePasswordForm"); 
			redirectAttributes.addFlashAttribute("error","���� ��й�ȣ�� Ʋ�Ƚ��ϴ�");
			return mv;
		}
		userServiceImpl.update(Userid,password);
		ModelAndView mv = new ModelAndView(); 
		mv.setViewName("home"); 
		return mv;
	}
}
