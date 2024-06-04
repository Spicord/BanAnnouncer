package me.tini.announcer.extension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Supplier;

import com.google.gson.Gson;

import me.tini.announcer.BanAnnouncerPlugin;

public class FileExtensionContainer extends ExtensionContainer {

    private static final Gson GSON = new Gson();

    private URLClassLoader loader;

    public FileExtensionContainer(File file) {
        loader = new URLClassLoader(
            new URL[] { fileToUrl(file) },
            FileExtensionContainer.class.getClassLoader()
        );

        InputStream extensionJson = loader.getResourceAsStream("extension.json");

        if (extensionJson == null) {
            throw new IllegalArgumentException("Archive is missing the extension.json file");
        }

        info = GSON.fromJson(new InputStreamReader(extensionJson), ExtensionInfo.class);
    }

    @Override
    public Supplier<AbstractExtension> getInstanceSupplier(BanAnnouncerPlugin plugin) {
        if (instance != null) {
            return () -> instance;
        }

        return () -> {
            try {
                Class<?> listenerClass = loader.loadClass(info.getMainClass());
                Constructor<?> constructor = listenerClass.getConstructor(BanAnnouncerPlugin.class);
                Object ins = constructor.newInstance(plugin);

                if (ins instanceof AbstractExtension) {
                    instance = (AbstractExtension) ins;
                    return instance;
                }

                throw new IllegalStateException("Unknown instance type: " + ins.getClass());
            } catch (Exception e) {
                throw new RuntimeException("Failed to create listener instance", e);
            }
        };
    }

    @Override
    public void close() {
        try {
            loader.close();
        } catch (IOException e) {}
    }

    private static URL fileToUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
