//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.12 um 02:27:58 PM CET 
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
 *         &lt;element name="shutdown" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}ShutdownTag"/>
 *         &lt;element name="queryBuffersByName" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}QueryBuffersByNameTag"/>
 *         &lt;element name="queryBuffersByMetainfo" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}QueryBuffersByMetainfoTag"/>
 *         &lt;element name="bufferNames" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}BufferNamesTag"/>
 *         &lt;element name="queryMetainfo" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}QueryMetainfoTag"/>
 *         &lt;element name="bufferMetainfo" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}BufferMetainfoTag"/>
 *         &lt;element name="getBufferConfiguration" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}GetBufferConfigurationTag"/>
 *         &lt;element name="setBufferConfiguration" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}SetBufferConfigurationTag"/>
 *         &lt;element name="releaseBuffer" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}ReleaseBufferTag"/>
 *         &lt;element name="bufferConfiguration" type="{http://infosys.tu-wien.ac.at/g2021/schemas/communication}BufferConfigurationTag"/>
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
        "shutdown",
        "queryBuffersByName",
        "queryBuffersByMetainfo",
        "bufferNames",
        "queryMetainfo",
        "bufferMetainfo",
        "getBufferConfiguration",
        "setBufferConfiguration",
        "releaseBuffer",
        "bufferConfiguration",
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
    protected ShutdownTag shutdown;
    protected QueryBuffersByNameTag queryBuffersByName;
    protected QueryBuffersByMetainfoTag queryBuffersByMetainfo;
    protected BufferNamesTag bufferNames;
    protected QueryMetainfoTag queryMetainfo;
    protected BufferMetainfoTag bufferMetainfo;
    protected GetBufferConfigurationTag getBufferConfiguration;
    protected SetBufferConfigurationTag setBufferConfiguration;
    protected ReleaseBufferTag releaseBuffer;
    protected BufferConfigurationTag bufferConfiguration;
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
     * Ruft den Wert der shutdown-Eigenschaft ab.
     *
     * @return possible object is
     * {@link ShutdownTag }
     */
    public ShutdownTag getShutdown() {
        return shutdown;
    }

    /**
     * Legt den Wert der shutdown-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link ShutdownTag }
     */
    public void setShutdown(ShutdownTag value) {
        this.shutdown = value;
    }

    /**
     * Ruft den Wert der queryBuffersByName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QueryBuffersByNameTag }
     *     
     */
    public QueryBuffersByNameTag getQueryBuffersByName() {
        return queryBuffersByName;
    }

    /**
     * Legt den Wert der queryBuffersByName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryBuffersByNameTag }
     *     
     */
    public void setQueryBuffersByName(QueryBuffersByNameTag value) {
        this.queryBuffersByName = value;
    }

    /**
     * Ruft den Wert der queryBuffersByMetainfo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QueryBuffersByMetainfoTag }
     *     
     */
    public QueryBuffersByMetainfoTag getQueryBuffersByMetainfo() {
        return queryBuffersByMetainfo;
    }

    /**
     * Legt den Wert der queryBuffersByMetainfo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryBuffersByMetainfoTag }
     *     
     */
    public void setQueryBuffersByMetainfo(QueryBuffersByMetainfoTag value) {
        this.queryBuffersByMetainfo = value;
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
     * Ruft den Wert der queryMetainfo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QueryMetainfoTag }
     *     
     */
    public QueryMetainfoTag getQueryMetainfo() {
        return queryMetainfo;
    }

    /**
     * Legt den Wert der queryMetainfo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryMetainfoTag }
     *     
     */
    public void setQueryMetainfo(QueryMetainfoTag value) {
        this.queryMetainfo = value;
    }

    /**
     * Ruft den Wert der bufferMetainfo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BufferMetainfoTag }
     *     
     */
    public BufferMetainfoTag getBufferMetainfo() {
        return bufferMetainfo;
    }

    /**
     * Legt den Wert der bufferMetainfo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BufferMetainfoTag }
     *     
     */
    public void setBufferMetainfo(BufferMetainfoTag value) {
        this.bufferMetainfo = value;
    }

    /**
     * Ruft den Wert der getBufferConfiguration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GetBufferConfigurationTag }
     *     
     */
    public GetBufferConfigurationTag getGetBufferConfiguration() {
        return getBufferConfiguration;
    }

    /**
     * Legt den Wert der getBufferConfiguration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GetBufferConfigurationTag }
     *     
     */
    public void setGetBufferConfiguration(GetBufferConfigurationTag value) {
        this.getBufferConfiguration = value;
    }

    /**
     * Ruft den Wert der setBufferConfiguration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SetBufferConfigurationTag }
     *     
     */
    public SetBufferConfigurationTag getSetBufferConfiguration() {
        return setBufferConfiguration;
    }

    /**
     * Legt den Wert der setBufferConfiguration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SetBufferConfigurationTag }
     *     
     */
    public void setSetBufferConfiguration(SetBufferConfigurationTag value) {
        this.setBufferConfiguration = value;
    }

    /**
     * Ruft den Wert der releaseBuffer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReleaseBufferTag }
     *     
     */
    public ReleaseBufferTag getReleaseBuffer() {
        return releaseBuffer;
    }

    /**
     * Legt den Wert der releaseBuffer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReleaseBufferTag }
     *     
     */
    public void setReleaseBuffer(ReleaseBufferTag value) {
        this.releaseBuffer = value;
    }

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
