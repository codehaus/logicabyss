package ruleml.translator.service;

/**
 * Generic interface for the rules translator to implement to can be used in
 * the translation framework.
 * 
 * @author jabarski
 *
 */
public interface RulesTranslator {
	/**
	 * The entry point of the translation. This will start translation and
	 * returns output.
	 * 
	 * @param o The object to be translated.
	 * @return The translated content.
	 */
	Object translate (Object o);
	
	/**
	 * Returns the input language of the translator.
	 * @return The input language of the translator.
	 */
	RulesLanguage getInputLanguage ();
	
	/**
	 * Returns the output language of the translator.
	 * @return The output language of the translator.
	 */
	RulesLanguage getOutputLanguage ();

}
