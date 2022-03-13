package ca.momoperes.canarywebhooks.embed;

import ca.momoperes.canarywebhooks.Payload;
import ca.momoperes.canarywebhooks.PayloadMap;

import java.awt.*;
import java.util.ArrayList;

public class DiscordEmbed extends Payload {

    private String title, description, url;
    private Color color;
    private EmbedAuthor author;
    private EmbedMedia thumbnail, video;
    private EmbedFooter footer;
    private ArrayList<EmbedField> fields;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public EmbedAuthor getAuthor() {
        return author;
    }

    public void setAuthor(EmbedAuthor author) {
        this.author = author;
    }

    public EmbedMedia getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(EmbedMedia thumbnail) {
        this.thumbnail = thumbnail;
    }

    public EmbedMedia getVideo() {
        return video;
    }

    public void setVideo(EmbedMedia video) {
        this.video = video;
    }

    public EmbedFooter getFooter() {
        return footer;
    }

    public void setFooter(EmbedFooter footer) {
        this.footer = footer;
    }

    public ArrayList<EmbedField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<EmbedField> fields) {
        this.fields = fields;
    }

    @Override
    public void save(PayloadMap map) {
        map.putIfExists("title", title);
        map.putIfExists("description", description);
        map.putIfExists("url", url);
        map.putIfExists("color", getIntColor());
        map.putIfExists("author", author);
        map.putIfExists("thumbnail", thumbnail);
        map.putIfExists("video", video);
        map.putIfExists("footer", footer);
        if (fields != null && fields.size() > 0) {
            map.put("fields", fields);
        }
    }

    public Integer getIntColor() {
        if (color != null) {
            return 65536 * color.getRed() + 256 * color.getGreen() + color.getBlue();
        }
        return null;
    }

    public static class Builder {
        private final DiscordEmbed embed;

        public Builder() {
            this.embed = new DiscordEmbed();
            this.embed.setFields(new ArrayList<EmbedField>());
        }

        public Builder withTitle(String title) {
            embed.setTitle(title);
            return this;
        }

        public Builder withDescription(String description) {
            embed.setDescription(description);
            return this;
        }

        public Builder withURL(String url) {
            embed.setUrl(url);
            return this;
        }

        public Builder withColor(Color color) {
            embed.setColor(color);
            return this;
        }

        public Builder withAuthor(EmbedAuthor author) {
            embed.setAuthor(author);
            return this;
        }

        public Builder withThumbnail(EmbedMedia thumbnail) {
            embed.setThumbnail(thumbnail);
            return this;
        }

        public Builder withVideo(EmbedMedia video) {
            embed.setVideo(video);
            return this;
        }

        public Builder withFooter(EmbedFooter footer) {
            embed.setFooter(footer);
            return this;
        }

        public Builder withField(EmbedField field) {
            embed.getFields().add(field);
            return this;
        }

        public DiscordEmbed build() {
            return embed;
        }
    }
}
