package ruleml.translator;

import static org.junit.Assert.fail;
import reactionruleml.RuleMLType;

import org.drools.KnowledgeBase;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import ruleml.translator.TestDataModel.Buy;
import ruleml.translator.TestDataModel.Keep;
import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator;

public class TestDrools {

//	@Test
	public void test1 () {
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
	
	@Test
	public void testRuleML2Drools_1() {
		System.out
				.println("***********************   RuleML -> Drl  **************************");
		try {
			// read the ruleml file
			RuleMLType ruleML = RuleML2DroolsTranslator
//					.readRuleML("ruleml/own_slotted.xml");
					.readRuleML("ruleml/DerivationRule.rrml");

			String drl = RuleML2DroolsTranslator.translate(ruleML);

			System.out.println(drl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

//	@Test
	public void testDrools2RuleML_1() {
		System.out
				.println("*************************   Drl -> RuleML ************************");

		try {
			// load up the knowledge base
			KnowledgeBase kbase = Drools2RuleMLTranslator
					.readKnowledgeBase("drools/test.drl");
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");

//			Buy buy1 = new Buy("Ti6o", "Dealer", "Objective");
//			Buy buy2 = new Buy("Margo", "Amazon", "USB");
//			Keep keep = new Keep("Ti6o", "Objective");
//
//			ksession.insert(buy1);
//			ksession.insert(buy2);
//			ksession.insert(keep);
//
//			ksession.fireAllRules();

			String result = Drools2RuleMLTranslator.translate(kbase);

			System.out.println(result);

			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		}
	}
}
