//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3-hudson-jaxb-ri-2.2-70- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.02 at 03:31:08 PM MEZ 
//


package reactionruleml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Rule.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Rule.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.ruleml.org/1.0/xsd}Rule.content"/>
 *       &lt;attGroup ref="{http://www.ruleml.org/1.0/xsd}Rule.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rule.type", propOrder = {
    "content"
})
public class RuleType {

    @XmlElementRefs({
        @XmlElementRef(name = "do", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "else", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "scope", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "label", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "elseDo", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "oid", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "qualification", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "elseAfter", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "then", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "on", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "if", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class),
        @XmlElementRef(name = "after", namespace = "http://www.ruleml.org/1.0/xsd", type = JAXBElement.class)
    })
    protected List<JAXBElement<?>> content;
    @XmlAttribute(name = "evaluation")
    protected String evaluation;
    @XmlAttribute(name = "style")
    protected String style;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "Else" is used by two different parts of a schema. See: 
     * line 66 of http://svn.codehaus.org/logicabyss/RuleML2Drools/trunk/resources/rrml/pr.xsd
     * line 98 of http://svn.codehaus.org/logicabyss/RuleML2Drools/trunk/resources/rrml/dr.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link ThenType }{@code >}
     * {@link JAXBElement }{@code <}{@link DoType }{@code >}
     * {@link JAXBElement }{@code <}{@link DoType }{@code >}
     * {@link JAXBElement }{@code <}{@link OidType }{@code >}
     * {@link JAXBElement }{@code <}{@link IfType }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link IfType }{@code >}
     * {@link JAXBElement }{@code <}{@link IfType }{@code >}
     * {@link JAXBElement }{@code <}{@link OnType }{@code >}
     * {@link JAXBElement }{@code <}{@link ThenType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<?>>();
        }
        return this.content;
    }

    /**
     * Gets the value of the evaluation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEvaluation() {
        if (evaluation == null) {
            return "strong";
        } else {
            return evaluation;
        }
    }

    /**
     * Sets the value of the evaluation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvaluation(String value) {
        this.evaluation = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyle() {
        if (style == null) {
            return "reasoning";
        } else {
            return style;
        }
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyle(String value) {
        this.style = value;
    }

}
