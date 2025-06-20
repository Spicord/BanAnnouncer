package me.tini.announcer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.tini.announcer.embed.Embed;

public class MessageFormatter {

    private static final Pattern placeholderPattern = Pattern.compile("([%]{1}[A-Za-z0-9_]{1,}[%]{1})");

    private final Map<String, String> map;

    private Function<String, String> otherHandler;

    public MessageFormatter() {
        this.map = new HashMap<String, String>();
    }

    public void setOtherPlaceholderHandler(Function<String, String> handler) {
        this.otherHandler = handler;
    }

    public MessageFormatter setString(String key, String value) {
        map.put(key, value);
        return this;
    }

    public Embed format(Embed embed) {
        return embed == null ? null : format0(embed);
    }

    private Embed format0(Embed embed) {
        Gson gson = new Gson();

        JsonObject root = gson.toJsonTree(embed).getAsJsonObject();

        formatElement(root);

        return gson.fromJson(root, Embed.class);
    }

    private void formatElement(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (isString(value)) {
                    boolean isUrl = "url".equals(key) || "icon_url".equals(key);

                    String val = getAsString(value);

                    val = setPlaceholders(val, isUrl);

                    object.addProperty(key, val);
                } else {
                    formatElement(value);
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {
                formatElement(array.get(i));
            }
        }
    }

    private String urlencode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    private String getAsString(JsonElement value) {
        return value.getAsJsonPrimitive().getAsString();
    }

    private boolean isString(JsonElement value) {
        return value.isJsonPrimitive() && value.getAsJsonPrimitive().isString();
    }

    private String setPlaceholders(String message, boolean isUrl) {
        Matcher matcher = placeholderPattern.matcher(message);
        while (matcher.find()) {
            final String found = matcher.group();
            final String foundNoSym = found.substring(1, found.length() - 1);

            String value = map.get(foundNoSym);

            if (value == null) {
                if (otherHandler != null) {
                    value = otherHandler.apply(foundNoSym);
                    if (value == null) {
                        value = "";
                    }
                } else {
                    value = "";
                }
            }

            if (isUrl) {
                value = urlencode(value);
            }

            message = message.replace(found, value);
        }
        return message;
    }
}
