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
package com.github.jsr330.instance;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.inject.Provider;

import com.github.jsr330.spi.ClassInjector;

/**
 * A simple provider that delegates the instantiation to the {@link ClassInjector} back when {@link #get()} is called.
 */
public class SimpleProvider<T> implements Provider<T> {
    
    protected Class<T> type;
    protected ClassInjector instancer;
    protected Map<String, Class<? extends T>[]> inheritanceTree;
    protected ClassLoader classLoader;
    protected Annotation qualifier;
    
    public SimpleProvider(Class<T> type, ClassInjector instancer, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader) {
        this.type = type;
        this.instancer = instancer;
        this.inheritanceTree = inheritanceTree;
        this.qualifier = qualifier;
        this.classLoader = classLoader;
    }
    
    @Override
    public T get() {
        return instancer.instance(type, inheritanceTree, classLoader, null, qualifier);
    }
    
}
