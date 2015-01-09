package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.communication.jaxb.Message;
import at.ac.tuwien.infosys.g2021.common.util.Loggers;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;

/**
 * Here is the implementation for reading XML messages from a stream and writing XML messages to a stream.
 */
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
}
