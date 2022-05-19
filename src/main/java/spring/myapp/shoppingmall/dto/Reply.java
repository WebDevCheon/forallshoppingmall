package spring.myapp.shoppingmall.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "reply")
public class Reply {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="rId")
	private int rid;  //���� ����� ���̵� ��ȣ
	@Column(name="gId")
	private String gid;  //å�� �̸�
	private String user_id;  //���� ��� �ø� ���� ���̵�
	private String content;  //���� ����� �� ����
	private int reviewpoint;  //���� ����Ʈ ����
	private String imgfileurl;  //�̹��� ������ url ���
	@CreationTimestamp
	private Timestamp uploadtime;  //���ε� �ð�
	private int bgroup;  //�亯�� �׷�
	private int tag;
	private int good;
	@OneToMany(mappedBy = "rid",cascade = CascadeType.REMOVE)   //@OneToMany(mappedBy = "reviewid",cascade = CascadeType.REMOVE)
	private List<ReviewReply> reviewreplylist = new ArrayList<ReviewReply>();
	@OneToMany(mappedBy = "reviewid",cascade = CascadeType.REMOVE)
	private List<ReviewRecommend> reviewrecommend;
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "bookid")
	private Goods goodsid;  //����� �ø� å�� ���̵�
}