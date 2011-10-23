package ruleml.test.translator.datamodel;

public class Sell {

	private Person seller;
	private Person buyer;
	private String item;

	public Sell(Person buyer, String item, Person seller) {
		this.seller = seller;
		this.buyer = buyer;
		this.item = item;
	}

	public void setSeller(Person seller) {
		this.seller = seller;
	}

	public Person getSeller() {
		return seller;
	}

	public void setByuer(Person buyer) {
		this.buyer = buyer;
	}

	public Person getBuyer() {
		return buyer;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItem() {
		return item;
	}
}
