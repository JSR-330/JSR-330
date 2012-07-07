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

import java.util.Map;

import com.github.jsr330.Injector;

/**
 * A ClassAnalyser gives back information needed by the {@link Injector}.
 */
public interface ClassAnalyser<T> {
    
    /**
     * Delivers the meta information of the classes passed.
     * 
     * @param classes The classes to analyse.
     */
    T analyse(Map<String, Class<?>> classes);
    
}
