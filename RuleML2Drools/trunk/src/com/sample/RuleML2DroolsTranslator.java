package com.sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.drools.io.ResourceFactory;
import org.drools.rule.Rule;

import datalog.AndInnerType;
import datalog.AssertType;
import datalog.AtomType;
import datalog.IfType;
import datalog.ImpliesType;
import datalog.ObjectFactory;
import datalog.OpAtomType;
import datalog.RelType;
import datalog.RuleMLType;
import datalog.VarType;

public class RuleML2DroolsTranslator {
	
	public static void main(String[] args) {
		// read the ruleml file
		readRuleML();
		// create the object model
		
		// map the objects to drl patterns
		
		// create the dlr output
	}
	
	public static RuleMLType readRuleML () {
		try {
			JAXBContext jContext = JAXBContext.newInstance("datalog");
			Unmarshaller unmarshaller = jContext.createUnmarshaller();
			JAXBElement<?> unmarshal = (JAXBElement<?>) unmarshaller.unmarshal(ResourceFactory.newClassPathResource("ruleml.xml").getInputStream());
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
	
	public static String testTransformToRuleML(Rule rule) throws JAXBException {
		JAXBContext jContext = JAXBContext.newInstance("datalog");
		System.out.println("context ok");

		StringBuffer result = new StringBuffer();
		ObjectFactory factory = new ObjectFactory();

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
