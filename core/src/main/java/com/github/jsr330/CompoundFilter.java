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

/**
 * This filter combines multiple other filters.
 */
public class CompoundFilter<T> implements GenericFilter<T> {
    
    /**
     * The combination mode.
     */
    public enum Mode {
        AND, OR, XOR
    }
    
    /**
     * The filters.
     */
    protected GenericFilter<T>[] filters;
    /**
     * The combination mode.
     */
    protected Mode mode;
    
    public CompoundFilter(Mode mode, GenericFilter<T>... filters) {
        this.filters = filters;
        this.mode = mode;
    }
    
    public Mode getMode() {
        return mode;
    }
    
    @Override
    public boolean filter(T value) {
        if (filters.length > 0) {
            if (mode == Mode.AND) {
                for (GenericFilter<T> filter : filters) {
                    if (!filter.filter(value)) {
                        return false;
                    }
                }
            } else if (mode == Mode.OR) {
                for (GenericFilter<T> filter : filters) {
                    if (filter.filter(value)) {
                        return true;
                    }
                }
                return false;
            } else if (mode == Mode.XOR && filters.length == 2) {
                return filters[0].filter(value) != filters[1].filter(value);
            }
        }
        
        return true;
    }
    
}
