package rainbow.db.dao.object;

import rainbow.core.model.object.IdNameObject;

public class _Goods extends IdNameObject<Integer>{

	private double price;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
}
