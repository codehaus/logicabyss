package ruleml.translator.ruleml2drl;

import java.util.List;

import reactionruleml.RetractType;
import ruleml.translator.ruleml2drl.DroolsBuilder.Drl;
import ruleml.translator.ruleml2drl.DroolsBuilder.Rule;

public class RetractProcessor extends RuleMLGenericProcessor{
	
	public RetractProcessor(RuleML2DroolsTranslator translator) {
		super(translator);
	}

	public void processRetract(RetractType retractType) {
		List<Object> formulaOrRulebaseOrAtom = retractType
				.getFormulaOrRulebaseOrAtom();
		currentRule = new Rule();

		for (Object o : formulaOrRulebaseOrAtom) {
			// create a empty rule
			currentRule = new Rule();

			// forward
			translator.dispatchType(o);

			// add the current rule to the list with rules
			if (currentRule != null) {
				currentRule.setRuleName("rule" + this.ruleNumber++);
				if (this.whenPatterns.isEmpty()) {
					currentRule.setWhenPart(new String[] { "eval(true)" });
				} else {
					currentRule.setWhenPart(this.whenPatterns.toArray());
				}

				processThenPatterns("retract");

				currentRule.setThenPart(this.thenPatterns.toArray());
				translator.getDrl().addRule(currentRule);

				// reset the patterns
				this.whenPatterns.clear();
				this.thenPatterns.clear();
			}
		}
	}
}
