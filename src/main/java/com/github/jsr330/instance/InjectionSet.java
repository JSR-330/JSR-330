package com.github.jsr330.instance;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

class InjectionSet {
    
    protected Method[] methods;
    protected Method[] staticMethods;
    protected Field[] fields;
    protected Field[] staticFields;
    protected Class<?> type;
    
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