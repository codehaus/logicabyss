package com.sample;

public class TestDataModel {
	public static class Keep {
		private String keeper;
		private String item;

		public Keep(String keeper, String item) {
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

	public static class Own {
		private String owner;
		private String item;

		public Own(String owner, String item) {
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

	public static class Buy {
		private String buyer;
		private String seller;
		private String item;

		public Buy(String buyer, String seller, String item) {
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

	public static class Sell {

		private String seller;
		private String buyer;
		private String item;

		public Sell(String buyer, String seller, String item) {
			this.seller = seller;
			this.buyer = buyer;
			this.item = item;
		}

		public void setSeller(String seller) {
			this.seller = seller;
		}

		public String getSeller() {
			return seller;
		}

		public void setByuer(String buyer) {
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
}
