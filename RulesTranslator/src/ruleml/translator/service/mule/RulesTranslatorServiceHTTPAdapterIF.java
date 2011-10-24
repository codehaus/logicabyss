package ruleml.translator.service.mule;

import ruleml.translator.service.RulesLanguage;
import ruleml.translator.service.UnknownRulesLanguageException;

/**
 * This class is the interface for the simplified WS-Translator Service with String parameter.
 * @author jabarski
 *
 */
public interface RulesTranslatorServiceHTTPAdapterIF {

	public abstract String translate(String input, String inLanguage,
			String inLanguageVersion, String outLanguage,
			String outLanguageVersion) throws UnknownRulesLanguageException;

	public abstract String getSupportedInputLanguages();

	public abstract String getSupportedOutputLanguages(String inLanguage,
			String inLanguageVersion);

	public abstract boolean supportsTranslation(String inLanguage,
			String inLanguageVersion, String outLanguage,
			String outLanguageVersion);

	public abstract RulesLanguage guessLanguage(String input)
			throws UnknownRulesLanguageException;
}