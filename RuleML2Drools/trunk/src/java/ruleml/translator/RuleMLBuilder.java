package ruleml.translator;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.drools.rule.Rule;

import datalog.AndInnerType;
import datalog.AssertType;
import datalog.AtomType;
import datalog.IfType;
import datalog.ImpliesType;
import datalog.IndType;
import datalog.ObjectFactory;
import datalog.OpAtomType;
import datalog.OrInnerType;
import datalog.RelType;
import datalog.RuleMLType;
import datalog.SlotType;
import datalog.ThenType;
import datalog.VarType;

public class RuleMLBuilder {
	private ObjectFactory factory = new ObjectFactory();
	
	public JAXBElement<SlotType> createSlot(JAXBElement<?> slotName,
			JAXBElement<?> content) {
		SlotType slotType = factory.createSlotType();
		slotType.getContent().add(slotName);
		slotType.getContent().add(content);
		return factory.createSlot(slotType);
	}

	public JAXBElement<VarType> createVar(String content) {
		VarType varType = factory.createVarType();
		varType.getContent().add(content);
		return factory.createVar(varType);
	}

	public JAXBElement<IndType> createInd(String content) {
		IndType indType = factory.createIndType();
		indType.getContent().add(content);
		return factory.createInd(indType);
	}

	public AtomType createAtom(JAXBElement<?>[] content) {
		AtomType atomType = factory.createAtomType();
		atomType.getContent().addAll(Arrays.asList(content));
		return atomType;
	}

	public AndInnerType createAnd(JAXBElement<?>[] content) {
		AndInnerType andType = factory.createAndInnerType();
		for (JAXBElement<?> jaxbElement : content) {
			andType.getFormulaOrAtomOrAnd().add(jaxbElement.getValue());	
		}
		return andType;
	}
	
	public OrInnerType createOr(JAXBElement<?>[] content) {
		OrInnerType orType = factory.createOrInnerType();
		for (JAXBElement<?> jaxbElement : content) {
			orType.getFormulaOrAtomOrAnd().add(jaxbElement.getValue());	
		}
		return orType;
	}

	public ObjectFactory getFactory() {
		return factory;
	}

	public RelType createRel(String content) {
		RelType relType = factory.createRelType();
		relType.getContent().add(content);
		return relType;
	}

	public JAXBElement<OpAtomType> createOp(RelType relType) {
		OpAtomType opAtomType = factory.createOpAtomType();
		opAtomType.setRel(relType);
		return factory.createAtomTypeOp(opAtomType);
	}

	// private JAXBElement<IfType> createIf(JAXBElement<?>[] content) {
	// IfType ifType = factory.createIfType();
	//
	// for (JAXBElement<?> jaxbElement : content) {
	// if (jaxbElement.getDeclaredType() instanceof AndInnerType) {
	//
	// }
	//
	// }
	//
	// }

	public void test(AndInnerType and) {
		try {
			JAXBContext jContext = JAXBContext.newInstance("datalog");
			System.out.println("context ok");

			RuleMLType ruleMLType = factory.createRuleMLType();
			List<Object> assertOrRetractOrQueryList = ruleMLType
					.getAssertOrRetractOrQuery();

			AssertType assertType = factory.createAssertType();
			assertOrRetractOrQueryList.add(assertType);

			List<Object> formulaOrRulebaseOrAtomList = assertType
					.getFormulaOrRulebaseOrAtom();

			// if
			IfType ifType = factory.createIfType();
			ifType.setAnd(and);

			// then
			ThenType thenType = factory.createThenType();
			
			// implies
			ImpliesType impliesType = factory.createImpliesType();
			impliesType.getContent().add(factory.createIf(ifType));
			impliesType.getContent().add(factory.createThen(thenType));

			// add implies to assert
			formulaOrRulebaseOrAtomList.add(impliesType);

			Marshaller marshaller = jContext.createMarshaller();

			System.out.println("marshaller  ready");

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			marshaller.marshal(ruleMLType, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public String testTransformToRuleML(Rule rule) throws JAXBException {
		JAXBContext jContext = JAXBContext.newInstance("datalog");
		System.out.println("context ok");

		StringBuffer result = new StringBuffer();

		RuleMLType ruleMLType = factory.createRuleMLType();
		List<Object> assertOrRetractOrQueryList = ruleMLType
				.getAssertOrRetractOrQuery();

		AssertType assertType = factory.createAssertType();
		assertOrRetractOrQueryList.add(assertType);

		List<Object> formulaOrRulebaseOrAtomList = assertType
				.getFormulaOrRulebaseOrAtom();

		// AND
		AndInnerType andInnerType = factory.createAndInnerType();

		// atom buy
		AtomType atomType = factory.createAtomType();

		RelType relType = factory.createRelType();
		relType.getContent().add("buy");

		OpAtomType opAtomType = factory.createOpAtomType();
		opAtomType.setRel(relType);

		VarType varType1 = factory.createVarType();
		varType1.getContent().add("person");

		VarType varType2 = factory.createVarType();
		varType2.getContent().add("merchant");

		VarType varType3 = factory.createVarType();
		varType3.getContent().add("object");

		atomType.getContent().add(factory.createOp(opAtomType));
		atomType.getContent().add(factory.createVar(varType1));
		atomType.getContent().add(factory.createVar(varType2));
		atomType.getContent().add(factory.createVar(varType3));

		andInnerType.getFormulaOrAtomOrAnd().add(atomType);

		// atom own
		atomType = factory.createAtomType();

		relType = factory.createRelType();
		relType.getContent().add("keep");

		opAtomType = factory.createOpAtomType();
		opAtomType.setRel(relType);

		varType1 = factory.createVarType();
		varType1.getContent().add("person");

		varType2 = factory.createVarType();
		varType2.getContent().add("object");

		atomType.getContent().add(factory.createOp(opAtomType));
		atomType.getContent().add(factory.createVar(varType1));
		atomType.getContent().add(factory.createVar(varType2));

		andInnerType.getFormulaOrAtomOrAnd().add(atomType);

		// if
		IfType ifType = factory.createIfType();
		ifType.setAnd(andInnerType);

		// implies
		ImpliesType impliesType = factory.createImpliesType();
		impliesType.getContent().add(factory.createIf(ifType));

		// add implies to assert
		formulaOrRulebaseOrAtomList.add(impliesType);

		Marshaller marshaller = jContext.createMarshaller();

		System.out.println("marshaller  ready");

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		marshaller.marshal(ruleMLType, System.out);

		return result.toString();
	}
}
