package spring.myapp.shoppingmall.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import spring.myapp.shoppingmall.dto.Coupon;
import spring.myapp.shoppingmall.dto.Goods;
import spring.myapp.shoppingmall.dto.Order;
import spring.myapp.shoppingmall.dto.Ordergoods;
import spring.myapp.shoppingmall.dto.Reply;
import spring.myapp.shoppingmall.dto.Shoppingbasket;
import spring.myapp.shoppingmall.dto.User;
import spring.myapp.shoppingmall.dto.Vbank;
import spring.myapp.shoppingmall.paging.Paging;
import spring.myapp.shoppingmall.service.CouponServiceImpl;
import spring.myapp.shoppingmall.service.OrderServiceImpl;
import spring.myapp.shoppingmall.service.ProductServiceImpl;
import spring.myapp.shoppingmall.service.ReplyServiceImpl;
import spring.myapp.shoppingmall.service.ShoppingBasketImpl;
import spring.myapp.shoppingmall.service.UserServiceImpl;

@Controller
public class MallController {
	private static final Logger logger = LoggerFactory.getLogger(MallController.class);

	@Autowired
	private ShoppingBasketImpl shoppingBasketImpl;		// ��ٱ��� ���

	@Autowired
	private CouponServiceImpl couponServiceImpl;	// ���� ���

	@Autowired
	private ProductServiceImpl productServiceImpl;	// å�� ���� ��ȸ ���

	@Autowired
	private OrderServiceImpl orderServiceImpl;	// �ֹ�,���� ���

	@Autowired
	private ReplyServiceImpl replyServiceImpl;	// å�� ��� ���

	@Autowired
	private UserServiceImpl userServiceImpl;	// ������ ���� ��ȸ ���

	@Autowired
	private Paging paging;	// ����¡ ���

	@RequestMapping("/") // Ȩ������
	public String home(HttpSession session, Model model, HttpServletResponse response,
			@CookieValue(value = "JSESSIONID", required = false) String JSESSIONID) {
		return "home";
	}

	@RequestMapping("/shopentrance")			// å �� ���� ���� �ִ� ��ü���� å���� �Ұ� ������
	public String shopdefault(Model model) {
		model.addAttribute("monthlist", productServiceImpl.getMonthBookList()); // �̴��� �α� ����
		model.addAttribute("todaybook", productServiceImpl.getTodayBook()); 	// ������ �α� ����
		return "/menu/shopentrance";
	}

	@RequestMapping("/shop") 					// å ī�װ� ����Ʈ
	public String shop(@RequestParam(value = "bigclass", required = false) String bigclass,
			@RequestParam(value = "subclass", required = false) String subclass, Model model,
			HttpServletRequest request) {
		if (bigclass == null || subclass == null) {
			bigclass = "novel"; 	 // å�� ��з�
			subclass = "japannovel"; // å�� �Һз�
		}
		pagingModelWithBook(model, request, bigclass, subclass); // å ��ǰ���� ����¡ó��
		model.addAttribute("bigclass", bigclass);
		model.addAttribute("subclass", subclass);
		return "/menu/shop"; 		// å ��� view
	}

	@RequestMapping("/product") 	// å �󼼺���
	public String product(@RequestParam(value = "bookname", required = false) String bookname, // gId�� å�� �̸�
			@RequestParam(value = "goods_id", required = false) String goods_id,
			@RequestParam(value = "mode", required = false) String mode, Model model, HttpSession session,
			HttpServletRequest request) {		// mode�� å�� ����� �ֽż����� ���� ��������� ���� ����
		model.addAttribute("good", productServiceImpl.getProductDetails(Integer.valueOf(goods_id)));
		model.addAttribute("mode", mode);
		if (mode.equals("'getreviewlatest'")) {
			logger.info("���� �ֽż� ��ȸ");
			pagingModelReplyLatest(model, request, bookname);
		} else {
			logger.info("���� ����� ��ȸ");
			pagingModelReply(model, request, bookname);
		}
		model.addAttribute("reviewreplylist", productServiceImpl.getReviewReply(bookname));
		return "/menu/product";
	}

