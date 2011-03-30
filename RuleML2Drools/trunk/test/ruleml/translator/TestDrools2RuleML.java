package ruleml.translator;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.compiler.DrlParser;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;

public class TestDrools2RuleML extends TestCase{

	@Test
	public void test_Assert() {
		System.out
				.println("------------------------   Drl -> RuleML : TEST ASSERT ------------------------");
		KnowledgeRuntimeLogger logger = null;
		try {
			Resource resource = ResourceFactory
					.newClassPathResource("drools/test_assert.drl");
			final DrlParser parser = new DrlParser();
			final PackageDescr pkgDescr = parser.parse(resource
					.getInputStream());

			// load up the knowledge base
			KnowledgeBase kbase = Drools2RuleMLTranslator
					.readKnowledgeBase("drools/test_assert.drl");
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");

			String result = Drools2RuleMLTranslator.translate(kbase, pkgDescr);

			
			System.out.println(result);
			
//			String expected = "";
//			assertEquals(expected, result);
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		} finally {
			logger.close();
		}
	}

	@Test
	public void test_Retract() {

		System.out
				.println("------------------------   Drl -> RuleML : TEST RETRACT ------------------------");
		KnowledgeRuntimeLogger logger = null;
		try {
			Resource resource = ResourceFactory
					.newClassPathResource("drools/test_retract.drl");
			final DrlParser parser = new DrlParser();
			final PackageDescr pkgDescr = parser.parse(resource
					.getInputStream());

			// load up the knowledge base
			KnowledgeBase kbase = Drools2RuleMLTranslator
					.readKnowledgeBase("drools/test_retract.drl");
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");

			String result = Drools2RuleMLTranslator.translate(kbase, pkgDescr);
			
			System.out.println(result);
			
//			String expected = "";
//			assertEquals(expected, result);
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		} finally {
			logger.close();
		}
	}
}
