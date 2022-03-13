package ca.momoperes.canarywebhooks;

import java.util.ArrayList;
import java.util.List;

import ca.momoperes.canarywebhooks.embed.DiscordEmbed;

public class DiscordMessage extends Payload {

    private String content;
    private String username;
    private String avatarURL;
    private boolean tts;
    private List<DiscordEmbed> embeds = new ArrayList<>();

    public DiscordMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public boolean isTTS() {
        return tts;
    }

    public void setTTS(boolean tts) {
        this.tts = tts;
    }

    public List<DiscordEmbed> getEmbeds() {
        return embeds;
    }

    public void setEmbeds(List<DiscordEmbed> embeds) {
        this.embeds = embeds;
    }

    @Override
    public void save(PayloadMap map) {
        map.put("content", content);
        map.putIfExists("username", username);
        map.putIfExists("avatar_url", avatarURL);
        map.put("tts", tts);
        if (embeds != null && embeds.size() > 0) {
            map.put("embeds", embeds);
        }
    }

    public static class Builder {
        private final String content;
        private final DiscordMessage message;

        public Builder(String content) {
            this.content = content;
            this.message = new DiscordMessage(content);
            this.message.setEmbeds(new ArrayList<DiscordEmbed>());
        }

        public Builder withUsername(String username) {
            message.setUsername(username);
            return this;
        }

        public Builder withAvatarURL(String avatarURL) {
            message.setAvatarURL(avatarURL);
            return this;
        }

        public Builder withTTS(boolean tts) {
            message.setTTS(tts);
            return this;
        }

        public Builder withEmbed(DiscordEmbed embed) {
            message.getEmbeds().add(embed);
            return this;
        }

        public DiscordMessage build() {
            return message;
        }
    }
}
