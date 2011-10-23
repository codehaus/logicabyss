package ruleml.translator.service.mule;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.axis.handlers.soap.SOAPService;
import org.mule.providers.soap.axis.AxisInitialisable;
import org.mule.umo.UMOEventContext;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.util.PropertiesUtils;

import ruleml.translator.service.RulesLanguage;
import ruleml.translator.service.RulesTranslatorService;
import ruleml.translator.service.RulesTranslatorServiceFactory;
import ruleml.translator.service.UnknownRulesLanguageException;

public class RulesTranslatorServiceHTTPAdapter implements AxisInitialisable,
		RulesTranslatorServiceHTTPAdapterIF {

	private static Logger logger = Logger
			.getLogger(RulesTranslatorServiceHTTPAdapter.class.getName());
	private RulesTranslatorService translatorService;

	public RulesTranslatorServiceHTTPAdapter() {
	}

	@Override
	public Object onCall(UMOEventContext eventContext) throws Exception {
		try {
			byte[] bytes = (byte[]) eventContext.getMessageAsBytes();
			String request;
			request = new String(bytes, "UTF8");
			request = URLDecoder.decode(request, "UTF8");
			int i = request.indexOf('?');
			String query = request.substring(i + 1);
			Properties p = PropertiesUtils.getPropertiesFromQueryString(query);

			RulesTranslatorService translatorService = RulesTranslatorServiceFactory
					.createTranslatorService();

			String input = p.get("input").toString();
			String inputLanguage = p.get("inputlanguage").toString();
			String inputVersion = p.get("inputversion").toString();
			String outputLanguage = p.get("outputlanguage").toString();
			String outputVersion = p.get("outputversion").toString();
			String methodName = p.get("method").toString();

			// get the method from the HTTP parameter
			Method[] methods = RulesTranslatorService.class
					.getDeclaredMethods();
			Method method = null;
			for (Method m : methods) {
				if (m.getName().equals(methodName)) {
					method = m;
					logger.info("Found method " + methodName
							+ " on the translation service to be called.");
				}
			}

			if (method == null) {
				throw new RuntimeException("Method " + methodName
						+ "not found for class "
						+ RulesTranslatorService.class.getName());
			}

			logger.info("Input : " + input);
			logger.info("Input language : " + inputLanguage);
			logger.info("Input version : " + inputVersion);
			logger.info("Output language : " + outputLanguage);
			logger.info("Output version : " + outputVersion);

			String result = translatorService.translate(input,
					new RulesLanguage(inputLanguage, inputVersion),
					new RulesLanguage(outputLanguage, outputVersion));

			logger.info("Output : " + input);

			return result;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "Error";
		}
	}

	@Override
	public String translateToRuleML(String input, String language,
			String version) throws UnknownRulesLanguageException {
		return translatorService.translateToRuleML(input, new RulesLanguage(
				language, version));
	}

	@Override
	public String translateToRuleML(String input)
			throws UnknownRulesLanguageException {
		return translatorService.translateToRuleML(input);
	}

	@Override
	public String translateFromRuleML(String input, String language,
			String version) throws UnknownRulesLanguageException {
		return translatorService.translateFromRuleML(input, new RulesLanguage(
				language, version));
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
	public RulesLanguage getRuleMLInfo() {
		return translatorService.getRuleMLInfo();
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
	public String getSupportedOutputLanguages() {
		String result = "";
		for (RulesLanguage l : translatorService.getSupportedOutputLanguages()) {
			result = result.concat(l.getName() + ":" + l.getVersion() + "\n");
		}
		return result;
	}

	@Override
	public boolean supportsTrnslation(String inLanguage,
			String inLanguageVersion, String outLanguage,
			String outLanguageVersion) {
		return translatorService.supportsTrnslation(new RulesLanguage(
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
		System.out.println("Initializing the service.");
		this.translatorService = RulesTranslatorServiceFactory
				.createTranslatorService();
	}


	@Override
	public String test (String t) {
		return "Hello " + t;
	}
	
}
