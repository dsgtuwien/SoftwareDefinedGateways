//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.04 um 02:07:43 PM CET 
//


package at.ac.tuwien.infosys.g2021.common.communication.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="establish" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}EstablishTag"/>
 *         &lt;element name="accepted" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}AcceptedTag"/>
 *         &lt;element name="rejected" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}RejectedTag"/>
 *         &lt;element name="disconnect" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}DisconnectTag"/>
 *         &lt;element name="queryBuffers" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}QueryBuffersTag"/>
 *         &lt;element name="bufferNames" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}BufferNamesTag"/>
 *         &lt;element name="queryMetaInfo" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}QueryMetaInfoTag"/>
 *         &lt;element name="bufferMetaInfo" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}BufferMetaInfoTag"/>
 *         &lt;element name="getImmediate" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}GetImmediateTag"/>
 *         &lt;element name="get" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}GetTag"/>
 *         &lt;element name="set" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}SetTag"/>
 *         &lt;element name="push" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}PushTag"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "establish",
        "accepted",
        "rejected",
        "disconnect",
        "queryBuffers",
        "bufferNames",
        "queryMetaInfo",
        "bufferMetaInfo",
        "getImmediate",
        "get",
        "set",
        "push"
})
@XmlRootElement(name = "message")
public class Message {

    protected EstablishTag establish;
    protected AcceptedTag accepted;
    protected RejectedTag rejected;
    protected DisconnectTag disconnect;
    protected QueryBuffersTag queryBuffers;
    protected BufferNamesTag bufferNames;
    protected QueryMetaInfoTag queryMetaInfo;
    protected BufferMetaInfoTag bufferMetaInfo;
    protected GetImmediateTag getImmediate;
    protected GetTag get;
    protected SetTag set;
    protected PushTag push;

    /**
     * Ruft den Wert der establish-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EstablishTag }
     *     
     */
    public EstablishTag getEstablish() {
        return establish;
    }

    /**
     * Legt den Wert der establish-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EstablishTag }
     *     
     */
    public void setEstablish(EstablishTag value) {
        this.establish = value;
    }

    /**
     * Ruft den Wert der accepted-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AcceptedTag }
     *     
     */
    public AcceptedTag getAccepted() {
        return accepted;
    }

    /**
     * Legt den Wert der accepted-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptedTag }
     *     
     */
    public void setAccepted(AcceptedTag value) {
        this.accepted = value;
    }

    /**
     * Ruft den Wert der rejected-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RejectedTag }
     *     
     */
    public RejectedTag getRejected() {
        return rejected;
    }

    /**
     * Legt den Wert der rejected-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RejectedTag }
     *     
     */
    public void setRejected(RejectedTag value) {
        this.rejected = value;
    }

    /**
     * Ruft den Wert der disconnect-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DisconnectTag }
     *     
     */
    public DisconnectTag getDisconnect() {
        return disconnect;
    }

    /**
     * Legt den Wert der disconnect-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DisconnectTag }
     *     
     */
    public void setDisconnect(DisconnectTag value) {
        this.disconnect = value;
    }

    /**
     * Ruft den Wert der queryBuffers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QueryBuffersTag }
     *     
     */
    public QueryBuffersTag getQueryBuffers() {
        return queryBuffers;
    }

    /**
     * Legt den Wert der queryBuffers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryBuffersTag }
     *     
     */
    public void setQueryBuffers(QueryBuffersTag value) {
        this.queryBuffers = value;
    }

    /**
     * Ruft den Wert der bufferNames-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BufferNamesTag }
     *     
     */
    public BufferNamesTag getBufferNames() {
        return bufferNames;
    }

    /**
     * Legt den Wert der bufferNames-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BufferNamesTag }
     *     
     */
    public void setBufferNames(BufferNamesTag value) {
        this.bufferNames = value;
    }

    /**
     * Ruft den Wert der queryMetaInfo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QueryMetaInfoTag }
     *     
     */
    public QueryMetaInfoTag getQueryMetaInfo() {
        return queryMetaInfo;
    }

    /**
     * Legt den Wert der queryMetaInfo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryMetaInfoTag }
     *     
     */
    public void setQueryMetaInfo(QueryMetaInfoTag value) {
        this.queryMetaInfo = value;
    }

    /**
     * Ruft den Wert der bufferMetaInfo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BufferMetaInfoTag }
     *     
     */
    public BufferMetaInfoTag getBufferMetaInfo() {
        return bufferMetaInfo;
    }

    /**
     * Legt den Wert der bufferMetaInfo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BufferMetaInfoTag }
     *     
     */
    public void setBufferMetaInfo(BufferMetaInfoTag value) {
        this.bufferMetaInfo = value;
    }

    /**
     * Ruft den Wert der getImmediate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GetImmediateTag }
     *     
     */
    public GetImmediateTag getGetImmediate() {
        return getImmediate;
    }

    /**
     * Legt den Wert der getImmediate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GetImmediateTag }
     *     
     */
    public void setGetImmediate(GetImmediateTag value) {
        this.getImmediate = value;
    }

    /**
     * Ruft den Wert der get-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GetTag }
     *     
     */
    public GetTag getGet() {
        return get;
    }

    /**
     * Legt den Wert der get-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GetTag }
     *     
     */
    public void setGet(GetTag value) {
        this.get = value;
    }

    /**
     * Ruft den Wert der set-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SetTag }
     *     
     */
    public SetTag getSet() {
        return set;
    }

    /**
     * Legt den Wert der set-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SetTag }
     *     
     */
    public void setSet(SetTag value) {
        this.set = value;
    }

    /**
     * Ruft den Wert der push-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PushTag }
     *     
     */
    public PushTag getPush() {
        return push;
    }

    /**
     * Legt den Wert der push-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PushTag }
     *     
     */
    public void setPush(PushTag value) {
        this.push = value;
    }

}
