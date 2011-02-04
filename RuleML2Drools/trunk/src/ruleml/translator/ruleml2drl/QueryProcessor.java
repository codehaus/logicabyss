package ruleml.translator.ruleml2drl;

import reactionruleml.QueryType;

public class QueryProcessor extends RuleMLGenericProcessor{

	public QueryProcessor(RuleML2DroolsTranslator translator) {
		super(translator);
	}

	public void processQuery(QueryType queryType) {
		translator.dispatchType(queryType.getFormulaOrRulebaseOrAtom());
	}

}
