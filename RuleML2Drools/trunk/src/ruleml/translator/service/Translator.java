package ruleml.translator.service;

/**
 * Generic interface for the rules translator to implement to can be used in
 * the translation framework.
 * 
 * @author jabarski
 *
 */
public interface Translator {
	/**
	 * The entry point of the translation. This will start translation and
	 * returns output.
	 * 
	 * @param o The object to be translated.
	 * @return The translated content.
	 */
	public Object translate (Object o);

}
