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

import java.util.List;
import java.util.Map;

public class Injector {
    
    protected ClassLoader classLoader;
    protected ClassScanner scanner = new ClassScanner();
    protected ClassAnalyser analyser = new ClassAnalyser();
    protected ClassInstancer instancer = new ClassInstancer();
    protected Map<String, Class<?>> classes;
    protected Map<String, List<Class<?>>> inheritance;
    
    public Injector() {
        this(Thread.currentThread().getContextClassLoader());
    }
    
    public Injector(ClassLoader classLoader) {
        this.classLoader = classLoader;
        update();
    }
    
    public <T> T getInstance(Class<T> type) {
        if (!inheritance.containsKey(type.getName())) {
            throw new RuntimeException("unknown type: " + type.getName());
        }
        
        return instancer.instance(type, inheritance, classLoader);
    }
    
    public void update() {
        classes = scanner.listClasses(classLoader);
        inheritance = analyser.getInheritanceTree(classes);
    }
    
}
