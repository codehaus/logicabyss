package ruleml.translator.service;

/**
 * Prototype implementation of the RulesTranslationService interface.
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class RulesTranslatorServiceImpl implements RulesTranslatorService {

	private IdentityHashMap<RulesLanguage, RulesTranslator> translatorsToRuleML = new IdentityHashMap<RulesLanguage, RulesTranslator>();
	private IdentityHashMap<RulesLanguage, RulesTranslator> translatorsFromRuleML = new IdentityHashMap<RulesLanguage, RulesTranslator>();

	@Override
	public String translate(String input, RulesLanguage in, RulesLanguage out)
			throws UnknownRulesLanguageException {
		List<RulesLanguage> mediators = new ArrayList<RulesLanguage>();
		List<RulesTranslator> inTranslators = new ArrayList<RulesTranslator>();

		for (RulesTranslator inTranslator : translatorsToRuleML.values()) {
			if (inTranslator.getInputLanguage().equals(in)) {
				
				if (inTranslator.getOutputLanguage().equals(out)) {
					return (String) inTranslator.translate(input);
				}
				
				mediators.add(inTranslator.getOutputLanguage());
				inTranslators.add(inTranslator);
			}
		}

		for (RulesTranslator outTranslator : translatorsFromRuleML.values()) {
			if (outTranslator.getOutputLanguage().equals(out)
					&& mediators.contains(outTranslator.getInputLanguage())) {
				RulesTranslator inTranslator = inTranslators.get(mediators
						.indexOf(outTranslator.getInputLanguage()));
				String ruleMLOutput = (String) inTranslator.translate(input);
				return (String) outTranslator.translate(ruleMLOutput);
			}
		}

		throw new UnknownRulesLanguageException(
				"Could not find translator for " + in + " -> " + out);

	}

	@Override
	public Set<RulesLanguage> getSupportedInputLanguages() {
		return translatorsToRuleML.keySet();
	}

	@Override
	public Set<RulesLanguage> getSupportedOutputLanguages(RulesLanguage in) {
		List<RulesLanguage> mediators = new ArrayList<RulesLanguage>();

		for (RulesTranslator inTranslator : translatorsToRuleML.values()) {
			if (inTranslator.getInputLanguage().equals(in)) {
				mediators.add(inTranslator.getOutputLanguage());
			}
		}

		for (RulesTranslator outTranslator : translatorsFromRuleML.values()) {
			if (mediators.contains(outTranslator.getInputLanguage())) {
				mediators.add(outTranslator.getOutputLanguage());
			}
		}

		return new HashSet<RulesLanguage>(mediators);
	}

	@Override
	public boolean supportsTranslation(RulesLanguage in, RulesLanguage out) {
		throw new UnsupportedOperationException(
		"The operation in not implemented yet");
	}

	@Override
	public RulesLanguage guessLanguage(String input)
			throws UnknownRulesLanguageException {
		throw new UnsupportedOperationException(
				"The operation in not implemented yet");
	}

	/**
	 * Method to add a translator to this service. It will be called from the
	 * configurator to initialize the translation service.
	 * 
	 * @param t
	 *            Translator to be added.
	 * @throws UnknownRulesLanguageException
	 *             Thrown in case of whrong version of the ruleml mediator
	 *             language.
	 */
	public void addTranslator(RulesTranslator t)
			throws UnknownRulesLanguageException {
		translatorsFromRuleML.put(t.getOutputLanguage(), t);
		translatorsToRuleML.put(t.getInputLanguage(), t);
	}
}
