package ruleml.test.translator.datamodel;

public class Keep {
	private String keeper;
	private String item;

	public Keep(String item, String keeper) {
		this.keeper = keeper;
		this.item = item;
	}

	public void setKeeper(String keeper) {
		this.keeper = keeper;
	}

	public String getKeeper() {
		return keeper;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItem() {
		return item;
	}
}