	@RequestMapping("/review/{reviewmode}")		// ����ڰ� å�� ���並 �ֽż� �Ǵ� ��������� ������ �Ҷ� �����ؼ� �������� ��ȯ���ִ� ReqestMapping
	public String getreviewhelp(@PathVariable String reviewmode,
			@RequestParam(value = "bookname", required = false) String bookname,
			@RequestParam(value = "goods_id", required = false) String goods_id, Model model,
			HttpServletRequest request) {
			if(reviewmode.equals("getreviewhelp")) {
				model.addAttribute("good", productServiceImpl.getProductDetails(Integer.valueOf(goods_id)));
				model.addAttribute("mode", "'getreviewhelp'");
				pagingModelReply(model, request, bookname);
				model.addAttribute("reviewreplylist", productServiceImpl.getReviewReply(bookname));
				return "/menu/getreviewhelp";
			} else {
				model.addAttribute("good", productServiceImpl.getProductDetails(Integer.valueOf(goods_id)));
				model.addAttribute("mode", "'getreviewlatest'");
				pagingModelReplyLatest(model, request, bookname);
				model.addAttribute("reviewreplylist", productServiceImpl.getReviewReply(bookname));
				return "/menu/getreviewlatest";
			}
	}

	@RequestMapping("/showbasket") 	// ������ ��ٱ��Ͽ� ���� å ��ȸ
	public String showbasket(Model model, HttpSession session) {
		String UserId = (String) (session.getAttribute("Userid"));
		List<Shoppingbasket> shoppingbaskets = shoppingBasketImpl.getShoppingBasketList(UserId); // ������ ���� ��ٱ��� ����� ��´�.
		User user = userServiceImpl.findUserById(UserId);
		model.addAttribute("list", shoppingbaskets);
		model.addAttribute("User", user);
		return "/order/shoppingbasketdesign";
	}

	@RequestMapping("/OrderResult")		// �ֹ� ��� ������
	public String orderresult(@RequestParam String merchant_id, Model model, Vbank vbank) {
		if (vbank.getVbanknum() != null) {
			model.addAttribute("vbank_date", vbank.getVbankdate());
			model.addAttribute("vbank_code", vbank.getVbankcode()); // ���� �ڵ� ��ȣ
			model.addAttribute("vbank_holder", vbank.getVbankholder()); // ������
			model.addAttribute("vbank_num", vbank.getVbanknum()); // ���� ���¹�ȣ
			model.addAttribute("vbank_name", vbank.getVbankname()); // ���� �̸�
		}
		if (orderServiceImpl.getMerchantId(merchant_id) != null)
			model.addAttribute("Order", orderServiceImpl.getMerchantId(merchant_id));
		model.addAttribute("Ordergoods", orderServiceImpl.getOrderGoods(merchant_id));
		return "/order/orderresult";
	}

	@RequestMapping("/showorder")		// ����ڰ� �ֹ��ߴ� ���� ������
	public String showorder(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		String id = (String) session.getAttribute("Userid");
		int curPageNum; 	// ���� ����ڰ� ���� ������
		String page = request.getParameter("page");
		logger.info("page : {}",page);
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; // ó�� �������� ���� ���(����¡ ó���� �������� ������ �ʾ��� ���)
		pagingRefactoringForOrder(curPageNum, request, model, id);	// ���� �ֹ� ������ ���� �������� ���� Block�� ù��° ���ڿ� ������ ���ڸ� ���� �Ӽ����� ���� -> ����¡ ó��
		List<Order> orders = orderServiceImpl.getOrderInfo(id, curPageNum); // ordertable�� �ֹ� �����͵�
		model.addAttribute("orders", orders);
		List<List<Ordergoods>> ordergoods = new ArrayList<List<Ordergoods>>();
		List<Vbank> vbanks = new ArrayList<Vbank>();
		for (int i = 0; i < orders.size(); i++) {
			ordergoods.add(orderServiceImpl.getOrderGoods(orders.get(i).getMerchant_id())); // �ֹ� ��ȣ�� ��Ų å��
			vbanks.add(orderServiceImpl.getVbankInfo(orders.get(i).getMerchant_id()));
		}
		model.addAttribute("ordergoods", ordergoods);
		model.addAttribute("vbanks", vbanks);
		return "/order/orderinfo";
	}
	
