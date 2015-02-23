package at.ac.tuwien.infosys.g2021.common.communication;

import at.ac.tuwien.infosys.g2021.common.ActorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.AdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.BufferClass;
import at.ac.tuwien.infosys.g2021.common.BufferConfiguration;
import at.ac.tuwien.infosys.g2021.common.DummyAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.DummyGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.FilteringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.GathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.LowpassAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.ScalingAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.SensorGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.TestGathererConfiguration;
import at.ac.tuwien.infosys.g2021.common.TriggeringAdapterConfiguration;
import at.ac.tuwien.infosys.g2021.common.util.NotYetImplementedError;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;

/** Here are the name of all JSON properties used in the client daemon communication. */
final class JsonInterface {

    // All the property names.
    final static String ACCEPTED = "accepted";
    final static String ADAPTER = "adapter";
    final static String ARGUMENTS = "arguments";
    final static String BUFFER = "buffer";
    final static String BUFFER_CONFIGURATION = "bufferConfiguration";
    final static String BUFFER_NAMES = "bufferNames";
    final static String BUFFER_METAINFO = "bufferMetainfo";
    final static String CONFIGURATION = "configuration";
    final static String CREATE = "create";
    final static String DISCONNECT = "disconnect";
    final static String ESTABLISH = "establish";
    final static String GATHERER = "gatherer";
    final static String GET = "get";
    final static String GET_BUFFER_CONFIGURATION = "getBufferConfiguration";
    final static String GET_IMMEDIATE = "getImmediate";
    final static String IS_HARDWARE = "isHardware";
    final static String METAINFO = "metainfo";
    final static String NAME = "name";
    final static String PUSH = "push";
    final static String QUERY_BUFFER_BY_METAINFO = "queryBufferByMetainfo";
    final static String QUERY_BUFFER_BY_NAME = "queryBufferByName";
    final static String QUERY_METAINFO = "queryMetainfo";
    final static String REASON = "reason";
    final static String REJECTED = "rejected";
    final static String RELEASE_BUFFER = "releaseBuffer";
    final static String SET = "set";
    final static String SET_BUFFER_CONFIGURATION = "setBufferConfiguration";
    final static String SHUTDOWN = "shutdown";
    final static String SPONTANEOUS = "spontaneous";
    final static String STATE = "state";
    final static String TIMESTAMP = "timestamp";
    final static String TOPIC = "topic";
    final static String TYPE = "type";
    final static String VALUE = "value";
    final static String VERSION = "version";

    /**
     * Converts a JSON object into its string representation.
     *
     * @param object the JSON object
     *
     * @return the string representation
     */
    String stringFromJSON(JsonObject object) {

        // handling null arguments
        if (object == null) return JsonObject.NULL.asString();

        StringWriter writer = new StringWriter();
        try {
            object.writeTo(writer);
        }
        catch (IOException e) {
            // writing into a string buffer throws no exceptions!
        }
        writer.flush();

        return writer.toString();
    }

    /**
     * Converts a string into a JSON object.
     *
     * @param data a string representation of a JSON object
     *
     * @return the JSON object
     */
    JsonObject stringToJSON(String data) {

        JsonObject result = null;

        if (data != null) {

            StringReader reader = new StringReader(data);

            try {
                result = JsonObject.readFrom(reader);
            }
            catch (Exception e) {
                // An exception is thrown, if the string was not a representation of a JSON object
                throw new IllegalArgumentException("not a JSON string", e);
            }
        }

        return result;
    }

    /**
     * Converts a buffer configuration to a BufferConfiguration object.
     *
     * @param configuration the JSON object
     *
     * @return the buffer configuration
     */
    BufferConfiguration configurationFromJSON(JsonObject configuration) {

        BufferConfiguration result = new BufferConfiguration();

        // Setting the buffer class
        JsonObject buffer = configuration.get(BUFFER).asObject();
        result.setBufferClass(BufferClass.valueOf(buffer.get(TYPE).asString()));

        // Converting the adapter settings
        JsonArray adapters = configuration.get(ADAPTER).asArray();
        for (JsonValue a : adapters.values()) {

            JsonObject adapter = a.asObject();

            switch (adapter.get(TYPE).asString()) {
                case "DUMMY":
                    result.getAdapterChain().add(new DummyAdapterConfiguration());
                    break;

                case "SCALE":
                    result.getAdapterChain().add(new ScalingAdapterConfiguration(adapter.get("a").asDouble(),
                                                                                 adapter.get("b").asDouble(),
                                                                                 adapter.get("c").asDouble()));
                    break;

                case "TRIGGER":
                    result.getAdapterChain().add(new TriggeringAdapterConfiguration(adapter.get("lowerThreshold").asDouble(),
                                                                                    adapter.get("upperThreshold").asDouble(),
                                                                                    adapter.get("lowerValue").asDouble(),
                                                                                    adapter.get("upperValue").asDouble()));
                    break;

                case "LOWPASS":
                    result.getAdapterChain().add(new LowpassAdapterConfiguration(adapter.get("interpolationFactor").asDouble()));
                    break;

                case "FILTER":
                    result.getAdapterChain().add(new LowpassAdapterConfiguration(adapter.get("minimumDifference").asDouble()));
                    break;

                default:
                    throw new NotYetImplementedError("unknown adapter class received");
            }
        }

        // Converting the gatherer
        JsonObject gatherer = configuration.get(GATHERER).asObject();
        switch (gatherer.get(TYPE).asString()) {
            case "DUMMY":
                result.setGatherer(new DummyGathererConfiguration());
                break;

            case "TEST":
                result.setGatherer(new TestGathererConfiguration());
                break;

            case "ACTOR":
                result.setGatherer(new ActorGathererConfiguration(gatherer.get(NAME).asString()));
                break;

            case "SENSOR":
                result.setGatherer(new SensorGathererConfiguration(gatherer.get(NAME).asString()));
                break;

            default:
                throw new NotYetImplementedError("unknown gatherer class received");
        }

        // Converting the metainfo
        JsonArray metainfo = configuration.get(METAINFO).asArray();
        result.getMetainfo().putAll(metainfoFromJSON(metainfo));

        return result;
    }

