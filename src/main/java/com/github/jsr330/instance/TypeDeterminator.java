package com.github.jsr330.instance;

import java.lang.annotation.Annotation;

public interface TypeDeterminator {
    
    Class<?> determineClass(Class<?> type, Class<?>[] candidates, Annotation qualifier, ClassLoader classLoader);
    
}