	private int pagingRefactoringForOrder(int curPageNum, HttpServletRequest request, Model model, String id) {	// ���� �ֹ� ������ ���� �������� ���� Block�� ù��° ���ڿ� ������ ���ڸ� ���� �Ӽ����� ����
		String page = request.getParameter("page");	
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; 	// ó�� �������� ���� ���(����¡ ó���� �������� ������ �ʾ��� ���)
		paging.pagingforOrder(curPageNum, model, id);
		return curPageNum;
	}

	@RequestMapping("/search")				// å�� �˻�
	public String search(@RequestParam String subject, @RequestParam(value = "search", required = false) String search,
			HttpServletRequest request, Model model) {
		pagingModelKwd(subject, search, request, model);
		model.addAttribute("search", search);
		model.addAttribute("subject", subject);
		return "/menu/search";
	}

	@RequestMapping("/jusoPopup")		// �ּ� ã�� API
	public String jusoPopup() {
		return "jusoPopup";
	}

	@RequestMapping(value = "/updatediscountpercent", method = RequestMethod.POST)
	public void updatediscountpercent(@RequestParam String couponId, @RequestParam String merchant_id) {
		couponServiceImpl.updateDiscountPercent(couponId, merchant_id);
	}

	@RequestMapping("/showcoupon") // ���� ���� ��ȸ
	public String showcoupon(HttpServletRequest request, HttpSession session, Model model) {
		String Id = (String) (session.getAttribute("Userid"));
		pagingModelCoupon(model, request, Id);
		return "showcoupon";
	}

	private Model pagingModelKwd(String subject, String search, HttpServletRequest request, Model model) { // ������� å �˻� ����Ʈ ���� DB���� ��ȸ�Ͽ� ��ȯ
		int curPageNum = 0; 	// ���� ����ڰ� ���� ������
		logger.info("����� �˻� Ű���� : {}", search);
		curPageNum = pagingRefactoringForSearch(curPageNum, request, model, subject, search);	// ���� �ش��ϴ� �������� ���� block�� ù��° ����,������ ���� ���
		List<Goods> list = paging.dtoWithKwd(curPageNum, search, subject);		// ����ڰ� �˻��� Ű���忡 �ش��ϴ� å ����Ʈ�� DB���� ��ȸ
		model.addAttribute("list", list);
		return model;
	}
	
	private int pagingRefactoringForSearch(int curPageNum, HttpServletRequest request, Model model, String subject,
			String search) {		// ���� ����ڰ� �˻��� �Ϸ��� �������� ���� block�� ù��° ����,������ ���� ���
		String page = request.getParameter("page");
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; 	// ó�� �������� ���� ���(����¡ ó���� �������� ������ �ʾ��� ���)
		paging.pagingforKwd(curPageNum, model, subject, search);
		return curPageNum;
	}
	
	
	
	private Model pagingModelWithBook(Model model, HttpServletRequest request, String bigclass, String subclass) {	// ����ڰ� ��з��߿��� �Һз��� �ش��ϴ� å ����Ʈ�� DB���� ��ȸ�Ͽ� ���� ��ȯ
		int curPageNum = 0; 	// ���� ����ڰ� ���� ������
		curPageNum = pagingRefactoringForBook(curPageNum, request, model, bigclass, subclass);		// ���� �ش��ϴ� �������� ���� block�� ù��° ����,������ ���� ���
		List<Goods> list = paging.dtosWithBook(curPageNum, bigclass, subclass);	// �ش��ϴ� �������� å ����Ʈ�� �̾ƿ�
		model.addAttribute("list", list);
		return model;
	}
	
