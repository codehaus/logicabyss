package ruleml.translator.ruleml2drl;

import java.util.List;

import reactionruleml.AssertType;
import reactionruleml.AtomType;
import reactionruleml.RuleType;
import ruleml.translator.ruleml2drl.DroolsBuilder.Drl;
import ruleml.translator.ruleml2drl.DroolsBuilder.Rule;

public class AssertProcessor extends RuleMLGenericProcessor {

	public AssertProcessor(RuleML2DroolsTranslator translator) {
		super(translator);
	}

	public void processAssert(AssertType assertType) {
		List<Object> formulaOrRulebaseOrAtom = assertType
				.getFormulaOrRulebaseOrAtom();

		for (Object o : formulaOrRulebaseOrAtom) {
			if (o instanceof AtomType) {
				// create a empty rule
				Rule currentRule = new Rule();
				
				// forward
				translator.dispatchType(o);
				
				currentRule.setRuleName("rule" + ruleNumber++);
				if (translator.getWhenPatterns().isEmpty()) {
					currentRule.setWhenPart(new String[] { "eval(true)" });
				} else {
					currentRule.setWhenPart(translator.getWhenPatterns().toArray());
				}
				
				processThenPatterns("insert");
				
				currentRule.setThenPart(translator.getThenPatterns().toArray());
				translator.getDrl().addRule(currentRule);
				
				// reset the patterns
				translator.getWhenPatterns().clear();
				translator.getThenPatterns().clear();

			} else if (o instanceof RuleType) {
				// only dispatch forward
				translator.dispatchType(o);
			}
			
		}
	}
}
