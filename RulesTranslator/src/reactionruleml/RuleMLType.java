//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3-hudson-jaxb-ri-2.2-70- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.02 at 03:31:08 PM MEZ 
//


package reactionruleml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RuleML.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RuleML.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.ruleml.org/1.0/xsd}RuleML.content"/>
 *       &lt;attGroup ref="{http://www.ruleml.org/1.0/xsd}RuleML.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement (name = "RuleML")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RuleML.type", propOrder = {
    "label",
    "scope",
    "qualification",
    "oid",
    "assertOrRetractOrQuery",
    "message"
})
public class RuleMLType {

    protected Object label;
    protected Object scope;
    protected Object qualification;
    protected OidType oid;
    @XmlElements({
        @XmlElement(name = "Query", type = QueryType.class),
        @XmlElement(name = "Assert", type = AssertType.class),
        @XmlElement(name = "Retract", type = RetractType.class)
    })
    protected List<Object> assertOrRetractOrQuery;
    @XmlElement(name = "Message")
    protected MessageType message;

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setLabel(Object value) {
        this.label = value;
    }

    /**
     * Gets the value of the scope property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getScope() {
        return scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setScope(Object value) {
        this.scope = value;
    }

    /**
     * Gets the value of the qualification property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getQualification() {
        return qualification;
    }

    /**
     * Sets the value of the qualification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setQualification(Object value) {
        this.qualification = value;
    }

    /**
     * Gets the value of the oid property.
     * 
     * @return
     *     possible object is
     *     {@link OidType }
     *     
     */
    public OidType getOid() {
        return oid;
    }

    /**
     * Sets the value of the oid property.
     * 
     * @param value
     *     allowed object is
     *     {@link OidType }
     *     
     */
    public void setOid(OidType value) {
        this.oid = value;
    }

    /**
     * Gets the value of the assertOrRetractOrQuery property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assertOrRetractOrQuery property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssertOrRetractOrQuery().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QueryType }
     * {@link AssertType }
     * {@link RetractType }
     * 
     * 
     */
    public List<Object> getAssertOrRetractOrQuery() {
        if (assertOrRetractOrQuery == null) {
            assertOrRetractOrQuery = new ArrayList<Object>();
        }
        return this.assertOrRetractOrQuery;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link MessageType }
     *     
     */
    public MessageType getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageType }
     *     
     */
    public void setMessage(MessageType value) {
        this.message = value;
    }

}
