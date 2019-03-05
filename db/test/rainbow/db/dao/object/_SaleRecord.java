package rainbow.db.dao.object;

import rainbow.core.model.object.IdObject;

public class _SaleRecord extends IdObject<Integer>{
	
	private int person;
	
	private int goods;
	
	private int qty;
	
	private double money;

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

}
