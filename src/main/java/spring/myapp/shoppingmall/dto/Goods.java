package spring.myapp.shoppingmall.dto;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "goods")
public class Goods {
	@Id
	@Column(name="Id")
	private int id;
	@NotEmpty(message = "�������� �Է����ּ���.")
	@Column(name="Name")
	private String name;
	@Min(value = 0,message = "������ �ּ��� 0���̿��� �մϴ�.")
	@Column(name="Price")
	private int price;
	@NotEmpty(message = "��ǰ������ �Է����ּ���.")
	private String kind;
	@Min(value = 0,message = "�ܿ����� �Է����ּ���.")
	private int remain;
	@NotEmpty(message = "��ǰ�� �̹��� ������ ������ּ���.")
	private String goodsprofile;
	private String goodscontent;
	@NotEmpty(message = "�۾��̸� �Է����ּ���.")
	private String writer;
	private String wcompany;
	private String tcontent;
	private String review;
	private String summary;
	private String writerintroduction;
	@Min(value = 1,message = "�������� �Է����ּ���.")
	private int page;
	private String bigclass;
	private String subclass;
	private int recommend;
	private String cover;
	private int width;
	private int height;
	private int purchase;
	@NotEmpty(message = "å�� isbn ��ȣ�� �Է����ּ���.")
	private String isbn;
	@NotEmpty(message = "å�� �������� �Է����ּ���.")
	private String publishday;
	@NotEmpty(message = "å�� Ŀ�� ������ �Է����ּ���.")
	private String trastlationer;
	@OneToMany(mappedBy = "goods",cascade = CascadeType.ALL)
	List<Shoppingbasket> cart = new ArrayList<Shoppingbasket>();
	@OneToMany(mappedBy = "book_id",cascade = CascadeType.ALL)
	private List<Bookrecommend> recommendlist = new ArrayList<Bookrecommend>();
}