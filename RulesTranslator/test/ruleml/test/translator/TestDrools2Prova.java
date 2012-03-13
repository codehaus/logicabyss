package ruleml.test.translator;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import ruleml.translator.service.RulesLanguage;
import ruleml.translator.service.RulesTranslatorService;
import ruleml.translator.service.RulesTranslatorServiceFactory;
import ws.prova.api2.ProvaCommunicator;
import ws.prova.api2.ProvaCommunicatorImpl;
import ws.prova.exchange.ProvaSolution;

/**
 * End-to-end test for rules translation service: Drools -> Prova.
 * 
 * @author jabarski
 *
 */
public class TestDrools2Prova {
	private static final String kAgent = "prova";
	private static final String kPort = null;
	private static final String drlFile = "rules/drools/test.drl"; 

	public static void main(String[] args) {
		// translate
		String result = testTranslateDrools2RuleML();

		// execute prova on the rule base
		execProvaRules(result);
	}

	/**
	 * Tests the translation: drools rulebase in prova.
	 * @return The result from tha translation.
	 */
	private static String testTranslateDrools2RuleML() {

		// create the translation service
		RulesTranslatorService translatorService = RulesTranslatorServiceFactory
				.createTranslatorService();

		try {

			// read the drl-rulebase
			final String input = Util.readFileAsString(drlFile);

			// translate
			return translatorService.translate(input,
					new RulesLanguage("Drools", "1.0"), new RulesLanguage(
							"Prova", "1.0"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "error";
	}

	/**
	 * Test the result from the translation Drools -> Prova. 
	 * Executes the rules on the translated rulebase.
	 * @param ruleBase The prova rulesbase.
	 */
	private static void execProvaRules(String ruleBase) {
		String inputRules = ruleBase + ":-solve(own(X,Y)).";

		System.out.println(inputRules);

		BufferedReader inRules = new BufferedReader(
				new StringReader(inputRules));

		ProvaCommunicator comm = new ProvaCommunicatorImpl(kAgent, kPort, null,
				ProvaCommunicatorImpl.SYNC, null);

		try {
			List<ProvaSolution[]> solutions = comm.consultSync(inRules,
					"to-remove-later", new Object[] {});
			printRusluts(solutions);
		} catch (Exception unlikely) {
			unlikely.printStackTrace();
		}
	}

	/**
	 * Prints the result from the prova query execution.
	 * @param resultSets The result from the prova execution.
	 */
	private static void printRusluts(List<ProvaSolution[]> resultSets) {
		System.out.println("**************  Print prova results *************");
		for (ProvaSolution[] resultSet : resultSets) {
			System.out.println("--- new ResultSet ---");
			for (ProvaSolution provaSolution : resultSet) {
				System.out.println("--- new ProvaSolution ---");
				for (Object entry : provaSolution.getNv().entrySet()) {
					System.out.println(entry);
				}
				// final Object ans1 = ((ProvaConstant)
				// provaSolution.getNv("Z")).getObject();
				// final Object ans2 = ((ProvaConstant)
				// provaSolution.getNv("W")).getObject();
				// System.out.println("Result : " + ans1);
				// System.out.println("Result : " + ans2);
			}
		}
	}
}
