package ruleml.translator.ruleml2drl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import reactionruleml.AndInnerType;
import reactionruleml.AndQueryType;
import reactionruleml.AssertType;
import reactionruleml.AtomType;
import reactionruleml.IfType;
import reactionruleml.ImpliesType;
import reactionruleml.IndType;
import reactionruleml.OpAtomType;
import reactionruleml.OrInnerType;
import reactionruleml.QueryType;
import reactionruleml.RelType;
import reactionruleml.RetractType;
import reactionruleml.RuleType;
import reactionruleml.SlotType;
import reactionruleml.ThenType;
import reactionruleml.VarType;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator.DrlPattern;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator.PartType;

public class RuleMLGenericProcessor {

	protected List<DrlPattern> whenPatterns = new ArrayList<DrlPattern>();
	protected List<DrlPattern> thenPatterns = new ArrayList<DrlPattern>();
	protected DrlPattern currentDrlPattern;
	protected List<String> boundVars = new ArrayList<String>();
	protected PartType currentContext = PartType.WHEN;
	protected static int ruleNumber = 1;

	protected RuleML2DroolsTranslator translator;

	public RuleMLGenericProcessor(RuleML2DroolsTranslator translator) {
		this.translator = translator;
	}

	/*********************** Methods to process single RuleML elements ****************/

	public DrlPattern processAtom(AtomType atomType) {
		translator.dispatchType(atomType.getContent());

		if (currentContext.equals(PartType.WHEN)) {
			whenPatterns.add(currentDrlPattern);
		} else {
			thenPatterns.add(currentDrlPattern);
		}

		return null;
	}

	public void processSlot(SlotType slotType) {
		List<JAXBElement<?>> content = slotType.getContent();

		// get the slot name
		String slotName = (String) ((IndType) content.get(0).getValue())
				.getContent().get(0);

		// get the slot value
		String slotValue = "";
		String rawSlotValue = "";

		// check if variable (var) or constant(ind)
		if (content.get(1).getValue() instanceof VarType) {
			rawSlotValue = ((VarType) content.get(1).getValue()).getContent()
					.get(0);
			slotValue = "$" + rawSlotValue;
		} else if (content.get(1).getValue() instanceof IndType) {
			rawSlotValue = (String) ((IndType) content.get(1).getValue())
					.getContent().get(0);
			slotValue = "\"" + rawSlotValue + "\"";
		}

		if (currentContext.equals(PartType.WHEN)) {
			// current context part = WHEN
			if (currentDrlPattern != null) {
				// check if the var was already bound
				if (boundVars.contains(rawSlotValue)
						|| content.get(1).getValue() instanceof IndType) {
					// set the pattern property ( buyer == $person)
					currentDrlPattern
							.addConstraint(slotName + "==" + slotValue);
				} else {
					// set the pattern property ( $person : buyer)
					currentDrlPattern.addConstraint(slotValue + ":" + slotName);
					// bind the var
					boundVars.add(rawSlotValue);
				}
			} else {
				System.out.println("Error, pattern not initiated !!!");
			}
		} else {
			// current context part = THEN
			if (currentDrlPattern != null) {
				currentDrlPattern.addConstraint(slotValue);
			} else {
				System.out.println("Error, pattern not initiated !!!");
			}
		}
	}

	public void processVar(VarType varType) {
		// List<String> content = varType.getContent();
	}

	public void processInd(IndType indType) {
		// List<Object> content = indType.getContent();
	}

	public void processOpAtom(OpAtomType opAtomType) {
		translator.dispatchType(opAtomType.getRel());
	}

	public void processRel(RelType relType) {
		String relName = relType.getContent().get(0);
		currentDrlPattern = new DrlPattern(relName);
	}

	public void processAnd(AndInnerType andType) {
		translator.dispatchType(andType.getFormulaOrAtomOrAnd());
	}

	public void processAnd(AndQueryType andType) {
		translator.dispatchType(andType.getFormulaOrAtomOrAnd());
	}

	public void processOr(OrInnerType orType) {
		translator.dispatchType(orType.getFormulaOrAtomOrAnd());
	}

	public void processIf(IfType ifType) {
		currentContext = PartType.WHEN;

		if (ifType.getAnd() != null) {
			translator.dispatchType(ifType.getAnd());
		}

		if (ifType.getOr() != null) {
			translator.dispatchType(ifType.getOr());
		}

		if (ifType.getAtom() != null) {
			translator.dispatchType(ifType.getAtom());
		}
	}

	public void processThen(ThenType thenType) {
		currentContext = PartType.THEN;
		translator.dispatchType(thenType.getAtom());
	}

	public void processImplies(ImpliesType impliesType) {
		translator.dispatchType(impliesType.getContent());
	}

	public void processRule(RuleType ruleType) {
		translator.dispatchType(ruleType.getContent());
	}

	public void processAssert(AssertType assertType) {
		// noop
	}

	public void processRetract(RetractType retractType) {
		// noop
	}

	public void processQuery(QueryType queryType) {
		// noop
	}

	public void processThenPatterns(String prefix) {
		for (DrlPattern pattern : this.thenPatterns) {
			pattern.setPrefix(prefix);
		}
	}

}
