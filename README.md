CONTENTS OF THIS FILE
---------------------

 * Introduction
 * Requirements
 * Installation
 * Configuration
 * Running the code
 * Build from source

INTRODUCTION
------------
This project implements a set of java libraries that provide programmatic access to sensors and actuators.
The software follows the client-server architecture: a Daemon process works as server and directly interfaces the underlying hardware.
The server exposes high level interfaces to access sensor data and actuators to the (local) clients that need them.

The libraries abstract the underlying hardware technology of sensors and actuators by means of the Data and Control points objects.

The libraries also implement the basic logic to create simple programs that runs in the software defined gateway, read and elaborate data from the sensors, and interact with the external world using the actuators.

Additional documentation can be found in the `protocols' folder, while examples of servers and clients can be found inside the `artifacts' folders.

REQUIREMENTS
------------
The libraries require Java 8 and the following additional jars:

 * minimal-json-0.9.1.jar. This package handles json formated information and can be downloaded from https://github.com/ralfstx/minimal-json.
   minimal-json-0.9.1.jar  is available under the terms of the MIT License.

* pi4j-core.jar. This packages implements a Java-Interface to the Raspberry Pi (RPi)  I2C-Bus and can be downloaded from http://pi4j.com/.
  pi4j-core.jar is available under the GNU General Lesser Public License (LGPL) version 3.0.

Additionally, JUnit (junit-4.10.jar) is required to run the test cases and can be downloaded from http://junit.org/.
JUnit is an Open Source Software and is released under the Eclipse Public License Version 1.0.

INSTALLATION
------------
There is no installation, just copy and run the distribution jars in your software defined gateway, e.g., the RPi board.

Note that a precompiled version of the libraries is available in the `artifacts' folder.

CONFIGURATION
-------------
Use the cfgTask.jar utility to configure your installation. The utility requires two configuration files in the JSON format.
The first specifies the unit of measure, and the second specifies the mappings between the low-level ports/pins and the abstract Data/Control points, 
and the pre-processing functions to transform low-level signals from sensors into data, and control commands to actuators inputs.

The distribution comes with a set of pre-implemented pre-processing functions.

Examples of configurations files are available in the `artifacts' folder.

RUNNING THE CODE
-------------
To run the Daemon use the start scripts provided in the `artifact' folder. To stop the daemon kill the corresponding java process, e.g., using on the RPi terminal Ctrl-C.

BUILD FROM SOURCE
-------------
To build the distribution from source use ANT and the provided build.xml file.
