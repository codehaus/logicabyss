package ruleml.test.translator;

import junit.framework.TestCase;

import org.junit.Test;

import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;

public class TestDrools2RuleML extends TestCase {

	@Test
	public void test() {
		System.out
				.println("------------------------   Drl -> RuleML : TEST ASSERT ------------------------");
		try {
			final String ruleBase = Util
					.readFileAsString("rules/drools/test.drl");
			
			Drools2RuleMLTranslator drools2RuleMLTranslator = new Drools2RuleMLTranslator();
			
//			KnowledgeBase kbase = drools2RuleMLTranslator.readKnowledgeBase(ruleBase);
//			StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
//			ksession.fireAllRules();
//			QueryResults results = ksession.getQueryResults( "testquery" );
//			System.out.println( "Results:" );
//			for ( QueryResultsRow row : results ) {
//				System.out.println("!!!");
//			    Own own= ( Own) row.get( "$own" );
//			    System.out.println( "Item:"+own.getItem() + ", Owner: "+own.getOwner()+"\n" );
//			}
//
//			query "testquery"
//				$own : Own()
//			end			
			
			Object result = drools2RuleMLTranslator.translate(ruleBase);
			System.out.println(result);

		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		}
	}
}
