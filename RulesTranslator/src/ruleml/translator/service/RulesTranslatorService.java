package ruleml.translator.service;

import java.util.List;
import java.util.Set;

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
	 * supported as input languages for the translator framework.
	 * 
	 * @return Set with language objects
	 */
	Set<RulesLanguage> getSupportedInputLanguages();

	/**
	 * Returns list with the business rules languages that are currently
	 * supported as output languages for the translator framework.
	 * 
	 * @return Set with language objects
	 */
	Set<RulesLanguage> getSupportedOutputLanguages();

	/**
	 * Checks if the translator can translate the rulebase from the 
	 * input to output language.
	 * 
	 * @return True if the translation is supported, false otherwise.
	 */
	boolean supportsTrnslation(RulesLanguage in, RulesLanguage out);

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
