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

/**
 * A class scanner is responsible for getting the names of the classes needed for DI.
 */
public interface ClassScanner {
    
    /**
     * Gets a classname to class mapping back.
     * 
     * @param loader The class loader to use for gathering information.
     */
    Map<String, Class<?>> scan(ClassLoader loader);
    
}
