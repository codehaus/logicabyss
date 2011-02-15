package ruleml.translator.drl2ruleml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.drools.rule.builder.dialect.java.parser.JavaLexer;

import reactionruleml.AssertType;
import reactionruleml.AtomType;
import reactionruleml.IndType;
import reactionruleml.RelType;
import reactionruleml.SlotType;
import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator.RuleStyle;

public class ThenPartAnalyzer {

	private RuleStyle ruleStyle;
	private WhenPartAnalyzer whenPartAnalyzer;

	public ThenPartAnalyzer(WhenPartAnalyzer whenPartAnalyzer) {
		this.whenPartAnalyzer = whenPartAnalyzer;
	}

	public RuleStyle getRuleStyle() {
		return ruleStyle;
	}

	JAXBElement<?> processThenPart(String consequence) {
		try {
			if (consequence.contains("insert")) {
				this.ruleStyle = RuleStyle.ASSERT;
				return createInsert(consequence);
			} else if (consequence.contains("retract")) {
				this.ruleStyle = RuleStyle.RETRACT;
				return createRetract(consequence);
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
			throw new RuntimeException("The retract statement in the Then-part does not match the pattern: retract ($Var)");
		}
		
		return whenPartAnalyzer.getAtomFromName (tokens.get(0).getText());
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
			RelType rel = Drools2RuleMLTranslator.builder.createRel(relName);
			jaxbElements.add(Drools2RuleMLTranslator.builder.getFactory()
					.createRel(rel));

			// iterate over the class properties
			for (int i = 0; i < classProperties.size(); i++) {
				JAXBElement<IndType> slotName = Drools2RuleMLTranslator.builder
						.createInd(classProperties.get(i));
				
				JAXBElement<?> slotValue = null;
				if (dataFromInsert.get(i).getText().contains("\"")) {
					String s = dataFromInsert.get(i).getText().replace("\"","");
					slotValue = Drools2RuleMLTranslator.builder
					.createInd(s);
				} else {
					slotValue = Drools2RuleMLTranslator.builder.createVar(dataFromInsert.get(i).getText());
				}

				JAXBElement<SlotType> slot = Drools2RuleMLTranslator.builder
						.createSlot(slotName, slotValue);
				jaxbElements.add(slot);
			}

			JAXBElement<AtomType> atom = Drools2RuleMLTranslator.builder
					.createAtom(jaxbElements
							.toArray(new JAXBElement<?>[jaxbElements.size()]));
//			JAXBElement<AssertType> assertType = Drools2RuleMLTranslator.builder
//					.createAssert(new JAXBElement<?>[] { atom });
//
//			return assertType;
			return atom;
		}
	}

	
	private List<CommonToken> getTokensFromThenPart (String insert) {
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
		
			// remove  the insert or retract
			result.remove(0);

			return result;
		} catch (Exception e) {
			throw new IllegalStateException(
					"Error: Could not parse the then part of the drolls source");
		}
		
	}
}
