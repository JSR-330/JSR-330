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
package com.github.jsr330.spi;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.github.jsr330.Injector;

/**
 * A class injector does the actual work for the DI framework: instance all static methods and fields once and getting an instance of type X
 * with respect to the possibly assigned {@link TypeConfig}.
 */
public interface ClassInjector {
    
    /**
     * Injects all static fields and methods of the classes specified once in a lifetime of an {@link Injector}.
     * 
     * @param classes The classes which static members are going to be injected.
     * @param inheritanceTree The entire inheritance tree of all known classes.
     * @param classLoader The class loader which loaded all the classes.
     */
    void injectStaticMembers(Map<String, Class<?>> classes, Map<String, Class<?>[]> inheritanceTree, ClassLoader classLoader);
    
    /**
     * Get an injected instance of the specified type back.
     * 
     * @param type The type to be instanced.
     * @param generics The type parameters of a may generic type - can be {@code null}.
     * @param qualifier A possible qualifier for the instance of the type.
     * @param inheritanceTree The entire inheritance tree of all known classes.
     * @param classLoader The class loader which loaded all the classes.
     * 
     * @return Returns the injected instance of the specified type.
     */
    <T> T instance(Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader, Class<?>[] generics, Annotation qualifier);
    
    /**
     * Assigns a new {@link TypeConfig}.
     * 
     * @param config The new TypeConfig - can be {@code null}.
     */
    void setTypeConfig(TypeConfig config);
    
}