	private int pagingRefactoringForBook(int curPageNum, HttpServletRequest request, Model model, String bigclass,
			String subclass) {			// ���� �ش��ϴ� �������� ���� block�� ó�� ����,������ ���� ���
		String page = request.getParameter("page");
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; // ó�� �������� ���� ���(����¡ ó���� �������� ������ �ʾ��� ���)
		paging.pagingforBook(curPageNum, model, bigclass, subclass);
		return curPageNum;
	}
	
	
	
	private Model pagingModelCoupon(Model model, HttpServletRequest request, String Id) { // Ư�� ����ڰ� ������ �ִ� ���� ����Ʈ�� DB���� ��ȸ�Ͽ� ���� ��ȯ
		int curPageNum = 0; // ���� ����ڰ� ���� ������
		curPageNum = pagingRefactoringForCoupon(curPageNum, request, model, Id); // ���� Ư�� ����ڰ� ���� ��������Ʈ �߿��� �ش��ϴ� �������� ���� block�� ù��° ����,������ ���� ���
		List<Coupon> list = paging.coupons(curPageNum, Id);
		model.addAttribute("list", list);
		return model;
	}
	
	private int pagingRefactoringForCoupon(int curPageNum, HttpServletRequest request, Model model, String Id) {
		String page = request.getParameter("page");		// ���� Ư�� ����ڰ� ���� ��������Ʈ �߿��� �ش��ϴ� �������� ���� block�� ù��° ����,������ ���� ���
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; // ó�� �������� ���� ���(����¡ ó���� �������� ������ �ʾ��� ���)
		paging.pagingforCoupon(curPageNum, model, Id);
		return curPageNum;
	}

	
	
	private Model pagingModelReply(Model model, HttpServletRequest request, String bookname) { // Ư�� å�� ���� ����� DB���� ��ȸ�Ͽ� ���� ��ȯ
		int curPageNum = 0; // ���� ����ڰ� ���� ������
		curPageNum = pagingRefactoringForReply(curPageNum, request, model, bookname);
		List<Reply> list = paging.reply(curPageNum, bookname);
		model.addAttribute("list", list);
		model.addAttribute("reviewlist", replyServiceImpl.getAllReply(bookname));
		return model;
	}

	private int pagingRefactoringForReply(int curPageNum, HttpServletRequest request, Model model, String bookname) {
		String page = request.getParameter("page");
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; // ó�� �������� ���� ���(����¡ ó���� �������� ������ �ʾ��� ���)
		paging.pagingforReply(curPageNum, model, bookname);
		return curPageNum;
	}
	
	private Model pagingModelReplyLatest(Model model, HttpServletRequest request, String bookname) {	// Ư�� å�� ���� ��� �߿� �ֽż��� �ش��ϴ� ��� ���並 DB�� ��ȸ�Ͽ� ���� ��ȯ
		int curPageNum = 0; // ���� ����ڰ� ���� ������
		curPageNum = pagingRefactoringForReplyLatest(curPageNum, request, model, bookname);
		List<Reply> list = paging.replylatest(curPageNum, bookname);
		model.addAttribute("list", list);
		model.addAttribute("reviewlist", replyServiceImpl.getAllReply(bookname));
		return model;
	}

	private int pagingRefactoringForReplyLatest(int curPageNum, HttpServletRequest request, Model model,
			String bookname) {
		String page = request.getParameter("page");
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; 	// ó�� �������� ���� ���(����¡ ó���� �������� ������ �ʾ��� ���)
		paging.pagingforReplyLatest(curPageNum, model, bookname);
		return curPageNum;
	}
}