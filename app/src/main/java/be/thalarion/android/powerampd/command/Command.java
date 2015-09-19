package be.thalarion.android.powerampd.command;


import android.content.Context;

import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public abstract class Command implements Executable {

    protected final List<String> cmdline;
    private final Permission permission;
    private State state;

    public Command(List<String> cmdline, Permission permission) {
        this.cmdline = cmdline;
        this.permission = permission;
    }

    @Override
    public void execute(State state)
            throws ProtocolException {
        this.state = state;

        // Authorize command
        if (!state.authorize(permission))
            throw new ProtocolException(ProtocolException.ACK_ERROR_PERMISSION, cmdline.get(0),
                    String.format("you don't have permission for \"%s\"", cmdline.get(0)));

        executeCommand(state); // throws ProtocolException
    }

    public abstract void executeCommand(State state)
            throws ProtocolException;

    /**
     * Helper methods
     */

    protected void checkMinArguments(int minArgs)
            throws ProtocolException {
        if (cmdline != null)
            if ((cmdline.size() - 1) < minArgs)
                throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                        String.format("too few arguments for \"%s\"", cmdline.get(0)));
    }

    protected void checkMaxArguments(int maxArgs)
            throws ProtocolException {
        if (cmdline != null)
            if ((cmdline.size() - 1) > maxArgs)
                throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                        String.format("too many arguments for \"%s\"", cmdline.get(0)));
    }

    /**
     * checkArguments - check if command has a valid number of arguments
     * This method should be called first in executeCommand if the argument list
     * is limited to a certain range.
     * @param minArgs
     * @param maxArgs
     * @throws ProtocolException
     */
    protected void checkArguments(int minArgs, int maxArgs)
            throws ProtocolException {
        if (cmdline != null)
            if (minArgs == maxArgs && (cmdline.size() - 1) != minArgs) {
                throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                        String.format("wrong number of arguments for \"%s\"", cmdline.get(0)));
            } else if ((cmdline.size() - 1) < minArgs) {
                throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                        String.format("too few arguments for \"%s\"", cmdline.get(0)));
            } else if ((cmdline.size() - 1) > maxArgs && maxArgs != 0) {
                throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                        String.format("too many arguments for \"%s\"", cmdline.get(0)));
            }
    }

    /**
     * getBoolean - get a boolean value from the commandline
     * @param index commandline index (> 0 for arguments)
     * @return boolean
     * @throws ProtocolException
     */
    protected boolean getBoolean(int index)
            throws ProtocolException {
        if (cmdline.get(index).equals("0")) {
            return false;
        } else if (cmdline.get(index).equals("1")) {
            return true;
        } else
            throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                    state.getContext().getString(R.string.proto_error_arg_boolean, cmdline.get(index)));
    }

}
