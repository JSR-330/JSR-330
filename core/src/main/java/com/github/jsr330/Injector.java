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
package com.github.jsr330;

import java.util.HashMap;
import java.util.Map;

import com.github.jsr330.analysis.InheritanceAnalyser;
import com.github.jsr330.instance.DefaultClassInjector;
import com.github.jsr330.scanning.DefaultClassScanner;
import com.github.jsr330.spi.ClassAnalyser;
import com.github.jsr330.spi.ClassInjector;
import com.github.jsr330.spi.ClassScanner;

public class Injector {
    
    protected ClassLoader classLoader;
    protected ClassScanner scanner = new DefaultClassScanner();
    protected ClassAnalyser<Map<String, Class<?>[]>> analyser = new InheritanceAnalyser();
    protected ClassInjector instancer = new DefaultClassInjector();
    protected Map<String, Class<?>> classes;
    protected Map<String, Class<?>[]> inheritance;
    
    public Injector() {
        this(Thread.currentThread().getContextClassLoader(), null, null, null);
    }
    
    public Injector(ClassLoader classLoader) {
        this(classLoader, null, null, null);
    }
    
    public Injector(ClassLoader classLoader, ClassScanner scanner, ClassAnalyser<Map<String, Class<?>[]>> analyser, ClassInjector instancer) {
        this.classLoader = classLoader;
        if (scanner != null) {
            this.scanner = scanner;
        }
        if (analyser != null) {
            this.analyser = analyser;
        }
        if (instancer != null) {
            this.instancer = instancer;
        }
        update();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type) {
        Map<String, Class<? extends Object>[]> map = new HashMap<String, Class<? extends Object>[]>(inheritance);
        return (T) instancer.instance((Class<Object>) type, map, classLoader, null, null);
    }
    
    public void update() {
        classes = scanner.scan(classLoader);
        inheritance = analyser.analyse(classes);
        instancer.injectStaticMembers(classes, inheritance, classLoader);
    }
    
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public ClassScanner getScanner() {
        return scanner;
    }
    
    public void setScanner(ClassScanner scanner) {
        this.scanner = scanner;
    }
    
    public ClassAnalyser<Map<String, Class<?>[]>> getAnalyser() {
        return analyser;
    }
    
    public void setAnalyser(ClassAnalyser<Map<String, Class<?>[]>> analyser) {
        this.analyser = analyser;
    }
    
    public ClassInjector getInstancer() {
        return instancer;
    }
    
    public void setInstancer(ClassInjector instancer) {
        this.instancer = instancer;
    }
    
}
