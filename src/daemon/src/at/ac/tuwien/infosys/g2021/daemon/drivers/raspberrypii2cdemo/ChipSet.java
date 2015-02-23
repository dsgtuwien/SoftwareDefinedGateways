package at.ac.tuwien.infosys.g2021.daemon.drivers.raspberrypii2cdemo;

/** There are a lot of chips mounted on the I²C bus. */
enum ChipSet {

    /** The input latches. */
    DI11 {
        @Override
        int getAddress() { return 0x00000020; }
    },
    DI21 {
        @Override
        int getAddress() { return 0x00000021; }
    },
    DI31 {
        @Override
        int getAddress() { return 0x00000022; }
    },
    DI41 {
        @Override
        int getAddress() { return 0x00000023; }
    },

    /** The output latches. */
    DO51 {
        @Override
        int getAddress() { return 0x00000038; }
    },
    DO61 {
        @Override
        int getAddress() { return 0x00000039; }
    },
    DO71 {
        @Override
        int getAddress() { return 0x0000003A; }
    },
    DO81 {
        @Override
        int getAddress() { return 0x0000003B; }
    },

    /** The multiplexer for the ADC chips */
    AIMUX {
        @Override
        int getAddress() { return 0x00000070; }
    },

    /** The ADC chips. Both groups have the same address, but they use different multiplexer settings. */
    AI11 {
        @Override
        int getAddress() { return 0x00000050; }

        @Override
        boolean isADC() { return true; }

        @Override
        int getMultiplexerValue() { return 0x00000005; }
    },
    AI12 {
        @Override
        int getAddress() { return 0x00000051; }

        @Override
        boolean isADC() { return true; }

        @Override
        int getMultiplexerValue() { return 0x00000005; }
    },
    AI13 {
        @Override
        int getAddress() { return 0x00000052; }

        @Override
        boolean isADC() { return true; }

        @Override
        int getMultiplexerValue() { return 0x00000005; }
    },
    AI21 {
        @Override
        int getAddress() { return 0x00000050; }

        @Override
        boolean isADC() { return true; }

        @Override
        int getMultiplexerValue() { return 0x00000004; }
    },
    AI22 {
        @Override
        int getAddress() { return 0x00000051; }

        @Override
        boolean isADC() { return true; }

        @Override
        int getMultiplexerValue() { return 0x00000004; }
    },
    AI23 {
        @Override
        int getAddress() { return 0x00000052; }

        @Override
        boolean isADC() { return true; }

        @Override
        int getMultiplexerValue() { return 0x00000004; }
    };

    /**
     * Returns the chip address on bus.
     *
     * @return the chip address
     */
    abstract int getAddress();

    /**
     * Is this chip an ADC?
     *
     * @return <tt>true</tt>, if this chip is an ADC
     */
    boolean isADC() { return false; }

    /**
     * Returns the address value for the ADC multiplexer.
     *
     * @return the multiplexer value or -1 if there is no multiplexing necessary.
     */
    int getMultiplexerValue() { return -1; }
}
