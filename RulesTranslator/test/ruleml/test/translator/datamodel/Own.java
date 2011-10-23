package ruleml.test.translator.datamodel;

public class Own {
	private String owner;
	private String item;

	public Own(String item, String owner) {
		this.owner = owner;
		this.item = item;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItem() {
		return item;
	}
}
