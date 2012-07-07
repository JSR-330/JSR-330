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

/**
 * A TypeDeterminator chooses the one implementation of a type based on the meta data passed it {@link #determineClass(Class, Class[], Annotation, ClassLoader)}.
 */
public interface TypeDeterminator {
    
    /**
     * Gives back the type to instance.
     * 
     * @param type The type to choose an implementation for.
     * @param candidates The known implementations.
     * @param qualifier A possible annotation for the specific instance - can be {@code null}.
     * @param classLoader The class loader to ask for implementations.
     * @return Returns an instantiable candidate from the candidate list.
     */
    <T> Class<T> determineClass(Class<T> type, Class<? extends T>[] candidates, Annotation qualifier, ClassLoader classLoader);
    
}
