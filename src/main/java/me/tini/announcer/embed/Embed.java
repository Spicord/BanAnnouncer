package me.tini.announcer.embed;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class Embed implements Serializable {

    private static final long serialVersionUID = 1L;

    private Embed() {
    }

    public Embed(String description) {
        this.embed = new EmbedData();
        this.embed.description = description;
    }

    public Embed(String title, String description) {
        this.embed = new EmbedData();
        this.embed.title = title;
        this.embed.description = description;
    }

    private Integer __version;
    private String content;
    private EmbedData embed;

    /**
     * @return true if the message has content
     */
    public boolean hasContent() {
        return content != null;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return true if the message has embed data
     */
    public boolean hasEmbedData() {
        return embed != null;
    }

    /**
     * @return the embed data
     */
    public EmbedData getEmbedData() {
        return embed;
    }

    public static class EmbedData implements Serializable {

        private static final long serialVersionUID = 1L;

        private String title;
        private String description;
        private String url;
        private Integer color = 0x1FFFFFFF;
        private String timestamp;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;
        private Field[] fields;

        public boolean hasTitle() {
            return title != null;
        }

        public boolean hasDescription() {
            return description != null;
        }

        public boolean hasUrl() {
            return url != null;
        }

        public boolean hasThumbnail() {
            return thumbnail != null;
        }

        public boolean hasTimestamp() {
            return timestamp != null;
        }

        public boolean hasImage() {
            return image != null;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @return the color
         */
        public Integer getColor() {
            return color;
        }

        /**
         * @return the timestamp
         */
        public OffsetDateTime getTimestamp() {
            return (timestamp == null || "0000-00-00T00:00:00".equals(timestamp))
                ? OffsetDateTime.now()
                : OffsetDateTime.parse(timestamp);
        }

        /**
         * @return true if the embed has a footer
         */
        public boolean hasFooter() {
            return footer != null;
        }

        /**
         * @return the footer
         */
        public Footer getFooter() {
            return footer;
        }

        /**
         * @return the thumbnail url
         */
        public String getThumbnailUrl() {
            return thumbnail.url;
        }

        /**
         * @return the image url
         */
        public String getImageUrl() {
            return image.url;
        }

        /**
         * @return true if the embed has an author
         */
        public boolean hasAuthor() {
            return author != null;
        }

        /**
         * @return the author
         */
        public Author getAuthor() {
            return author;
        }

        /**
         * @return true if the embed has fields
         */
        public boolean hasFields() {
            return fields != null;
        }

        /**
         * @return the fields
         */
        public Field[] getFields() {
            return fields;
        }

        public void removeAuthor() {
            author = null;
        }
    }

    public class Footer implements Serializable {

        private static final long serialVersionUID = 1L;

        private String icon_url;
        private String text;

        /**
         * @return the icon url
         */
        public String getIconUrl() {
            return icon_url;
        }

        /**
         * @return the text
         */
        public String getText() {
            return text;
        }
    }

    public class Thumbnail implements Serializable {

        private static final long serialVersionUID = 1L;

        private String url;
    }

    public class Image implements Serializable {

        private static final long serialVersionUID = 1L;

        private String url;
    }

    public class Author implements Serializable {

        private static final long serialVersionUID = 1L;

        private String name;
        private String url;
        private String icon_url;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        public boolean hasUrl() {
            return url != null;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @return the icon url
         */
        public String getIconUrl() {
            return icon_url;
        }

        public boolean hasIconUrl() {
            return icon_url != null;
        }
    }

    public class Field implements Serializable {

        private static final long serialVersionUID = 1L;

        private String name;
        private String value;
        private Boolean inline;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @return the inline flag
         */
        public Boolean isInline() {
            return inline == null ? false : inline;
        }
    }

    /**
     * Converts a json string to a {@link Embed} object.
     * 
     * @param json the json to be parsed.
     * @return the {@link Embed} object.
     */
    public static Embed fromJson(String json) {
        return EmbedParser.parse(json);
    }

    /**
     * Converts a string to a {@link Embed} object.
     * 
     * @param content the embed content
     * @return the {@link Embed} object.
     */
    public static Embed fromString(String content) {
        return new Embed(content);
    }

    /**
     * Converts this object to a json string.
     * 
     * @return the json string.
     */
    public String toJson() {
        return EmbedParser.GSON.toJson(this);
    }

    /**
     * Converts this object to a json string.
     * 
     * @return the json string.
     */
    @Override
    public String toString() {
        return toJson();
    }

    /**
     * Clones this instance.
     */
    @Override
    public Embed clone() {
        return fromJson(toJson());
    }

    public Webhook toWebhook() {
        return Webhook.fromEmbed(this);
    }

    public static Embed fromWebhook(Webhook webhook) {
        Embed embed = new Embed();
        embed.content = webhook.getContent();
        if (webhook.getEmbeds() != null && webhook.getEmbeds().length > 0) {
            embed.embed = webhook.getEmbeds()[0];
        } else {
            embed.embed = new EmbedData();
        }
        return embed;
    }

    /**
     * Gets the embed version used to know if the embed
     * is just a single json or if some values needs to be
     * evaluated.
     * 
     * @return the embed version number
     */
    public int getVersion() {
        return __version == null ? 0 : __version;
    }
}
