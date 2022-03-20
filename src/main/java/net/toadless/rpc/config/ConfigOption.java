package net.toadless.rpc.config;

public enum ConfigOption
{
    CLIENTID("clientid", "000000000000000000"),

    STATE("state", null),
    DETAILS("details", null),

    TIMESTAMP("timestamp", "false"),

    LARGEIMAGEKEY("largeimagekey", null),
    LARGEIMAGETEXT("largeimagetext", null),

    SMALLIMAGEKEY("smallimagekey", null),
    SMALLIMAGETEXT("smallimagetext", null);

    private final String key;
    private final String defaultValue;

    ConfigOption(String key, String defaultValue)
    {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public String getKey()
    {
        return key;
    }
}