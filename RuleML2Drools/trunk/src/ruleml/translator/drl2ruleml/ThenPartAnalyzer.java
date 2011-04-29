package ruleml.translator.drl2ruleml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.drools.rule.builder.dialect.java.parser.JavaLexer;

import reactionruleml.AtomType;
import reactionruleml.IndType;
import reactionruleml.OidType;
import reactionruleml.OpAtomType;
import reactionruleml.RelType;
import reactionruleml.SlotType;
import ruleml.translator.drl2ruleml.VariableBindingsManager.PropertyInfo;
import ruleml.translator.drl2ruleml.VariableBindingsManager.PropertyInfo.ValueType;

public class ThenPartAnalyzer {

	private WhenPartAnalyzer whenPartAnalyzer;

	public ThenPartAnalyzer(WhenPartAnalyzer whenPartAnalyzer) {
		this.whenPartAnalyzer = whenPartAnalyzer;
	}

	JAXBElement<?> processThenPart(String consequence) {
		try {
			if (consequence.contains("insert")) {
				JAXBElement<?> thenPart = createInsert(consequence);
				return Drools2RuleMLTranslator.builder
						.createAssert(new JAXBElement<?>[] { thenPart });
			} else if (consequence.contains("retract")) {
				JAXBElement<?> thenPart = createRetract(consequence);
				return Drools2RuleMLTranslator.builder
						.createRetract(new JAXBElement<?>[] { thenPart });
			} else {
				throw new IllegalStateException(
						"Can not process the then part because it is not an insert or retract");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private JAXBElement<?> createRetract(String retract) {
		List<CommonToken> tokens = getTokensFromThenPart(retract);
		if (tokens.size() != 1) {
			throw new RuntimeException(
					"The retract statement in the Then-part does not match the pattern: retract ($Var)");
		}

		JAXBElement<OidType> oid = Drools2RuleMLTranslator.builder
				.createOid(tokens.get(0).getText());

		return Drools2RuleMLTranslator.builder
				.createAtom(new JAXBElement<?>[] { oid });
	}

	private JAXBElement<?> createInsert(String insert)
			throws ClassNotFoundException {
		// get the data from consequence insert
		List<CommonToken> dataFromInsert = getTokensFromThenPart(insert);

		// get the name of the relation
		String relName = "";
		if (dataFromInsert.size() == 0) {
			throw new IllegalStateException(
					"The insert does not contain data: " + insert);
		} else {
			relName = dataFromInsert.get(0).getText();
			dataFromInsert.remove(0);
		}

		// get the properties of the relation from the java class
		if (relName.isEmpty()) {
			throw new IllegalStateException("The relation name is empty: "
					+ relName);
		} else {
			Class<?> clazz = Class.forName("ruleml.translator.TestDataModel$"
					+ relName);
			List<String> classProperties = Drools2RuleMLTranslator
					.getPropertiesFromClass(clazz);

			if (dataFromInsert.size() != classProperties.size()) {
				System.out
						.printf("Warning : the relation properties count %s does not coincide with arguments found in the counstructor:"
								+ classProperties, insert);
			}

			// create the list with elements, the content of the atom relation
			List<JAXBElement<?>> jaxbElements = new ArrayList<JAXBElement<?>>();
			RelType relType = Drools2RuleMLTranslator.builder.createRel(relName);
			jaxbElements.add(Drools2RuleMLTranslator.builder.createOp(relType));

			// iterate over the class properties
			for (int i = 0; i < classProperties.size(); i++) {
				// get the slot name
				JAXBElement<IndType> slotName = Drools2RuleMLTranslator.builder
						.createInd(classProperties.get(i));

				// get the slot value
				JAXBElement<?> slotValue = getSlotValue(dataFromInsert.get(i)
						.getText());

				// create slot
				JAXBElement<SlotType> slot = Drools2RuleMLTranslator.builder
						.createSlot(slotName, slotValue);
				jaxbElements.add(slot);
			}

			// create atom
			JAXBElement<AtomType> atom = Drools2RuleMLTranslator.builder
					.createAtom(jaxbElements
							.toArray(new JAXBElement<?>[jaxbElements.size()]));
			return atom;
		}
	}

	private JAXBElement<?> getSlotValue(String value) {
		// set the slot value
		JAXBElement<?> slotValue = null;

		PropertyInfo propertyInfo = whenPartAnalyzer.getBindingsManager().get(
				value);
		if (propertyInfo == null) {
			// value is a constant
			value = value.replace("\"", "");
			slotValue = Drools2RuleMLTranslator.builder.createInd(value);
		} else {
			if (propertyInfo.getType().equals(ValueType.IND)) {
				slotValue = Drools2RuleMLTranslator.builder
						.createInd(propertyInfo.getValue());
			} else {
				slotValue = Drools2RuleMLTranslator.builder
						.createVar(propertyInfo.getVar());
			}
		}

		return slotValue;
	}

	private List<CommonToken> getTokensFromThenPart(String insert) {
		List<CommonToken> result = new ArrayList<CommonToken>();
		try {
			CharStream cs = new ANTLRStringStream(insert);
			JavaLexer lexer = new JavaLexer(cs);

			CommonTokenStream tokens = new CommonTokenStream();
			tokens.setTokenSource(lexer);

			for (Object o : tokens.getTokens()) {
				CommonToken token = (CommonToken) o;

				if (token.getType() == 4 || token.getType() == 8) {
					result.add(token);
				}
			}

			// remove the insert or retract
			result.remove(0);

			return result;
		} catch (Exception e) {
			throw new IllegalStateException(
					"Error: Could not parse the then part of the drolls source");
		}

	}
}
