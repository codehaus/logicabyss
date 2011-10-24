package ruleml.translator.service.mule;

import java.util.logging.Logger;

import org.apache.axis.handlers.soap.SOAPService;
import org.mule.providers.soap.axis.AxisInitialisable;
import org.mule.umo.lifecycle.InitialisationException;

import ruleml.translator.service.RulesLanguage;
import ruleml.translator.service.RulesTranslatorService;
import ruleml.translator.service.RulesTranslatorServiceFactory;
import ruleml.translator.service.UnknownRulesLanguageException;

/**
 * The implementation of the adapter for the WS-Translator Service.
 * @author jabarski
 *
 */
public class RulesTranslatorServiceHTTPAdapter implements AxisInitialisable,
		RulesTranslatorServiceHTTPAdapterIF {

	private static Logger logger = Logger
			.getLogger(RulesTranslatorServiceHTTPAdapter.class.getName());
	private RulesTranslatorService translatorService;

	public RulesTranslatorServiceHTTPAdapter() {
	}

	@Override
	public String translate(String input, String inLanguage,
			String inLanguageVersion, String outLanguage,
			String outLanguageVersion) throws UnknownRulesLanguageException {
		return translatorService.translate(input, new RulesLanguage(inLanguage,
				inLanguageVersion), new RulesLanguage(outLanguage,
				outLanguageVersion));
	}

	@Override
	public String getSupportedInputLanguages() {
		String result = "";
		for (RulesLanguage l : translatorService.getSupportedInputLanguages()) {
			result = result.concat(l.getName() + ":" + l.getVersion() + "\n");
		}
		return result;
	}

	@Override
	public String getSupportedOutputLanguages(String inLanguage,
			String inLanguageVersion) {
		String result = "";
		for (RulesLanguage l : translatorService
				.getSupportedOutputLanguages(new RulesLanguage(inLanguage,
						inLanguageVersion))) {
			result = result.concat(l.getName() + ":" + l.getVersion() + "\n");
		}
		return result;
	}

	@Override
	public boolean supportsTranslation(String inLanguage,
			String inLanguageVersion, String outLanguage,
			String outLanguageVersion) {
		return translatorService.supportsTranslation(new RulesLanguage(
				inLanguage, inLanguageVersion), new RulesLanguage(outLanguage,
				outLanguageVersion));
	}

	@Override
	public RulesLanguage guessLanguage(String input)
			throws UnknownRulesLanguageException {
		return translatorService.guessLanguage(input);
	}

	@Override
	public void initialise(SOAPService service) throws InitialisationException {
		System.out.println("Initializing the RulesTranslator service.");
		this.translatorService = RulesTranslatorServiceFactory
				.createTranslatorService();
	}
}
