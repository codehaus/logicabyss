package ruleml.translator.service;

import java.util.List;

/**
 * Interface for the business rules translation service.
 * 
 * @author jabarski
 */
public interface RulesTranslatorService {
	/**
	 * Translates the input in RuleML
	 * 
	 * @param input
	 *            The input (rules, queries or facts)
	 * @param l
	 *            The language for the input
	 * @return The result of the translation
	 * @throws UnknownRulesLanguageException
	 *             In case of unsupported rules language.
	 */
	String translateToRuleML(String input, RulesLanguage l)
			throws UnknownRulesLanguageException;

	/**
	 * Translates the input in RuleML. The translator tries to 
	 * identify the language for the input.
	 * 
	 * @param input
	 *            The input (rules, queries or facts)
	 * @return The result of the translation
	 * @throws UnknownRulesLanguageException
	 *             In case of unsupported rules language.
	 */
	String translateToRuleML(String input) throws 
		UnknownRulesLanguageException;

	/**
	 * Translates the input from RuleML to the output language.
	 * 
	 * @param input
	 *            The RuleML input to be translated.
	 * @param l
	 *            The output language.
	 * @return The translated output.
	 * @throws UnknownRulesLanguageException
	 *             In case of unsupported rules language.
	 */
	String translateFromRuleML(String input, RulesLanguage l)
			throws UnknownRulesLanguageException;

	/**
	 * Translates the input in the output language. The translator 
	 * uses always RuleML as a intermediator.
	 * 
	 * @param input
	 *            The input (rules, queries or facts)
	 * @param in
	 *            The language for the input
	 * @param out
	 *            The language for the output
	 * @return The result of the translation
	 * @throws UnknownRulesLanguageException
	 *             In case of unsupported rules language.
	 */
	String translate(String input, RulesLanguage in, RulesLanguage out)
			throws UnknownRulesLanguageException;

	/**
	 * Gets the information about current version of RuleML that is used 
	 * for the translations.
	 * 
	 * @return The information about RuleML as an object
	 */
	RulesLanguage getRuleMLInfo();

	/**
	 * Returns list with the business rules languages that are currently
	 * supported in the translator framework.
	 * 
	 * @return List with language objects
	 */
	List<RulesLanguage> getSupportedLangugages();

	/**
	 * Checks if the translator understands the given business rules 
	 * language.
	 * 
	 * @return True if the language is supported, false otherwise.
	 */
	boolean supportsLanguage(RulesLanguage l);

	/**
	 * Tries to guess the rules language from the input source.
	 * 
	 * @param input
	 *            The rule input that should be analyzed.
	 * @return The rules language of the input.
	 * @throws UnknownRulesLanguageException
	 *             In case of unsupported rules language.
	 */
	RulesLanguage guessLanguage(String input)
			throws UnknownRulesLanguageException;
}
