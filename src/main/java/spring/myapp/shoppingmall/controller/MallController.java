package spring.myapp.shoppingmall.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
	private ShoppingBasketImpl shoppingBasketImpl;

	@Autowired
	private CouponServiceImpl couponServiceImpl;

	@Autowired
	private ProductServiceImpl productServiceImpl;

	@Autowired
	private OrderServiceImpl orderServiceImpl;

	@Autowired
	private ReplyServiceImpl replyServiceImpl;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private Paging paging;

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
		if (request.getSession().getAttribute("Userid") != null)
			logger.info("ID : " + request.getSession().getAttribute("Userid") + ", ��з� : {}, �Һз�: {}", bigclass,
					subclass);
		else
			logger.info("��з� : {}, �Һз�: {}", bigclass, subclass);
		
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
			HttpServletRequest request) {
		model.addAttribute("good", productServiceImpl.getProductDetails(Integer.valueOf(goods_id)));
		model.addAttribute("mode", mode);
		logger.info("getreviewlatest : {}", mode);
		if (mode.equals("'getreviewlatest'")) {
			logger.info("replylatest ����");
			pagingModelReplyLatest(model, request, bookname);
		} else {
			logger.info("replyhelp ����");
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
				logger.info("reviewmode : " + reviewmode.equals("getreviewhelp"));
				model.addAttribute("good", productServiceImpl.getProductDetails(Integer.valueOf(goods_id)));
				model.addAttribute("mode", "'getreviewhelp'");
				pagingModelReply(model, request, bookname);
				model.addAttribute("reviewreplylist", productServiceImpl.getReviewReply(bookname));
				return "/menu/getreviewhelp";
			} else {
				logger.info("reviewmode : " + reviewmode.equals("getreviewlatest"));
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

	public static String getToken(JSONObject json, String requestURL) {
		String _token = "";
		try {
			String requestString = "";
			URL url = new URL(requestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			OutputStream os = connection.getOutputStream();
			os.write(json.toString().getBytes());
			connection.connect();
			StringBuilder sb = new StringBuilder();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				requestString = sb.toString();
			}
			os.flush();
			connection.disconnect();
			try {
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObj = (JSONObject) jsonParser.parse(requestString);
				if ((Long) jsonObj.get("code") == 0) {
					logger.info("jsonObj : " + jsonObj);
					JSONObject getToken = (JSONObject) jsonObj.get("response");
					logger.info("getToken : " + getToken);
					logger.info("getToken.get('access_token') : " + getToken.get("access_token"));
					_token = (String) getToken.get("access_token");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			_token = "";
		}
		return _token;
	}

	private int getamount(String merchant_uid) {		// �ֹ� �Ѿ�
		return orderServiceImpl.getPriceByMerchantId(merchant_uid);
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

	@RequestMapping(value = "/iamport-webhook", method = RequestMethod.POST) // ���� ������ �Ա����� ������ �ϰų�,���� ���¿� �ݾ��� �־����� �߻�
	public void webhook(@RequestParam(required = false) String imp_uid,		 // �ٸ� ���� ������ ���� ��� ����
			@RequestParam(required = false) String merchant_uid, HttpServletRequest request,
			HttpServletResponse response, @RequestParam(required = false) String status, Model model) {
		if (status != null && !status.equals("paid")) // ������¿� �Ա��߰ų� ���� �Ϸ�
			return;
		int webhookflag = 0;
		Order order = orderServiceImpl.getMerchantId(merchant_uid);
		if (!order.getPaymethod().equals("vbank")) {
			webhookflag = 0;
			logger.info("������Ʈ ���� ��û stopping.");
			return;
		} else {
			webhookflag = 1;
		}
		if (webhookflag == 1) {
			try {
				JSONObject json = new JSONObject();
				String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
				String imp_secret = URLEncoder.encode(
						"75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
				json.put("imp_key", imp_key);
				json.put("imp_secret", imp_secret);
				String token = getToken(json, "https://api.iamport.kr/users/getToken");
				JSONObject getdata = null;
				try {
					String requestString = "";
					URL url = new URL("https://api.iamport.kr/payments/" + imp_uid);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Authorization", token);
					OutputStream os = connection.getOutputStream();
					connection.connect();
					StringBuilder sb = new StringBuilder();
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(connection.getInputStream(), "utf-8"));
						String line = null;
						while ((line = br.readLine()) != null) {
							sb.append(line + "\n");
						}
						br.close();
						requestString = sb.toString();
					}
					os.flush();
					connection.disconnect();
					JSONParser jsonParser = new JSONParser();
					JSONObject jsonObj = (JSONObject) jsonParser.parse(requestString);
					logger.info("��ȯ�� JSON��ü : {}" + jsonObj);
					if ((Long) jsonObj.get("code") == 0) {
						getdata = (JSONObject) jsonObj.get("response"); // ������ å�� �ֹ� �Ҷ�,������ �Ա� ��û�� �ߴ� ������ �ٽ� ������
						logger.info(
								"getwebhookresponsedata(������Ʈ �����κ��� ������ ���� ���� -> �ٸ� ���� ��İ� ������ �Ա� ���� ����� ���� �����鵵 ����.) ==>> {}",
								getdata);
						int bepaid = getamount(merchant_uid);
						String amount = String.valueOf(getdata.get("amount"));
						String mystatus = String.valueOf(getdata.get("status"));
						if (String.valueOf(getdata.get("vbank_num")) == null) // ������ �Ա��� �ƴϸ� webhook�� �����Ų��. -> ������ �Աݸ�
							return;											  // ������ ����� ���̱� �����̴�.
						String vbankholder = String.valueOf(getdata.get("vbank_holder"));
						String vbanknum = String.valueOf(getdata.get("vbank_num"));
						String vbankcode = String.valueOf(getdata.get("vbank_code"));
						String paymethod = String.valueOf(getdata.get("pay_method"));

						Vbank vbank = new Vbank();
						vbank.setVbanknum(vbanknum);
						vbank.setVbankholder(vbankholder);
						vbank.setVbankcode(vbankcode);

						Order vbankorder = new Order();
						vbankorder.setMerchant_id(merchant_uid);
						vbankorder.setStatus(mystatus);
						vbankorder.setImp_uid(imp_uid);
						vbankorder.setPaymethod(paymethod);
						vbankorder.setPrice(bepaid);

						if (bepaid <= Integer.valueOf(amount) && vbanknum != null) {
							switch (mystatus) {
							case "paid": // ������ �Աݸ� �ش�...
								orderServiceImpl.updateStatusWebhook(vbankorder, vbank); // ���� �Ϸ�� �ٲٱ�
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping("/mobile")	// IMPORT �������� ī��� ������ ������ �Ϸ�� ���Ŀ� ���θ� ������ ���� ������ �����ְ� Redirect �� URL(Mobile���� ������������)
	public String mobile(@RequestParam String coupon, @RequestParam String imp_uid, @RequestParam String merchant_uid,
			HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam String[] booknamelist,
			@RequestParam Integer[] bookqtylist, @RequestParam String amount, HttpSession session) {
		String referer = (String) request.getHeader("REFERER");		// REFERER ��� ���� : ����ڰ� ���࿡ �ֹ� ���߿� �ڷ� ���� ��ư�� ������ �Ǹ�,
		logger.info("referer : {}", referer);						// 				      ��Ȯ�� �ֹ��� DB�� ������ �ȵɼ� �ֱ� ������ ������ �����ϱ� ����
		try {
			JSONObject json = new JSONObject();
			orderServiceImpl.insertPrice(amount, merchant_uid);
			String imp_key = URLEncoder.encode("2645427372556228", "UTF-8");
			String imp_secret = URLEncoder.encode(
					"75USkRpzuQ8T8WeQcJrO1GFKEERYRDAYIuR2lgCQ6LKfHY5THxIJenuS2mRTZsSHWJKiZm967TlPRrJz", "UTF-8");
			json.put("imp_key", imp_key);
			json.put("imp_secret", imp_secret);
			logger.info("Before getToken sessionId : {}", session.getId());
			String token = getToken(json, "https://api.iamport.kr/users/getToken");
			logger.info("After getToken sessionId : {}", session.getId());
			JSONObject getdata = null;
			try {
				String requestString = "";
				URL url = new URL("https://api.iamport.kr/payments/" + imp_uid);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Authorization", token);
				OutputStream os = connection.getOutputStream();
				connection.connect();
				StringBuilder sb = new StringBuilder();
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();
					requestString = sb.toString();
				}
				os.flush();
				connection.disconnect();
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObj = (JSONObject) jsonParser.parse(requestString);
				logger.info("After Payment sessionId : {}", session.getId());
				logger.info("jsonObj {}", jsonObj);
				if ((Long) jsonObj.get("code") == 0) {
					getdata = (JSONObject) jsonObj.get("response");
					logger.info("getmobileresponsedata : {}", getdata);
					int bepaid = getamount(merchant_uid);
					String getamount = String.valueOf(getdata.get("amount"));
					String mystatus = String.valueOf(getdata.get("status"));
					String paymethod = String.valueOf(getdata.get("pay_method"));
					logger.info("getamount(������Ʈ �������� �� ���� �ݾ�) : {}", getamount);
					logger.info("mobile bepaid <= getamount  : {}", (bepaid <= Integer.valueOf(getamount)));
					logger.info("mobile mystatus : {}", mystatus);
					SecurityContextHolder sch = new SecurityContextHolder();
					logger.info("SecurityContextHolder : {}", sch);
					logger.info("SecurityContext : {}", sch.getContext());
					logger.info("Authentication : {}", sch.getContext().getAuthentication());

					if (mystatus.equals("paid")
							&& referer.contains("https://ksmobile.inicis.com/smart/mobileAcsCancel")) {
						return "redirect:/showbasket";
					}
					if (bepaid <= Integer.valueOf(getamount) && merchant_uid.equals(getdata.get("merchant_uid"))) {
						switch (mystatus) {
						case "ready":
							SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Calendar time = Calendar.getInstance();
							String vbank_num = String.valueOf(getdata.get("vbank_num"));
							String vbank_code = String.valueOf(getdata.get("vbank_code"));
							String vbank_date = timeformat.format(time.getTime());
							String vbank_name = String.valueOf(getdata.get("vbank_name"));
							String vbank_holder = String.valueOf(getdata.get("vbank_holder"));
							String vbank_person = String.valueOf(getdata.get("buyer_name"));

							Vbank vbank = new Vbank();
							vbank.setVbanknum(vbank_num);
							vbank.setVbankname(vbank_name);
							vbank.setVbankdate(vbank_date);
							vbank.setVbankholder(vbank_holder);
							vbank.setVbankcode(vbank_code);

							Order vbankorder = new Order();
							vbankorder.setMerchant_id(merchant_uid);
							vbankorder.setName((String) getdata.get("buyer_name"));
							vbankorder.setStatus((String) getdata.get("status"));
							vbankorder.setImp_uid(imp_uid);
							vbankorder.setPaymethod(paymethod);
							vbankorder.setPrice(Integer.valueOf(getamount));
							vbankorder.setCouponid(coupon);

							if (orderServiceImpl.mobileCheckByMerchantUid(merchant_uid)) {
								model.addAttribute("vbank_date", vbank_date);
								model.addAttribute("vbank_holder", vbank_holder); // ������
								model.addAttribute("vbank_num",
										orderServiceImpl.getVbankInfo(merchant_uid).getVbanknum()); // ���� ���¹�ȣ
								model.addAttribute("vbank_name", vbank_name); // ���� �̸�
								model.addAttribute("vbank_code", vbank_code);
								model.addAttribute("Order", orderServiceImpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderServiceImpl.getOrderGoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							}
							int vbankinsertcheck = orderServiceImpl.InsertVbankAndUpdateStatus(vbankorder, vbank,
									booknamelist, bookqtylist);
							if (vbankinsertcheck == 1) {
								model.addAttribute("vbank_date", vbank_date);
								model.addAttribute("vbank_holder", vbank_holder); // ������
								model.addAttribute("vbank_num",
										orderServiceImpl.getVbankInfo(merchant_uid).getVbanknum()); // ���� ���¹�ȣ
								model.addAttribute("vbank_name", vbank_name); // ���� �̸�
								model.addAttribute("vbank_code", vbank_code);
								model.addAttribute("Order", orderServiceImpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderServiceImpl.getOrderGoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							} else {
								return "/error/404code";
							}
						case "paid":
							Order order = new Order();
							order.setMerchant_id(merchant_uid);
							order.setName((String) getdata.get("buyer_name"));
							order.setStatus((String) getdata.get("status"));
							order.setImp_uid(imp_uid);
							order.setPaymethod(paymethod);
							order.setPrice(Integer.valueOf(getamount));
							order.setCouponid(coupon);

							if (orderServiceImpl.mobileCheckByMerchantUid(merchant_uid)) {
								model.addAttribute("Order", orderServiceImpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderServiceImpl.getOrderGoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							}
							int paidcheck = orderServiceImpl.updateStatusAndOrder(order, booknamelist, bookqtylist);
							if (paidcheck == 1) {
								model.addAttribute("Order", orderServiceImpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderServiceImpl.getOrderGoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							} else {
								return "/error/404code";
							}
						case "failed":
							logger.info("mystatus : failed");
							orderServiceImpl.deleteMerchantId((String) getdata.get("merchant_uid"));
							return "redirect:/showbasket";
						}
					} else {
						return "mobilefailed";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		logger.info("{}�� �ֹ� ���� �������� Ȯ��",id);
		List<Order> orders = orderServiceImpl.getOrderInfo(id, curPageNum); // ordertable�� �ֹ� �����͵�
		model.addAttribute("orders", orders);
		List<List<Ordergoods>> ordergoods = new ArrayList<List<Ordergoods>>();
		List<Vbank> vbanks = new ArrayList<Vbank>();
		for (int i = 0; i < orders.size(); i++) {
			ordergoods.add(orderServiceImpl.getOrderGoods(orders.get(i).getMerchant_id())); // �ֹ� ��ȣ�� ��Ų å��
			vbanks.add(orderServiceImpl.getVbankInfo(orders.get(i).getMerchant_id()));
			logger.info("vbanks : {}", vbanks);
			logger.info("orders.get(i).getMerchant_id() : {}", orders.get(i).getMerchant_id());
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
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforOrder(curPageNum, model, id);
		return curPageNum;
	}

	@RequestMapping("/search")				// å�� ã�� ���� URL
	public String search(@RequestParam String subject, @RequestParam(value = "search", required = false) String search,
			HttpServletRequest request, Model model) {
		logger.info("search : {}", search == null);
		logger.info("search : {}", search == "");
		logger.info("search : {}", search == " ");
		pagingModelKwd(subject, search, request, model);
		model.addAttribute("search", search);
		model.addAttribute("subject", subject);
		return "/menu/search";
	}

	@RequestMapping("/jusoPopup")
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
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
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
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
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
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforCoupon(curPageNum, model, Id);
		return curPageNum;
	}

	
	
	private Model pagingModelReply(Model model, HttpServletRequest request, String bookname) { // Ư�� å�� ���� ����� DB���� ��ȸ�Ͽ� ���� ��ȯ
		int curPageNum = 0; // ���� ����ڰ� ���� ������
		curPageNum = pagingRefactoringForReply(curPageNum, request, model, bookname);
		List<Reply> list = paging.reply(curPageNum, bookname);
		logger.info("list : {}", list);
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
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforReply(curPageNum, model, bookname);
		return curPageNum;
	}
	
	private Model pagingModelReplyLatest(Model model, HttpServletRequest request, String bookname) {	// Ư�� å�� ���� ��� �߿� �ֽż��� �ش��ϴ� ��� ���並 DB�� ��ȸ�Ͽ� ���� ��ȯ
		int curPageNum = 0; // ���� ����ڰ� ���� ������
		curPageNum = pagingRefactoringForReplyLatest(curPageNum, request, model, bookname);
		logger.info("curPageNum refactoring : {}", curPageNum);
		List<Reply> list = paging.replylatest(curPageNum, bookname);
		logger.info("list : {}", list);
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
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforReplyLatest(curPageNum, model, bookname);
		return curPageNum;
	}
}