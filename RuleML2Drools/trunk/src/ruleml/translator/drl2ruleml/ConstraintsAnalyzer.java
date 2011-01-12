package ruleml.translator.drl2ruleml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.base.ClassFieldReader;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.OrConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.VariableConstraint;

import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator.PropertyInfo;
import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator.PropertyInfo.ValueType;

public class ConstraintsAnalyzer {
	
	private List<?> other = new ArrayList();
	
	public List getOther () {
		return this.other;
	}
	
	/**
	 * Processes all the constrains of the pattern
	 * @param pattern The pattern that is being analyzed
	 * @return List of property informations that contain data about the constraints 
	 */
	public List<PropertyInfo> processConstraints(Pattern pattern) {
		// the result list
		List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();

		// map for search
		Map<String, PropertyInfo> map = new HashMap<String, PropertyInfo>();

		// iterate over the constraints
		for (Object constraint : pattern.getConstraints()) {
			// process the constraint
			PropertyInfo temp = processConstraint(constraint);
			
			if (temp != null) {
				// check if the map already contains the new property
				if (map.containsKey(temp.getName())) {
					// merge the content to the existing one
					mergePropertyInfos(map.get(temp.getName()), temp);
				} else {
					// put the new in the map
					map.put(temp.getName(), temp);
					propertyInfos.add(temp);
				}
			}
		}

		return propertyInfos;
	}

	/**
	 * Dispatches one constraint and process it
	 * @param propertyInfos
	 * @param map
	 * @param constraint
	 */
	private PropertyInfo processConstraint(Object constraint) {
		PropertyInfo temp = null;

		if (constraint instanceof Declaration) {
			temp = processDeclaration(constraint);
		} else if (constraint instanceof LiteralConstraint) {
			temp = processLiteralConstraint(constraint);
			((LiteralConstraint)constraint).getEvaluator();
		} else if (constraint instanceof VariableConstraint) {
			temp = processVarConstraint(constraint);
		} else if (constraint instanceof OrConstraint) {
			// OrConstraint orConstraint = (OrConstraint) constraint;
			// AlphaNodeFieldConstraint[] alphaConstraints = orConstraint
			// .getAlphaConstraints();
			// for (AlphaNodeFieldConstraint alphaNodeFieldConstraint :
			// alphaConstraints) {
			// if (alphaNodeFieldConstraint instanceof LiteralConstraint) {
			// processLiteralConstraint(alphaNodeFieldConstraint);
			// }
			// }
		}
		
		return temp;
	}

	/**
	 * Merges the values of two property informations. 
	 * @param property1 The first property, where the result will be returned.
	 * @param property2 The second property.
	 */
	private void mergePropertyInfos(PropertyInfo property1,
			PropertyInfo property2) {
		property1.setVar(property2.getVar() == null ? property1.getVar()
				: property2.getVar());
		property1.setValue(property2.getValue() == null ? property1.getValue()
				: property2.getValue());

		if (property1.getValue() != null) {
			property1.setType(ValueType.IND);
		}
	}

	/**
	 * Processes Declaration from pattern
	 * 
	 * @param constraint
	 *            Not casted Declaration as constraint
	 * @return The drools reader of the property for the declaration
	 */
	private PropertyInfo processDeclaration(Object constraint) {
		Declaration declaration = (Declaration) constraint;
		ClassFieldReader field = (ClassFieldReader) declaration.getExtractor();

		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName(field.getFieldName());
		propertyInfo.setVar(declaration.getIdentifier());
		propertyInfo.setType(ValueType.VAR);
		return propertyInfo;
	}

	/**
	 * Processes LiteralConstraint from pattern (i.e buyer == "John")
	 * 
	 * @param constraint
	 *            Not converted LiteralConstraint as constraint
	 * @return The drools reader of the property for the declaration
	 */
	private PropertyInfo processLiteralConstraint(Object constraint) {
		LiteralConstraint literalConstraint = (LiteralConstraint) constraint;
		ClassFieldReader field = (ClassFieldReader) literalConstraint
				.getFieldExtractor();

//		literalConstraint.
		
		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName(field.getFieldName());
		propertyInfo.setValue(literalConstraint.getField().getValue()
				.toString());
		propertyInfo.setType(ValueType.IND);
		return propertyInfo;
	}

	/**
	 * Processes VarConstraint from pattern (i.e buyer == $person)
	 * 
	 * @param constraint
	 *            Not casted VarConstraint as constraint
	 * @return The drools reader of the property for the declaration
	 */
	private PropertyInfo processVarConstraint(Object constraint) {
		VariableConstraint variableConstraint = (VariableConstraint) constraint;
		ClassFieldReader field = (ClassFieldReader) variableConstraint
				.getFieldExtractor();

		if (variableConstraint.getRequiredDeclarations().length > 0) {

			PropertyInfo propertyInfo = new PropertyInfo();
			propertyInfo.setName(field.getFieldName());
			propertyInfo
					.setVar(variableConstraint.getRequiredDeclarations()[0]
							.getIdentifier());
			propertyInfo.setType(ValueType.VAR);
			return propertyInfo;
		}

		throw new RuntimeException("VariableConstratint is empty !!!"
				+ variableConstraint);
	}

	// /**
	// * Processes OrConstraint from pattern
	// *
	// * @param elements
	// * The content elements (slot, var, rel, ind)
	// * @param constraint
	// * Not casted rConstrainte constraint
	// * @return The drools reader of the property for the declaration
	// */
	// private JAXBElement<SlotType> processOrConstraint(Object constraint) {}

}
