package at.ac.tuwien.infosys.g2021.common.communication;

/**
 * This class contains the response of the daemon to a message sent.
 */
class Answer {

    private Object answer;

    /**
     * Creating an empty answer.
     */
    Answer() { this(null); }

    /**
     * Creating an answer.
     */
    Answer(Object a) { answer = a; }

    /**
     * Reading the answer.
     *
     * @return the answer
     */
    @SuppressWarnings(value = "unchecked")
    <T> T get() { return (T)answer; }
}

