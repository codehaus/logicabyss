package ruleml.translator.ruleml2drl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.drools.io.ResourceFactory;

import reactionruleml.AndInnerType;
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
import reactionruleml.RuleMLType;
import reactionruleml.RuleType;
import reactionruleml.SlotType;
import reactionruleml.ThenType;
import reactionruleml.VarType;
import ruleml.translator.ruleml2drl.DroolsBuilder.Rule;

/**
 * Translator for RuleML intput to Drools DLR-source.
 * 
 * @author Jabarski
 */
public class RuleML2DroolsTranslator {

	private List<DrlPattern> whenPatterns = new ArrayList<DrlPattern>();
	private List<DrlPattern> thenPatterns = new ArrayList<DrlPattern>();
	private DrlPattern currentDrlPattern;
	private Object currentResult;
	private List<String> boundVars = new ArrayList<String>();
	private PartType currentPartType = PartType.WHEN;
	private List<DroolsBuilder.Rule> rules = new ArrayList<DroolsBuilder.Rule>();
	private DroolsBuilder.Rule currentRule;
	private int ruleNumber = 1;

	// contains the both context state alternatives for Drools source
	// (when,then)
	private enum PartType {
		WHEN, THEN
	}

	// Representation for the drools pattern
	public static class DrlPattern {
		enum RelComponentType {
			IND, VAR, DATA
		}

		private String relName;
		private List<String> relComponents = new ArrayList<String>();
		private String prefix;

		public DrlPattern(String relName) {
			// format the name with capital letter
			this.relName = relName.substring(0, 1).toUpperCase()
					+ relName.substring(1);
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;
		}

		public void addComponent(String relComponent) {
			this.getRelComponents().add(relComponent);
		}

		public List<String> getRelComponents() {
			return relComponents;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();

			if (getPrefix() != null) {
				sb.append(prefix + "( new ");
			}

			sb.append(relName).append("(");
			for (String relComponent : relComponents) {
				sb.append(relComponent);
				sb.append(",");
			}

			if (sb.charAt(sb.length() - 1) == ',') {
				sb.replace(sb.length() - 1, sb.length(), "");
			}
			sb.append(")");

			if (getPrefix() != null) {
				sb.append(");");
			}

			return sb.toString();
		}

	}

