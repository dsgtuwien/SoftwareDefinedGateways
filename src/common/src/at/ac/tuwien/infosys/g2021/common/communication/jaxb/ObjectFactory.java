//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.04 um 02:07:43 PM CET 
//


package at.ac.tuwien.infosys.g2021.common.communication.jaxb;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the at.ac.tuwien.infosys.g2021.common.communication.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: at.ac.tuwien.infosys.g2021.common.communication.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Message }
     * 
     */
    public Message createMessage() {
        return new Message();
    }

    /**
     * Create an instance of {@link EstablishTag }
     * 
     */
    public EstablishTag createEstablishTag() {
        return new EstablishTag();
    }

    /**
     * Create an instance of {@link AcceptedTag }
     * 
     */
    public AcceptedTag createAcceptedTag() {
        return new AcceptedTag();
    }

    /**
     * Create an instance of {@link RejectedTag }
     * 
     */
    public RejectedTag createRejectedTag() {
        return new RejectedTag();
    }

    /**
     * Create an instance of {@link DisconnectTag }
     * 
     */
    public DisconnectTag createDisconnectTag() {
        return new DisconnectTag();
    }

    /**
     * Create an instance of {@link QueryBuffersTag }
     * 
     */
    public QueryBuffersTag createQueryBuffersTag() {
        return new QueryBuffersTag();
    }

    /**
     * Create an instance of {@link BufferNamesTag }
     * 
     */
    public BufferNamesTag createBufferNamesTag() {
        return new BufferNamesTag();
    }

    /**
     * Create an instance of {@link QueryMetaInfoTag }
     * 
     */
    public QueryMetaInfoTag createQueryMetaInfoTag() {
        return new QueryMetaInfoTag();
    }

    /**
     * Create an instance of {@link BufferMetaInfoTag }
     * 
     */
    public BufferMetaInfoTag createBufferMetaInfoTag() {
        return new BufferMetaInfoTag();
    }

    /**
     * Create an instance of {@link GetImmediateTag }
     * 
     */
    public GetImmediateTag createGetImmediateTag() {
        return new GetImmediateTag();
    }

    /**
     * Create an instance of {@link GetTag }
     * 
     */
    public GetTag createGetTag() {
        return new GetTag();
    }

    /**
     * Create an instance of {@link SetTag }
     * 
     */
    public SetTag createSetTag() {
        return new SetTag();
    }

    /**
     * Create an instance of {@link PushTag }
     * 
     */
    public PushTag createPushTag() {
        return new PushTag();
    }

    /**
     * Create an instance of {@link MetaInfoTag }
     * 
     */
    public MetaInfoTag createMetaInfoTag() {
        return new MetaInfoTag();
    }

}
