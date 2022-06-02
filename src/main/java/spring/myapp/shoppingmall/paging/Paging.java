package spring.myapp.shoppingmall.paging;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.dto.Coupon;
import spring.myapp.shoppingmall.dto.Goods;
import spring.myapp.shoppingmall.dto.Refund;
import spring.myapp.shoppingmall.dto.Reply;

@Service
@Transactional
public class Paging {
	private static final Logger logger = LoggerFactory.getLogger(Paging.class);
	@Autowired
	private MallDao Malldao;
    private final static int pageCount = 5;  //pageCount�� �� ���� ������ �� �ִ� �ִ��� ������ �� 
    private final static int shopPage = 6;   //shopPage��  �� �������� ��� å�� ������ ������ �������ִ� ��
    private final static int adminpageCount = 10;
    private final static int adminPage = 5;
    
    // �˻� ��� ����¡
    public List<Goods> dtoWithKwd(int curPageNum,String search,String subject) {
    	List<Goods> list = null;
    	list = Malldao.getsearchinfo(curPageNum,search,subject);
    	return list;
    }
    
	public void pagingforKwd(int curPageNum, Model model,String subject,String search) {
		int blockNum = 0;
        blockNum = (int)Math.floor((curPageNum-1)/ pageCount);
        int blockStartNum = (pageCount * blockNum) + 1;					//����� ���� ����
        int blockLastNum = blockStartNum + (pageCount-1);	
        int total = Malldao.getCountKwd(subject,search);
        int lastPageNum = 0;
    	if(total % shopPage == 0) {
            lastPageNum = (int)Math.floor(total/shopPage);			//������ �������� ��1
         }
         else {
            lastPageNum = (int)Math.floor(total/shopPage) + 1;		//������ �������� ��2
         }
		model.addAttribute("curPageNum",curPageNum).addAttribute("blockStartNum",blockStartNum).addAttribute("blockLastNum",blockLastNum).addAttribute("lastPageNum",lastPageNum);
	}

	// å�� ����Ʈ ����¡
	public List<Goods> dtosWithBook(int curPageNum, String bigclass, String subclass) {
    	List<Goods> list = null;									
    	list = Malldao.getCurPageDtosWithBook(curPageNum,bigclass,subclass);					//���� �������� �ش��ϴ� ��ǰ ����Ʈ��
    	return list;
	}

	public void pagingforBook(int curPageNum, Model model, String bigclass, String subclass) {
		int blockNum = 0;
        blockNum = (int)Math.floor((curPageNum-1)/ pageCount);
        int blockStartNum = (pageCount * blockNum) + 1;					// ����� ���� ����
        int blockLastNum = blockStartNum + (pageCount-1);	
        int total = Malldao.getCountWithBook(bigclass, subclass);
        int lastPageNum = 0;
    	if(total % shopPage == 0)
            lastPageNum = (int)Math.floor(total/shopPage);			//������ �������� ��1
         else
            lastPageNum = (int)Math.floor(total/shopPage) + 1;		//������ �������� ��2
		model.addAttribute("curPageNum",curPageNum).addAttribute("blockStartNum",blockStartNum).
			addAttribute("blockLastNum",blockLastNum).addAttribute("lastPageNum",lastPageNum);
	}
	
	// ������ ���� ����¡
	public List<Coupon> coupons(int curPageNum, String id) {
		List<Coupon> list = null;									
    	list = Malldao.getCurPageCoupons(curPageNum,id);					// ���� �������� �ش��ϴ� ���� ����Ʈ
    	return list;
	}
	
	public void pagingforCoupon(int curPageNum, Model model, String id) {
		int blockNum = 0;
        blockNum = (int)Math.floor((curPageNum-1)/ pageCount);
        int blockStartNum = (pageCount * blockNum) + 1;					//����� ���� ����
        int blockLastNum = blockStartNum + (pageCount-1);	
        int total = Malldao.getNumberCoupons(id);
        int lastPageNum = 0;
    	if(total % shopPage == 0) {
            lastPageNum = (int)Math.floor(total/shopPage);			//������ �������� ��1
         }
         else {
            lastPageNum = (int)Math.floor(total/shopPage) + 1;		//������ �������� ��2
         }
		model.addAttribute("curPageNum",curPageNum).addAttribute("blockStartNum",blockStartNum).addAttribute("blockLastNum",blockLastNum).addAttribute("lastPageNum",lastPageNum);
	}

	// å �󼼺��� â���� ���� ���� ����¡
	public List<Reply> reply(int curPageNum, String bookname) {
		List<Reply> list = null;									
    	list = Malldao.getCurPageReplies(curPageNum,bookname);					//���� �������� �ش��ϴ� ���� ����Ʈ
    	return list;
	}
	
