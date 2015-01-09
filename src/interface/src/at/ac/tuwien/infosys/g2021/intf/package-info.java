/**
 * <p>
 *     This package contains the Java interface to the driver layer used in the software-defined gateway G2021.
 *     It implements the access to sensor / actor data in an uniform way and supports the configuration of the
 *     low level interface to the device drivers (so called buffers).
 * </p>
 *
 * <p>
 *     The basic interface to actors and sensor data is implemented in the class <tt>{@link at.ac.tuwien.infosys.g2021.intf.DataPoint}</tt>. An
 *     instance of the class <tt>{@link at.ac.tuwien.infosys.g2021.intf.DataPoint}</tt> is simply an interface to all the buffer values needed by
 *     an higher level application (so called GBot).
 * </p>
 * <p>
 *     As requested time triggered retrieval of all the buffer values is implemented in the class
 *     <tt>{@link at.ac.tuwien.infosys.g2021.intf.TimeControl}</tt>. A simple timer is used, to retrieve the buffer
 *     values (method <tt>{@link at.ac.tuwien.infosys.g2021.intf.DataPoint#getAll()}</tt>) and put them into a blocking queue. The timer can be started,
 *     stopped and restarted. State changes of buffers or transient value changes have no effect to the content of the output queue or the
 *     mode of timer operation. Therefore this kind of data retrieval is not recommended.
 * </p>
 * <p>
 *     The management of the actors and sensors (so called buffers) is an issue of the class <tt>{@link at.ac.tuwien.infosys.g2021.intf.BufferManager}</tt>.
 *     For further information look at the <tt>{@link at.ac.tuwien.infosys.g2021.intf.BufferManager}s</tt> class description.
 * </p>
 */
package at.ac.tuwien.infosys.g2021.intf;
