package ruleml.translator;

import static org.junit.Assert.fail;

import org.drools.KnowledgeBase;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import reactionruleml.RuleMLType;
import ruleml.translator.TestDataModel.Buy;
import ruleml.translator.TestDataModel.Keep;
import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

//import static org.junit.Assert.fail;

public class TestRuleML2Drools {
	// @Test
	public void test1() {
		try {
			// load up the knowledge base
			KnowledgeBase kbase = Drools2RuleMLTranslator
					.readKnowledgeBase("drools/test.drl");
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");

			Buy buy1 = new Buy("Ti6o", "Dealer", "Objective");
			Buy buy2 = new Buy("Margo", "Amazon", "USB");
			Keep keep = new Keep("Ti6o", "Objective");

			ksession.insert(buy1);
			ksession.insert(buy2);
			ksession.insert(keep);

			System.out.println(ksession.getFactCount());

			ksession.fireAllRules();

			System.out.println(ksession.getFactCount());

			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		}
	}

	// @Test
	public void testRuleML2Drools_1() {
		System.out
				.println("***********************   RuleML -> Drl: Derivation rule  **************************");
		try {
			// read the ruleml file
			RuleMLType ruleML = RuleML2DroolsTranslator
					.readRuleML("ruleml/DerivationRule.rrml");

			String drl = RuleML2DroolsTranslator.translate(ruleML);

			String expected = "package org.ruleml.translator\n"
					+ "import org.ruleml.translator.TestDataModel.*;\n" + "\n"
					+ "query \"query3\"\n"
					+ "		Person(name==\"John\",age==\"25\")\n"
					+ "		Likes(subject==\"John\",object==\"whom\")\n" + "\n"
					+ "end\n" + "\n" + "rule \"rule1\"\n" + "	when\n"
					+ "		Likes($X:subject,object==\"wine\")\n" + "\n"
					+ "	then\n" + "		insert( new Likes(\"John\",$X));\n" + "\n"
					+ "end\n" + "rule \"rule2\"\n" + "	when\n"
					+ "		eval(true)\n" + "\n" + "	then\n"
					+ "		insert( new Likes(\"Mary\",\"wine\"));\n" + "\n"
					+ "end\n" + "rule \"rule4\"\n" + "	when\n"
					+ "		$var: Likes(subject==\"Mary\",object==\"wine\")\n"
					+ "\n" + "	then\n" + "		retract($var)\n" + "\n" + "end\n";

			assertEquals(expected, drl);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	// @Test
	public void test_assert() {
		System.out
				.println("***********************   RuleML -> Drl: test_assert  **************************");

		try {
			// read the ruleml file
			RuleMLType ruleML = RuleML2DroolsTranslator
					.readRuleML("ruleml/test_assert.ruleml");

			String drl = RuleML2DroolsTranslator.translate(ruleML);

			System.out.println(drl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test_retract() {
		System.out
				.println("***********************   RuleML -> Drl: test_retract  **************************");

		try {
			// read the ruleml file
			RuleMLType ruleML = RuleML2DroolsTranslator
					.readRuleML("ruleml/test_retract.ruleml");

			String drl = RuleML2DroolsTranslator.translate(ruleML);

			System.out.println(drl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
