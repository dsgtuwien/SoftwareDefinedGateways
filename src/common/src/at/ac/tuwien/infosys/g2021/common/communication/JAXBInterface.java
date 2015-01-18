package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.AdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.DummyAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.FilteringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.GathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.LowpassAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.TestGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.TriggeringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.AdapterTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.BufferConfigurationTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.FilteringAdapterTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.GathererTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.LowpassAdapterTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.Message;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.MetainfoTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.ObjectFactory;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.ScalingAdapterTag;
import at.ac.tuwien.infosys.g2021.common.communication.jaxb.TriggeringAdapterTag;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import at.ac.tuwien.infosys.g2021.common.util.NotYetImplementedError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;

/** Here is the implementation for reading XML messages from a stream and writing XML messages to a stream. */
class JAXBInterface {

    // The logger
    private final static Logger logger = Loggers.getLogger(JAXBInterface.class);

    /** Object initialisation. */
    JAXBInterface() {}

    /**
     * Writing a message to an output stream.
     *
     * @param document        the message
     * @param xmlOutputStream the stream
     *
     * @throws JAXBException if the message
     */
    void writeXML(Message document, OutputStream xmlOutputStream) throws JAXBException {

        try {
            JAXBContext context = JAXBContext.newInstance(document.getClass().getPackage().getName());
            Marshaller m = context.createMarshaller();

            // Writing the message
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            m.marshal(document, xmlOutputStream);

            logger.finer("A XML message has been written to a stream.");
        }
        catch (Exception e) {
            throw new JAXBException(e);
        }
    }

    /**
     * Analyzing a received XML message.
     *
     * @param schema         the schema
     * @param xmlInputStream the input stream with the xml
     *
     * @return the received message
     *
     * @throws JAXBException if there was an illegal message on the input stream
     */
    @SuppressWarnings(value = "unchecked")
    Message readXML(Schema schema, InputStream xmlInputStream) throws JAXBException {

        ValidationEventCollector vec = new ValidationEventCollector();

        try {
            JAXBContext jc = JAXBContext.newInstance(Message.class.getPackage().getName());
            Unmarshaller u = jc.createUnmarshaller();
            u.setSchema(schema);
            u.setEventHandler(vec);

            Message result = (Message)u.unmarshal(xmlInputStream);
            logger.finer("A XML-message has been read.");
            return result;
        }
        finally {
            if (vec.hasEvents()) {

                logger.warning("Some problems has been occurred reading a XML message:");

                Logger rootLogger = Logger.getLogger("");
                for (ValidationEvent ve : vec.getEvents()) {

                    ValidationEventLocator vel = ve.getLocator();

                    int line = vel.getLineNumber();
                    int column = vel.getColumnNumber();
                    String msg = ve.getMessage();

                    rootLogger.info(String.format("\tline %d(%d): %s", line, column, msg));
                }
            }
        }
    }

    /**
     * Converts a buffer configuration XML message to a BufferConfiguration object.
     *
     * @param configuration the XML message
     *
     * @return the buffer configuration
     */
    BufferConfiguration configurationFromXML(BufferConfigurationTag configuration) {

        BufferConfiguration result = new BufferConfiguration();

        // Setting the buffer class
        result.setBufferClass(BufferClass.valueOf(configuration.getKind()));

        // Converting the gatherer
        GathererTag gatherer = configuration.getGatherer();
        if (gatherer.getDummy() != null) {
            result.setGatherer(new DummyGathererConfiguration());
        }
        else if (gatherer.getTest() != null) {
            result.setGatherer(new TestGathererConfiguration());
        }
        else {
            throw new NotYetImplementedError("unknown gatherer class received");
        }

        // Converting the adapter settings
        for (AdapterTag adapter : configuration.getAdapter()) {
            if (adapter.getDummy() != null) {
                result.getAdapterChain().add(new DummyAdapterConfiguration());
            }
            else if (adapter.getScale() != null) {

                ScalingAdapterTag scalingAdapter = adapter.getScale();

                result.getAdapterChain().add(new ScalingAdapterConfiguration(scalingAdapter.getA(),
                                                                             scalingAdapter.getB(),
                                                                             scalingAdapter.getC()));
            }
            else if (adapter.getTrigger() != null) {

                TriggeringAdapterTag triggeringAdapter = adapter.getTrigger();

                result.getAdapterChain().add(new TriggeringAdapterConfiguration(triggeringAdapter.getLowerThreshold(),
                                                                                triggeringAdapter.getUpperThreshold(),
                                                                                triggeringAdapter.getLowerValue(),
                                                                                triggeringAdapter.getUpperValue()));
            }
            else if (adapter.getLowpass() != null) {

                LowpassAdapterTag lowpassAdapter = adapter.getLowpass();

                result.getAdapterChain().add(new LowpassAdapterConfiguration(lowpassAdapter.getInterpolationFactor()));
            }
            else if (adapter.getFilter() != null) {

                FilteringAdapterTag filteringAdapter = adapter.getFilter();

                result.getAdapterChain().add(new FilteringAdapterConfiguration(filteringAdapter.getMinimumDifference()));
            }
            else {
                throw new NotYetImplementedError("unknown gatherer class received");
            }
        }

        // Converting the metainfo
        for (MetainfoTag metainfo : configuration.getMetainfo()) result.getMetainfo().put(metainfo.getName(), metainfo.getInfo());

        return result;
    }

