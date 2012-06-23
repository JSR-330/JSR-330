package com.github.jsr330.spi.config.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface InstancingBinder<T> extends ConditionalBinder<T> {
    
    ConditionalBinder<T> using(Constructor<T> constructor);
    
    ConditionalBinder<T> using(Method factoryMethod);
    
}
