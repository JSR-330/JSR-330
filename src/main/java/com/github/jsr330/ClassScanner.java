package com.github.jsr330;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassScanner {
    
    private static final Pattern FILE_SEPARATOR_REGEX = Pattern.compile("\\" + System.getProperty("file.separator"));
    private static final Pattern ENTRY_SEPARATOR_REGEX = Pattern.compile("/");
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassScanner.class);
    
    public Map<String, Class<?>> listClasses(ClassLoader loader) {
        Enumeration<URL> urls;
        URL url;
        String tmp;
        Map<String, Class<?>> classes = new TreeMap<String, Class<?>>();
        File file;
        
        try {
            urls = loader.getResources("META-INF");
            while (urls.hasMoreElements()) {
                url = urls.nextElement();
                tmp = url.toExternalForm();
                tmp = tmp.substring(0, tmp.length() - 8);
                LOGGER.debug("listClasses - source={}", tmp);
                
                try {
                    if (tmp.startsWith("file:")) {
                        file = new File(new URI(tmp));
                        traverseFile(file.getAbsolutePath(), file, loader, classes);
                    } else if (tmp.startsWith("jar:")) {
                        file = new File(new URI(tmp.substring(4, tmp.length() - 2)));
                        traverseJar(file.getAbsolutePath(), file.toURI().toURL(), loader, classes);
                    }
                } catch (URISyntaxException exception) {
                    LOGGER.debug("error while generating URI", exception);
                }
            }
        } catch (IOException exception) {
            LOGGER.debug("error while traversing jar", exception);
        }
        
        return classes;
    }
    
    protected void traverseJar(String base, URL url, ClassLoader loader, Map<String, Class<?>> classes) {
        JarInputStream stream = null;
        JarEntry entry;
        String name;
        
        try {
            stream = new JarInputStream(url.openStream());
            while ((entry = stream.getNextJarEntry()) != null) {
                if ((name = entry.getName()).endsWith(".class")) {
                    name = ENTRY_SEPARATOR_REGEX.matcher(name).replaceAll(".");
                    name = name.substring(0, name.length() - 6);
                    LOGGER.debug("traverseJar - name={}", name);
                    
                    createClass(loader, classes, name);
                }
            }
        } catch (Exception exception) {
            LOGGER.debug("error while traversing jar", exception);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException exception) {
                    LOGGER.debug("error while closing stream", exception);
                }
            }
        }
    }
    
    protected void traverseFile(String base, File file, ClassLoader loader, Map<String, Class<?>> classes) {
        String name;
        
        if (file.isDirectory()) {
            for (File tmp : file.listFiles()) {
                traverseFile(base, tmp, loader, classes);
            }
        } else if ((name = file.getAbsolutePath()).endsWith(".class")) {
            name = name.substring(base.length() + 1);
            name = FILE_SEPARATOR_REGEX.matcher(name).replaceAll(".");
            name = name.substring(0, name.length() - 6);
            LOGGER.debug("traverseFile - name={}", name);
            
            createClass(loader, classes, name);
        }
    }
    
    protected void createClass(ClassLoader loader, Map<String, Class<?>> classes, String name) {
        try {
            classes.put(name, Class.forName(name, false, loader));
        } catch (Throwable exception) {
            LOGGER.debug("error while loading class", exception);
        }
    }
    
}
