package ruleml.translator.ruleml2drl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import naffolog.AndInnerType;
import naffolog.AssertType;
import naffolog.AtomType;
import naffolog.IfType;
import naffolog.ImpliesType;
import naffolog.IndType;
import naffolog.OpAtomType;
import naffolog.OrInnerType;
import naffolog.QueryType;
import naffolog.RelType;
import naffolog.RetractType;
import naffolog.RuleMLType;
import naffolog.SlotType;
import naffolog.ThenType;
import naffolog.VarType;

import org.drools.io.ResourceFactory;

/**
 * Translator for RuleML intput to Drools DLR-source.
 * @author Jabarski
 */
public class RuleML2DroolsTranslator {

	private List<DrlPattern> whenPatterns = new ArrayList<DrlPattern>();
	private List<DrlPattern> thenPatterns = new ArrayList<DrlPattern>();
	private DrlPattern currentDlrPattern;
	private Object currentResult;
	private List<String> boundVars = new ArrayList<String>();
	private PartType currentPartType = PartType.IF;

	// this type contains the both context state alternatives for RuleML (if, then)  
	private enum PartType {
		IF, THEN
	}

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
	 * @param fileName The name of the resource.
	 * @return The JAXB parent type for the ruleml 1.0 object model.
	 */
	public static RuleMLType readRuleML(String fileName) {
		try {
			JAXBContext jContext = JAXBContext.newInstance("datalog");
			Unmarshaller unmarshaller = jContext.createUnmarshaller();
			JAXBElement<?> unmarshal = (JAXBElement<?>) unmarshaller
					.unmarshal(ResourceFactory.newClassPathResource(fileName
							).getInputStream());
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
	 * The entry point of the translation. This will start translation and returns
	 * the dlr-source output.
	 * @return Dlr-Source 
	 */
	public static String translate(RuleMLType ruleML) {
		RuleML2DroolsTranslator translator = new RuleML2DroolsTranslator();

		translator.dispatchType(ruleML);

		DroolsBuilder.Drl drl = new DroolsBuilder.Drl("org.ruleml.translator",
				new String[] { "org.ruleml.translator.TestDataModel.*" });

		drl.addRule("buy&Keep", translator.whenPatterns.toArray(),
				translator.thenPatterns.toArray());
		
		return drl.toString();
	}
	
	/**
	 * The main method for the dispatching of ruleml types within a translation.
	 * @param value The RuleML type to be transformed: in the most cases this will
	 * be RuleMLType, but also other subtypes can be transformed. 
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
		} else if (value instanceof AtomType) {
			processAtom((AtomType) value);
		} else if (value instanceof AndInnerType) {
			processAnd((AndInnerType) value);
		} else if (value instanceof OrInnerType) {
			processOr((OrInnerType) value);
		} else if (value instanceof RuleMLType) {
			processRuleML ((RuleMLType)value);
		} else if (value instanceof List) {
			for (Object o : (List)value) {
				dispatchType(o);
			}
		} else if (value instanceof JAXBElement<?>) {
			dispatchType(((JAXBElement<?>)value).getValue());
		}
	}

	/*********************** Methods to process single RuleML elements ****************/

	private DrlPattern processAtom(AtomType atomType) {
		dispatchType(atomType.getContent());
		
		if (currentPartType.equals(PartType.IF)) {
			whenPatterns.add(currentDlrPattern);
		} else {
			thenPatterns.add(currentDlrPattern);
		}

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

		// check the current context part (IF or THEN)
		if (currentPartType.equals(PartType.IF)) {
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
		currentDlrPattern = new DrlPattern(relName);
	}

	private void processAnd(AndInnerType andType) {
		dispatchType(andType.getFormulaOrAtomOrAnd());
	}

	private void processOr(OrInnerType orType) {
		dispatchType( orType.getFormulaOrAtomOrAnd());
	}

	private void processIf(IfType ifType) {
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
		currentPartType = PartType.THEN;
		dispatchType(thenType.getAtom());
	}

	private void processImplies(ImpliesType impliesType) {
		dispatchType(impliesType.getContent());
	}

	private void processAssert(AssertType assertType) {
		dispatchType( assertType.getFormulaOrRulebaseOrAtom() );
	}
	
	private void processRetract(RetractType retractType) {
		dispatchType( retractType.getFormulaOrRulebaseOrAtom() );
	}
	
	private void processQuery(QueryType queryType) {
		dispatchType( queryType.getFormulaOrRulebaseOrAtom() );
	}
	
	private void processRuleML(RuleMLType value) {
		dispatchType( value.getAssertOrRetractOrQuery() );
	}
}
