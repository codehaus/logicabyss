package ruleml.translator.ruleml2drl;

import java.util.List;

import reactionruleml.RelType;
import reactionruleml.RetractType;
import ruleml.translator.ruleml2drl.DroolsBuilder.Drl;
import ruleml.translator.ruleml2drl.DroolsBuilder.Rule;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator.DrlPattern;

public class RetractProcessor extends RuleMLGenericProcessor {

	private String varToRetract = "$var";

	public RetractProcessor(RuleML2DroolsTranslator translator) {
		super(translator);
	}

	@Override
	public void processRel(RelType relType) {
		super.processRel(relType);
		currentDrlPattern.setVariable(varToRetract);
	}

	public void processRetract(RetractType retractType) {
		List<Object> formulaOrRulebaseOrAtom = retractType
				.getFormulaOrRulebaseOrAtom();

		for (Object o : formulaOrRulebaseOrAtom) {
			// create an empty rule
			Rule currentRule = new Rule();

			// forward
			translator.dispatchType(o);

			// add the current rule to the list with rules
			currentRule.setRuleName("rule" + ruleNumber++);
			if (translator.getWhenPatterns().isEmpty()) {
				if (translator.getThenPatterns().size() != 1  ) {
					throw new IllegalStateException("The then part should contain only one action to be transformed!");
				}

				translator.getThenPatterns().get(0).setVariable(varToRetract);
				currentRule.setWhenPart(translator.getThenPatterns().toArray());
				currentRule.setThenPart(new String[] {"retract ("+varToRetract+");"});
				
//				throw new IllegalArgumentException(
//						"Retract condition can not be empty");
			} else {
				currentRule.setWhenPart(translator.getWhenPatterns().toArray());
				currentRule.setThenPart(new String[] { "retract("
						+ varToRetract + ")" });

			}

			translator.getDrl().addRule(currentRule);
			// reset the patterns
			translator.getWhenPatterns().clear();
			translator.getThenPatterns().clear();
		}
	}
}
