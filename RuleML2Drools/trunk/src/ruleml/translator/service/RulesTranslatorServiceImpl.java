package ruleml.translator.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RulesTranslatorServiceImpl implements RulesTranslatorService {

	private RulesLanguage ruleML = new RulesLanguage("RuleML", "1.0");
	private Map<RulesLanguage, Translator> translatorsToRuleML = new HashMap<RulesLanguage, Translator>();
	private Map<RulesLanguage, Translator> translatorsFromRuleML = new HashMap<RulesLanguage, Translator>();

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
	public String translateToLanguage(String input, RulesLanguage in,
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportsLanguage(RulesLanguage l) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RulesLanguage guessLanguage(String input)
			throws UnknownRulesLanguageException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addTranslator(Translator t) throws UnknownRulesLanguageException {
		if (t.getInputLanguage().equals(ruleML)) {
			translatorsFromRuleML.put(t.getOutputLanguage(),t);
		} else if(t.getOutputLanguage().equals(ruleML)) {
			translatorsToRuleML.put(t.getInputLanguage(),t);
		} else {
			throw new UnknownRulesLanguageException("Neither the input nor the output languages is RuleML");
		}
	}

}
