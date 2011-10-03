package ruleml.translator.service;

/**
 * Prototype implementation of the RulesTranslationService interface.
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RulesTranslatorServiceImpl implements RulesTranslatorService {

	private RulesLanguage ruleML = new RulesLanguage("RuleML", "1.0");
	private Map<RulesLanguage, RulesTranslator> translatorsToRuleML = new HashMap<RulesLanguage, RulesTranslator>();
	private Map<RulesLanguage, RulesTranslator> translatorsFromRuleML = new HashMap<RulesLanguage, RulesTranslator>();

	public RulesTranslatorServiceImpl() {

	}

	@Override
	public String translateToRuleML(String input, RulesLanguage l)
			throws UnknownRulesLanguageException {
		if (!translatorsToRuleML.containsKey(l)) {
			throw new UnknownRulesLanguageException("Unknown language " + l);
		}

		Object output = translatorsToRuleML.get(l).translate(input);
		return (String) output;
	}

	@Override
	public String translateToRuleML(String input)
			throws UnknownRulesLanguageException {
		return null;
	}

	@Override
	public String translateFromRuleML(String input, RulesLanguage l)
			throws UnknownRulesLanguageException {
		if (!translatorsFromRuleML.containsKey(l)) {
			throw new UnknownRulesLanguageException("Unknown language " + l);
		}

		Object output = translatorsFromRuleML.get(l).translate(input);
		return (String) output;
	}

	@Override
	public String translate(String input, RulesLanguage in,
			RulesLanguage out) throws UnknownRulesLanguageException {
		String ruleMLOutput = translateToRuleML(input, in);
		return translateFromRuleML(ruleMLOutput, out);
	}

	@Override
	public RulesLanguage getRuleMLInfo() {
		return ruleML;
	}

	@Override
	public List<RulesLanguage> getSupportedLangugages() {
		throw new UnsupportedOperationException ("The operation in not implemented yet");
	}

	@Override
	public boolean supportsLanguage(RulesLanguage l) {
		throw new UnsupportedOperationException ("The operation in not implemented yet");
	}

	@Override
	public RulesLanguage guessLanguage(String input)
			throws UnknownRulesLanguageException {
		throw new UnsupportedOperationException ("The operation in not implemented yet");
	}

	/**
	 * Method to add a translator to this service. It will be called from the 
	 * configurator to initialize the translation service.
	 * @param t Translator to be added.
	 * @throws UnknownRulesLanguageException Thrown in case of whrong version
	 * of the ruleml mediator language.
	 */
	public void addTranslator(RulesTranslator t) throws UnknownRulesLanguageException {
		if (t.getInputLanguage().equals(ruleML)) {
			translatorsFromRuleML.put(t.getOutputLanguage(),t);
		} else if(t.getOutputLanguage().equals(ruleML)) {
			translatorsToRuleML.put(t.getInputLanguage(),t);
		} else {
			throw new UnknownRulesLanguageException("Neither the input nor the output languages is RuleML");
		}
	}

}
