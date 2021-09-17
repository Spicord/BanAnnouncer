package eu.mcdb.ban_announcer.extension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.google.gson.Gson;

public class ExtensionClassLoader extends URLClassLoader {

    private static final Gson GSON = new Gson();

    public ExtensionClassLoader(File file) {
        super(new URL[]{getURL(file)}, ExtensionClassLoader.class.getClassLoader());
    }

    public Extension getExtension() {
        InputStream extensionJson = getResourceAsStream("extension.json");

        if (extensionJson == null) {
            return null;
        }

        Extension ext = GSON.fromJson(new InputStreamReader(extensionJson), Extension.class);
        ext.setClassLoader(this);

        return ext;
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException e) {}
    }

    private static URL getURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
