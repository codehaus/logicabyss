package ruleml.translator.drl2ruleml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;

import reactionruleml.DoType;
import reactionruleml.IfType;
import reactionruleml.RuleMLType;
import reactionruleml.RuleType;
import reactionruleml.ThenType;

/**
 * Translator for Drools rules to RuleML
 * 
 * @author Jabarski
 */
public class Drools2RuleMLTranslator {

	// This are the possible types for a rule or query in drools
	public enum RuleStyle {
		ASSERT, RETRACT, QUERY;
	}

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

				// process the LHS (WHEN part)
				WhenPartAnalyzer whenPartAnalyzer = new WhenPartAnalyzer();
				JAXBElement<?> whenPart = whenPartAnalyzer
						.processGroupElement(transformedLhs[0]);

				// process the RHS(Then part), and set the type
				ThenPartAnalyzer thenPartAnalyzer = new ThenPartAnalyzer(
						whenPartAnalyzer);
				JAXBElement<?> thenPart = thenPartAnalyzer
						.processThenPart(pkgDescr.getRules().get(0)
								.getConsequence().toString());

				result += translator.wrapElement(whenPart, thenPart,
						thenPartAnalyzer.getRuleStyle());
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
	private String wrapElement(JAXBElement<?> whenPart,
			JAXBElement<?> thenPart, RuleStyle ruleStyle) {
		// create the wrapper for the current use case ( RuleML ->
		// Assert/Retract/Query -> Rule -> If , Do)
		JAXBElement<IfType> ifType = builder
				.createIf(new JAXBElement<?>[] { whenPart });

		JAXBElement<?> assertOrRetract = null;
		if (ruleStyle == RuleStyle.ASSERT) {
			assertOrRetract = builder
					.createAssert(new JAXBElement<?>[] { thenPart });
		} else if (ruleStyle == RuleStyle.RETRACT) {
			assertOrRetract = builder
					.createRetract(new JAXBElement<?>[] { thenPart });
		}

		// JAXBElement<ThenType> thenType = builder
		// .createThen(new JAXBElement<?>[] { thenPart });

		JAXBElement<DoType> doType = builder
				.createDo(new JAXBElement<?>[] { assertOrRetract });

		JAXBElement<RuleType> ruleType = builder
				.createRule(new JAXBElement<?>[] { ifType, doType });

		JAXBElement<?> ruleMLContent = builder
					.createAssert(new JAXBElement<?>[] { ruleType });

		JAXBElement<RuleMLType> ruleML = builder
				.createRuleML(new JAXBElement<?>[] { ruleMLContent });

		// serialize and return
		return builder.marshal(ruleML, true);
	}

	/**
	 * Gets all the properties of a data class (relation) to translate them in
	 * ruleml.
	 * 
	 * @param pattern
	 *            The Drl Pattern for which the relation should be created.
	 * @return List of all properties of the class represented from the pattern.
	 */
	public static List<String> getPropertiesFromClass(Class<?> clazz) {
		List<String> propertiesList = new ArrayList<String>();
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(clazz, Object.class);
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
