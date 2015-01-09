package at.ac.tuwien.infosys.g2021.common.util;

import java.util.logging.Logger;

/**
 * A little factory, to get appropriate loggers.
 */
public class Loggers {

    /**
     * Provides the appropriate default logger for all objects in a package. The typical usage is:
     * <pre>
     * private final Logger LOGGER = Loggers.getLogger(this);
     * </pre>
     *
     * @param object an instance of the class, which uses the logger
     *
     * @return the appropriate logger
     */
    public static Logger getLogger(Object object) { return getLogger(object.getClass()); }

    /**
     * Provides the appropriate default logger for all classes in a package. The typical usage is:
     * <pre>
     * private static final Logger LOGGER = Loggers.getLogger(SomeClass.class);
     * </pre>
     *
     * @param clazz the class, which uses the logger
     *
     * @return the appropriate logger
     */
    public static Logger getLogger(Class<?> clazz) { return Logger.getLogger(clazz.getPackage().getName()); }
}
