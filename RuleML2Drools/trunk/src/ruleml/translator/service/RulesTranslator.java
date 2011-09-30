package ruleml.translator.service;

import java.util.List;

/**
 * Interface for the business rules translation service.
 * 
 * @author jabarski
 */
public interface RulesTranslator {
	/**
	 * Translates the input in RuleML
	 * @param input The input (rules, queries or facts)
	 * @param l The language for the input
	 * @return The result of the translation
	 */
	String translateToRuleML(String input, RulesLanguage l)
			throws UnknownRulesLanguageException;

	/**
	 * Translates the input in RuleML. The tranlslator tries to indetify the
	 * language for the input 
	 * @param input The input (rules, queries or facts)
	 * @return The result of the translation
	 */
	String translateToRuleML(String input) throws UnknownRulesLanguageException;

	/**
	 * Translates the input in the other language. The translator uses always RuleML
	 * as a intermediator.
	 * @param input The input (rules, queries or facts)
	 * @param in The language for the input
	 * @param out The language for the output
	 * @return The result of the translation 
	 */
	String translateToLanguage(String input, RulesLanguage in, RulesLanguage out)
			throws UnknownRulesLanguageException;

	// Gibt die Information über die aktuellbentutzte RuleML-Version
	/**
	 * Gets the information about current version of RuleML that is used for 
	 * the translations.
	 * @return The information about RuleML as an object
	 */
	RulesLanguage getRuleMLInfo();

	/**
	 * Returns list with the business rules languages that are currently supported
	 * in the translator framework.
	 * @return List with language objects 
	 */
	List<RulesLanguage> getSupportedLangugages();

	/**
	 * Checks if the translator understands the given business rules language
	 * @return True if the language is supported, false otherwise. 
	 */
	boolean supportsLanguage(RulesLanguage l);
}
