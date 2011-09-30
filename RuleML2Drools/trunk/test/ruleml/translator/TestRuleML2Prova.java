package ruleml.translator;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;
import ruleml.translator.ruleml2prova.RuleML2ProvaTranslator;
import ws.prova.api2.ProvaCommunicator;
import ws.prova.api2.ProvaCommunicatorImpl;
import ws.prova.exchange.ProvaSolution;

public class TestRuleML2Prova {
	static final String kAgent = "prova";

	static final String kPort = null;

	private static ProvaCommunicator comm;

	public static void main(String[] args) {
		testXSL();
	}

	static void testXSL() {

		RuleML2ProvaTranslator ruleml2ProvaTranslator = new RuleML2ProvaTranslator();
		ruleml2ProvaTranslator.setXSLT("resources/rrml2prova_1.0.xsl");

		Drools2RuleMLTranslator drools2RuleMLTranslator = new Drools2RuleMLTranslator();
		
		try {

			final String ruleBase = Util.readFileAsString("rules/drools/test.drl");

			// Drools -> ruleML translation
			Object ruleML = drools2RuleMLTranslator.translate(ruleBase);

			System.out.println(ruleML);
			
			// RuleML -> Prova translation
			Object result = ruleml2ProvaTranslator.doTransform(ruleML, "UTF-8");

			// execute prova on the rule base
			test2(result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void test2(String ruleBase) {
		String inputRules = ruleBase+ ":-solve(own(X,Y)).";

		System.out.println(inputRules);

		BufferedReader inRules = new BufferedReader(
				new StringReader(inputRules));

		comm = new ProvaCommunicatorImpl(kAgent, kPort, null,
				ProvaCommunicatorImpl.SYNC, null);

		try {
			List<ProvaSolution[]> solutions = comm.consultSync(inRules,
					"to-remove-later", new Object[] {});
			printRusluts(solutions);
		} catch (Exception unlikely) {
			unlikely.printStackTrace();
		}
	}

	static void printRusluts(List<ProvaSolution[]> resultSets) {
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
