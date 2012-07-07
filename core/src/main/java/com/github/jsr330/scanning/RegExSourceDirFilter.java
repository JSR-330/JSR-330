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
package com.github.jsr330.scanning;

import java.net.URI;
import java.util.regex.Pattern;

import com.github.jsr330.GenericFilter;

/**
 * A regexp filter for the source directories and / or jar files.
 */
public class RegExSourceDirFilter implements GenericFilter<URI> {
    
    protected Pattern regex;
    
    public RegExSourceDirFilter(String regex) {
        this.regex = Pattern.compile(regex);
    }
    
    public RegExSourceDirFilter(Pattern regex) {
        this.regex = regex;
    }
    
    @Override
    public boolean filter(URI value) {
        return regex.matcher(value.toString()).matches();
    }
    
}
