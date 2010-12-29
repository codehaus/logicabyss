package com.sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.drools.io.ResourceFactory;

import datalog.RuleMLType;

public class Drools2RuleMLTranslator {

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
}
