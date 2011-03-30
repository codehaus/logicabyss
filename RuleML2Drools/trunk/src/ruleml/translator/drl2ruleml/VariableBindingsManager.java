package ruleml.translator.drl2ruleml;

import java.util.HashMap;
import java.util.Map;

public class VariableBindingsManager {
	private Map<String, PropertyInfo> boundVarsOnFieldName = new HashMap<String, PropertyInfo>();
	private Map<String, PropertyInfo> boundVarsOnDeclaration = new HashMap<String, PropertyInfo>();

	public static class PropertyInfo {
		private String name;
		private String value;
		private String var;
		private ValueType type;
		private boolean active = true;;
		private String clazz;

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

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
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


//	public PropertyInfo getFromFieldName(String fieldName) {
//		return boundVarsOnFieldName.get(fieldName);
//	}
//
//	public PropertyInfo getFromDeclaration(String declaration) {
//		return boundVarsOnDeclaration.get(declaration);
//	}
	
	public PropertyInfo get(String key) {
		if (boundVarsOnFieldName.containsKey(key)) {
			return boundVarsOnFieldName.get(key);
		}
		
		if (boundVarsOnDeclaration.containsKey(key)) {
			return boundVarsOnDeclaration.get(key);
		}
		
		return null;
	}
	
	public boolean containsKey (String key) {
		if (boundVarsOnFieldName.containsKey(key) || boundVarsOnDeclaration.containsKey(key)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void put(PropertyInfo propertyInfo) {
		
		if (propertyInfo.getName() != null) {
			boundVarsOnFieldName.put(propertyInfo.getName(), propertyInfo);
		}
		
		if (propertyInfo.getVar() != null) {
			boundVarsOnDeclaration.put(propertyInfo.getVar(), propertyInfo);
		}
	}
}
