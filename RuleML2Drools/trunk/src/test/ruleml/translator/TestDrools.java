package ruleml.translator;

import org.junit.Test;

import datalog.RuleMLType;


public class TestDrools {

	@Test
	public void test () {
		// read the ruleml file
		RuleMLType ruleML = RuleML2DroolsTranslator.readRuleML("ruleml1.xml");
		
		String drl = RuleML2DroolsTranslator.translate(ruleML);
		
		System.out.println(drl);
	}
}
