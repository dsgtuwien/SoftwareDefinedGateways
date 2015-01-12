//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.09 um 07:33:28 PM CET 
//


package at.ac.tuwien.infosys.g2021.common.communication.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r AdapterTag complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AdapterTag">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="dummy" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}DummyAdapterTag"/>
 *         &lt;element name="scale" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}ScalingAdapterTag"/>
 *         &lt;element name="trigger" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}TriggeringAdapterTag"/>
 *         &lt;element name="lowpass" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}LowpassAdapterTag"/>
 *         &lt;element name="filter" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}FilteringAdapterTag"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdapterTag", propOrder = {
        "dummy",
        "scale",
        "trigger",
        "lowpass",
        "filter"
})
public class AdapterTag {

    protected DummyAdapterTag dummy;
    protected ScalingAdapterTag scale;
    protected TriggeringAdapterTag trigger;
    protected LowpassAdapterTag lowpass;
    protected FilteringAdapterTag filter;

    /**
     * Ruft den Wert der dummy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DummyAdapterTag }
     *     
     */
    public DummyAdapterTag getDummy() {
        return dummy;
    }

    /**
     * Legt den Wert der dummy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DummyAdapterTag }
     *     
     */
    public void setDummy(DummyAdapterTag value) {
        this.dummy = value;
    }

    /**
     * Ruft den Wert der scale-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ScalingAdapterTag }
     *     
     */
    public ScalingAdapterTag getScale() {
        return scale;
    }

    /**
     * Legt den Wert der scale-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ScalingAdapterTag }
     *     
     */
    public void setScale(ScalingAdapterTag value) {
        this.scale = value;
    }

    /**
     * Ruft den Wert der trigger-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TriggeringAdapterTag }
     *     
     */
    public TriggeringAdapterTag getTrigger() {
        return trigger;
    }

    /**
     * Legt den Wert der trigger-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TriggeringAdapterTag }
     *     
     */
    public void setTrigger(TriggeringAdapterTag value) {
        this.trigger = value;
    }

    /**
     * Ruft den Wert der lowpass-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LowpassAdapterTag }
     *     
     */
    public LowpassAdapterTag getLowpass() {
        return lowpass;
    }

    /**
     * Legt den Wert der lowpass-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LowpassAdapterTag }
     *     
     */
    public void setLowpass(LowpassAdapterTag value) {
        this.lowpass = value;
    }

    /**
     * Ruft den Wert der filter-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FilteringAdapterTag }
     *     
     */
    public FilteringAdapterTag getFilter() {
        return filter;
    }

    /**
     * Legt den Wert der filter-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FilteringAdapterTag }
     *     
     */
    public void setFilter(FilteringAdapterTag value) {
        this.filter = value;
    }
}
