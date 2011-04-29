package ruleml.translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

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

	static void test2() {
		final String rulebase = "rules/ruleml/assert.out.xml";

		String inputRules = "buy(\"Tisho\",\"laptop\",\"Amazon\"). \n" +
				"keep(\"Tisho\",\"laptop\"). \n" +
				":-solve(own(X,Y)).";

		BufferedReader inRules = new BufferedReader(
				new StringReader(inputRules));

		comm = new ProvaCommunicatorImpl(kAgent, kPort, rulebase,
				ProvaCommunicatorImpl.SYNC, null);

		try {
			List<ProvaSolution[]> solutions = comm.consultSync(inRules, "to-remove-later", new Object[] {});
//			List<ProvaSolution[]> solutions = comm.getInitializationSolutions();
			printRusluts(solutions);
		} catch (Exception unlikely) {
			unlikely.printStackTrace();
		}
	}

	// static void test1() {
	// final String rulebase = "rules/prova/test.prova";
	// String inputRules = "pappy(Person) :- happy(Person).";
	// BufferedReader inRules = new BufferedReader(
	// new StringReader(inputRules));
	//
	// comm = new ProvaCommunicatorImpl(kAgent, kPort, rulebase,
	// ProvaCommunicatorImpl.SYNC, null);
	//
	// try {
	// comm.consultSync(inRules, "to-remove-later", new Object[] {});
	// } catch (Exception unlikely) {
	// unlikely.printStackTrace();
	// }
	//
	// String input = ":- solve(happy(Person)).\n :- solve(pappy(Person)).";
	// BufferedReader in = new BufferedReader(new StringReader(input));
	//
	// try {
	// List<ProvaSolution[]> resultSets = comm.consultSync(in, "goals",
	// new Object[] {});
	// printRusluts(resultSets);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	static void printRusluts(List<ProvaSolution[]> resultSets) {
		System.out.println("Starting PrintResult *************");
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

	public static String readFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	public static void testXSL() {

		test2();

		// RuleML2ProvaTranslator ruleml2ProvaTranslator = new
		// RuleML2ProvaTranslator();
		// Prova2RuleMLTranslator prova2RuleMLTranslator = new
		// Prova2RuleMLTranslator();
		//
		// try {
		// File file = new File("test.txt");
		// System.out.println(file.getCanonicalPath());
		//
		// String srcRuleML = readFileAsString("rules/ruleml/assert.ruleml");
		// ruleml2ProvaTranslator.setXSLT("resources/rrml2prova_1.0.xsl");
		// Object result = ruleml2ProvaTranslator.doTransform(srcRuleML,
		// "UTF-8");
		//
		// System.out.println(result);
		//
		// String resultProva = readFileAsString("result.prova");
		// System.out.println(resultProva.equals(doTransform.toString()));
		//
		// String srcprovaML = readFileAsString("test.ruleml");
		// Object doTransform = ml2ProvaTranslator.doTransform(srcprovaML,
		// "UTF-8");
		// System.out.println(doTransform);
		// String resultProva = readFileAsString("result.prova");
		//
		// System.out.println(result.equals(doTransform.toString()));
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}
}
