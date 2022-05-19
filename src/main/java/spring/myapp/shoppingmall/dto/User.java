package spring.myapp.shoppingmall.dto;

import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User{
	@Id
	@Column(name="Id")
	@Size(min=5,max=20,message = "���̵��� ���̴� �ּ� 5���� �ִ� 20���� �����Դϴ�.")
	private String id;
	@Size(min=5,max=60,message = "�ּ� �ټ� ���� �̻� 60 ���� ���� ��й�ȣ�� �Է��ϼž��մϴ�.")
	@Column(name="Password")
	private String password;
	@NotEmpty(message = "�̸��� �Է��ϼž� �մϴ�.")
	private String name;
	@NotEmpty(message = "�ּҸ� �Է��ϼž� �մϴ�.")
	private String address;
	@NotEmpty(message = "������ �Է��ϼž� �մϴ�.")
	private String sex;
	@NotNull(message = "���̸� �Է��ϼž� �մϴ�.")
	private int age;
	@NotEmpty(message = "��ȭ��ȣ�� �Է����ֽñ� �ٶ��ϴ�.")
	@Column(name="phonenumber")
	private String phoneNumber;
	@NotEmpty(message = "�̸����� �Է����ֽñ� �ٶ��ϴ�.")
	private String email;
	private String authorities;
	private int enabled;
	private String grade;
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int emailconfirm;
	private int naver;
	private int id_num;
	@OneToMany(mappedBy="user_id",cascade=CascadeType.ALL)
	private List<Shoppingbasket> carts = new ArrayList<Shoppingbasket>();
}