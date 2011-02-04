package ruleml.translator.ruleml2drl;

import java.util.List;

import reactionruleml.AssertType;
import ruleml.translator.ruleml2drl.DroolsBuilder.Drl;
import ruleml.translator.ruleml2drl.DroolsBuilder.Rule;

public class AssertProcessor extends RuleMLGenericProcessor {

	public AssertProcessor(RuleML2DroolsTranslator translator) {
		super(translator);
	}

	public void processAssert(AssertType assertType) {
		List<Object> formulaOrRulebaseOrAtom = assertType
				.getFormulaOrRulebaseOrAtom();
		currentRule = new Rule();

		for (Object o : formulaOrRulebaseOrAtom) {
			// create a empty rule
			currentRule = new Rule();

			// forward
			translator.dispatchType(o);

			// add the current rule to the list with rules
			if (currentRule != null) {
				currentRule.setRuleName("rule" + ruleNumber++);
				if (this.whenPatterns.isEmpty()) {
					currentRule.setWhenPart(new String[] { "eval(true)" });
				} else {
					currentRule.setWhenPart(this.whenPatterns.toArray());
				}

				processThenPatterns("insert");

				currentRule.setThenPart(this.thenPatterns.toArray());
				translator.getDrl().addRule(currentRule);

				// reset the patterns
				this.whenPatterns.clear();
				this.thenPatterns.clear();
			}

		}
	}
}
