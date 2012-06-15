package com.github.jsr330.instance;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.inject.Provider;

public class SimpleProvider implements Provider<Object> {
    
    protected Class<?> type;
    protected ClassInstancer instancer;
    protected Map<String, Class<?>[]> inheritanceTree;
    protected ClassLoader classLoader;
    protected Annotation qualifier;
    
    public SimpleProvider(Class<?> type, ClassInstancer instancer, Map<String, Class<?>[]> inheritanceTree, Annotation qualifier, ClassLoader classLoader) {
        this.type = type;
        this.instancer = instancer;
        this.inheritanceTree = inheritanceTree;
        this.qualifier = qualifier;
        this.classLoader = classLoader;
    }
    
    @Override
    public Object get() {
        return instancer.instance(type, inheritanceTree, classLoader, null, qualifier);
    }
    
}
