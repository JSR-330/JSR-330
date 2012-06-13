package com.github.jsr330;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.inject.Provider;

public class SimpleProvider implements Provider<Object> {
    
    protected Class<?> type;
    protected ClassInstancer instancer;
    protected Map<String, List<Class<?>>> inheritanceTree;
    protected ClassLoader classLoader;
    protected Annotation[] annotations;
    
    public SimpleProvider(Class<?> type, ClassInstancer instancer, Map<String, List<Class<?>>> inheritanceTree, Annotation[] annotations,
            ClassLoader classLoader) {
        this.type = type;
        this.instancer = instancer;
        this.inheritanceTree = inheritanceTree;
        this.annotations = annotations;
        this.classLoader = classLoader;
    }
    
    @Override
    public Object get() {
        return instancer.instance(type, null, annotations, inheritanceTree, classLoader);
    }
    
}