	public void pagingforReply(int curPageNum, Model model, String bookname) {
		int blockNum = 0;
        blockNum = (int)Math.floor((curPageNum-1)/ pageCount);
        int blockStartNum = (pageCount * blockNum) + 1;					//����� ���� ����
        int blockLastNum = blockStartNum + (pageCount-1);	
        int total = Malldao.getNumberReply(bookname);
        int lastPageNum = 0;
    	if(total % shopPage == 0) {
            lastPageNum = (int)Math.floor(total/shopPage);			//������ �������� ��1
         }
         else {
            lastPageNum = (int)Math.floor(total/shopPage) + 1;		//������ �������� ��2
         }
    	logger.info("blockNum : {}",blockNum);
    	logger.info("blockStartNum : {}",blockStartNum);
    	logger.info("blockLastNum : {}",blockLastNum);
		model.addAttribute("curPageNum",curPageNum).addAttribute("blockStartNum",blockStartNum).addAttribute("blockLastNum",blockLastNum).addAttribute("lastPageNum",lastPageNum);
	}
	
	// å �󼼺��� â���� �ֽż����� ���� ���� ����¡
	public List<Reply> replylatest(int curPageNum, String bookname) {
		List<Reply> list = null;									
    	list = Malldao.getCurPageRepliesLatest(curPageNum,bookname);					//���� �������� �ش��ϴ� ��ǰ ����Ʈ��
    	return list;
	}
	
	public void pagingforReplyLatest(int curPageNum, Model model, String bookname) {
		int blockNum = 0;
        blockNum = (int)Math.floor((curPageNum-1)/ pageCount);
        int blockStartNum = (pageCount * blockNum) + 1;					//����� ���� ����
        int blockLastNum = blockStartNum + (pageCount-1);	
        int total = Malldao.getNumberReply(bookname);
        int lastPageNum = 0;
    	if(total % shopPage == 0) {
            lastPageNum = (int)Math.floor(total/shopPage);			//������ �������� ��1
         }
         else {
            lastPageNum = (int)Math.floor(total/shopPage) + 1;		//������ �������� ��2
         }
		model.addAttribute("curPageNum",curPageNum).addAttribute("blockStartNum",blockStartNum).addAttribute("blockLastNum",blockLastNum).addAttribute("lastPageNum",lastPageNum);
	}

	// �ֹ� ���� ����¡
	public void pagingforOrder(int curPageNum,Model model,String id) {
		int blockNum = 0;
        blockNum = (int)Math.floor((curPageNum-1)/ pageCount);
        int blockStartNum = (pageCount * blockNum) + 1;					//����� ���� ����
        int blockLastNum = blockStartNum + (pageCount-1);	
        int total = Malldao.getCountOrder(id);
        int lastPageNum = 0;
    	if(total % shopPage == 0)
            lastPageNum = (int)Math.floor(total/shopPage);			//������ �������� ��1
         else
            lastPageNum = (int)Math.floor(total/shopPage) + 1;		//������ �������� ��2
		model.addAttribute("curPageNum",curPageNum).addAttribute("blockStartNum",blockStartNum).addAttribute("blockLastNum",blockLastNum).addAttribute("lastPageNum",lastPageNum);
	}

	// �̴��� �α⼭�� ��� ������(������ ������)���� �����ڰ� ���ŷ��� ������ å�� �� �� �ֵ��� ���ִ� ����¡ ó��
	public void pagingforAdmin(int curPageNum, Model model) {
		int blockNum = 0;
        blockNum = (int)Math.floor((curPageNum-1)/ adminpageCount);
        int blockStartNum = (adminpageCount * blockNum) + 1;					//����� ���� ����
        int blockLastNum = blockStartNum + (adminpageCount-1);	
        int total = Malldao.getCountGoods();
        int lastPageNum = 0;
    	if(total % adminPage == 0)
            lastPageNum = (int)Math.floor(total/adminPage);			//������ �������� ��1
         else
            lastPageNum = (int)Math.floor(total/adminPage) + 1;		//������ �������� ��2
		model.addAttribute("curPageNum",curPageNum).addAttribute("blockStartNum",blockStartNum).addAttribute("blockLastNum",blockLastNum).addAttribute("lastPageNum",lastPageNum);
	}

	// ������ ������ ȯ�� ����¡
	public List<Refund> refundlist(int curPageNum) {
		List<Refund> refundlist = null;									
    	refundlist = Malldao.getCurPageRequestRefund(curPageNum);					//���� �������� �ش��ϴ� ��ǰ ����Ʈ��
    	return refundlist;
	}

	public void pagingforRefund(int curPageNum, Model model) {
		int blockNum = 0;
        blockNum = (int)Math.floor((curPageNum-1)/ pageCount);
        int blockStartNum = (pageCount * blockNum) + 1;					//����� ���� ����
        int blockLastNum = blockStartNum + (pageCount-1);	
        int total = Malldao.getRequertRefundCount();
        int lastPageNum = 0;
    	if(total % shopPage == 0)
            lastPageNum = (int)Math.floor(total/shopPage);			//������ �������� ��1
         else
            lastPageNum = (int)Math.floor(total/shopPage) + 1;		//������ �������� ��2
		model.addAttribute("curPageNum",curPageNum).addAttribute("blockStartNum",blockStartNum).addAttribute("blockLastNum",blockLastNum).addAttribute("lastPageNum",lastPageNum);
	}
}