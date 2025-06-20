package me.tini.announcer.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public final class ReflectUtils {

    private ReflectUtils() {}

    public static File getJarFile(Class<?> clazz) {
        URL url = getClassLocation(clazz);

        if (url == null) { // shouldn't happen but who knows...
            return null;
        }

        if ("jar".equals(url.getProtocol())) {
            // jar:file:/[...].jar!/[...].class

            String urlString = url.toString();

            try {
                urlString = urlString.substring(4, urlString.indexOf("!/"));
                return new File(new URL(urlString).toURI());
            } catch (MalformedURLException
                   | URISyntaxException
                   | IndexOutOfBoundsException e) {
                System.err.println("Unable to get File from URL: " + e.getMessage());
            }
        } else if ("file".equals(url.getProtocol())) {
            // file:/[...].jar    <- jar file
            // file:/[...].class  <- running class from command line
            // file:/[...]/       <- classPath

            String urlString = url.toString();

            if (urlString.endsWith("/") || urlString.endsWith(".jar")) {
                try {
                    return new File(url.toURI());
                } catch (URISyntaxException e) {
                    System.err.println("Unable to get File from URL: " + e.getMessage());
                }
            } else if (urlString.endsWith(".class")) {
                String path = clazz.getCanonicalName().replace('.', '/') + ".class";
                String newUrl = urlString.substring(0, urlString.length() - path.length());

                try {
                    return new File(new URL(newUrl).toURI());
                } catch (MalformedURLException
                        | URISyntaxException
                        | IndexOutOfBoundsException e) {
                    System.err.println("Unable to get File from URL: " + e.getMessage());
                }
            }
        }

        return null;
    }

    private static URL getClassLocation(Class<?> clazz) {
        try {
            return clazz.getProtectionDomain().getCodeSource().getLocation();
        } catch (SecurityException | NullPointerException e) {/*ignore*/}

        return clazz.getResource(clazz.getName() + ".class");
    }
}
