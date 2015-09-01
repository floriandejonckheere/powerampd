package be.thalarion.android.powerampd;

public interface Protocol {

    class Handshake implements Protocol {
        @Override
        public String toString() {
            return "OK MPD 0.19.0\n";
        }
    }

    class Completion implements Protocol {
        @Override
        public String toString() {
            return "OK\n";
        }
    }

    public class Error implements Protocol {
        public static final int UNKNOWN_COMMAND = 5;

        private final int error;
        private final int line;
        private final String command;
        private final String message;

        public Error(int error, int line, String command, String message) {
            this.error = error;
            this.line = line;
            this.command = command;
            this.message = message;
        }

        @Override
        public String toString() {
            return String.format("ACK [%d@%d] {} %s\n", error, line, message);
        }
    }
}
