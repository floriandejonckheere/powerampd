package be.thalarion.android.powerampd.command;

import java.util.List;

import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public abstract class SpecificCommand implements Command {

    protected final List<String> cmdline;
    private final Permission permission;
    private final int minArgs;
    private final int maxArgs;

    public SpecificCommand(List<String> cmdline, Permission permission, int minArgs, int maxArgs)
            throws ProtocolException {
        this.cmdline = cmdline;
        this.permission = permission;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;

        if (this.minArgs == this.maxArgs && (cmdline.size() - 1) != this.minArgs) {
            throw new ProtocolException(ProtocolException.ACK_ERROR_ARG,
                    String.format("wrong number of arguments for \"%s\"", cmdline.get(0)));
        } else if ((cmdline.size() - 1) < this.minArgs) {
            throw new ProtocolException(ProtocolException.ACK_ERROR_ARG,
                    String.format("too few arguments for \"%s\"", cmdline.get(0)));
        } else if ((cmdline.size() - 1) > this.maxArgs && this.maxArgs != 0) {
            throw new ProtocolException(ProtocolException.ACK_ERROR_ARG,
                    String.format("too many arguments for \"%s\"", cmdline.get(0)));
        }
    }

    @Override
    public void execute(Handle handle)
            throws ProtocolException {
        handle.authorize(permission);
    }
}
