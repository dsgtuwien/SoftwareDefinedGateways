//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.04 um 02:07:43 PM CET 
//


package at.ac.tuwien.infosys.g2021.common.communication.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r BufferMetaInfoTag complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="BufferMetaInfoTag">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="metaInfo" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}MetaInfoTag" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BufferMetaInfoTag", propOrder = {
        "name",
        "metaInfo"
})
public class BufferMetaInfoTag {

    @XmlElement(required = true)
    protected String name;
    protected List<MetaInfoTag> metaInfo;

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the metaInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metaInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetaInfo().add(newItem);
     * </pre>
     *
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MetaInfoTag }
     *
     * 
     */
    public List<MetaInfoTag> getMetaInfo() {
        if (metaInfo == null) {
            metaInfo = new ArrayList<MetaInfoTag>();
        }
        return this.metaInfo;
    }
}
