package at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo;

import at.ac.tuwien.infosys.g2021.daemon.PortClass;

/** This enum contains all the wellknown ports. */
enum PortSet {

    /** The output ports. */
    DO51 {
        @Override
        ChipSet fromChip() { return ChipSet.DO51; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 0); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 0); }
    },
    DO52 {
        @Override
        ChipSet fromChip() { return ChipSet.DO51; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 1); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 1); }
    },
    DO53 {
        @Override
        ChipSet fromChip() { return ChipSet.DO51; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 2); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 2); }
    },
    DO54 {
        @Override
        ChipSet fromChip() { return ChipSet.DO51; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 3); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 3); }
    },
    DO55 {
        @Override
        ChipSet fromChip() { return ChipSet.DO51; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 4); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 4); }
    },
    DO56 {
        @Override
        ChipSet fromChip() { return ChipSet.DO51; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 5); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 5); }
    },
    DO57 {
        @Override
        ChipSet fromChip() { return ChipSet.DO51; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 6); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 6); }
    },
    DO58 {
        @Override
        ChipSet fromChip() { return ChipSet.DO51; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 7); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 7); }
    },
    DO61 {
        @Override
        ChipSet fromChip() { return ChipSet.DO61; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 0); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 0); }
    },
    DO62 {
        @Override
        ChipSet fromChip() { return ChipSet.DO61; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 1); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 1); }
    },
    DO63 {
        @Override
        ChipSet fromChip() { return ChipSet.DO61; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 2); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 2); }
    },
    DO64 {
        @Override
        ChipSet fromChip() { return ChipSet.DO61; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 3); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 3); }
    },
    DO65 {
        @Override
        ChipSet fromChip() { return ChipSet.DO61; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 4); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 4); }
    },
    DO66 {
        @Override
        ChipSet fromChip() { return ChipSet.DO61; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 5); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 5); }
    },
    DO67 {
        @Override
        ChipSet fromChip() { return ChipSet.DO61; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 6); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 6); }
    },
    DO68 {
        @Override
        ChipSet fromChip() { return ChipSet.DO61; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 7); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 7); }
    },
    DO71 {
        @Override
        ChipSet fromChip() { return ChipSet.DO71; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 0); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 0); }
    },
    DO72 {
        @Override
        ChipSet fromChip() { return ChipSet.DO71; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 1); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 1); }
    },
    DO73 {
        @Override
        ChipSet fromChip() { return ChipSet.DO71; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 2); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 2); }
    },
    DO74 {
        @Override
        ChipSet fromChip() { return ChipSet.DO71; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 3); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 3); }
    },
    DO75 {
        @Override
        ChipSet fromChip() { return ChipSet.DO71; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 4); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 4); }
    },
    DO76 {
        @Override
        ChipSet fromChip() { return ChipSet.DO71; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 5); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 5); }
    },
    DO77 {
        @Override
        ChipSet fromChip() { return ChipSet.DO71; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 6); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 6); }
    },
    DO78 {
        @Override
        ChipSet fromChip() { return ChipSet.DO71; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 7); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 7); }
    },
    DO81 {
        @Override
        ChipSet fromChip() { return ChipSet.DO81; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 0); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 0); }
    },
    DO82 {
        @Override
        ChipSet fromChip() { return ChipSet.DO81; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 1); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 1); }
    },
    DO83 {
        @Override
        ChipSet fromChip() { return ChipSet.DO81; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 2); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 2); }
    },
    DO84 {
        @Override
        ChipSet fromChip() { return ChipSet.DO81; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 3); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 3); }
    },
    DO85 {
        @Override
        ChipSet fromChip() { return ChipSet.DO81; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 4); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 4); }
    },
    DO86 {
        @Override
        ChipSet fromChip() { return ChipSet.DO81; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 5); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 5); }
    },
    DO87 {
        @Override
        ChipSet fromChip() { return ChipSet.DO81; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 6); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 6); }
    },
    DO88 {
        @Override
        ChipSet fromChip() { return ChipSet.DO81; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_OUTPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 7); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 7); }
    },

    /** The input ports. */
    DI11 {
        @Override
        ChipSet fromChip() { return ChipSet.DI11; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 0); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 0); }
    },
    DI12 {
        @Override
        ChipSet fromChip() { return ChipSet.DI11; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 1); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 1); }
    },
    DI13 {
        @Override
        ChipSet fromChip() { return ChipSet.DI11; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 2); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 2); }
    },
    DI14 {
        @Override
        ChipSet fromChip() { return ChipSet.DI11; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 3); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 3); }
    },
    DI15 {
        @Override
        ChipSet fromChip() { return ChipSet.DI11; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 4); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 4); }
    },
    DI16 {
        @Override
        ChipSet fromChip() { return ChipSet.DI11; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 5); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 5); }
    },
    DI17 {
        @Override
        ChipSet fromChip() { return ChipSet.DI11; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 6); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 6); }
    },
    DI18 {
        @Override
        ChipSet fromChip() { return ChipSet.DI11; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 7); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 7); }
    },
    DI21 {
        @Override
        ChipSet fromChip() { return ChipSet.DI21; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 0); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 0); }
    },
    DI22 {
        @Override
        ChipSet fromChip() { return ChipSet.DI21; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 1); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 1); }
    },
    DI23 {
        @Override
        ChipSet fromChip() { return ChipSet.DI21; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 2); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 2); }
    },
    DI24 {
        @Override
        ChipSet fromChip() { return ChipSet.DI21; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 3); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 3); }
    },
    DI25 {
        @Override
        ChipSet fromChip() { return ChipSet.DI21; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 4); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 4); }
    },
    DI26 {
        @Override
        ChipSet fromChip() { return ChipSet.DI21; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 5); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 5); }
    },
    DI27 {
        @Override
        ChipSet fromChip() { return ChipSet.DI21; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 6); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 6); }
    },
    DI28 {
        @Override
        ChipSet fromChip() { return ChipSet.DI21; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 7); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 7); }
    },
    DI31 {
        @Override
        ChipSet fromChip() { return ChipSet.DI31; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 0); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 0); }
    },
    DI32 {
        @Override
        ChipSet fromChip() { return ChipSet.DI31; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 1); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 1); }
    },
    DI33 {
        @Override
        ChipSet fromChip() { return ChipSet.DI31; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 2); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 2); }
    },
    DI34 {
        @Override
        ChipSet fromChip() { return ChipSet.DI31; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 3); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 3); }
    },
    DI35 {
        @Override
        ChipSet fromChip() { return ChipSet.DI31; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 4); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 4); }
    },
    DI36 {
        @Override
        ChipSet fromChip() { return ChipSet.DI31; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 5); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 5); }
    },
    DI37 {
        @Override
        ChipSet fromChip() { return ChipSet.DI31; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 6); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 6); }
    },
    DI38 {
        @Override
        ChipSet fromChip() { return ChipSet.DI31; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 7); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 7); }
    },
    DI41 {
        @Override
        ChipSet fromChip() { return ChipSet.DI41; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 0); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 0); }
    },
    DI42 {
        @Override
        ChipSet fromChip() { return ChipSet.DI41; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 1); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 1); }
    },
    DI43 {
        @Override
        ChipSet fromChip() { return ChipSet.DI41; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 2); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 2); }
    },
    DI44 {
        @Override
        ChipSet fromChip() { return ChipSet.DI41; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 3); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 3); }
    },
    DI45 {
        @Override
        ChipSet fromChip() { return ChipSet.DI41; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 4); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 4); }
    },
    DI46 {
        @Override
        ChipSet fromChip() { return ChipSet.DI41; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 5); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 5); }
    },
    DI47 {
        @Override
        ChipSet fromChip() { return ChipSet.DI41; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 6); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 6); }
    },
    DI48 {
        @Override
        ChipSet fromChip() { return ChipSet.DI41; }

        @Override
        PortClass portClass() { return PortClass.DIGITAL_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return rawValueToNumber(value, 7); }

        @Override
        Integer numberToRawValue(int read, Number value) { return numberToRawValue(read, value, 7); }
    },
    AI11 {
        @Override
        ChipSet fromChip() { return ChipSet.AI11; }

        @Override
        String unit() { return "%"; }

        @Override
        PortClass portClass() { return PortClass.ANALOG_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return 100.0 * linearize(value / 255.0, ai11Linearization); }
    },
    AI12 {
        @Override
        ChipSet fromChip() { return ChipSet.AI12; }

        @Override
        String unit() { return "%"; }

        @Override
        PortClass portClass() { return PortClass.ANALOG_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return 100.0 * linearize(value / 255.0, ai12Linearization); }
    },
    AI13 {
        @Override
        ChipSet fromChip() { return ChipSet.AI13; }

        @Override
        String unit() { return "%"; }

        @Override
        PortClass portClass() { return PortClass.ANALOG_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return 100.0 * linearize(value / 255.0, ai13Linearization); }
    },
    AI21 {
        @Override
        ChipSet fromChip() { return ChipSet.AI21; }

        @Override
        String unit() { return "°C"; }

        @Override
        PortClass portClass() { return PortClass.ANALOG_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return 70.0 * linearize(value / 255.0, ai21Linearization) - 20.0; }
    },
    AI22 {
        @Override
        ChipSet fromChip() { return ChipSet.AI22; }

        @Override
        String unit() { return "°C"; }

        @Override
        PortClass portClass() { return PortClass.ANALOG_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return 70.0 * linearize(value / 255.0, ai22Linearization) - 20.0; }
    },
    AI23 {
        @Override
        ChipSet fromChip() { return ChipSet.AI23; }

        @Override
        String unit() { return "%"; }

        @Override
        PortClass portClass() { return PortClass.ANALOG_INPUT; }

        @Override
        Number rawValueToNumber(int value) { return 100.0 * linearize(value / 255.0, ai23Linearization); }
    };

    // This are constant arrays used for linearization of analog input values.
    protected static double ai11Linearization[] = new double[] {0.0, 0.004, 0.024, 0.050, 0.075, 0.145, 0.168, 0.208, 0.337, 0.463, 0.508, 0.730, 0.810, 1.0, 1.0};
    protected static double ai12Linearization[] = new double[] {0.0, 0.004, 0.024, 0.050, 0.075, 0.100, 0.125, 0.157, 0.255, 0.412, 0.505, 0.660, 0.788, 1.0, 1.0};
    protected static double ai13Linearization[] = new double[] {0.0, 0.004, 0.020, 0.040, 0.060, 0.075, 0.090, 0.120, 0.275, 0.396, 0.570, 0.700, 0.820, 1.0, 1.0};
    protected static double ai21Linearization[] = new double[] {0.0, 0.004, 0.024, 0.050, 0.075, 0.100, 0.125, 0.157, 0.255, 0.412, 0.505, 0.660, 0.788, 1.0, 1.0};
    protected static double ai22Linearization[] = new double[] {0.0, 0.004, 0.024, 0.050, 0.075, 0.100, 0.125, 0.157, 0.255, 0.412, 0.505, 0.660, 0.788, 1.0, 1.0};
    protected static double ai23Linearization[] = new double[] {0.0, 0.004, 0.024, 0.050, 0.075, 0.100, 0.125, 0.157, 0.255, 0.412, 0.505, 0.660, 0.788, 1.0, 1.0};

    protected static double y[] = new double[] {0.0, 0.100, 0.200, 0.300, 0.400, 0.500, 0.600, 0.700, 0.750, 0.800, 0.850, 0.900, 0.950, 1.0, 1.0};

    /**
     * Returns the chip, where this port is located.
     *
     * @return the chip
     */
    abstract ChipSet fromChip();

    /**
     * Returns the port class of this port.
     *
     * @return the port class
     */
    abstract PortClass portClass();

    /**
     * Returns the unit as string.
     *
     * @return the unit or <tt>null</tt>, if there is no unit known
     */
    String unit() { return null; }

    /**
     * Converts a raw value to a Number-Object.
     *
     * @param value the raw value read
     *
     * @return the raw value as number
     */
    abstract Number rawValueToNumber(int value);

    /**
     * Converts a raw value to a Number-Object.
     *
     * @param value the raw value read
     * @param shift the bit offset from the LSB
     *
     * @return the raw value as number
     */
    protected Number rawValueToNumber(int value, int shift) {

        if (shift > 0) value >>>= shift;
        return value & 0x00000001;
    }

    /**
     * Linearization of an analog value.
     *
     * @param value the value read
     * @param table the linearization table
     *
     * @return the linearized value
     */
    protected double linearize(double value, double[] table) {

        double u;
        double result;

        for (int i = 1; i < table.length; i++) {

            if (value <= table[i]) {

                u = (value - table[i - 1]) / (table[i] - table[i - 1]);
                result = u * (y[i] - y[i - 1]) + y[i - 1];

                return result;
            }
        }

        return 1.0;
    }

    /**
     * Converts a Number to a raw value.
     *
     * @param read  the last value read
     * @param value the Number
     *
     * @return the raw value as Integer or <tt>null</tt>, if this conversion isn't possible
     */
    Integer numberToRawValue(int read, Number value) { return null; }

    /**
     * Converts a Number to a raw value.
     *
     * @param read  the last value read
     * @param value the Number
     * @param shift the bit offset from the LSB
     *
     * @return the raw value as Integer or <tt>null</tt>, if this conversion isn't possible
     */
    protected Integer numberToRawValue(int read, Number value, int shift) {

        int mask = 0x00000001;

        if (shift > 0) mask <<= shift;

        if (Math.round(value.floatValue()) == 0) return read & ~mask;
        else return read | mask;
    }
}

