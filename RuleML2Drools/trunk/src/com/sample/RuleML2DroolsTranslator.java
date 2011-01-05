package com.sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.drools.io.ResourceFactory;

import datalog.AndInnerType;
import datalog.AssertType;
import datalog.AtomType;
import datalog.IfType;
import datalog.ImpliesType;
import datalog.IndType;
import datalog.OpAtomType;
import datalog.OrInnerType;
import datalog.RelType;
import datalog.RuleMLType;
import datalog.SlotType;
import datalog.ThenType;
import datalog.VarType;

public class RuleML2DroolsTranslator {

	private List<DlrPattern> dlRPatterns = new ArrayList<DlrPattern>();
	private DlrPattern currentDlrPattern;
	private Object currentResult;
	private List<String> boundVars = new ArrayList<String>();
	private PartType currentPartType = PartType.IF;

	private enum PartType {
		IF, THEN
	}

	public static class DlrPattern {
		enum RelComponentType {
			IND, VAR, DATA
		}

		private String relName;
		private List<String> relComponents = new ArrayList<String>();
		private String prefix;

		public DlrPattern(String relName) {
			// format the name with capital letter
			this.relName = relName.substring(0,1).toUpperCase() + relName.substring(1);
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

	public static void main(String[] args) {

		RuleML2DroolsTranslator translator = new RuleML2DroolsTranslator();

		// read the ruleml file
		RuleMLType ruleML = translator.readRuleML();

		AssertType assertType = (AssertType) ruleML.getAssertOrRetractOrQuery()
				.get(0);

		System.out.println("package com.sample");
		System.out.println("import com.sample.TestDataModel.*;");
		System.out.println("rule \"buy&Keep\"");
		
		translator.dispatchType(assertType.getFormulaOrRulebaseOrAtom().get(0));

		System.out.println("end");

		// create the object model

		// map the objects to drl patterns

		// create the dlr output

	}

	public RuleMLType readRuleML() {
		try {
			JAXBContext jContext = JAXBContext.newInstance("datalog");
			Unmarshaller unmarshaller = jContext.createUnmarshaller();
			JAXBElement<?> unmarshal = (JAXBElement<?>) unmarshaller
					.unmarshal(ResourceFactory.newClassPathResource(
							"ruleml1.xml").getInputStream());
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
		} else if (value instanceof ImpliesType) {
			processImplies((ImpliesType) value);
		} else if (value instanceof AtomType) {
			processAtom((AtomType) value);
		} else if (value instanceof AndInnerType) {
			processAnd((AndInnerType) value);
		} else if (value instanceof OrInnerType) {
			processOr((OrInnerType) value);
		}
	}

	/*********************** Methods to process single RuleML elements ****************/

	private DlrPattern processAtom(AtomType atomType) {
		if (atomType.getContent().size() == 0)
			throw new IllegalArgumentException(
					"There is no relation in this atom");
		// get the name of the relation
		// String relName = ((OpAtomType)
		// atomType.getContent().get(0).getValue())
		// .getRel().getContent().get(0);

		// System.out.println("Raltaion name: " + relName);

		// create the pattern
		// DlrPattern pattern = new DlrPattern(relName);

		// iterate over the rest components of the relation
		for (int i = 0; i < atomType.getContent().size(); i++) {
			Object value = atomType.getContent().get(i).getValue();
			// dispatch the value to the correct method
			dispatchType(value);
		}

		dlRPatterns.add(currentDlrPattern);

		System.out.println(currentDlrPattern);

		return null;
	}

	private void processSlot(SlotType slotType) {
		List<JAXBElement<?>> content = slotType.getContent();

		// process the var
		dispatchType(content.get(1).getValue());

		// get the temp result
		String temp = (String) currentResult;

		// process the ind
		dispatchType(content.get(0).getValue());

		// check if the part is IF or THEN
		if (currentPartType == PartType.IF) {
			if (currentDlrPattern != null) {
				// check if the var was already bound
				if (boundVars.contains(temp)) {
					currentDlrPattern.addComponent(currentResult + "==" + "$"
							+ temp);
				} else {
					// set the pattern property
					currentDlrPattern.addComponent("$" + temp + ":"
							+ currentResult);
					// bind the var
					boundVars.add(temp);
				}
			} else {
				System.out.println("Error, pattern not initiated !!!");
			}
		} else {
			if (currentDlrPattern != null) {
				currentDlrPattern.addComponent("$" + temp);
				currentDlrPattern.setPrefix("insert");
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
		List<String> content = indType.getContent();
		currentResult = content.get(0);
	}

	private void processOpAtom(OpAtomType opAtomType) {
		dispatchType(opAtomType.getRel());
	}

	private void processRel(RelType relType) {
		String relName = relType.getContent().get(0);
		currentDlrPattern = new DlrPattern(relName);
	}

	private void processAnd(AndInnerType andType) {
		List<Object> formulaOrAtomOrAnd = andType.getFormulaOrAtomOrAnd();
		for (Object object : formulaOrAtomOrAnd) {
			dispatchType(object);
		}
	}

	private void processOr(OrInnerType orType) {
		System.out.println("OR");
		List<Object> formulaOrAtomOrAnd = orType.getFormulaOrAtomOrAnd();
		for (Object object : formulaOrAtomOrAnd) {
			dispatchType(object);
		}
	}

	private void processIf(IfType ifType) {
		System.out.println("when");
		currentPartType = PartType.IF;

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
		System.out.println("then");
		currentPartType = PartType.THEN;
		dispatchType(thenType.getAtom());
	}

	private void processImplies(ImpliesType impliesType) {
		List<JAXBElement<?>> content = impliesType.getContent();

		if (content.size() == 0) {
			System.out.println("Imlies type is empty !!!" + impliesType);
		}

		// iterate over the content elements of the slot
		for (JAXBElement<?> jaxbElement : content) {
			dispatchType(jaxbElement.getValue());
		}
	}

	private void processAssert(AssertType assertType) {
	}
}
