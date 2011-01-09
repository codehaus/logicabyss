//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3-hudson-jaxb-ri-2.2-70- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.01.09 at 07:26:06 PM CET 
//


package naffolog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Naf.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Naf.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.ruleml.org/1.0/xsd}Naf.content"/>
 *       &lt;attGroup ref="{http://www.ruleml.org/1.0/xsd}Naf.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Naf.type", propOrder = {
    "oid",
    "weak",
    "atom",
    "and",
    "or",
    "neg",
    "implies",
    "equivalent",
    "forall",
    "exists"
})
public class NafType {

    protected OidType oid;
    protected WeakType weak;
    @XmlElement(name = "Atom")
    protected AtomType atom;
    @XmlElement(name = "And")
    protected AndInnerType and;
    @XmlElement(name = "Or")
    protected OrInnerType or;
    @XmlElement(name = "Neg")
    protected NegType neg;
    @XmlElement(name = "Implies")
    protected ImpliesType implies;
    @XmlElement(name = "Equivalent")
    protected EquivalentType equivalent;
    @XmlElement(name = "Forall")
    protected ForallType forall;
    @XmlElement(name = "Exists")
    protected ExistsType exists;
    @XmlAttribute(name = "mapMaterial")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mapMaterial;
    @XmlAttribute(name = "mapClosure")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mapClosure;
    @XmlAttribute(name = "mapDirection")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mapDirection;

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
     * Gets the value of the weak property.
     * 
     * @return
     *     possible object is
     *     {@link WeakType }
     *     
     */
    public WeakType getWeak() {
        return weak;
    }

    /**
     * Sets the value of the weak property.
     * 
     * @param value
     *     allowed object is
     *     {@link WeakType }
     *     
     */
    public void setWeak(WeakType value) {
        this.weak = value;
    }

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

    /**
     * Gets the value of the neg property.
     * 
     * @return
     *     possible object is
     *     {@link NegType }
     *     
     */
    public NegType getNeg() {
        return neg;
    }

    /**
     * Sets the value of the neg property.
     * 
     * @param value
     *     allowed object is
     *     {@link NegType }
     *     
     */
    public void setNeg(NegType value) {
        this.neg = value;
    }

    /**
     * Gets the value of the implies property.
     * 
     * @return
     *     possible object is
     *     {@link ImpliesType }
     *     
     */
    public ImpliesType getImplies() {
        return implies;
    }

    /**
     * Sets the value of the implies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImpliesType }
     *     
     */
    public void setImplies(ImpliesType value) {
        this.implies = value;
    }

    /**
     * Gets the value of the equivalent property.
     * 
     * @return
     *     possible object is
     *     {@link EquivalentType }
     *     
     */
    public EquivalentType getEquivalent() {
        return equivalent;
    }

    /**
     * Sets the value of the equivalent property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquivalentType }
     *     
     */
    public void setEquivalent(EquivalentType value) {
        this.equivalent = value;
    }

    /**
     * Gets the value of the forall property.
     * 
     * @return
     *     possible object is
     *     {@link ForallType }
     *     
     */
    public ForallType getForall() {
        return forall;
    }

    /**
     * Sets the value of the forall property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForallType }
     *     
     */
    public void setForall(ForallType value) {
        this.forall = value;
    }

    /**
     * Gets the value of the exists property.
     * 
     * @return
     *     possible object is
     *     {@link ExistsType }
     *     
     */
    public ExistsType getExists() {
        return exists;
    }

    /**
     * Sets the value of the exists property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExistsType }
     *     
     */
    public void setExists(ExistsType value) {
        this.exists = value;
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

}
