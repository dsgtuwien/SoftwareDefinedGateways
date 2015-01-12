//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.09 um 07:33:28 PM CET 
//


package at.ac.tuwien.infosys.g2021.common.communication.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r TriggeringAdapterTag complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TriggeringAdapterTag">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="upperThreshold" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="lowerThreshold" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="upperValue" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="lowerValue" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TriggeringAdapterTag")
public class TriggeringAdapterTag {

    @XmlAttribute(name = "upperThreshold", required = true)
    protected double upperThreshold;
    @XmlAttribute(name = "lowerThreshold", required = true)
    protected double lowerThreshold;
    @XmlAttribute(name = "upperValue", required = true)
    protected double upperValue;
    @XmlAttribute(name = "lowerValue", required = true)
    protected double lowerValue;

    /**
     * Ruft den Wert der upperThreshold-Eigenschaft ab.
     * 
     */
    public double getUpperThreshold() {
        return upperThreshold;
    }

    /**
     * Legt den Wert der upperThreshold-Eigenschaft fest.
     * 
     */
    public void setUpperThreshold(double value) {
        this.upperThreshold = value;
    }

    /**
     * Ruft den Wert der lowerThreshold-Eigenschaft ab.
     * 
     */
    public double getLowerThreshold() {
        return lowerThreshold;
    }

    /**
     * Legt den Wert der lowerThreshold-Eigenschaft fest.
     * 
     */
    public void setLowerThreshold(double value) {
        this.lowerThreshold = value;
    }

    /**
     * Ruft den Wert der upperValue-Eigenschaft ab.
     * 
     */
    public double getUpperValue() {
        return upperValue;
    }

    /**
     * Legt den Wert der upperValue-Eigenschaft fest.
     * 
     */
    public void setUpperValue(double value) {
        this.upperValue = value;
    }

    /**
     * Ruft den Wert der lowerValue-Eigenschaft ab.
     * 
     */
    public double getLowerValue() {
        return lowerValue;
    }

    /**
     * Legt den Wert der lowerValue-Eigenschaft fest.
     * 
     */
    public void setLowerValue(double value) {
        this.lowerValue = value;
    }
}
