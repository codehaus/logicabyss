package ruleml.translator.drl2ruleml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import naffolog.AndInnerType;
import naffolog.AssertType;
import naffolog.AtomType;
import naffolog.IfType;
import naffolog.ImpliesType;
import naffolog.IndType;
import naffolog.OpAtomType;
import naffolog.RelType;
import naffolog.RuleMLType;
import naffolog.SlotType;

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

import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator.PropertyInfo.ValueType;

/**
 * Translator for Drools rules to RuleML
 * 
 * @author Jabarski
 */
public class Drools2RuleMLTranslator {

	public static RuleMLBuilder builder = new RuleMLBuilder();

	public static class PropertyInfo {
		private String name;
		private String value;
		private String var;
		private ValueType type;
		private boolean active = true;;
		private Class clazz;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Class getClazz() {
			return clazz;
		}

		public void setClazz(Class clazz) {
			this.clazz = clazz;
		}

		public void setVar(String var) {
			this.var = var;
		}

		public String getVar() {
			return var;
		}

		public void setType(ValueType type) {
			this.type = type;
		}

		public ValueType getType() {
			return type;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public boolean isActive() {
			return active;
		}

		public enum ValueType {
			VAR, IND, DATA
		}
	}

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
		JAXBElement<IfType> ifType = builder
				.createIf(new JAXBElement<?>[] { element });
		JAXBElement<ImpliesType> implies = builder
				.createImplies(new JAXBElement<?>[] { ifType });
		JAXBElement<AssertType> assertType = builder
				.createAssert(new JAXBElement<?>[] { implies });
		JAXBElement<RuleMLType> ruleML = builder
				.createRuleML(new JAXBElement<?>[] { assertType });

		// serialize and return
		return builder.marshal(ruleML);
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
		List<JAXBElement<?>> elements = new ArrayList<JAXBElement<?>>();

		// iterate over the elements in the element group and collect them in
		// the list (patterns or groups)
		for (Object obj : groupElement.getChildren()) {
			if (obj instanceof Pattern) {
				// process the pattern
				elements.add(processPattern((Pattern) obj));
			} else if (obj instanceof GroupElement) {
				// recursive call to same method
				elements.add(processGroupElement((GroupElement) obj));
			}
		}

		// processes the type of the groupelement (AND,OR,NOT,FORALL,EXISTS)
		Type type = groupElement.getType();
		if (type.equals(Type.AND)) {
			return builder.createAnd(elements
					.toArray(new JAXBElement<?>[elements.size()]));
		} else if (type.equals(Type.OR)) {
			return builder.createOr(elements
					.toArray(new JAXBElement<?>[elements.size()]));
		} else if (type.equals(Type.NOT)) {
			return builder.createNeg(elements
					.toArray(new JAXBElement<?>[elements.size()]));
		} else if (type.equals(Type.EXISTS)) {
			return builder.createExists(elements
					.toArray(new JAXBElement<?>[elements.size()]));
		}
		throw new UnsupportedOperationException();

	}

	/**
	 * Analyzes the pattern.
	 * 
	 * @param pattern
	 *            Pattern to be analyzed
	 * @return Ruleml Atom.
	 */
	private JAXBElement<?> processPattern(Pattern pattern) {

		// add all the constraints to the list (slots)
		List<JAXBElement<?>> atomContent = new ArrayList<JAXBElement<?>>();

		// creates the ruleml REL with the relation name
		JAXBElement<OpAtomType> rel = processRel(pattern);
		atomContent.add(rel);

		// process all the constraints of the pattern
		ConstraintsAnalyzer constraintsAnalyzer = new ConstraintsAnalyzer();
		List<PropertyInfo> propertyInfos = constraintsAnalyzer
				.processConstraints(pattern);

		// convert the propertyinfos in slots
		List<JAXBElement<SlotType>> slots = convertPropertyInfosInSlots(propertyInfos);
		atomContent.addAll(slots);

		// put the unused relation properties in slots
		List<JAXBElement<SlotType>> unUsedProperties = getUnusedProperties(
				slots, pattern);
		atomContent.addAll(unUsedProperties);

		if (constraintsAnalyzer.getOther().size() > 0 ) {
			List<JAXBElement<?>> other = constraintsAnalyzer.getOther();
			other.add(builder.createAtom(atomContent
					.toArray(new JAXBElement<?>[atomContent.size()])));
			JAXBElement<AndInnerType> and = builder.createAnd(other.toArray(new JAXBElement<?>[other.size()]));
			return and;
		}
		
		return builder.createAtom(atomContent
				.toArray(new JAXBElement<?>[atomContent.size()]));
	}

	/**
	 * Converts the list with property informations in slots
	 * 
	 * @param propertyInfos
	 *            The list property information created from the pattern
	 *            constraints
	 * @return List with ruleml elements (slots)
	 */
	private List<JAXBElement<SlotType>> convertPropertyInfosInSlots(
			List<PropertyInfo> propertyInfos) {
		List<JAXBElement<SlotType>> result = new ArrayList<JAXBElement<SlotType>>();

		// for all the propertyinfos
		for (PropertyInfo propertyInfo : propertyInfos) {
			JAXBElement<?> content = null;

			if (propertyInfo.getType().equals(ValueType.IND)) {
				content = builder.createInd(propertyInfo.getValue());
			} else if (propertyInfo.getType().equals(ValueType.VAR)) {
				content = builder.createVar(propertyInfo.getVar());
			}

			// create slot
			JAXBElement<SlotType> slot = builder.createSlot(
					builder.createInd(propertyInfo.getName()), content);
			// add to the result list
			result.add(slot);
		}

		return result;
	}

	/**
	 * Returns the unused properties of a relation from a pattern. This
	 * properties has not been seen in any constraint of the pattern.
	 * 
	 * @param slots
	 *            The slots that were found in pattern constraints.
	 * @param pattern
	 *            The pattern that is being analyzed.
	 * @return List of ruleml elements (slots)
	 */
	private List<JAXBElement<SlotType>> getUnusedProperties(
			List<JAXBElement<SlotType>> slots, Pattern pattern) {
		// get the properties of the relation
		List<String> properties = getRelationPropertiesFromClass(pattern);

		// iterate over the slots and remove all the slotted properties
		for (JAXBElement<SlotType> slot : slots) {
			// get the name of the property from the slot (ind)
			Object ind = slot.getValue().getContent().get(0).getValue();
			if (ind instanceof IndType) {
				IndType indType = (IndType) ind;
				String property = indType.getContent().get(0);
				// remove the slotted property from the list
				properties.remove(property);
			}
		}

		// accumulator for the rest slots
		List<JAXBElement<SlotType>> result = new ArrayList<JAXBElement<SlotType>>();

		// iterate over the rest properties and create new slot for each
		for (String property : properties) {
			// create the slot
			result.add(builder.createSlot(builder.createInd(property),
					builder.createVar("......")));
		}

		return result;
	}

	/**
	 * Creates OpAtomType from pattern. This is the name of the relation in
	 * RuleML
	 * 
	 * @param pattern
	 *            The drools pattern that is being analyzed
	 * @return RuleML OpAtomType that represents the name of the relation
	 */
	private JAXBElement<OpAtomType> processRel(Pattern pattern) {
		// create the rel
		String relName = ((ClassObjectType) pattern.getObjectType())
				.getClassType().getSimpleName();
		RelType relType = builder.createRel(relName);
		return builder.createOp(relType);
	}

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
