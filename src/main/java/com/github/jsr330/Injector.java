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

import java.util.Map;

import com.github.jsr330.analysis.ClassAnalyser;
import com.github.jsr330.analysis.InheritanceAnalyser;
import com.github.jsr330.instance.ClassInstancer;
import com.github.jsr330.instance.DefaultClassInstancer;
import com.github.jsr330.scanning.ClassScanner;
import com.github.jsr330.scanning.DefaultClassScanner;

public class Injector {
    
    protected ClassLoader classLoader;
    protected ClassScanner scanner = new DefaultClassScanner();
    protected ClassAnalyser<Map<String, Class<?>[]>> analyser = new InheritanceAnalyser();
    protected ClassInstancer instancer = new DefaultClassInstancer();
    protected Map<String, Class<?>> classes;
    protected Map<String, Class<?>[]> inheritance;
    
    public Injector() {
        this(Thread.currentThread().getContextClassLoader(), null, null, null);
    }
    
    public Injector(ClassLoader classLoader) {
        this(classLoader, null, null, null);
    }
    
    public Injector(ClassLoader classLoader, ClassScanner scanner, ClassAnalyser<Map<String, Class<?>[]>> analyser, ClassInstancer instancer) {
        this.classLoader = classLoader;
        this.scanner = scanner;
        this.analyser = analyser;
        this.instancer = instancer;
        update();
    }
    
    public <T> T getInstance(Class<T> type) {
        if (!inheritance.containsKey(type.getName())) {
            throw new RuntimeException("unknown type: " + type.getName());
        }
        
        return instancer.instance(type, inheritance, classLoader);
    }
    
    public void update() {
        classes = scanner.scan(classLoader);
        inheritance = analyser.analyse(classes);
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
    
    public ClassInstancer getInstancer() {
        return instancer;
    }
    
    public void setInstancer(ClassInstancer instancer) {
        this.instancer = instancer;
    }
    
}
