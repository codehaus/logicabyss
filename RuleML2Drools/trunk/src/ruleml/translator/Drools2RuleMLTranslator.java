package ruleml.translator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.ClassFieldReader;
import org.drools.base.ClassObjectType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.GroupElement.Type;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.VariableConstraint;
import org.drools.runtime.StatefulKnowledgeSession;

import ruleml.translator.TestDataModel.Buy;
import ruleml.translator.TestDataModel.Keep;


import datalog.AndInnerType;
import datalog.AtomType;
import datalog.OpAtomType;
import datalog.OrInnerType;
import datalog.RelType;

public class Drools2RuleMLTranslator {

	private RuleMLBuilder creator = new RuleMLBuilder();
	
	/**
	 * Main test method
	 */
	public static final void main(String[] args) {
		try {
			Drools2RuleMLTranslator translator = new Drools2RuleMLTranslator();

			// load up the knowledge base
			KnowledgeBase kbase = translator.readKnowledgeBase();
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");

			// Sell sell1 = new Sell("Ti6o", "Dealer", "Objective");
			// Sell sell2 = new Sell("Margo", "Amazon", "USB");
			Buy buy1 = new Buy("Ti6o", "Dealer", "Objective");
			Buy buy2 = new Buy("Margo", "Amazon", "USB");
			Keep keep = new Keep("Ti6o", "Objective");

			ksession.insert(buy1);
			ksession.insert(buy2);
			ksession.insert(keep);

			ksession.fireAllRules();

			Rule rule1 = (Rule) kbase.getRule("ruleml.translator", "buy&Keep");
			GroupElement[] transformedLhs = rule1.getTransformedLhs();
			translator.transformWhen(transformedLhs[0]);

			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Helper method for reading the knowledge base
	 * @return The knowledge base red from file 
	 */
	private KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("test.drl"),
				ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error : errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

	/**
	 * The main method for the transformation. Transforms the root-groupelement
	 * @param groupElement The root groupElement
	 */
	private void transformWhen(GroupElement groupElement) {

		// add all the constraints to the list (slots)
		List<JAXBElement<?>> atoms = new ArrayList<JAXBElement<?>>();
		
		// iterate over the element groups
		for (Object obj : groupElement.getChildren()) {
			if (obj instanceof Pattern) {
				Pattern pattern = (Pattern) obj;

				// get the properties of the relation
				List<String> propertiesList = getRelationPropertiesFromClass(pattern);

				// add all the constraints to the list (slots)
				List<JAXBElement<?>> atomContent = new ArrayList<JAXBElement<?>>();

				// create the rel
				String relName = ((ClassObjectType)pattern.getObjectType()).getClassType().getSimpleName();
				RelType relType = creator.createRel(relName);
				JAXBElement<OpAtomType> opType = creator.createOp(relType);
				atomContent.add(opType);

				// iterate over the constratints
				for (Object constraint : pattern.getConstraints()) {
					ClassFieldReader field = null;

					if (constraint instanceof Declaration) {
						field = processDeclaration(atomContent, constraint);
					} else if (constraint instanceof LiteralConstraint) {
						field = processLiteralConstraint(atomContent, constraint);
					} else if (constraint instanceof VariableConstraint) {
						field = processVarConstraint(atomContent, constraint);
					}

					// remove the current property from the list
					if (field != null) {
						propertiesList.remove(field.getFieldName());
					}
				}

				// add the rest of the properties of the relation
				for (String property : propertiesList) {
					System.out.printf("SLOT: %s VAR %s\n", property, ".....");
				}

				AtomType atom = creator.createAtom(atomContent
						.toArray(new JAXBElement<?>[atomContent.size()]));

				atoms.add(creator.getFactory().createAtom(atom));

			} else if (obj instanceof GroupElement) {
				transformWhen((GroupElement) obj);
			}
		}

		// create the main part of the group element
		Type type = groupElement.getType();
		if(type.equals(Type.AND)) {
			// create AND
			AndInnerType and = creator.createAnd(atoms.toArray(new JAXBElement<?>[atoms.size()]));
			creator.test(and);
		} else if (type.equals(Type.OR)) {
			// create OR
			OrInnerType or = creator.createOr(atoms.toArray(new JAXBElement<?>[atoms.size()]));
		} else if (type.equals(Type.NOT)) {
			// TODO create NOT
		} else if (type.equals(Type.EXISTS)) {
			// TODO create EXISTS
		}			
	}

	/**
	 * TODO
	 * Not implemented yet.
	 */
	private void transformThen () {
	}
	
	/**
	 * Processes Declaration from pattern
	 * @param elements The content elements (slot, var, rel, ind)
	 * @param constraint Not converted Declaration as constraint
	 * @return The drools reader of the property for the declaration 
	 */
	private ClassFieldReader processDeclaration(List<JAXBElement<?>> elements,
			Object constraint) {
		ClassFieldReader field;
		Declaration declaration = (Declaration) constraint;
		field = (ClassFieldReader) declaration.getExtractor();

		elements.add(creator.createSlot(creator.createInd(field.getFieldName()),
				creator.createVar(declaration.getIdentifier())));
		return field;
	}

	/**
	 * Processes LiteralConstraint from pattern (i.e  buyer == "John")
	 * @param elements The content elements (slot, var, rel, ind)
	 * @param constraint Not converted LiteralConstraint as constraint
	 * @return The drools reader of the property for the declaration 
	 */
	private ClassFieldReader processLiteralConstraint(
			List<JAXBElement<?>> elements, Object constraint) {
		ClassFieldReader field;
		LiteralConstraint literalConstraint = (LiteralConstraint) constraint;
		field = (ClassFieldReader) literalConstraint.getFieldExtractor();

		elements.add(creator.createSlot(creator.createInd(field.getFieldName()),
				creator.createVar(literalConstraint.getField().getValue().toString())));
		return field;
	}

	/**
	 * Processes VarConstraint from pattern (i.e buyer == $person)
	 * @param elements The content elements (slot, var, rel, ind)
	 * @param constraint Not converted VarConstraint as constraint
	 * @return The drools reader of the property for the declaration 
	 */
	private ClassFieldReader processVarConstraint(
			List<JAXBElement<?>> elements, Object constraint) {
		ClassFieldReader field;
		VariableConstraint variableConstraint = (VariableConstraint) constraint;
		field = (ClassFieldReader) variableConstraint.getFieldExtractor();

		if (variableConstraint.getRequiredDeclarations().length > 0) {
			elements.add(creator.createSlot(creator.createInd(field.getFieldName()),
					creator.createVar(variableConstraint.getRequiredDeclarations()[0]
							.getIdentifier())));
		}
		return field;
	}

	/**
	 * Gets all the properties of a data class (relation) to translate them in ruleml. 
	 * @param pattern The Dlr Pattern for which the relation should be created.
	 * @return List of all properties of the class represented from the pattern.
	 */
	private static List<String> getRelationPropertiesFromClass(Pattern pattern) {
		List<String> propertiesList = new ArrayList<String>();
		ClassObjectType relClass = (ClassObjectType) pattern.getObjectType();
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(relClass.getClassType(),
					Object.class);
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				propertiesList.add(propertyDescriptor.getDisplayName());
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return propertiesList;
	}
}
