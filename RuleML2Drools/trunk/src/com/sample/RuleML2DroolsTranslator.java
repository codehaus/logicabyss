package com.sample;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.drools.base.ClassFieldReader;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.VariableConstraint;
import org.drools.spi.ObjectType;

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
	
	public static void testTransform(Rule rule1) {
		Map<String, String> bindVars = new HashMap<String, String>();

		GroupElement[] transformedLhs = rule1.getTransformedLhs();

		transformPattern(transformedLhs[0], bindVars);

		System.out.println();
	}

	private static void transformPattern(GroupElement groupElement,
			Map<String, String> bindVars) {

		// iterate over the element groups
		for (Object obj : groupElement.getChildren()) {
			if (obj instanceof Pattern) {
				Pattern pattern = (Pattern) obj;
				
				// get the columns of the relation
				List<String> columnList = new ArrayList<String>(); 
				System.out.println( pattern.getObjectType().getClass());
				Class<? extends ObjectType> relClass = pattern.getObjectType().getClass();
				BeanInfo beanInfo;
				try {
					beanInfo = Introspector.getBeanInfo(relClass);
					PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
					for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
						columnList.add(propertyDescriptor.getDisplayName());
					}
					System.out.println("Rel:" + columnList);
				} catch (IntrospectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// iterate over the constratints
				for (Object constraint : pattern.getConstraints()) {
					if (constraint instanceof Declaration) {
						Declaration declaration = (Declaration) constraint;
						ClassFieldReader o = (ClassFieldReader) declaration
						.getExtractor();
						
						// check if the variable has been bound
						if (bindVars.containsKey(declaration.getIdentifier())) {
							// create VAR with bound name
							System.out.printf("<VAR> %1 </VAR>",bindVars.get(declaration.getIdentifier()));
						} else {
							// put the variable with the bound name in the map
							bindVars.put(declaration.getIdentifier(),
									o.getFieldName());
							System.out.printf("Var %1,%2 put in the map",declaration.getIdentifier(),o.getFieldName());
						}
					} else if (constraint instanceof LiteralConstraint) {
						LiteralConstraint literalConstraint = (LiteralConstraint) constraint;
						ClassFieldReader classFieldReader = (ClassFieldReader) literalConstraint
						.getFieldExtractor();
						
						System.out.println(classFieldReader.getFieldName());
					} else if (constraint instanceof VariableConstraint) {
						VariableConstraint variableConstraint = (VariableConstraint) constraint;
						ClassFieldReader classFieldReader = (ClassFieldReader) variableConstraint
						.getFieldExtractor();
						
						System.out.println(classFieldReader.getFieldName());
					}
				}
			} else if (obj instanceof GroupElement) {
				transformPattern((GroupElement) obj, bindVars);
			}
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
