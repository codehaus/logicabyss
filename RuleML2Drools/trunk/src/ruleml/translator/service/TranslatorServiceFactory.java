package ruleml.translator.service;

import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;
import ruleml.translator.prova2ruleml.Prova2RuleMLTranslator;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator;
import ruleml.translator.ruleml2prova.RuleML2ProvaTranslator;

/**
 * Creates a translator servicea and makes the configuraiton for it. It couples the instances 
 * of the individual translators with the central translator service.
 * 
 * @author jabarski
 */
public class TranslatorServiceFactory {
	public static RulesTranslatorService createTranslatorService () {
		RulesTranslatorServiceImpl translatorService = new RulesTranslatorServiceImpl();
		try {
			translatorService.addTranslator(new Drools2RuleMLTranslator());
			
			translatorService.addTranslator(new RuleML2DroolsTranslator());
			
			translatorService.addTranslator(new Prova2RuleMLTranslator());
			
			RuleML2ProvaTranslator ruleml2ProvaTranslator = new RuleML2ProvaTranslator();
			ruleml2ProvaTranslator.setXSLT("resources/rrml2prova_1.0.xsl");
			translatorService.addTranslator(ruleml2ProvaTranslator);

		} catch (UnknownRulesLanguageException e) {
			e.printStackTrace();
		}
		
		return translatorService;
	}
}
