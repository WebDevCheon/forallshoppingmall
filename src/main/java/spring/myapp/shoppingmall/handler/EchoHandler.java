package spring.myapp.shoppingmall.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import spring.myapp.shoppingmall.controller.MallController;
import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.service.CouponServiceImpl;

public class EchoHandler extends TextWebSocketHandler {
	private static final Logger logger = LoggerFactory.getLogger(EchoHandler.class);
	private Map<String,Object> userSessions = new HashMap<>();
	private Map<String,Integer> couponAlarmChecklist = new HashMap<>();
	@Autowired
	private MallDao Malldao;
	
	@Autowired
	private CouponServiceImpl couponserviceimpl;
	
	// 클라이언트와 연결 이후에 실행되는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String senderId = getId(session);  //사용자의 ID를 반환
		userSessions.put(senderId,session);  //사용자의 ID를 아이디 : session 객체 식으로 mapping
		logger.info("{}",session.getAttributes().get("Userid"));
		logger.info("senderId : " + senderId);
		logger.info("userSessions : " + userSessions.toString());
		logger.info("연결 성공");
		logger.info("{}",couponAlarmChecklist.get(senderId));
		if(couponAlarmChecklist.get(senderId) == null)
			couponAlarmChecklist.put(senderId,0);
	}

	private String getId(WebSocketSession session) {
		Map<String,Object> httpSession = session.getAttributes();
		logger.info("{}",httpSession);
		String loginUser = (String)httpSession.get("Userid");
		if(loginUser == null) {
			return session.getId();
		}
		else
			return loginUser;
	}

	// 클라이언트가 서버로 메시지를 전송했을 때 실행되는 메서드
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		logger.info("text message : " + message.getPayload());
		String userid = message.getPayload();  //admin
		String[] strs = message.getPayload().split(",");  //admin,makecoupon
		if(strs.length == 1) {  
			if(couponAlarmChecklist.get(userid) == 1) {  //쿠폰을 전에 확인 했을때
				logger.info("strs.length == 1 첫번째 : " + couponserviceimpl.getcouponscountbyuserId(userid));
				return;
			} else {  //쿠폰을 아직 확인 안한 경우
				int canreceivecouponcount = couponserviceimpl.getcouponscountbyuserId(userid);
				logger.info("strs.length == 1 두번째 : " + couponserviceimpl.getcouponscountbyuserId(userid));
				if(canreceivecouponcount > 0) {
					session.sendMessage(new TextMessage("받을 수 있는 쿠폰 개수가 " + canreceivecouponcount + "개가 있습니다."));
					couponAlarmChecklist.put(userid,1);
				} 
			} 
		} else {  //쿠폰을 받은 경우 (쿠폰 받기를 눌렀을때)
			couponAlarmChecklist.put(strs[0],null);
			logger.info("{}",couponAlarmChecklist.get(userid));
			logger.info(couponAlarmChecklist.toString());
		}
	}
	// 클라이언트와 연결을 끊었을 때 실행되는 메소드
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("웹소켓 연결 끊겼습니다.");
		userSessions.remove(session.getAttributes().get("Userid"));
	}
}