package com.github.jsr330.spi.config.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.inject.Provider;

class TypeContainerConfig<V> {
    
    protected Provider<? extends V> provider;
    protected Method factoryMethod;
    protected Constructor<V> constructor;
    protected Class<V> type;
    protected Class<? extends V> implementation;
    protected boolean singleton = false;
    protected BindingCondition<V> condition;
    
    public TypeContainerConfig(Class<V> type) {
        this.type = type;
    }
    
    public BindingCondition<V> getCondition() {
        return condition;
    }
    
    public void setCondition(BindingCondition<V> condition) {
        this.condition = condition;
    }
    
    public Provider<? extends V> getProvider() {
        return provider;
    }
    
    public void setProvider(Provider<? extends V> provider) {
        this.provider = provider;
    }
    
    public Method getFactoryMethod() {
        return factoryMethod;
    }
    
    public void setFactoryMethod(Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }
    
    public Constructor<V> getConstructor() {
        return constructor;
    }
    
    public void setConstructor(Constructor<V> constructor) {
        this.constructor = constructor;
    }
    
    public Class<V> getType() {
        return type;
    }
    
    public void setType(Class<V> type) {
        this.type = type;
    }
    
    public Class<? extends V> getImplementation() {
        return implementation;
    }
    
    public void setImplementation(Class<? extends V> implementation) {
        this.implementation = implementation;
    }
    
    public boolean isSingleton() {
        return singleton;
    }
    
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
    
}