    /**
     * Converts a "bufferConfiguration" message to a JSON data structure.
     *
     * @param configuration the buffer configuration
     *
     * @return the JSON object containing the configuration
     *
     * @throws java.io.IOException if the send operation fails
     */
    JsonObject configurationToJSON(BufferConfiguration configuration) throws IOException {

        JsonObject result = new JsonObject();

        // Converting the buffer type
        JsonObject buffer = new JsonObject();
        result.add(BUFFER, buffer);
        buffer.add(TYPE, configuration.getBufferClass().name());

        // Converting the info about the gatherer.
        GathererConfiguration gathererConfiguration = configuration.getGatherer();

        JsonObject gatherer = new JsonObject();
        result.add(GATHERER, gatherer);
        gatherer.add(TYPE, gathererConfiguration.kindOfGatherer().name());

        switch (gathererConfiguration.kindOfGatherer()) {
            case DUMMY:
            case TEST:
                // They need no additional Arguments.
                break;

            case ACTOR:
                gatherer.add(NAME, ((ActorGathererConfiguration)gathererConfiguration).getPortName());
                break;

            case SENSOR:
                gatherer.add(NAME, ((SensorGathererConfiguration)gathererConfiguration).getPortName());
                break;

            default:
                throw new NotYetImplementedError("unknown gatherer class: " + gathererConfiguration.kindOfGatherer());
        }

        // Converting the info about the adapter chain.
        JsonArray adapters = new JsonArray();
        result.add(ADAPTER, adapters);

        for (AdapterConfiguration adapter : configuration.getAdapterChain()) {

            JsonObject adapterObject = new JsonObject();
            adapterObject.add(TYPE, adapter.kindOfAdapter().name());

            switch (adapter.kindOfAdapter()) {
                case DUMMY:
                    break;

                case SCALE:

                    ScalingAdapterConfiguration scalingAdapterConfiguration = (ScalingAdapterConfiguration)adapter;

                    adapterObject.add("a", scalingAdapterConfiguration.getA());
                    adapterObject.add("b", scalingAdapterConfiguration.getB());
                    adapterObject.add("c", scalingAdapterConfiguration.getC());
                    break;

                case TRIGGER:

                    TriggeringAdapterConfiguration triggeringAdapterConfiguration = (TriggeringAdapterConfiguration)adapter;

                    adapterObject.add("lowerThreshold", triggeringAdapterConfiguration.getLowerThreshold());
                    adapterObject.add("upperThreshold", triggeringAdapterConfiguration.getUpperThreshold());
                    adapterObject.add("lowerValue", triggeringAdapterConfiguration.getLowerOutput());
                    adapterObject.add("upperValue", triggeringAdapterConfiguration.getUpperOutput());
                    break;

                case LOWPASS:

                    LowpassAdapterConfiguration lowpassAdapterConfiguration = (LowpassAdapterConfiguration)adapter;

                    adapterObject.add("interpolationFactor", lowpassAdapterConfiguration.getInterpolationFactor());
                    break;

                case FILTER:

                    FilteringAdapterConfiguration filteringAdapterConfiguration = (FilteringAdapterConfiguration)adapter;

                    adapterObject.add("minimumDifference", filteringAdapterConfiguration.getMinimumDifference());
                    break;

                default:
                    throw new NotYetImplementedError("unknown adapter class: " + adapter.kindOfAdapter());
            }

            adapters.add(adapterObject);
        }

        // Converting the buffer metainfo.
        result.add(METAINFO, metainfoToJSON(configuration.getMetainfo()));

        return result;
    }

    /**
     * Fills all the meta information of a buffer into an array of metainfo-objects.
     *
     * @param metainfo the buffer meta information
     *
     * @return a JSON array containing the meta info
     */
    JsonArray metainfoToJSON(Map<String, String> metainfo) {

        JsonArray metainfoArray = new JsonArray();

        if (metainfo != null) {
            for (Map.Entry<String, String> e : metainfo.entrySet()) {

                JsonObject entry = new JsonObject();

                entry.add(JsonInterface.TOPIC, e.getKey());
                entry.add(JsonInterface.METAINFO, e.getValue());
                metainfoArray.add(entry);
            }
        }

        return metainfoArray;
    }

    /**
     * Fills all the meta information of a buffer into a list of MetaInfo-Tags.
     *
     * @param metainfo the buffer meta information
     *
     * @return the map containing the metainfo
     */
    Map<String, String> metainfoFromJSON(JsonArray metainfo) {

        Map<String, String> result = new TreeMap<>();

        try {
            if (metainfo != null) {
                for (JsonValue e : metainfo.values()) {
                    JsonObject obj = e.asObject();
                    result.put(obj.get(TOPIC).asString(), obj.get(METAINFO).asString());
                }
            }
        }
        catch (Exception e) {
            // If anything goes wrong, this was not a metainfo array!
            throw new IllegalArgumentException("not a buffer metainfo");
        }

        return result;
    }

    // Initializes this instance
    JsonInterface() {}
}
