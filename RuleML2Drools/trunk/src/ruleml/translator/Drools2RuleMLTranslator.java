package ruleml.translator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;

import naffolog.AssertType;
import naffolog.AtomType;
import naffolog.IfType;
import naffolog.ImpliesType;
import naffolog.OpAtomType;
import naffolog.RelType;
import naffolog.RuleMLType;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.ClassFieldReader;
import org.drools.base.ClassObjectType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.GroupElement.Type;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.OrConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.VariableConstraint;
import org.drools.spi.AlphaNodeFieldConstraint;

/**
 * Translator for Drools rules to RuleML
 * 
 * @author Jabarski
 */
public class Drools2RuleMLTranslator {

	private RuleMLBuilder creator = new RuleMLBuilder();

	/**
	 * Helper method for reading the knowledge base
	 * 
	 * @return The knowledge base red from file
	 */
	public static KnowledgeBase readKnowledgeBase(String fileName)
			throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(fileName),
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
	 * The main entry point for the translation
	 * 
	 * @param kbase
	 *            The knowledge base
	 * @return Serialized RuleML
	 */
	public static String translate(KnowledgeBase kbase) {
		Drools2RuleMLTranslator translator = new Drools2RuleMLTranslator();

		String result = "";

		// get the packages from the knowledge base
		Collection<KnowledgePackage> knowledgePackages = kbase
				.getKnowledgePackages();
		// iterate over the packages
		for (KnowledgePackage knowledgePackage : knowledgePackages) {
			// get the rules from the package
			Collection<org.drools.definition.rule.Rule> rules = knowledgePackage
					.getRules();
			// iterate over the rules in the package
			for (org.drools.definition.rule.Rule rule : rules) {
				// get the rule
				Rule rule_ = (Rule) kbase.getRule(rule.getPackageName(),
						rule.getName());
				// get the root group element
				GroupElement[] transformedLhs = rule_.getTransformedLhs();
				// transform the rule
				JAXBElement<?> element = translator
						.processGroupElement(transformedLhs[0]);

				result += translator.wrapElement(element)
						+ "\n*********************************************************\n";
			}
		}
		return result;
	}

	/**
	 * Creates the wrapper on the group element
	 * 
	 * @param element
	 *            The main group elelemnt of the Drools-LHS
	 * @return Serialized RuleML
	 */
	private String wrapElement(JAXBElement<?> element) {
		// create the wrapper for the current use case ( RuleML -> Assert ->
		// Implies -> If)
		JAXBElement<IfType> ifType = creator
				.createIf(new JAXBElement<?>[] { element });
		JAXBElement<ImpliesType> implies = creator
				.createImplies(new JAXBElement<?>[] { ifType });
		JAXBElement<AssertType> assertType = creator
				.createAssert(new JAXBElement<?>[] { implies });
		JAXBElement<RuleMLType> ruleML = creator
				.createRuleML(new JAXBElement<?>[] { assertType });

		// serialize and return
		return creator.marshal(ruleML);
	}

	/**
	 * The main method for a transformation of the lhs-part of drools model.
	 * Transforms the root-groupelement
	 * 
	 * @param groupElement
	 *            The root groupElement
	 */
	private JAXBElement<?> processGroupElement(GroupElement groupElement) {

		// collector for all the atoms in the when part
		List<JAXBElement<?>> atoms = new ArrayList<JAXBElement<?>>();

		// iterate over the elements in the element group and collect them in the list (patterns or groups)
		for (Object obj : groupElement.getChildren()) {
			if (obj instanceof Pattern) {
				// process the pattern
				atoms.add(processPattern((Pattern) obj));
			} else if (obj instanceof GroupElement) {
				// recursive call to same method
				atoms.add(processGroupElement((GroupElement) obj));
			}
		}

		// processes the type of the groupelement (AND,OR,NOT,FORALL,EXISTS)
		Type type = groupElement.getType();
		if (type.equals(Type.AND)) {
			return creator.createAnd(atoms.toArray(new JAXBElement<?>[atoms
					.size()]));
		} else if (type.equals(Type.OR)) {
			return creator.createOr(atoms.toArray(new JAXBElement<?>[atoms
					.size()]));
		} else if (type.equals(Type.NOT)) {
			return creator.createNeg(atoms.toArray(new JAXBElement<?>[atoms
					.size()]));
		} else if (type.equals(Type.EXISTS)) {
			return creator.createExists(atoms.toArray(new JAXBElement<?>[atoms
			                                      					.size()]));		}
		throw new UnsupportedOperationException();

	}

	private JAXBElement<AtomType> processPattern(Pattern pattern) {
		// get the properties of the relation
		List<String> propertiesList = getRelationPropertiesFromClass(pattern);

		// add all the constraints to the list (slots)
		List<JAXBElement<?>> atomContent = new ArrayList<JAXBElement<?>>();

		// creates the ruleml REL with the relation name
		processRel(pattern, atomContent);

		// process all the constraints of the pattern
		processConstraints(pattern, propertiesList, atomContent);

		// TODO add the rest of the properties of the relation
		for (String property : propertiesList) {
			System.out.printf("SLOT: %s VAR %s\n", property, ".....");
		}

		return creator.createAtom(atomContent
				.toArray(new JAXBElement<?>[atomContent.size()]));
	}

