package be.thalarion.android.powerampd;

import android.content.Context;

import java.util.List;

import be.thalarion.android.powerampd.Handle;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public abstract class Command {

    private final List<String> command;
    private final Permission permission;
    private final int minArgs;
    private final int maxArgs;

    public Command(List<String> command, Permission permission, int minArgs, int maxArgs)
            throws ProtocolException {
        this.command = command;
        this.permission = permission;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;

        if(this.minArgs == this.maxArgs && (command.size() - 1) != this.minArgs) {
            throw new ProtocolException(ProtocolException.ACK_ERROR_ARG,
                    String.format("wrong number of arguments for \"%s\"", command.get(0)));
        } else if((command.size() - 1) < this.minArgs) {
            throw new ProtocolException(ProtocolException.ACK_ERROR_ARG,
                    String.format("too few arguments for \"%s\"", command.get(0)));
        } else if((command.size() - 1) > this.maxArgs && this.maxArgs != 0) {
            throw new ProtocolException(ProtocolException.ACK_ERROR_ARG,
                    String.format("too many arguments for \"%s\"", command.get(0)));
        }
    }

    public abstract void execute(Handle handle, List<String> cmdline)
            throws ProtocolException;
}
