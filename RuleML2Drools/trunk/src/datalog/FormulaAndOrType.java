//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.11.14 at 09:34:39 AM MEZ 
//


package datalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for formula-and-or.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="formula-and-or.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.ruleml.org/1.0/xsd}formula-and-or.content"/>
 *       &lt;attGroup ref="{http://www.ruleml.org/1.0/xsd}formula.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "formula-and-or.type", propOrder = {
    "atom",
    "and",
    "or"
})
public class FormulaAndOrType {

    @XmlElement(name = "Atom")
    protected AtomType atom;
    @XmlElement(name = "And")
    protected AndInnerType and;
    @XmlElement(name = "Or")
    protected OrInnerType or;

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
     * Gets the value of the and property.
     * 
     * @return
     *     possible object is
     *     {@link AndInnerType }
     *     
     */
    public AndInnerType getAnd() {
        return and;
    }

    /**
     * Sets the value of the and property.
     * 
     * @param value
     *     allowed object is
     *     {@link AndInnerType }
     *     
     */
    public void setAnd(AndInnerType value) {
        this.and = value;
    }

    /**
     * Gets the value of the or property.
     * 
     * @return
     *     possible object is
     *     {@link OrInnerType }
     *     
     */
    public OrInnerType getOr() {
        return or;
    }

    /**
     * Sets the value of the or property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrInnerType }
     *     
     */
    public void setOr(OrInnerType value) {
        this.or = value;
    }

}
