package spring.myapp.shoppingmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.myapp.shoppingmall.dao.MallDao;
import spring.myapp.shoppingmall.dto.Reply;
import spring.myapp.shoppingmall.dto.ReviewReply;

@Service
public class ReplyServiceImpl implements ReplyService{
	@Autowired
	private MallDao Malldao;
	
	@Override
	public List<Reply> commentlist(String gId){
		return Malldao.commentlist(gId);
	}
	
	@Override
	public void contentreplydelete(String bookname, String rId){
		Malldao.contentreplydelete(bookname,rId);
	}
	
	@Override
	public void contentreplymodify(String bookname, String rId, String content){
		Malldao.contentreplymodify(bookname,rId,content);
	}
	
	@Override
	public boolean addComment(String bookname,String user_id,String reply){
		return Malldao.addComment(bookname,user_id,reply);
	}
	
	@Override
	public boolean addreview(Reply reply) {
		return Malldao.addreview(reply);
	}
	
	@Override
	public List<Reply> getAllReply(String bookname){
		return Malldao.getAllReply(bookname);
	}
	
	@Override
	public void addreviewreply(ReviewReply userreview,int rid) {
		Malldao.addreviewreply(userreview,rid);
	}
	@Override
	public void reviewmodify(String content, int reviewid) {
		Malldao.reviewmodify(content,reviewid);
	}

	@Override
	public void reviewdelete(int reviewid) {
		Malldao.reviedelete(reviewid);
	}
	
	@Override
	public void reviewreplydelete(int reviewreplyid) {
		Malldao.reviewreplydelete(reviewreplyid);
	}

	@Override
	public void reviewrecommend(int reviewid,String userid) {
		Malldao.reviewrecommend(reviewid,userid);
	}

	@Override
	public void reviewreplyrecommend(int reviewreplyid,String userid) {
		Malldao.reviewreplyrecommend(reviewreplyid,userid);
	}

	@Override
	public boolean reviewrecommendcheck(int reviewid,String userid) {
		return Malldao.reviewrecommendcheck(reviewid,userid);
	}
	
	@Override
	public boolean reviewreplyrecommendcheck(int reviewreplyid, String userid) {
		return Malldao.reviewreplyrecommendcheck(reviewreplyid,userid);
	}
	
	@Override
	public Reply getreviewbyrid(int reviewid) {
		return Malldao.getreviewbyid(reviewid);
	}

	@Override
	public int pastreviewcheck(String Userid,String bookname) {
		return Malldao.pastreviewcheck(Userid,bookname);
	}
}
