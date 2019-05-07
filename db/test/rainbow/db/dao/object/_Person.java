package rainbow.db.dao.object;

import java.time.LocalDate;

import rainbow.core.model.object.IdNameObject;

public class _Person extends IdNameObject<Integer> {

	private LocalDate birthday;
	
	private _Gender gender;
	
	private int mobile;

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public _Gender getGender() {
		return gender;
	}

	public void setGender(_Gender gender) {
		this.gender = gender;
	}

	public int getMobile() {
		return mobile;
	}

	public void setMobile(int mobile) {
		this.mobile = mobile;
	}

	public _Person() {
	}

	public _Person(Integer id, String name) {
		super(id, name);
	}

	public static _Person zhang3() {
		_Person p = new _Person(3, "张三");
		p.setGender(_Gender.男);
		p.setBirthday(LocalDate.of(1982, 1, 1));
		p.setMobile(7);
		return p;
	}
	
	public static _Person li4() {
		_Person p = new _Person(4, "李四");
		p.setGender(_Gender.男);
		p.setBirthday(LocalDate.of(1988, 7, 1));
		p.setMobile(30);
		return p;
	}
	
	public static _Person wang5() {
		_Person p = new _Person(5, "王五");
		p.setGender(_Gender.女);
		p.setBirthday(LocalDate.of(2000, 11, 22));
		p.setMobile(10);
		return p;
	}
	
	public static _Person zhao6() {
		_Person p = new _Person(6, "赵六");
		p.setGender(_Gender.女);
		p.setBirthday(LocalDate.of(2000, 11, 22));
		p.setMobile(20);
		return p;
	}
}
