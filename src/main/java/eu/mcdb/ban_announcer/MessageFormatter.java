package eu.mcdb.ban_announcer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.spicord.embed.Embed;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MessageFormatter {

    private final Map<String, String> map;
    private final char special;

    public MessageFormatter(char c) {
        this.map = new HashMap<String, String>();
        this.special = c;
    }

    public MessageFormatter() {
        this('%');
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
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();

            if (val == null) {
                continue;
            }

            if (isUrl) {
                val = urlencode(val);
            }

            message = message.replace(special + key + special, val);
        }
        return message;
    }
}
