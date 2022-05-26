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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
	private ShoppingBasketImpl shoppingbasketimpl;

	@Autowired
	private CouponServiceImpl couponserviceimpl;

	@Autowired
	private ProductServiceImpl productserviceimpl;

	@Autowired
	private OrderServiceImpl orderserviceimpl;

	@Autowired
	private ReplyServiceImpl replyserviceimpl;

	@Autowired
	private UserServiceImpl userserviceimpl;

	@Autowired
	private Paging paging;

	@RequestMapping("/") // 홈페이지
	public String home(HttpSession session, Model model, HttpServletResponse response,
			@CookieValue(value = "JSESSIONID", required = false) String JSESSIONID) {
		return "home";
	}

	@RequestMapping("/shopentrance")			// 책 상세 보기 전에 있는 전체적인 책들의 소개 페이지
	public String shopdefault(Model model) {
		model.addAttribute("monthlist", productserviceimpl.getmonthbooklist()); // 이달의 인기 서적
		model.addAttribute("todaybook", productserviceimpl.gettodaybook()); 	// 오늘의 인기 서적
		return "/menu/shopentrance";
	}

	@RequestMapping("/shop") 					// 책 카테고리 리스트
	public String shop(@RequestParam(value = "bigclass", required = false) String bigclass,
			@RequestParam(value = "subclass", required = false) String subclass, Model model,
			HttpServletRequest request) {
		if (request.getSession().getAttribute("Userid") != null)
			logger.info("ID : " + request.getSession().getAttribute("Userid") + ", 대분류 : {}, 소분류: {}", bigclass,
					subclass);
		else
			logger.info("대분류 : {}, 소분류: {}", bigclass, subclass);
		
		if (bigclass == null || subclass == null) {
			bigclass = "novel"; 	 // 책의 대분류
			subclass = "japannovel"; // 책의 소분류
		}
		pagingModelWithBook(model, request, bigclass, subclass); // 책 상품들의 페이징처리
		model.addAttribute("bigclass", bigclass);
		model.addAttribute("subclass", subclass);
		return "/menu/shop"; 		// 책 목록 view
	}

	@RequestMapping("/product") 	// 책 상세보기
	public String product(@RequestParam(value = "bookname", required = false) String bookname, // gId는 책의 이름
			@RequestParam(value = "goods_id", required = false) String goods_id,
			@RequestParam(value = "mode", required = false) String mode, Model model, HttpSession session,
			HttpServletRequest request) {
		model.addAttribute("good", productserviceimpl.getproductdetails(Integer.valueOf(goods_id)));
		model.addAttribute("mode", mode);
		logger.info("getreviewlatest : {}", mode);
		if (mode.equals("'getreviewlatest'")) {
			logger.info("replylatest 실행");
			pagingModelReplyLatest(model, request, bookname);
		} else {
			logger.info("replyhelp 실행");
			pagingModelReply(model, request, bookname);
		}
		model.addAttribute("reviewreplylist", productserviceimpl.getreviewreply(bookname));
		return "/menu/product";
	}

	@RequestMapping("/review/{reviewmode}")		// 사용자가 책의 리뷰를 최신순 또는 도움순으로 보려고 할때 선택해서 페이지를 반환해주는 ReqestMapping
	public String getreviewhelp(@PathVariable String reviewmode,
			@RequestParam(value = "bookname", required = false) String bookname,
			@RequestParam(value = "goods_id", required = false) String goods_id, Model model,
			HttpServletRequest request) {
			if(reviewmode.equals("getreviewhelp")) {
				logger.info("reviewmode : " + reviewmode.equals("getreviewhelp"));
				model.addAttribute("good", productserviceimpl.getproductdetails(Integer.valueOf(goods_id)));
				model.addAttribute("mode", "'getreviewhelp'");
				pagingModelReply(model, request, bookname);
				model.addAttribute("reviewreplylist", productserviceimpl.getreviewreply(bookname));
				return "/menu/getreviewhelp";
			} else {
				logger.info("reviewmode : " + reviewmode.equals("getreviewlatest"));
				model.addAttribute("good", productserviceimpl.getproductdetails(Integer.valueOf(goods_id)));
				model.addAttribute("mode", "'getreviewlatest'");
				pagingModelReplyLatest(model, request, bookname);
				model.addAttribute("reviewreplylist", productserviceimpl.getreviewreply(bookname));
				return "/menu/getreviewlatest";
			}
	}

	@RequestMapping("/showbasket") 	// 유저가 장바구니에 담은 책 조회
	public String showbasket(Model model, HttpSession session) {
		String UserId = (String) (session.getAttribute("Userid"));
		List<Shoppingbasket> shoppingbaskets = shoppingbasketimpl.getshoppingbasketlist(UserId); // 유저가 담은 장바구니 목록을 담는다.
		User user = userserviceimpl.finduserbyid(UserId);
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

	private int getamount(String merchant_uid) {		// 주문 총액
		return orderserviceimpl.getpriceBymerchantid(merchant_uid);
	}

	@RequestMapping("/OrderResult")		// 주문 결과 페이지
	public String orderresult(@RequestParam String merchant_id, Model model, Vbank vbank) {
		if (vbank.getVbanknum() != null) {
			model.addAttribute("vbank_date", vbank.getVbankdate());
			model.addAttribute("vbank_code", vbank.getVbankcode()); // 은행 코드 번호
			model.addAttribute("vbank_holder", vbank.getVbankholder()); // 구매자
			model.addAttribute("vbank_num", vbank.getVbanknum()); // 은행 계좌번호
			model.addAttribute("vbank_name", vbank.getVbankname()); // 은행 이름
		}
		if (orderserviceimpl.getMerchantId(merchant_id) != null)
			model.addAttribute("Order", orderserviceimpl.getMerchantId(merchant_id));
		model.addAttribute("Ordergoods", orderserviceimpl.getordergoods(merchant_id));
		return "/order/orderresult";
	}

	@RequestMapping(value = "/iamport-webhook", method = RequestMethod.POST) // 고객이 무통장 입금으로 결제를 하거나,가상 계좌에 금액을 넣었을때 발생
	public void webhook(@RequestParam(required = false) String imp_uid,		 // 다른 결제 수단은 웹훅 사용 안함
			@RequestParam(required = false) String merchant_uid, HttpServletRequest request,
			HttpServletResponse response, @RequestParam(required = false) String status, Model model) {
		if (status != null && !status.equals("paid")) // 가상계좌에 입금했거나 결제 완료
			return;
		int webhookflag = 0;
		Order order = orderserviceimpl.getMerchantId(merchant_uid);
		if (!order.getPaymethod().equals("vbank")) {
			webhookflag = 0;
			logger.info("아임포트 서버 요청 stopping.");
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
					logger.info("반환된 JSON객체 : {}" + jsonObj);
					if ((Long) jsonObj.get("code") == 0) {
						getdata = (JSONObject) jsonObj.get("response"); // 이전에 책을 주문 할때,무통장 입금 신청을 했던 정보를 다시 가져옴
						logger.info(
								"getwebhookresponsedata(아임포트 서버로부터 가져온 결제 정보 -> 다른 결제 방식과 무통장 입금 결제 방식의 결제 정보들도 있음.) ==>> {}",
								getdata);
						int bepaid = getamount(merchant_uid);
						String amount = String.valueOf(getdata.get("amount"));
						String mystatus = String.valueOf(getdata.get("status"));
						if (String.valueOf(getdata.get("vbank_num")) == null) // 무통장 입금이 아니면 webhook을 종료시킨다. -> 무통장 입금만
							return;											  // 웹훅을 사용할 것이기 때문이다.
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
							case "paid": // 무통장 입금만 해당...
								orderserviceimpl.updatestatuswebhook(vbankorder, vbank); // 결제 완료로 바꾸기
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

	@RequestMapping("/mobile")	// IMPORT 서버에서 카드사 서버로 결제가 완료된 이후에 쇼핑몰 서버에 결제 정보를 돌려주고 Redirect 될 URL(Mobile기기로 결제했을때만)
	public String mobile(@RequestParam String coupon, @RequestParam String imp_uid, @RequestParam String merchant_uid,
			HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam String[] booknamelist,
			@RequestParam Integer[] bookqtylist, @RequestParam String amount, HttpSession session) {
		String referer = (String) request.getHeader("REFERER");		// REFERER 사용 이유 : 사용자가 만약에 주문 도중에 뒤로 가기 버튼을 누르게 되면,
		logger.info("referer : {}", referer);						// 				      정확한 주문이 DB에 저장이 안될수 있기 때문에 사전에 차단하기 위함
		try {
			JSONObject json = new JSONObject();
			orderserviceimpl.insertprice(amount, merchant_uid);
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
					logger.info("getamount(아임포트 서버에서 온 결제 금액) : {}", getamount);
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

							if (orderserviceimpl.mobilecheckbymerchantuid(merchant_uid)) {
								model.addAttribute("vbank_date", vbank_date);
								model.addAttribute("vbank_holder", vbank_holder); // 구매자
								model.addAttribute("vbank_num",
										orderserviceimpl.getvbankinfo(merchant_uid).getVbanknum()); // 은행 계좌번호
								model.addAttribute("vbank_name", vbank_name); // 은행 이름
								model.addAttribute("vbank_code", vbank_code);
								model.addAttribute("Order", orderserviceimpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderserviceimpl.getordergoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							}
							int vbankinsertcheck = orderserviceimpl.InsertVbankAndUpdateStatus(vbankorder, vbank,
									booknamelist, bookqtylist);
							if (vbankinsertcheck == 1) {
								model.addAttribute("vbank_date", vbank_date);
								model.addAttribute("vbank_holder", vbank_holder); // 구매자
								model.addAttribute("vbank_num",
										orderserviceimpl.getvbankinfo(merchant_uid).getVbanknum()); // 은행 계좌번호
								model.addAttribute("vbank_name", vbank_name); // 은행 이름
								model.addAttribute("vbank_code", vbank_code);
								model.addAttribute("Order", orderserviceimpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderserviceimpl.getordergoods(merchant_uid));
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

							if (orderserviceimpl.mobilecheckbymerchantuid(merchant_uid)) {
								model.addAttribute("Order", orderserviceimpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderserviceimpl.getordergoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							}
							int paidcheck = orderserviceimpl.updatestatusandorder(order, booknamelist, bookqtylist);
							if (paidcheck == 1) {
								model.addAttribute("Order", orderserviceimpl.getMerchantId(merchant_uid));
								model.addAttribute("Ordergoods", orderserviceimpl.getordergoods(merchant_uid));
								model.addAttribute("method", "mobile");
								return "/order/orderresult";
							} else {
								return "/error/404code";
							}
						case "failed":
							logger.info("mystatus : failed");
							orderserviceimpl.deletemerchantid((String) getdata.get("merchant_uid"));
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

	@RequestMapping("/showorder")		// 사용자가 주문했던 내역 페이지
	public String showorder(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		String id = (String) session.getAttribute("Userid");
		int curPageNum; 	// 현재 사용자가 보는 페이지
		String page = request.getParameter("page");
		logger.info("page : {}",page);
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; // 처음 페이지에 들어가는 경우(페이징 처리된 페이지를 누르지 않았을 경우)
		pagingRefactoringForOrder(curPageNum, request, model, id);	// 고객의 주문 내역을 보는 페이지가 속한 Block의 첫번째 숫자와 마지막 숫자를 모델의 속성값을 넣음 -> 페이징 처리
		logger.info("{}가 주문 내역 페이지를 확인",id);
		List<Order> orders = orderserviceimpl.getorderinfo(id, curPageNum); // ordertable의 주문 데이터들
		model.addAttribute("orders", orders);
		List<List<Ordergoods>> ordergoods = new ArrayList<List<Ordergoods>>();
		List<Vbank> vbanks = new ArrayList<Vbank>();
		for (int i = 0; i < orders.size(); i++) {
			ordergoods.add(orderserviceimpl.getordergoods(orders.get(i).getMerchant_id())); // 주문 번호로 시킨 책들
			vbanks.add(orderserviceimpl.getvbankinfo(orders.get(i).getMerchant_id()));
			logger.info("vbanks : {}", vbanks);
			logger.info("orders.get(i).getMerchant_id() : {}", orders.get(i).getMerchant_id());
		}
		model.addAttribute("ordergoods", ordergoods);
		model.addAttribute("vbanks", vbanks);
		return "/order/orderinfo";
	}
	
	private int pagingRefactoringForOrder(int curPageNum, HttpServletRequest request, Model model, String id) {	// 고객의 주문 내역을 보는 페이지가 속한 Block의 첫번째 숫자와 마지막 숫자를 모델의 속성값을 넣음
		String page = request.getParameter("page");	
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; 	// 처음 페이지에 들어가는 경우(페이징 처리된 페이지를 누르지 않았을 경우)
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforOrder(curPageNum, model, id);
		return curPageNum;
	}

	@RequestMapping("/search")				// 책을 찾기 위한 URL
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
		couponserviceimpl.updatediscountpercent(couponId, merchant_id);
	}

	@RequestMapping("/showcoupon") // 쿠폰 내역 조회
	public String showcoupon(HttpServletRequest request, HttpSession session, Model model) {
		String Id = (String) (session.getAttribute("Userid"));
		pagingModelCoupon(model, request, Id);
		return "showcoupon";
	}

	private Model pagingModelKwd(String subject, String search, HttpServletRequest request, Model model) { // 사용자의 책 검색 리스트 모델을 DB에서 조회하여 반환
		int curPageNum = 0; 	// 현재 사용자가 보는 페이지
		logger.info("사용자 검색 키워드 : {}", search);
		curPageNum = pagingRefactoringForSearch(curPageNum, request, model, subject, search);	// 현재 해당하는 페이지가 속한 block의 첫번째 숫자,마지막 숫자 계산
		List<Goods> list = paging.dtoWithKwd(curPageNum, search, subject);		// 사용자가 검색한 키워드에 해당하는 책 리스트를 DB에서 조회
		model.addAttribute("list", list);
		return model;
	}
	
	private int pagingRefactoringForSearch(int curPageNum, HttpServletRequest request, Model model, String subject,
			String search) {		// 현재 사용자가 검색을 하려는 페이지가 속한 block의 첫번째 숫자,마지막 숫자 계산
		String page = request.getParameter("page");
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; 	// 처음 페이지에 들어가는 경우(페이징 처리된 페이지를 누르지 않았을 경우)
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforKwd(curPageNum, model, subject, search);
		return curPageNum;
	}

	
	
	private Model pagingModelWithBook(Model model, HttpServletRequest request, String bigclass, String subclass) {	// 사용자가 대분류중에서 소분류에 해당하는 책 리스트를 DB에서 조회하여 모델을 반환
		int curPageNum = 0; 	// 현재 사용자가 보는 페이지
		curPageNum = pagingRefactoringForBook(curPageNum, request, model, bigclass, subclass);		// 현재 해당하는 페이지가 속한 block의 첫번째 숫자,마지막 숫자 계산
		List<Goods> list = paging.dtosWithBook(curPageNum, bigclass, subclass);	// 해당하는 페이지의 책 리스트를 뽑아옴
		model.addAttribute("list", list);
		return model;
	}
	
	private int pagingRefactoringForBook(int curPageNum, HttpServletRequest request, Model model, String bigclass,
			String subclass) {			// 현재 해당하는 페이지가 속한 block의 처음 숫자,마지막 숫자 계산
		String page = request.getParameter("page");
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; // 처음 페이지에 들어가는 경우(페이징 처리된 페이지를 누르지 않았을 경우)
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforBook(curPageNum, model, bigclass, subclass);
		return curPageNum;
	}

	
	
	private Model pagingModelCoupon(Model model, HttpServletRequest request, String Id) { // 특정 사용자가 가지고 있는 쿠폰 리스트를 DB에서 조회하여 모델을 반환
		int curPageNum = 0; // 현재 사용자가 보는 페이지
		curPageNum = pagingRefactoringForCoupon(curPageNum, request, model, Id); // 현재 특정 사용자가 받은 쿠폰리스트 중에서 해당하는 페이지가 속한 block의 첫번째 숫자,마지막 숫자 계산
		List<Coupon> list = paging.coupons(curPageNum, Id);
		model.addAttribute("list", list);
		return model;
	}
	
	private int pagingRefactoringForCoupon(int curPageNum, HttpServletRequest request, Model model, String Id) {
		String page = request.getParameter("page");		// 현재 특정 사용자가 받은 쿠폰리스트 중에서 해당하는 페이지가 속한 block의 첫번째 숫자,마지막 숫자 계산
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; // 처음 페이지에 들어가는 경우(페이징 처리된 페이지를 누르지 않았을 경우)
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforCoupon(curPageNum, model, Id);
		return curPageNum;
	}

	
	
	private Model pagingModelReply(Model model, HttpServletRequest request, String bookname) { // 특정 책의 리뷰 댓글을 DB에서 조회하여 모델을 반환
		int curPageNum = 0; // 현재 사용자가 보는 페이지
		curPageNum = pagingRefactoringForReply(curPageNum, request, model, bookname);
		List<Reply> list = paging.reply(curPageNum, bookname);
		logger.info("list : {}", list);
		model.addAttribute("list", list);
		model.addAttribute("reviewlist", replyserviceimpl.getAllReply(bookname));
		return model;
	}

	private int pagingRefactoringForReply(int curPageNum, HttpServletRequest request, Model model, String bookname) {
		String page = request.getParameter("page");
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; // 처음 페이지에 들어가는 경우(페이징 처리된 페이지를 누르지 않았을 경우)
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforReply(curPageNum, model, bookname);
		return curPageNum;
	}
	
	private Model pagingModelReplyLatest(Model model, HttpServletRequest request, String bookname) {	// 특정 책의 리뷰 댓글 중에 최신순에 해당하는 댓글 리뷰를 DB에 조회하여 모델을 반환
		int curPageNum = 0; // 현재 사용자가 보는 페이지
		curPageNum = pagingRefactoringForReplyLatest(curPageNum, request, model, bookname);
		logger.info("curPageNum refactoring : {}", curPageNum);
		List<Reply> list = paging.replylatest(curPageNum, bookname);
		logger.info("list : {}", list);
		model.addAttribute("list", list);
		model.addAttribute("reviewlist", replyserviceimpl.getAllReply(bookname));
		return model;
	}

	private int pagingRefactoringForReplyLatest(int curPageNum, HttpServletRequest request, Model model,
			String bookname) {
		String page = request.getParameter("page");
		if (page != null)
			curPageNum = Integer.valueOf(page);
		else
			curPageNum = 1; 	// 처음 페이지에 들어가는 경우(페이징 처리된 페이지를 누르지 않았을 경우)
		logger.info("page : {}", page);
		logger.info("curPageNum : {}", curPageNum);
		paging.pagingforReplyLatest(curPageNum, model, bookname);
		return curPageNum;
	}
}