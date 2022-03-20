package net.toadless.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)
    {
        RPC rpc = new RPC();
        LOGGER.debug("Beginning startup sequence.");
        try
        {
            rpc.start();
            LOGGER.debug("RPC started successfully.");
        }
        catch (Exception exception)
        {
            rpc.getLogger().error("An unexpected exception occurred", exception);
            System.exit(1);
        }
    }
}