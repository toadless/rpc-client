package net.toadless.rpc;

import net.toadless.discordrpc.*;

import net.toadless.rpc.config.ConfigOption;
import net.toadless.rpc.config.Configuration;
import net.toadless.rpc.handlers.TaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import java.util.concurrent.TimeUnit;

public class RPC
{
    private final Logger logger;
    private final LocalDateTime startTimestamp;
    private final Configuration configuration;
    private final TaskHandler taskHandler;
    private final DiscordEventHandlers handlers;
    private final DiscordRichPresence presence;

    public RPC()
    {
        printVanity();

        this.logger = LoggerFactory.getLogger(RPC.class);
        this.configuration = new Configuration(this);
        this.startTimestamp = LocalDateTime.now();
        this.taskHandler = new TaskHandler();
        this.handlers = new DiscordEventHandlers();
        this.presence = new DiscordRichPresence();

        handlers.ready = (user) -> logger.info("RPC ready, logged in as " + user.username + "#" + user.discriminator + "!");
        handlers.disconnected = (errorCode, message) -> logger.warn(errorCode + " " + message);
        handlers.errored = (errorCode, message) -> logger.error(errorCode + " " + message);
    }

    public void start()
    {
        getLogger().info("Start Time: " + getStartTimestamp());

        presence.details = getConfiguration().getString(ConfigOption.DETAILS);
        presence.state = getConfiguration().getString(ConfigOption.STATE);

        presence.largeImageKey = getConfiguration().getString(ConfigOption.LARGEIMAGEKEY);
        presence.largeImageText = getConfiguration().getString(ConfigOption.LARGEIMAGETEXT);

        presence.smallImageKey = getConfiguration().getString(ConfigOption.SMALLIMAGEKEY);
        presence.smallImageText = getConfiguration().getString(ConfigOption.SMALLIMAGETEXT);

        presence.startTimestamp = (getConfiguration().getString(ConfigOption.TIMESTAMP).equals("true")) ?
                System.currentTimeMillis() / 1000 : null;

        DiscordRPC.discordInitialize(getConfiguration().getString(ConfigOption.CLIENTID), handlers, false);
        DiscordRPC.discordUpdatePresence(presence);
        getTaskHandler().addRepeatingTask(DiscordRPC::discordRunCallbacks, "discord-callback-handler", TimeUnit.SECONDS, 2);
    }

    private void printVanity()
    {
        String vanity =
                """                                                  
rrrrr   rrrrrrrrr   ppppp   ppppppppp       cccccccccccccccc
r::::rrr:::::::::r  p::::ppp:::::::::p    cc:::::::::::::::c
r:::::::::::::::::r p:::::::::::::::::p  c:::::::::::::::::c
rr::::::rrrrr::::::rpp::::::ppppp::::::pc:::::::cccccc:::::c
 r:::::r     r:::::r p:::::p     p:::::pc::::::c     ccccccc
 r:::::r     rrrrrrr p:::::p     p:::::pc:::::c             
 r:::::r             p:::::p     p:::::pc:::::c             
 r:::::r             p:::::p    p::::::pc::::::c     ccccccc
 r:::::r             p:::::ppppp:::::::pc:::::::cccccc:::::c
 r:::::r             p::::::::::::::::p  c:::::::::::::::::c
 r:::::r             p::::::::::::::pp    cc:::::::::::::::c
 rrrrrrr             p::::::pppppppp        cccccccccccccccc
                     p:::::p                                
                     p:::::p                                
                    p:::::::p                               
                    p:::::::p                               
                    p:::::::p                               
                    ppppppppp                               
                                                            """;

        System.out.println(vanity);
    }

    public LocalDateTime getStartTimestamp()
    {
        return this.startTimestamp;
    }

    public Configuration getConfiguration()
    {
        return this.configuration;
    }

    public TaskHandler getTaskHandler()
    {
        return this.taskHandler;
    }

    public Logger getLogger()
    {
        return this.logger;
    }
}
