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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Plex-repo.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Plex-repo.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.ruleml.org/1.0/xsd}Plex-repo.content"/>
 *       &lt;attGroup ref="{http://www.ruleml.org/1.0/xsd}Plex.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Plex-repo.type", propOrder = {
    "argOrIndOrData"
})
public class PlexRepoType {

    @XmlElements({
        @XmlElement(name = "arg", type = ArgType.class),
        @XmlElement(name = "Expr", type = ExprType.class),
        @XmlElement(name = "Skolem", type = SkolemType.class),
        @XmlElement(name = "Ind", type = IndType.class),
        @XmlElement(name = "Var", type = VarType.class),
        @XmlElement(name = "Plex", type = PlexType.class),
        @XmlElement(name = "Data"),
        @XmlElement(name = "repo", type = RepoType.class),
        @XmlElement(name = "Reify", type = ReifyType.class)
    })
    protected List<Object> argOrIndOrData;

    /**
     * Gets the value of the argOrIndOrData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the argOrIndOrData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArgOrIndOrData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArgType }
     * {@link ExprType }
     * {@link SkolemType }
     * {@link IndType }
     * {@link VarType }
     * {@link PlexType }
     * {@link Object }
     * {@link RepoType }
     * {@link ReifyType }
     * 
     * 
     */
    public List<Object> getArgOrIndOrData() {
        if (argOrIndOrData == null) {
            argOrIndOrData = new ArrayList<Object>();
        }
        return this.argOrIndOrData;
    }

}