    /**
     * Sends a "bufferConfiguration" message to the communication partner.
     *
     * @param configuration the buffer configuration
     *
     * @throws java.io.IOException if the send operation fails
     */
    BufferConfigurationTag configurationToXML(BufferConfiguration configuration) throws IOException {

        ObjectFactory objectFactory = new ObjectFactory();
        BufferConfigurationTag result = objectFactory.createBufferConfigurationTag();

        result.setKind(configuration.getBufferClass().name());

        // Converting the info about the gatherer.
        GathererTag gathererTag = objectFactory.createGathererTag();
        GathererConfiguration gathererConfiguration = configuration.getGatherer();

        switch (gathererConfiguration.kindOfGatherer()) {
            case DUMMY:
                gathererTag.setDummy(objectFactory.createDummyGathererTag());
                break;

            case TEST:
                gathererTag.setTest(objectFactory.createTestGathererTag());
                break;

            default:
                throw new NotYetImplementedError("unknown gatherer class: " + gathererConfiguration.kindOfGatherer());
        }
        result.setGatherer(gathererTag);

        // Converting the info about the adapter chain.
        for (AdapterConfiguration adapter : configuration.getAdapterChain()) {

            AdapterTag adapterTag = objectFactory.createAdapterTag();

            switch (adapter.kindOfAdapter()) {
                case DUMMY:
                    adapterTag.setDummy(objectFactory.createDummyAdapterTag());
                    break;

                case SCALE:
                    ScalingAdapterConfiguration scalingAdapterConfiguration = (ScalingAdapterConfiguration)adapter;
                    ScalingAdapterTag scalingAdapterTag = objectFactory.createScalingAdapterTag();

                    scalingAdapterTag.setA(scalingAdapterConfiguration.getA());
                    scalingAdapterTag.setB(scalingAdapterConfiguration.getB());
                    scalingAdapterTag.setC(scalingAdapterConfiguration.getC());

                    adapterTag.setScale(scalingAdapterTag);
                    break;

                case TRIGGER:
                    TriggeringAdapterConfiguration triggeringAdapterConfiguration = (TriggeringAdapterConfiguration)adapter;
                    TriggeringAdapterTag triggeringAdapterTag = objectFactory.createTriggeringAdapterTag();

                    triggeringAdapterTag.setLowerThreshold(triggeringAdapterConfiguration.getLowerThreshold());
                    triggeringAdapterTag.setUpperThreshold(triggeringAdapterConfiguration.getUpperThreshold());
                    triggeringAdapterTag.setLowerValue(triggeringAdapterConfiguration.getLowerOutput());
                    triggeringAdapterTag.setUpperValue(triggeringAdapterConfiguration.getUpperOutput());

                    adapterTag.setTrigger(triggeringAdapterTag);
                    break;

                case LOWPASS:
                    LowpassAdapterConfiguration lowpassAdapterConfiguration = (LowpassAdapterConfiguration)adapter;
                    LowpassAdapterTag lowpassAdapterTag = objectFactory.createLowpassAdapterTag();

                    lowpassAdapterTag.setInterpolationFactor(lowpassAdapterConfiguration.getInterpolationFactor());

                    adapterTag.setLowpass(lowpassAdapterTag);
                    break;

                case FILTER:
                    FilteringAdapterConfiguration filteringAdapterConfiguration = (FilteringAdapterConfiguration)adapter;
                    FilteringAdapterTag filteringAdapterTag = objectFactory.createFilteringAdapterTag();

                    filteringAdapterTag.setMinimumDifference(filteringAdapterConfiguration.getMinimumDifference());

                    adapterTag.setFilter(filteringAdapterTag);
                    break;

                default:
                    throw new NotYetImplementedError("unknown adapter class: " + adapter.kindOfAdapter());
            }

            result.getAdapter().add(adapterTag);
        }

        // Converting the buffer metainfo.
        metainfoToXML(configuration.getMetainfo(), result.getMetainfo());

        return result;
    }

    /**
     * Fills all the meta information of a buffer into a list of MetaInfo-Tags.
     *
     * @param metainfo the buffer meta information
     * @param tagList  the list of XML-tags
     */
    void metainfoToXML(Map<String, String> metainfo, List<MetainfoTag> tagList) {

        for (Map.Entry<String, String> entry : metainfo.entrySet()) {

            ObjectFactory objectFactory = new ObjectFactory();
            MetainfoTag metainfoTag = objectFactory.createMetainfoTag();

            metainfoTag.setName(entry.getKey());
            metainfoTag.setInfo(entry.getValue());

            tagList.add(metainfoTag);
        }
    }
}

