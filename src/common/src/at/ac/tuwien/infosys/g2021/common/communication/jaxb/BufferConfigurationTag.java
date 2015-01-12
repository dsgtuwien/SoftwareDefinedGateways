//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.09 um 07:33:28 PM CET 
//


package at.ac.tuwien.infosys.g2021.common.communication.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r BufferConfigurationTag complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="BufferConfigurationTag">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="gatherer" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}GathererTag"/>
 *         &lt;element name="adapter" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}AdapterTag" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="metainfo" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}MetainfoTag" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="kind" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BufferConfigurationTag", propOrder = {
        "gatherer",
        "adapter",
        "metainfo"
})
public class BufferConfigurationTag {

    @XmlElement(required = true)
    protected GathererTag gatherer;
    protected List<AdapterTag> adapter;
    protected List<MetainfoTag> metainfo;
    @XmlAttribute(name = "kind", required = true)
    protected String kind;

    /**
     * Ruft den Wert der gatherer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GathererTag }
     *     
     */
    public GathererTag getGatherer() {
        return gatherer;
    }

    /**
     * Legt den Wert der gatherer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GathererTag }
     *     
     */
    public void setGatherer(GathererTag value) {
        this.gatherer = value;
    }

    /**
     * Gets the value of the adapter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the adapter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdapter().add(newItem);
     * </pre>
     *
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdapterTag }
     *
     * 
     */
    public List<AdapterTag> getAdapter() {
        if (adapter == null) {
            adapter = new ArrayList<AdapterTag>();
        }
        return this.adapter;
    }

    /**
     * Gets the value of the metainfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metainfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetainfo().add(newItem);
     * </pre>
     *
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MetainfoTag }
     *
     * 
     */
    public List<MetainfoTag> getMetainfo() {
        if (metainfo == null) {
            metainfo = new ArrayList<MetainfoTag>();
        }
        return this.metainfo;
    }

    /**
     * Ruft den Wert der kind-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKind() {
        return kind;
    }

    /**
     * Legt den Wert der kind-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKind(String value) {
        this.kind = value;
    }
}
