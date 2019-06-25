package rainbow.db.dao.object;

import java.time.LocalDate;

import rainbow.core.model.object.IdObject;

public class _SaleRecord extends IdObject {
	
	private int inx;

	private String person;

	private String goods;

	private int qty;

	private double money;

	private LocalDate time;

	public int getInx() {
		return inx;
	}

	public void setInx(int inx) {
		this.inx = inx;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getGoods() {
		return goods;
	}

	public void setGoods(String goods) {
		this.goods = goods;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public LocalDate getTime() {
		return time;
	}

	public void setTime(LocalDate time) {
		this.time = time;
	}

	public _SaleRecord() {
	}

	public _SaleRecord(String id, int inx, String person, String goods, int qty, double money, LocalDate time) {
		this.id = id;
		this.inx = inx;
		this.person = person;
		this.goods = goods;
		this.qty = qty;
		this.money = money;
		this.time = time;
	}

	public _SaleRecord(String id, int inx) {
		this.id = id;
		this.inx = inx;
	}
	
	public _SaleRecord person(String person) {
		this.person = person;
		return this;
	}
	
	public _SaleRecord goods(String goods) {
		this.goods = goods;
		return this;
	}

	public _SaleRecord qty(int qty) {
		this.qty = qty;
		return this;
	}

	public _SaleRecord money(double money) {
		this.money = money;
		return this;
	}

	public _SaleRecord time(LocalDate time) {
		this.time = time;
		return this;
	}
}
