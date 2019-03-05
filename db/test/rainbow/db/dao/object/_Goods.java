package rainbow.db.dao.object;

import rainbow.core.model.object.NameObject;

public class _Goods extends NameObject<Integer>{

	private double price;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
}
