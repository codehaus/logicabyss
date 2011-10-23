package ruleml.test.translator.datamodel;

public class Buy {
	private String buyer;
	private String seller;
	private String item;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buyer == null) ? 0 : buyer.hashCode());
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((seller == null) ? 0 : seller.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Buy other = (Buy) obj;
		if (buyer == null) {
			if (other.buyer != null)
				return false;
		} else if (!buyer.equals(other.buyer))
			return false;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (seller == null) {
			if (other.seller != null)
				return false;
		} else if (!seller.equals(other.seller))
			return false;
		return true;
	}

	public Buy(String buyer, String item, String seller) {
		this.setSeller(seller);
		this.setBuyer(buyer);
		this.setItem(item);
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getSeller() {
		return seller;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getBuyer() {
		return buyer;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItem() {
		return item;
	}
}