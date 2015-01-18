//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.12 um 02:27:58 PM CET 
//


package at.ac.tuwien.infosys.g2021.common.communication.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r GathererTag complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="GathererTag">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="dummy" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}DummyGathererTag"/>
 *         &lt;element name="test" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}TestGathererTag"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GathererTag", propOrder = {
        "dummy",
        "test"
})
public class GathererTag {

    protected DummyGathererTag dummy;
    protected TestGathererTag test;

    /**
     * Ruft den Wert der dummy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DummyGathererTag }
     *     
     */
    public DummyGathererTag getDummy() {
        return dummy;
    }

    /**
     * Legt den Wert der dummy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DummyGathererTag }
     *     
     */
    public void setDummy(DummyGathererTag value) {
        this.dummy = value;
    }

    /**
     * Ruft den Wert der test-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TestGathererTag }
     *     
     */
    public TestGathererTag getTest() {
        return test;
    }

    /**
     * Legt den Wert der test-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TestGathererTag }
     *     
     */
    public void setTest(TestGathererTag value) {
        this.test = value;
    }
}
