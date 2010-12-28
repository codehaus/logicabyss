package com.sample;

public class TestDataModel {
	public static class Keep {
		private String person;
		private String object;

		public Keep(String person, String object) {
			this.person = person;
			this.object = object;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getPerson() {
			return person;
		}

		public void setObject(String object) {
			this.object = object;
		}

		public String getObject() {
			return object;
		}
	}

	public static class Own {
		private String person;
		private String object;

		public Own(String person, String object) {
			this.person = person;
			this.object = object;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getPerson() {
			return person;
		}

		public void setObject(String object) {
			this.object = object;
		}

		public String getObject() {
			return object;
		}
	}

	public static class Buy {
		private String merchant;
		private String person;
		private String object;

		public Buy(String person, String merchant, String object) {
			this.merchant = merchant;
			this.setPerson(person);
			this.setObject(object);
		}

		public void setMerchant(String merchant) {
			this.merchant = merchant;
		}

		public String getMerchant() {
			return merchant;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getPerson() {
			return person;
		}

		public void setObject(String object) {
			this.object = object;
		}

		public String getObject() {
			return object;
		}
	}

	public static class Sell {

		private String merchant;
		private String person;
		private String object;

		public Sell(String person, String merchant, String object) {
			this.merchant = merchant;
			this.setPerson(person);
			this.setObject(object);
		}

		public void setMerchant(String merchant) {
			this.merchant = merchant;
		}

		public String getMerchant() {
			return merchant;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getPerson() {
			return person;
		}

		public void setObject(String object) {
			this.object = object;
		}

		public String getObject() {
			return object;
		}
	}
}
