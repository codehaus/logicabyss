package ruleml.translator.service.mule;

import org.apache.axis.handlers.soap.SOAPService;
import org.mule.umo.UMOEventContext;
import org.mule.umo.lifecycle.InitialisationException;

import ruleml.translator.service.RulesLanguage;
import ruleml.translator.service.UnknownRulesLanguageException;

public interface RulesTranslatorServiceHTTPAdapterIF {

	// @Override
	public abstract Object onCall(UMOEventContext eventContext)
			throws Exception;

	public abstract String translateToRuleML(String input, String language,
			String version) throws UnknownRulesLanguageException;

	public abstract String translateToRuleML(String input)
			throws UnknownRulesLanguageException;

	public abstract String translateFromRuleML(String input, String language,
			String version) throws UnknownRulesLanguageException;

	public abstract String translate(String input, String inLanguage,
			String inLanguageVersion, String outLanguage,
			String outLanguageVersion) throws UnknownRulesLanguageException;

	public abstract RulesLanguage getRuleMLInfo();

	public abstract String getSupportedInputLanguages();

	public abstract String getSupportedOutputLanguages();

	public abstract boolean supportsTrnslation(String inLanguage,
			String inLanguageVersion, String outLanguage,
			String outLanguageVersion);

	public abstract RulesLanguage guessLanguage(String input)
			throws UnknownRulesLanguageException;

	public abstract void initialise(SOAPService service)
			throws InitialisationException;

	String test(String t);
}