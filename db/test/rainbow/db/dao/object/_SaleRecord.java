package rainbow.db.dao.object;

import java.time.LocalDate;

import rainbow.core.model.object.IdObject;

public class _SaleRecord extends IdObject<Integer>{
	
	private int person;
	
	private int goods;
	
	private int qty;
	
	private double money;
	
	private LocalDate time; 

	public int getPerson() {
		return person;
	}

	public void setPerson(int person) {
		this.person = person;
	}

	public int getGoods() {
		return goods;
	}

	public void setGoods(int goods) {
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

	public _SaleRecord(int id, int person, int goods, int qty, double money, LocalDate time) {
		this.id = id;
		this.person = person;
		this.goods = goods;
		this.qty = qty;
		this.money = money;
		this.time = time;
	}

}
