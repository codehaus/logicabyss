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
import reactionruleml.RuleMLType;
import reactionruleml.RuleType;
import reactionruleml.SlotType;
import reactionruleml.ThenType;
import reactionruleml.VarType;
import ruleml.translator.ruleml2drl.DroolsBuilder.Drl;
import ruleml.translator.ruleml2drl.DroolsBuilder.Rule;

/**
 * Translator for RuleML intput to Drools DLR-source.
 * 
 * @author Jabarski
 */
public class RuleML2DroolsTranslator {

	private Drl drl = new Drl();
	RuleMLGenericProcessor currentProcessor;

	public Drl getDrl() {
		return drl;
	}

	public void setDrl(Drl drl) {
		this.drl = drl;
	}
	
	// contains the both context state alternatives for Drools source
	// (when,then)
	enum PartType {
		WHEN, THEN
	}

	// Representation for the drools pattern
	public static class DrlPattern {
		enum RelComponentType {
			IND, VAR, DATA
		}

		private String className;
		private String variable;
		private List<String> constraints = new ArrayList<String>();
		private String prefix;

		public String getVariable() {
			return variable;
		}

		public void setVariable(String variable) {
			this.variable = variable;
		}

		public DrlPattern(String relName) {
			// format the name with capital letter
			this.className = relName.substring(0, 1).toUpperCase()
					+ relName.substring(1);
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;
		}

		public void addConstraint(String constraint) {
			this.getConstraints().add(constraint);
		}

		public List<String> getConstraints() {
			return constraints;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();

			if (getPrefix() != null) {
				sb.append(prefix + "( new ");
			}
			
			if (getVariable() != null) {
				sb.append(variable + ": ");
			}

			sb.append(className).append("(");
			for (String relComponent : constraints) {
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
	 * returns the drl-source output.
	 * 
	 * @return Drl-Source
	 */
	public static String translate(RuleMLType ruleML) {

		RuleML2DroolsTranslator translator = new RuleML2DroolsTranslator();
		
		translator.dispatchType(ruleML);

		translator.getDrl().setPackage_("org.ruleml.translator");
		translator.getDrl().setImports(new String[] { "org.ruleml.translator.TestDataModel.*" });
		
		return translator.getDrl().toString();
	}
	
	/**
	 * The main method for the dispatching of ruleml types within a translation.
	 * 
	 * @param value
	 *            The RuleML type to be transformed: in the most cases this will
	 *            be RuleMLType, but also other subtypes can be transformed.
	 */
	public void dispatchType(Object value) {
		if (value instanceof SlotType) {
			currentProcessor.processSlot((SlotType) value);
		} else if (value instanceof IndType) {
			currentProcessor.processInd((IndType) value);
		} else if (value instanceof VarType) {
			currentProcessor.processVar((VarType) value);
		} else if (value instanceof OpAtomType) {
			currentProcessor.processOpAtom((OpAtomType) value);
		} else if (value instanceof RelType) {
			currentProcessor.processRel((RelType) value);
		} else if (value instanceof IfType) {
			currentProcessor.processIf((IfType) value);
		} else if (value instanceof ThenType) {
			currentProcessor.processThen((ThenType) value);
		} else if (value instanceof AssertType) {
			currentProcessor = new AssertProcessor(this);
			currentProcessor.processAssert((AssertType) value);
		} else if (value instanceof RetractType) {
			currentProcessor = new RetractProcessor(this);
			currentProcessor.processRetract((RetractType) value);
		} else if (value instanceof QueryType) {
			currentProcessor = new QueryProcessor(this);
			currentProcessor.processQuery((QueryType) value);
		} else if (value instanceof ImpliesType) {
			currentProcessor.processImplies((ImpliesType) value);
		} else if (value instanceof RuleType) {
			currentProcessor.processRule((RuleType) value);
		} else if (value instanceof AtomType) {
			currentProcessor.processAtom((AtomType) value);
		} else if (value instanceof AndInnerType) {
			currentProcessor.processAnd((AndInnerType) value);
		} else if (value instanceof AndQueryType) {
			currentProcessor.processAnd((AndQueryType) value);			
		} else if (value instanceof OrInnerType) {
			currentProcessor.processOr((OrInnerType) value);
		} else if (value instanceof RuleMLType) {
			dispatchType(((RuleMLType)value).getAssertOrRetractOrQuery());
		} else if (value instanceof List) {
			for (Object o : (List) value) {
				dispatchType(o);
			}
		} else if (value instanceof JAXBElement<?>) {
			dispatchType(((JAXBElement<?>) value).getValue());
		}
	}	
}
