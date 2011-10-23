package ruleml.translator.service;

/**
 * Exception class in case of use of language the is not understood from
 * the RulesTranslator.
 * 
 * @author jabarski
 *
 */
public class UnknownRulesLanguageException extends Exception{

	public UnknownRulesLanguageException(String msg) {
		super(msg);
	}

}
