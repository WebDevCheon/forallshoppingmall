package spring.myapp.shoppingmall.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Exception {
	
	@RequestMapping("/access_denied_page")	 // ���� �ź� ������
	public String access_denied_page() {
		return "/error/access_denied_page";
	}
	
	@RequestMapping("/error")	// ���� ������
	public String error() {
		return "/error/404code";
	}
}
