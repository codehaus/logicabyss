//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3-hudson-jaxb-ri-2.2-70- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.01.09 at 07:26:06 PM CET 
//


package naffolog;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Assert.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Assert.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.ruleml.org/1.0/xsd}Assert.content"/>
 *       &lt;attGroup ref="{http://www.ruleml.org/1.0/xsd}Assert.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Assert.type", propOrder = {
    "oid",
    "formulaOrRulebaseOrAtom"
})
public class AssertType {

    protected OidType oid;
    @XmlElements({
        @XmlElement(name = "Exists", type = ExistsType.class),
        @XmlElement(name = "Equivalent", type = EquivalentType.class),
        @XmlElement(name = "Implies", type = ImpliesType.class),
        @XmlElement(name = "Atom", type = AtomType.class),
        @XmlElement(name = "Forall", type = ForallType.class),
        @XmlElement(name = "formula", type = FormulaAssertType.class),
        @XmlElement(name = "Entails", type = EntailsType.class),
        @XmlElement(name = "Neg", type = NegType.class),
        @XmlElement(name = "Or", type = OrInnerType.class),
        @XmlElement(name = "Rulebase", type = RulebaseType.class),
        @XmlElement(name = "And", type = AndInnerType.class)
    })
    protected List<Object> formulaOrRulebaseOrAtom;
    @XmlAttribute(name = "mapMaterial")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mapMaterial;
    @XmlAttribute(name = "mapDirection")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mapDirection;
    @XmlAttribute(name = "mapClosure")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mapClosure;

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
     * Gets the value of the formulaOrRulebaseOrAtom property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the formulaOrRulebaseOrAtom property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFormulaOrRulebaseOrAtom().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExistsType }
     * {@link EquivalentType }
     * {@link ImpliesType }
     * {@link AtomType }
     * {@link ForallType }
     * {@link FormulaAssertType }
     * {@link EntailsType }
     * {@link NegType }
     * {@link OrInnerType }
     * {@link RulebaseType }
     * {@link AndInnerType }
     * 
     * 
     */
    public List<Object> getFormulaOrRulebaseOrAtom() {
        if (formulaOrRulebaseOrAtom == null) {
            formulaOrRulebaseOrAtom = new ArrayList<Object>();
        }
        return this.formulaOrRulebaseOrAtom;
    }

    /**
     * Gets the value of the mapMaterial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapMaterial() {
        if (mapMaterial == null) {
            return "yes";
        } else {
            return mapMaterial;
        }
    }

    /**
     * Sets the value of the mapMaterial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapMaterial(String value) {
        this.mapMaterial = value;
    }

    /**
     * Gets the value of the mapDirection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapDirection() {
        if (mapDirection == null) {
            return "bidirectional";
        } else {
            return mapDirection;
        }
    }

    /**
     * Sets the value of the mapDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapDirection(String value) {
        this.mapDirection = value;
    }

    /**
     * Gets the value of the mapClosure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapClosure() {
        return mapClosure;
    }

    /**
     * Sets the value of the mapClosure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapClosure(String value) {
        this.mapClosure = value;
    }

}