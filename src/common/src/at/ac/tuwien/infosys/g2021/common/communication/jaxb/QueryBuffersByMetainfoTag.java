//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.12 um 02:27:58 PM CET 
//


package at.ac.tuwien.infosys.g2021.common.communication.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r QueryBuffersByMetainfoTag complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="QueryBuffersByMetainfoTag">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="topic" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="metainfo" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryBuffersByMetainfoTag")
public class QueryBuffersByMetainfoTag {

    @XmlAttribute(name = "topic", required = true)
    protected String topic;
    @XmlAttribute(name = "metainfo", required = true)
    protected String metainfo;

    /**
     * Ruft den Wert der topic-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Legt den Wert der topic-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTopic(String value) {
        this.topic = value;
    }

    /**
     * Ruft den Wert der metainfo-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetainfo() {
        return metainfo;
    }

    /**
     * Legt den Wert der metainfo-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetainfo(String value) {
        this.metainfo = value;
    }
}
