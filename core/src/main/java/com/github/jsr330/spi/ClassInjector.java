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

public interface ClassInjector {
    
    void injectStaticMembers(Map<String, Class<?>> classes, Map<String, Class<?>[]> inheritanceTree, ClassLoader classLoader);
    
    <T> T instance(Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader, Class<?>[] generics, Annotation qualifier);
    
    void setTypeConfig(TypeConfig config);
    
}
