package ruleml.translator.drl2ruleml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import javax.xml.bind.JAXBElement;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.GroupElement.Type;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.builder.dialect.java.JavaAnalysisResult;
import org.drools.rule.builder.dialect.java.parser.JavaLexer;
import org.drools.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.drools.rule.builder.dialect.java.parser.JavaParser;

import reactionruleml.AndInnerType;
import reactionruleml.AssertType;
import reactionruleml.AtomType;
import reactionruleml.DoType;
import reactionruleml.IfType;
import reactionruleml.IndType;
import reactionruleml.OpAtomType;
import reactionruleml.RelType;
import reactionruleml.RuleMLType;
import reactionruleml.RuleType;
import reactionruleml.SlotType;
import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator.PropertyInfo.ValueType;

/**
 * Translator for Drools rules to RuleML
 * 
 * @author Jabarski
 */
public class Drools2RuleMLTranslator {

	// This are the possible types for a rule or query in drools
	private enum DroolsRuleType {
		ASSERT, RETRACT, QUERY;
	}

	private DroolsRuleType currentRuleType = DroolsRuleType.ASSERT;

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
	 * @param pkgDescr
	 *            The output of the drools parser in raw form
	 * @return Serialized RuleML
	 */
	public static String translate(KnowledgeBase kbase, PackageDescr pkgDescr) {
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

				// process the RHS(Then) part and set the type
				JAXBElement<?> thenPart = translator.processThenPart(pkgDescr.getRules().get(0).getConsequence().toString());

				// transform the rule
				JAXBElement<?> whenPart = translator
						.processGroupElement(transformedLhs[0]);

				result += translator.wrapElement(whenPart, thenPart)
						+ "\n*********************************************************\n";
			}
		}
		return result;
	}

	/**
	 * Creates the wrapper on the group element
	 * 
	 * @param whenPart
	 *            The when part of the drools source (LHS)
	 * @param thenPart
	 *            The then part of the drools source (RHS)
	 * @return Serialized RuleML
	 */
	private String wrapElement(JAXBElement<?> whenPart, JAXBElement<?> thenPart) {
		// create the wrapper for the current use case ( RuleML ->
		// Assert/Retract/Query ->
		// If , Do)
		JAXBElement<IfType> ifType = builder
				.createIf(new JAXBElement<?>[] { whenPart });
		JAXBElement<DoType> doType = builder
				.createDo(new JAXBElement<?>[] { thenPart });

		JAXBElement<RuleType> ruleType = builder.createRule (new JAXBElement<?>[] { ifType, doType });
		
		JAXBElement<?> ruleMLContent = null;
		if (currentRuleType == DroolsRuleType.ASSERT) {
			ruleMLContent = builder.createAssert(new JAXBElement<?>[] { ruleType });
		} else if (currentRuleType == DroolsRuleType.RETRACT) {
			ruleMLContent = builder.createRetract(new JAXBElement<?>[] {
					ifType, doType });
		} else if (currentRuleType == DroolsRuleType.QUERY) {
			ruleMLContent = builder
					.createQuery(new JAXBElement<?>[] { ifType });
		}

		JAXBElement<RuleMLType> ruleML = builder
				.createRuleML(new JAXBElement<?>[] { ruleMLContent });

		// serialize and return
		return builder.marshal(ruleML);
	}

	/**
	 * The main method for a transformation of the lhs-part of drools model.
	 * Transforms the root-groupelement
	 * 
	 * @param groupElement
	 *            The root groupElement
	 * @param thenPart
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
		List<JAXBElement<SlotType>> unusedProperties = getUnusedProperties(
				slots, pattern);
		atomContent.addAll(unusedProperties);

		if (constraintsAnalyzer.getOther().size() > 0) {
			List<JAXBElement<?>> other = constraintsAnalyzer.getOther();
			other.add(builder.createAtom(atomContent
					.toArray(new JAXBElement<?>[atomContent.size()])));
			JAXBElement<AndInnerType> and = builder.createAnd(other
					.toArray(new JAXBElement<?>[other.size()]));
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
		List<String> properties = getPropertiesFromClass(((ClassObjectType) pattern.getObjectType()).getClassType());

		// iterate over the slots and remove all the slotted properties
		for (JAXBElement<SlotType> slot : slots) {
			// get the name of the property from the slot (ind)
			Object ind = slot.getValue().getContent().get(0).getValue();
			if (ind instanceof IndType) {
				IndType indType = (IndType) ind;
				String property = (String) indType.getContent().get(0);
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

	private JAXBElement<?> processThenPart(String consequence) {
		try {
//			String consequence = "insert   ( new Own(\"Mary\",\"iPod\"))";
			// String consequence = "retract($B)";

			if (consequence.contains("insert")) {
				this.currentRuleType = DroolsRuleType.ASSERT;
				return createInsert(consequence);
			} else if (consequence.contains("retract")) {
				this.currentRuleType = DroolsRuleType.RETRACT;
				// return createRetract(consequence);
			} else {
				throw new IllegalStateException(
						"Can not process the then part because it is not an insert or retract");
			}

			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private JAXBElement<?> createInsert(String insert)
			throws ClassNotFoundException {
		// get the data from consequence insert
		List<String> dataFromInsert = getDataFromInsert(insert);

		// get the name of the relation
		String relName = "";
		if (dataFromInsert.size() == 0) {
			throw new IllegalStateException(
					"The insert does not contain data: " + insert);
		} else {
			relName = dataFromInsert.get(0).trim();
			dataFromInsert.remove(0);
		}

		// get the properties of the relation from the java class
		if (relName.isEmpty()) {
			throw new IllegalStateException("The relation name is empty: "
					+ relName);
		} else {
			System.out.println("!"+relName+"!");
			Class<?> clazz = Class.forName("ruleml.translator.TestDataModel$"+relName);
			List<String> classProperties = getPropertiesFromClass(clazz);

			System.out.println(dataFromInsert);
			System.out.println(classProperties);
			
			if (dataFromInsert.size() != classProperties.size()) {
				System.out
						.printf("Warning : the relation properties count %s does not coincide with arguments found in the counstructor:"
								+ classProperties, insert);
			}

			// create the list with elements, the content of the atom relation
			List<JAXBElement<?>> jaxbElements = new ArrayList<JAXBElement<?>>();
			RelType rel = Drools2RuleMLTranslator.builder.createRel(relName);
			jaxbElements.add(Drools2RuleMLTranslator.builder.getFactory().createRel(rel));
			
			// iterate over the class properties
			for (int i =0; i<classProperties.size();i++) {
				JAXBElement<IndType> slotName = Drools2RuleMLTranslator.builder.createInd(classProperties.get(i));
				JAXBElement<IndType> slotValue = Drools2RuleMLTranslator.builder.createInd(dataFromInsert.get(i));

				JAXBElement<SlotType> slot = Drools2RuleMLTranslator.builder.createSlot(slotName, slotValue);
				jaxbElements.add(slot);
			}

			JAXBElement<AtomType> atom = Drools2RuleMLTranslator.builder.createAtom(jaxbElements.toArray(new JAXBElement<?>[jaxbElements.size()]));
			JAXBElement<AssertType> assertType = Drools2RuleMLTranslator.builder.createAssert(new JAXBElement<?>[] {atom});
				
			return assertType;
		}
	}

	private List<String> getDataFromInsert(String insert) {
		insert = insert.trim();
		List<String> result = new ArrayList<String>();

		System.out.println(insert);
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("insert\\s*\\(\\s*(.+)\\s*\\);");
		java.util.regex.Matcher m = p.matcher(insert);

		if (m.matches() && m.groupCount() == 1) {
			String s1 = m.group(1);
			System.out.println("s1 : " + s1);
			p = java.util.regex.Pattern.compile("new\\s+(.+)\\s*\\(\\s*(.+)\\s*\\)");
			m = p.matcher(s1);

			if (m.matches() && m.groupCount() == 2) {
				result.add(m.group(1));
				String s2 = m.group(2);
				System.out.println("s2 : " + s2);
				p = java.util.regex.Pattern.compile("\"?(\\S+)\"?(\\s*,\\s*\"?(\\S+)\"?\\s*)*");
				m = p.matcher(s2);

				System.out.println(m.matches());
				System.out.println(m.groupCount());
				
				for (int i = 1; i <= m.groupCount(); i += 2) {
					result.add(m.group(i));
				}
			}
		}

		System.out.println(result);
		return result;
	}

	/**
	 * Gets all the properties of a data class (relation) to translate them in
	 * ruleml.
	 * 
	 * @param pattern
	 *            The Drl Pattern for which the relation should be created.
	 * @return List of all properties of the class represented from the pattern.
	 */
	public List<String> getPropertiesFromClass(Class<?> clazz) {
		List<String> propertiesList = new ArrayList<String>();
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(clazz,Object.class);
			PropertyDescriptor[] propertyDescriptors = beanInfo
			.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				propertiesList.add(propertyDescriptor.getDisplayName());
			}
			return propertiesList;
		} catch (IntrospectionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
