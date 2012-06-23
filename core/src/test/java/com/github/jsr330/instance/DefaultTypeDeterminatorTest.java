package com.github.jsr330.instance;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

import org.junit.Before;
import org.junit.Test;

public class DefaultTypeDeterminatorTest {
    
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Single {
    }
    
    public class Bean {
    }
    
    public class SingleBean extends Bean {
    }
    
    public class MultiBean extends Bean {
    }
    
    DefaultTypeDeterminator defaultTypeDeterminator;
    Class<CharSequence> type;
    Class<? extends CharSequence>[] candidates;
    Annotation qualifier;
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    Single anno = new Single() {
        
        @Override
        public Class<? extends Annotation> annotationType() {
            return Single.class;
        }
        
    };
    
    @Before
    public void init() {
        defaultTypeDeterminator = new DefaultTypeDeterminator();
        type = CharSequence.class;
    }
    
    @Test
    public void determineClass_WithoutQualifier() {
        assertEquals(type, defaultTypeDeterminator.determineClass(type, candidates, null, classLoader));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void determineClass_WithQualifier_NoCandidate() {
        candidates = new Class[] {};
        assertEquals(type, defaultTypeDeterminator.determineClass(type, candidates, anno, classLoader));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void determineClass_WithQualifier_WithCandidate() {
        candidates = new Class[] { SingleBean.class };
        assertEquals(SingleBean.class, defaultTypeDeterminator.determineClass(type, candidates, anno, classLoader));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void determineClass_WithQualifier_MultiCandidate() {
        candidates = new Class[] { SingleBean.class, MultiBean.class };
        assertEquals(SingleBean.class, defaultTypeDeterminator.determineClass(type, candidates, anno, classLoader));
    }
    
}
