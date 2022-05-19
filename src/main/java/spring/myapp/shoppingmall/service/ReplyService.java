package spring.myapp.shoppingmall.service;

import java.util.List;

import spring.myapp.shoppingmall.dto.Reply;
import spring.myapp.shoppingmall.dto.ReviewReply;

public interface ReplyService {
	public List<Reply> commentlist(String gId);
	public void contentreplydelete(String gId, String rId);
	public void contentreplymodify(String gId, String rId, String content);
	public boolean addComment(String gId,String user_id,String reply);
	public boolean addreview(Reply reply); 
	public List<Reply> getAllReply(String bookname);
	public void addreviewreply(ReviewReply userreview,int rid);
	public void reviewmodify(String content, int reviewid);
	public void reviewdelete(int reviewid);
	public void reviewreplydelete(int reviewreplyid);
	public void reviewrecommend(int reviewid,String userid);
	public void reviewreplyrecommend(int reviewreplyid,String userid);
	public boolean reviewrecommendcheck(int reviewid,String userid);
	public boolean reviewreplyrecommendcheck(int reviewreplyid, String userid);
	public Reply getreviewbyrid(int reviewid);
	public int pastreviewcheck(String Userid,String bookname);
}