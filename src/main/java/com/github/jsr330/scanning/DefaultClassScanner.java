/**
 * Copyright 2012 the contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.github.jsr330.scanning;

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

import com.github.jsr330.GenericFilter;

public class DefaultClassScanner implements ClassScanner {
    
    private static final Pattern FILE_SEPARATOR_REGEX = Pattern.compile("\\" + System.getProperty("file.separator"));
    private static final Pattern ENTRY_SEPARATOR_REGEX = Pattern.compile("/");
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClassScanner.class);
    
    protected ClassScanner parent;
    protected GenericFilter<URI> sourceDirFilter;
    protected GenericFilter<String> classNameFilter;
    
    public DefaultClassScanner() {
        this(null, null, null);
    }
    
    public DefaultClassScanner(ClassScanner parent) {
        this(parent, null, null);
    }
    
    public DefaultClassScanner(GenericFilter<URI> sourceDirFilter, GenericFilter<String> classNameFilter) {
        this(null, sourceDirFilter, classNameFilter);
    }
    
    public DefaultClassScanner(ClassScanner parent, GenericFilter<URI> sourceDirFilter, GenericFilter<String> classNameFilter) {
        this.parent = parent;
        this.sourceDirFilter = sourceDirFilter;
        this.classNameFilter = classNameFilter;
    }
    
    @Override
    public Map<String, Class<?>> scan(ClassLoader loader) {
        Enumeration<URL> urls;
        URL url;
        String tmp;
        Map<String, Class<?>> classes = new TreeMap<String, Class<?>>();
        Map<String, Class<?>> parentClasses;
        File file;
        URI uri;
        
        if (parent != null) {
            if ((parentClasses = parent.scan(loader)) != null) {
                classes.putAll(parentClasses);
            }
        }
        
        try {
            urls = loader.getResources("META-INF");
            while (urls.hasMoreElements()) {
                url = urls.nextElement();
                tmp = url.toExternalForm();
                tmp = tmp.substring(0, tmp.length() - 8);
                LOGGER.debug("listClasses - source={}", tmp);
                
                try {
                    if (tmp.startsWith("file:")) {
                        uri = new URI(tmp);
                        if (sourceDirFilter == null || sourceDirFilter != null && sourceDirFilter.filter(uri)) {
                            file = new File(uri);
                            traverseFile(file.getAbsolutePath(), file, loader, classes);
                        }
                    } else if (tmp.startsWith("jar:")) {
                        uri = new URI(tmp.substring(4, tmp.length() - 2));
                        if (sourceDirFilter == null || sourceDirFilter != null && sourceDirFilter.filter(uri)) {
                            file = new File(uri);
                            traverseJar(file.getAbsolutePath(), file.toURI().toURL(), loader, classes);
                        }
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
        if (classNameFilter == null || classNameFilter != null && classNameFilter.filter(name)) {
            try {
                classes.put(name, Class.forName(name, false, loader));
            } catch (Throwable exception) {
                LOGGER.debug("error while loading class", exception);
            }
        }
    }
    
}
