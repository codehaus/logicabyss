package ruleml.translator.service;

/**
 * This is class that represents a business rules language.
 * 
 * @author jabarski
 */
public class RulesLanguage {
	private String name;
	private String version;
	private String description;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the business rules language
	 * @param version
	 *            The version of the business rules language
	 */
	public RulesLanguage(String name, String version) {
		this.name = name;
		this.version = version;
	}

	/**
	 * Getter for the name of the business rules language
	 * 
	 * @return The name as a string
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name of the business rules language
	 * @param name The name to be set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for the version of the business rules language
	 * @return The version of the business rules language
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Setter for the version of the business rules language
	 * @param version The version to be set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Getter for the description of the business rules language
	 * @return The description of the business rules language
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter for the description of the business rules language
	 * @param description The description to be set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		RulesLanguage other = (RulesLanguage) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RulesLanguage [name=" + name + ", version=" + version + "]";
	}

}
