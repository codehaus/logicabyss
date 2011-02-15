package ruleml.translator;

import static org.junit.Assert.fail;

import org.drools.KnowledgeBase;
import org.drools.command.assertion.AssertEquals;
import org.drools.compiler.DrlParser;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import reactionruleml.RuleMLType;
import ruleml.translator.TestDataModel.Buy;
import ruleml.translator.TestDataModel.Keep;
import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator;
import static org.junit.Assert.*;

public class TestDrools {

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
				.println("***********************   RuleML -> Drl  **************************");
		System.out
				.println("------------------------   Own slotted  ----------------------------");
		try {
			// read the ruleml file
			RuleMLType ruleML = RuleML2DroolsTranslator
					.readRuleML("ruleml/DerivationRule.rrml");

			String drl = RuleML2DroolsTranslator.translate(ruleML);

			System.out.println(drl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	// @Test
	public void testRuleML2Drools_2() {
		System.out
				.println("------------------------   Derivation rule  ------------------------");
		try {
			// read the ruleml file
			RuleMLType ruleML = RuleML2DroolsTranslator
					.readRuleML("ruleml/own_slotted.xml");

			String drl = RuleML2DroolsTranslator.translate(ruleML);

			System.out.println(drl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	// @Test
	public void testRuleML2Drools_3() {
		System.out
				.println("***********************   Production rule  **************************");
		try {
			// read the ruleml file
			RuleMLType ruleML = RuleML2DroolsTranslator
					.readRuleML("ruleml/ProductionRule.rrml");

			String drl = RuleML2DroolsTranslator.translate(ruleML);

			System.out.println(drl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDrools2RuleML_1() {
		System.out
				.println("------------------------   Drl -> RuleML : TEST ASSERT ------------------------");
		KnowledgeRuntimeLogger logger = null;
		try {
			Resource resource = ResourceFactory
					.newClassPathResource("drools/test1.drl");
			final DrlParser parser = new DrlParser();
			final PackageDescr pkgDescr = parser.parse(resource
					.getInputStream());

			// load up the knowledge base
			KnowledgeBase kbase = Drools2RuleMLTranslator
					.readKnowledgeBase("drools/test1.drl");
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");

			String result = Drools2RuleMLTranslator.translate(kbase, pkgDescr);
			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><RuleML xmlns=\"http://www.ruleml.org/1.0/xsd\"><Assert><Rule><if><And><And><Atom><op><Rel>LessThan</Rel></op><Var>VAR1</Var><Ind>13</Ind></Atom><Atom><op><Rel>Person</Rel></op><slot><Ind>name</Ind><Ind>john</Ind></slot><slot><Ind>age</Ind><Var>VAR1</Var></slot></Atom></And><Atom><op><Rel>Buy</Rel></op><slot><Ind>buyer</Ind><Var>$person</Var></slot><slot><Ind>seller</Ind><Var>$merchant</Var></slot><slot><Ind>item</Ind><Var>$object</Var></slot></Atom><Atom><op><Rel>Keep</Rel></op><slot><Ind>keeper</Ind><Var>$person</Var></slot><slot><Ind>item</Ind><Ind>test</Ind></slot></Atom></And></if><do><Assert><Atom><Rel>Own</Rel><slot><Ind>item</Ind><Var>$person</Var></slot><slot><Ind>owner</Ind><Var>$object</Var></slot></Atom></Assert></do></Rule></Assert></RuleML>";

			System.out.println(result);
			
			assertEquals(expected, result);
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		} finally {
			logger.close();
		}
	}

	@Test
	public void testDrools2RuleML_2() {

		System.out
				.println("------------------------   Drl -> RuleML : TEST RETRACT ------------------------");
		KnowledgeRuntimeLogger logger = null;
		try {
			Resource resource = ResourceFactory
					.newClassPathResource("drools/test2.drl");
			final DrlParser parser = new DrlParser();
			final PackageDescr pkgDescr = parser.parse(resource
					.getInputStream());

			// load up the knowledge base
			KnowledgeBase kbase = Drools2RuleMLTranslator
					.readKnowledgeBase("drools/test2.drl");
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");

			String result = Drools2RuleMLTranslator.translate(kbase, pkgDescr);
			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><RuleML xmlns=\"http://www.ruleml.org/1.0/xsd\"><Retract><Rule><if><And><Atom><op><Rel>Buy</Rel></op><slot><Ind>buyer</Ind><Ind>Ti6o</Ind></slot><slot><Ind>seller</Ind><Ind>Dealer</Ind></slot><slot><Ind>item</Ind><Ind>Objective</Ind></slot></Atom><Atom><op><Rel>Person</Rel></op><slot><Ind>name</Ind><Var>$buyer</Var></slot><slot><Ind>age</Ind><Var></Var></slot></Atom></And></if><do><Retract><Atom><op><Rel>Buy</Rel></op><slot><Ind>buyer</Ind><Ind>Ti6o</Ind></slot><slot><Ind>seller</Ind><Ind>Dealer</Ind></slot><slot><Ind>item</Ind><Ind>Objective</Ind></slot></Atom></Retract></do></Rule></Retract></RuleML>";
			
			System.out.println(result);
			assertEquals(expected, result);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		} finally {
			logger.close();
		}
	}
}
