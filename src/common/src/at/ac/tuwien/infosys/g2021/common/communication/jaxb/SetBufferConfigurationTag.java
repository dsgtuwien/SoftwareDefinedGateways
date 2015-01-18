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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r SetBufferConfigurationTag complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SetBufferConfigurationTag">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bufferConfiguration" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}BufferConfigurationTag"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="create" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetBufferConfigurationTag", propOrder = {
        "bufferConfiguration"
})
public class SetBufferConfigurationTag {

    @XmlElement(required = true)
    protected BufferConfigurationTag bufferConfiguration;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "create", required = true)
    protected boolean create;

    /**
     * Ruft den Wert der bufferConfiguration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BufferConfigurationTag }
     *     
     */
    public BufferConfigurationTag getBufferConfiguration() {
        return bufferConfiguration;
    }

    /**
     * Legt den Wert der bufferConfiguration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BufferConfigurationTag }
     *     
     */
    public void setBufferConfiguration(BufferConfigurationTag value) {
        this.bufferConfiguration = value;
    }

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
     * Ruft den Wert der create-Eigenschaft ab.
     * 
     */
    public boolean isCreate() {
        return create;
    }

    /**
     * Legt den Wert der create-Eigenschaft fest.
     * 
     */
    public void setCreate(boolean value) {
        this.create = value;
    }
}
