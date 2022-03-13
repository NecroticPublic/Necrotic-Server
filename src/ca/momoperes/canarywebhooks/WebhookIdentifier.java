package ca.momoperes.canarywebhooks;

public class WebhookIdentifier {

    private final String name, channelId, token, avatar, guildId, id;

    public WebhookIdentifier(String name, String channelId, String token, String avatar, String guildId, String id) {
        this.name = name;
        this.channelId = channelId;
        this.token = token;
        this.avatar = avatar;
        this.guildId = guildId;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getToken() {
        return token;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getId() {
        return id;
    }
}
