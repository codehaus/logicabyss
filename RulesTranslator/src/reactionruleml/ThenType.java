//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3-hudson-jaxb-ri-2.2-70- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.02 at 03:31:08 PM MEZ 
//


package reactionruleml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for then.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="then.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.ruleml.org/1.0/xsd}then.content"/>
 *       &lt;attGroup ref="{http://www.ruleml.org/1.0/xsd}then.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "then.type", propOrder = {
    "atom",
    "equal",
    "_assert",
    "retract",
    "update"
})
public class ThenType {

    @XmlElement(name = "Atom")
    protected AtomType atom;
    @XmlElement(name = "Equal")
    protected EqualType equal;
    @XmlElement(name = "Assert")
    protected AssertType _assert;
    @XmlElement(name = "Retract")
    protected RetractType retract;
    @XmlElement(name = "Update")
    protected UpdateType update;
    @XmlAttribute(name = "arg")
    protected String arg;

    /**
     * Gets the value of the atom property.
     * 
     * @return
     *     possible object is
     *     {@link AtomType }
     *     
     */
    public AtomType getAtom() {
        return atom;
    }

    /**
     * Sets the value of the atom property.
     * 
     * @param value
     *     allowed object is
     *     {@link AtomType }
     *     
     */
    public void setAtom(AtomType value) {
        this.atom = value;
    }

    /**
     * Gets the value of the equal property.
     * 
     * @return
     *     possible object is
     *     {@link EqualType }
     *     
     */
    public EqualType getEqual() {
        return equal;
    }

    /**
     * Sets the value of the equal property.
     * 
     * @param value
     *     allowed object is
     *     {@link EqualType }
     *     
     */
    public void setEqual(EqualType value) {
        this.equal = value;
    }

    /**
     * Gets the value of the assert property.
     * 
     * @return
     *     possible object is
     *     {@link AssertType }
     *     
     */
    public AssertType getAssert() {
        return _assert;
    }

    /**
     * Sets the value of the assert property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssertType }
     *     
     */
    public void setAssert(AssertType value) {
        this._assert = value;
    }

    /**
     * Gets the value of the retract property.
     * 
     * @return
     *     possible object is
     *     {@link RetractType }
     *     
     */
    public RetractType getRetract() {
        return retract;
    }

    /**
     * Sets the value of the retract property.
     * 
     * @param value
     *     allowed object is
     *     {@link RetractType }
     *     
     */
    public void setRetract(RetractType value) {
        this.retract = value;
    }

    /**
     * Gets the value of the update property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateType }
     *     
     */
    public UpdateType getUpdate() {
        return update;
    }

    /**
     * Sets the value of the update property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateType }
     *     
     */
    public void setUpdate(UpdateType value) {
        this.update = value;
    }

    /**
     * Gets the value of the arg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg() {
        return arg;
    }

    /**
     * Sets the value of the arg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg(String value) {
        this.arg = value;
    }

}
