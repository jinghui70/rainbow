package rainbow.db.dao.object;

import rainbow.core.model.object.IdNameObject;

public class _Goods extends IdNameObject{

	private double price;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public _Goods() {
	}

	public _Goods(String id, String name) {
		super(id, name);
	}
	
	public static _Goods iPhone6() {
		_Goods p = new _Goods("6", "iPhone6");
		p.setPrice(0.6);
		return p;
	}
	
	public static _Goods iPhone7() {
		_Goods p = new _Goods("7", "iPhone7");
		p.setPrice(0.7);
		return p;
	}
	
	public static _Goods iPhoneX() {
		_Goods p = new _Goods("10", "iPhoneX");
		p.setPrice(1);
		return p;
	}
	
	public static _Goods p30() {
		_Goods p = new _Goods("30", "P30");
		p.setPrice(0.5);
		return p;
	}

	
}
