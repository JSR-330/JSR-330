package com.github.jsr330.instance;

import java.lang.annotation.Annotation;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTypeDeterminator implements TypeDeterminator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTypeDeterminator.class);
    
    @Override
    public Class<?> determineClass(Class<?> type, Class<?>[] candidates, Annotation qualifier, ClassLoader classLoader) {
        String namePrefix = "";
        
        if (qualifier != null) {
            if (qualifier instanceof Named) {
                namePrefix = ((Named) qualifier).value();
            } else {
                namePrefix = qualifier.annotationType().getSimpleName();
            }
        }
        
        if (candidates != null && namePrefix.trim().length() > 0) {
            for (Class<?> candidate : candidates) {
                if (candidate.getSimpleName().toLowerCase().startsWith(namePrefix.toLowerCase())) {
                    LOGGER.debug("determineClass - returning decent {} for {}", candidate, type);
                    return candidate;
                }
            }
        }
        
        LOGGER.debug("determineClass - returning {} for {}", type, type);
        return type;
    }
    
}
