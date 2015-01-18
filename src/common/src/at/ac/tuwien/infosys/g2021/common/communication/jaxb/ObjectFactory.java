//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.01.12 um 02:27:58 PM CET 
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
     * Create an instance of {@link ShutdownTag }
     *
     */
    public ShutdownTag createShutdownTag() {
        return new ShutdownTag();
    }

    /**
     * Create an instance of {@link QueryBuffersByNameTag }
     * 
     */
    public QueryBuffersByNameTag createQueryBuffersByNameTag() {
        return new QueryBuffersByNameTag();
    }

    /**
     * Create an instance of {@link QueryBuffersByMetainfoTag }
     * 
     */
    public QueryBuffersByMetainfoTag createQueryBuffersByMetainfoTag() {
        return new QueryBuffersByMetainfoTag();
    }

    /**
     * Create an instance of {@link BufferNamesTag }
     * 
     */
    public BufferNamesTag createBufferNamesTag() {
        return new BufferNamesTag();
    }

    /**
     * Create an instance of {@link QueryMetainfoTag }
     * 
     */
    public QueryMetainfoTag createQueryMetainfoTag() {
        return new QueryMetainfoTag();
    }

    /**
     * Create an instance of {@link BufferMetainfoTag }
     * 
     */
    public BufferMetainfoTag createBufferMetainfoTag() {
        return new BufferMetainfoTag();
    }

    /**
     * Create an instance of {@link GetBufferConfigurationTag }
     * 
     */
    public GetBufferConfigurationTag createGetBufferConfigurationTag() {
        return new GetBufferConfigurationTag();
    }

    /**
     * Create an instance of {@link SetBufferConfigurationTag }
     * 
     */
    public SetBufferConfigurationTag createSetBufferConfigurationTag() {
        return new SetBufferConfigurationTag();
    }

    /**
     * Create an instance of {@link ReleaseBufferTag }
     * 
     */
    public ReleaseBufferTag createReleaseBufferTag() {
        return new ReleaseBufferTag();
    }

    /**
     * Create an instance of {@link BufferConfigurationTag }
     * 
     */
    public BufferConfigurationTag createBufferConfigurationTag() {
        return new BufferConfigurationTag();
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
     * Create an instance of {@link AdapterTag }
     * 
     */
    public AdapterTag createAdapterTag() {
        return new AdapterTag();
    }

    /**
     * Create an instance of {@link FilteringAdapterTag }
     * 
     */
    public FilteringAdapterTag createFilteringAdapterTag() {
        return new FilteringAdapterTag();
    }

    /**
     * Create an instance of {@link MetainfoTag }
     * 
     */
    public MetainfoTag createMetainfoTag() {
        return new MetainfoTag();
    }

    /**
     * Create an instance of {@link GathererTag }
     * 
     */
    public GathererTag createGathererTag() {
        return new GathererTag();
    }

    /**
     * Create an instance of {@link TriggeringAdapterTag }
     * 
     */
    public TriggeringAdapterTag createTriggeringAdapterTag() {
        return new TriggeringAdapterTag();
    }

    /**
     * Create an instance of {@link LowpassAdapterTag }
     * 
     */
    public LowpassAdapterTag createLowpassAdapterTag() {
        return new LowpassAdapterTag();
    }

    /**
     * Create an instance of {@link ScalingAdapterTag }
     * 
     */
    public ScalingAdapterTag createScalingAdapterTag() {
        return new ScalingAdapterTag();
    }

    /**
     * Create an instance of {@link TestGathererTag }
     * 
     */
    public TestGathererTag createTestGathererTag() {
        return new TestGathererTag();
    }

    /**
     * Create an instance of {@link DummyGathererTag }
     * 
     */
    public DummyGathererTag createDummyGathererTag() {
        return new DummyGathererTag();
    }

    /**
     * Create an instance of {@link DummyAdapterTag }
     * 
     */
    public DummyAdapterTag createDummyAdapterTag() {
        return new DummyAdapterTag();
    }
}