	/**
	 * Method to read ruleml 1.0 resource.
	 * 
	 * @param fileName
	 *            The name of the resource.
	 * @return The JAXB parent type for the ruleml 1.0 object model.
	 */
	public static RuleMLType readRuleML(String fileName) {
		try {
			JAXBContext jContext = JAXBContext.newInstance("reactionruleml");
			Unmarshaller unmarshaller = jContext.createUnmarshaller();
			JAXBElement<?> unmarshal = (JAXBElement<?>) unmarshaller
					.unmarshal(ResourceFactory.newClassPathResource(fileName)
							.getInputStream());
			RuleMLType ruleMLType = (RuleMLType) unmarshal.getValue();
			return ruleMLType;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * The entry point of the translation. This will start translation and
	 * returns the dlr-source output.
	 * 
	 * @return Dlr-Source
	 */
	public static String translate(RuleMLType ruleML) {
		RuleML2DroolsTranslator translator = new RuleML2DroolsTranslator();

		translator.dispatchType(ruleML);

		DroolsBuilder.Drl drl = new DroolsBuilder.Drl("org.ruleml.translator",
				new String[] { "org.ruleml.translator.TestDataModel.*" });

		for (Rule rule : translator.rules) {
			drl.addRule(rule);
		}

		return drl.toString();
	}

	/**
	 * The main method for the dispatching of ruleml types within a translation.
	 * 
	 * @param value
	 *            The RuleML type to be transformed: in the most cases this will
	 *            be RuleMLType, but also other subtypes can be transformed.
	 */
	private void dispatchType(Object value) {
		if (value instanceof SlotType) {
			processSlot((SlotType) value);
		} else if (value instanceof IndType) {
			processInd((IndType) value);
		} else if (value instanceof VarType) {
			processVar((VarType) value);
		} else if (value instanceof OpAtomType) {
			processOpAtom((OpAtomType) value);
		} else if (value instanceof RelType) {
			processRel((RelType) value);
		} else if (value instanceof IfType) {
			processIf((IfType) value);
		} else if (value instanceof ThenType) {
			processThen((ThenType) value);
		} else if (value instanceof AssertType) {
			processAssert((AssertType) value);
		} else if (value instanceof RetractType) {
			processRetract((RetractType) value);
		} else if (value instanceof QueryType) {
			processQuery((QueryType) value);
		} else if (value instanceof ImpliesType) {
			processImplies((ImpliesType) value);
		} else if (value instanceof RuleType) {
			processRule((RuleType) value);
		} else if (value instanceof AtomType) {
			processAtom((AtomType) value);
		} else if (value instanceof AndInnerType) {
			processAnd((AndInnerType) value);
		} else if (value instanceof OrInnerType) {
			processOr((OrInnerType) value);
		} else if (value instanceof RuleMLType) {
			processRuleML((RuleMLType) value);
		} else if (value instanceof List) {
			for (Object o : (List) value) {
				dispatchType(o);
			}
		} else if (value instanceof JAXBElement<?>) {
			dispatchType(((JAXBElement<?>) value).getValue());
		}
	}

	/*********************** Methods to process single RuleML elements ****************/

	private DrlPattern processAtom(AtomType atomType) {
		dispatchType(atomType.getContent());

		if (currentPartType.equals(PartType.WHEN)) {
			whenPatterns.add(currentDrlPattern);
		} else {
			thenPatterns.add(currentDrlPattern);
		}

		return null;
	}

	private void processSlot(SlotType slotType) {
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

		if (currentPartType.equals(PartType.WHEN)) {
			// current context part = WHEN
			if (currentDrlPattern != null) {
				// check if the var was already bound
				if (boundVars.contains(rawSlotValue) || content.get(1).getValue() instanceof IndType) {
					// set the pattern property ( buyer == $person)
					currentDrlPattern.addComponent(slotName + "==" + slotValue);
				} else {
					// set the pattern property ( $person : buyer)
					currentDrlPattern.addComponent(slotValue + ":" + slotName);
					// bind the var
					boundVars.add(rawSlotValue);
				}
			} else {
				System.out.println("Error, pattern not initiated !!!");
			}
		} else {
			// current context part = THEN
			if (currentDrlPattern != null) {
				currentDrlPattern.addComponent(slotValue);
			} else {
				System.out.println("Error, pattern not initiated !!!");
			}
		}
	}

	private void processVar(VarType varType) {
		List<String> content = varType.getContent();
		currentResult = content.get(0);
	}

	private void processInd(IndType indType) {
		List<Object> content = indType.getContent();
		currentResult = content.get(0);
	}

	private void processOpAtom(OpAtomType opAtomType) {
		dispatchType(opAtomType.getRel());
	}

	private void processRel(RelType relType) {
		String relName = relType.getContent().get(0);
		currentDrlPattern = new DrlPattern(relName);
	}

	private void processAnd(AndInnerType andType) {
		dispatchType(andType.getFormulaOrAtomOrAnd());
	}

	private void processOr(OrInnerType orType) {
		dispatchType(orType.getFormulaOrAtomOrAnd());
	}

	private void processIf(IfType ifType) {
		currentPartType = PartType.WHEN;

		if (ifType.getAnd() != null) {
			dispatchType(ifType.getAnd());
		}

		if (ifType.getOr() != null) {
			dispatchType(ifType.getOr());
		}

		if (ifType.getAtom() != null) {
			dispatchType(ifType.getAtom());
		}
	}

	private void processThen(ThenType thenType) {
		currentPartType = PartType.THEN;
		dispatchType(thenType.getAtom());
	}

	private void processImplies(ImpliesType impliesType) {
		dispatchType(impliesType.getContent());
	}

	private void processRule(RuleType ruleType) {
		dispatchType(ruleType.getContent());
	}

	private void processAssert(AssertType assertType) {
		List<Object> formulaOrRulebaseOrAtom = assertType
				.getFormulaOrRulebaseOrAtom();
		currentRule = new Rule();

		for (Object o : formulaOrRulebaseOrAtom) {
			// create a empty rule
			currentRule = new Rule();

			// forward
			dispatchType(o);

			// add the current rule to the list with rules
			if (currentRule != null) {
				currentRule.setRuleName("rule" + this.ruleNumber++);
				if (this.whenPatterns.isEmpty()) {
					currentRule.setWhenPart(new String[] { "eval(true)" });
				} else {
					currentRule.setWhenPart(this.whenPatterns.toArray());
				}
				
				processThenPatterns ("insert");
				
				currentRule.setThenPart(this.thenPatterns.toArray());
				rules.add(currentRule);

				// reset the patterns
				this.whenPatterns.clear();
				this.thenPatterns.clear();
			}

		}
	}

	private void processRetract(RetractType retractType) {
		List<Object> formulaOrRulebaseOrAtom = retractType.getFormulaOrRulebaseOrAtom();
		currentRule = new Rule();

		for (Object o : formulaOrRulebaseOrAtom) {
			// create a empty rule
			currentRule = new Rule();

			// forward
			dispatchType(o);

			// add the current rule to the list with rules
			if (currentRule != null) {
				currentRule.setRuleName("rule" + this.ruleNumber++);
				if (this.whenPatterns.isEmpty()) {
					currentRule.setWhenPart(new String[] { "eval(true)" });
				} else {
					currentRule.setWhenPart(this.whenPatterns.toArray());
				}
				
				processThenPatterns ("retract");
				
				currentRule.setThenPart(this.thenPatterns.toArray());
				rules.add(currentRule);

				// reset the patterns
				this.whenPatterns.clear();
				this.thenPatterns.clear();
			}
		}
	}

	private void processThenPatterns (String prefix) {
		for (DrlPattern pattern : this.thenPatterns) {
			pattern.setPrefix(prefix);
		}
	}
	
	private void processQuery(QueryType queryType) {
		dispatchType(queryType.getFormulaOrRulebaseOrAtom());
	}

	private void processRuleML(RuleMLType value) {
		dispatchType(value.getAssertOrRetractOrQuery());
	}
}
