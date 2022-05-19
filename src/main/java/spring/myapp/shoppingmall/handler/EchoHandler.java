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
	
	// Ŭ���̾�Ʈ�� ���� ���Ŀ� ����Ǵ� �޼���
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String senderId = getId(session);  //������� ID�� ��ȯ
		userSessions.put(senderId,session);  //������� ID�� ���̵� : session ��ü ������ mapping
		logger.info("{}",session.getAttributes().get("Userid"));
		logger.info("senderId : " + senderId);
		logger.info("userSessions : " + userSessions.toString());
		logger.info("���� ����");
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

	// Ŭ���̾�Ʈ�� ������ �޽����� �������� �� ����Ǵ� �޼���
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		logger.info("text message : " + message.getPayload());
		String userid = message.getPayload();  //admin
		String[] strs = message.getPayload().split(",");  //admin,makecoupon
		if(strs.length == 1) {  
			if(couponAlarmChecklist.get(userid) == 1) {  //������ ���� Ȯ�� ������
				logger.info("strs.length == 1 ù��° : " + couponserviceimpl.getcouponscountbyuserId(userid));
				return;
			} else {  //������ ���� Ȯ�� ���� ���
				int canreceivecouponcount = couponserviceimpl.getcouponscountbyuserId(userid);
				logger.info("strs.length == 1 �ι�° : " + couponserviceimpl.getcouponscountbyuserId(userid));
				if(canreceivecouponcount > 0) {
					session.sendMessage(new TextMessage("���� �� �ִ� ���� ������ " + canreceivecouponcount + "���� �ֽ��ϴ�."));
					couponAlarmChecklist.put(userid,1);
				} 
			} 
		} else {  //������ ���� ��� (���� �ޱ⸦ ��������)
			couponAlarmChecklist.put(strs[0],null);
			logger.info("{}",couponAlarmChecklist.get(userid));
			logger.info(couponAlarmChecklist.toString());
		}
	}
	// Ŭ���̾�Ʈ�� ������ ������ �� ����Ǵ� �޼ҵ�
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("������ ���� ������ϴ�.");
		userSessions.remove(session.getAttributes().get("Userid"));
	}
}