	private void processRel(Pattern pattern, List<JAXBElement<?>> atomContent) {
		// create the rel
		String relName = ((ClassObjectType) pattern.getObjectType())
				.getClassType().getSimpleName();
		RelType relType = creator.createRel(relName);
		JAXBElement<OpAtomType> opType = creator.createOp(relType);
		atomContent.add(opType);
	}

	private void processConstraints(Pattern pattern,
			List<String> propertiesList, List<JAXBElement<?>> atomContent) {
		// iterate over the constratints
		for (Object constraint : pattern.getConstraints()) {
			ClassFieldReader field = null;

			if (constraint instanceof Declaration) {
				field = processDeclaration(atomContent, constraint);
			} else if (constraint instanceof LiteralConstraint) {
				field = processLiteralConstraint(atomContent, constraint);
			} else if (constraint instanceof VariableConstraint) {
				field = processVarConstraint(atomContent, constraint);
			} else if (constraint instanceof OrConstraint) {
				OrConstraint orConstraint = (OrConstraint) constraint;
				AlphaNodeFieldConstraint[] alphaConstraints = orConstraint
						.getAlphaConstraints();
				for (AlphaNodeFieldConstraint alphaNodeFieldConstraint : alphaConstraints) {
					if (alphaNodeFieldConstraint instanceof LiteralConstraint) {
						processLiteralConstraint(atomContent,
								alphaNodeFieldConstraint);
					}
				}
			}

			// remove the current property from the list
			if (field != null) {
				propertiesList.remove(field.getFieldName());
			}
		}
	}

	/**
	 * Processes Declaration from pattern
	 * 
	 * @param elements
	 *            The content elements (slot, var, rel, ind)
	 * @param constraint
	 *            Not casted Declaration as constraint
	 * @return The drools reader of the property for the declaration
	 */
	private ClassFieldReader processDeclaration(List<JAXBElement<?>> elements,
			Object constraint) {
		ClassFieldReader field;
		Declaration declaration = (Declaration) constraint;
		field = (ClassFieldReader) declaration.getExtractor();

		elements.add(creator.createSlot(
				creator.createInd(field.getFieldName()),
				creator.createVar(declaration.getIdentifier())));
		return field;
	}

	/**
	 * Processes LiteralConstraint from pattern (i.e buyer == "John")
	 * 
	 * @param elements
	 *            The casted elements (slot, var, rel, ind)
	 * @param constraint
	 *            Not converted LiteralConstraint as constraint
	 * @return The drools reader of the property for the declaration
	 */
	private ClassFieldReader processLiteralConstraint(
			List<JAXBElement<?>> elements, Object constraint) {
		ClassFieldReader field;
		LiteralConstraint literalConstraint = (LiteralConstraint) constraint;
		field = (ClassFieldReader) literalConstraint.getFieldExtractor();

		elements.add(creator.createSlot(
				creator.createInd(field.getFieldName()),
				creator.createInd(literalConstraint.getField().getValue()
						.toString())));
		return field;
	}

	/**
	 * Processes VarConstraint from pattern (i.e buyer == $person)
	 * 
	 * @param elements
	 *            The content elements (slot, var, rel, ind)
	 * @param constraint
	 *            Not casted VarConstraint as constraint
	 * @return The drools reader of the property for the declaration
	 */
	private ClassFieldReader processVarConstraint(
			List<JAXBElement<?>> elements, Object constraint) {
		ClassFieldReader field;
		VariableConstraint variableConstraint = (VariableConstraint) constraint;
		field = (ClassFieldReader) variableConstraint.getFieldExtractor();

		if (variableConstraint.getRequiredDeclarations().length > 0) {
			elements.add(creator.createSlot(creator.createInd(field
					.getFieldName()), creator.createVar(variableConstraint
					.getRequiredDeclarations()[0].getIdentifier())));
		}
		return field;
	}

	/**
	 * Processes OrConstraint from pattern
	 * 
	 * @param elements
	 *            The content elements (slot, var, rel, ind)
	 * @param constraint
	 *            Not casted rConstrainte constraint
	 * @return The drools reader of the property for the declaration
	 */
	// private ClassFieldReader processOrConstraint(List<JAXBElement<?>>
	// elements,
	// Object constraint) {
	// ClassFieldReader field;
	//
	//
	// return field;
	// }

	/**
	 * TODO Not implemented yet.
	 */
	private void transformThen() {
	}

	/**
	 * Gets all the properties of a data class (relation) to translate them in
	 * ruleml.
	 * 
	 * @param pattern
	 *            The Dlr Pattern for which the relation should be created.
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
