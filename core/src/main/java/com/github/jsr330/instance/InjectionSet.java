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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class InjectionSet {
    
    protected Method[] methods;
    protected Method[] staticMethods;
    protected Field[] fields;
    protected Field[] staticFields;
    protected Class<?> type;
    
    public Method[] getMethods() {
        return methods;
    }
    
    public void setMethods(Method[] methods) {
        this.methods = methods;
    }
    
    public Method[] getStaticMethods() {
        return staticMethods;
    }
    
    public void setStaticMethods(Method[] staticMethods) {
        this.staticMethods = staticMethods;
    }
    
    public Field[] getFields() {
        return fields;
    }
    
    public void setFields(Field[] fields) {
        this.fields = fields;
    }
    
    public Field[] getStaticFields() {
        return staticFields;
    }
    
    public void setStaticFields(Field[] staticFields) {
        this.staticFields = staticFields;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public void setType(Class<?> type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName());
        builder.append(" [methods=");
        builder.append(Arrays.toString(methods));
        builder.append(",\nstaticMethods=");
        builder.append(Arrays.toString(staticMethods));
        builder.append(",\nfields=");
        builder.append(Arrays.toString(fields));
        builder.append(",\nstaticFields=");
        builder.append(Arrays.toString(staticFields));
        builder.append(",\ntype=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }
    
}