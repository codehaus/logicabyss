package ruleml.translator.service;

import java.util.Iterator;
import java.util.ServiceLoader;

import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;
import ruleml.translator.prova2ruleml.Prova2RuleMLTranslator;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator;
import ruleml.translator.ruleml2prova.RuleML2ProvaTranslator;

/**
 * Creates a translator servicea and makes the configuraiton for it. It couples
 * the instances of the individual translators with the central translator
 * service.
 * 
 * @author jabarski
 */
public class RulesTranslatorServiceFactory {
	public static RulesTranslatorService createTranslatorService() {
		RulesTranslatorServiceImpl translatorService = new RulesTranslatorServiceImpl();

		try {
			ServiceLoader<RulesTranslator> translatorsLoader = ServiceLoader
					.load(RulesTranslator.class);
			Iterator<RulesTranslator> iterator = translatorsLoader.iterator();
			while (iterator.hasNext()) {
				RulesTranslator translator = (RulesTranslator) iterator.next();
				translatorService.addTranslator(translator);
			}

		} catch (UnknownRulesLanguageException e) {
			e.printStackTrace();
		}

		return translatorService;
	}
}
