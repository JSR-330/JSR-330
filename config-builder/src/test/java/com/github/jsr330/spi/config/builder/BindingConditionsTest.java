package com.github.jsr330.spi.config.builder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.junit.Test;

import com.github.jsr330.spi.ClassInjector;

public class BindingConditionsTest {
    
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    BindingCondition<Object> nonConfirming = new BindingCondition<Object>() {
        
        @Override
        public boolean fulfilled(ClassInjector injector, Class<Object> type, Map<String, Class<? extends Object>[]> inheritanceTree, Annotation qualifier,
                ClassLoader classLoader) {
            return false;
        }
        
    };
    BindingCondition<Object> confirming = new BindingCondition<Object>() {
        
        @Override
        public boolean fulfilled(ClassInjector injector, Class<Object> type, Map<String, Class<? extends Object>[]> inheritanceTree, Annotation qualifier,
                ClassLoader classLoader) {
            return true;
        }
        
    };
    
    @Test
    public void qualifierIs() {
        assertTrue(BindingConditions.qualifierIs(Tire.class, Drivers.class).fulfilled(null, Tire.class, null, new Drivers() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Drivers.class;
            }
            
        }, classLoader));
    }
    
    @Test
    public void qualifierIsNot() {
        assertFalse(BindingConditions.qualifierIs(Tire.class, Inject.class).fulfilled(null, Tire.class, null, new Drivers() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Drivers.class;
            }
            
        }, classLoader));
    }
    
    @Test
    public void isNamed() {
        assertTrue(BindingConditions.isNamed(Tire.class, "spare").fulfilled(null, Tire.class, null, new Named() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Named.class;
            }
            
            @Override
            public String value() {
                return "spare";
            }
            
        }, classLoader));
    }
    
    @Test
    public void isNotNamed() {
        assertFalse(BindingConditions.isNamed(Tire.class, "spare").fulfilled(null, Tire.class, null, new Named() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Named.class;
            }
            
            @Override
            public String value() {
                return "notspare";
            }
            
        }, classLoader));
    }
    
    @Test
    public void isNamedIgnoringCase() {
        assertTrue(BindingConditions.isNamedIgnoringCase(Tire.class, "spare").fulfilled(null, Tire.class, null, new Named() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Named.class;
            }
            
            @Override
            public String value() {
                return "SPARE";
            }
            
        }, classLoader));
    }
    
    @Test
    public void isNotNamedIgnoringCase() {
        assertFalse(BindingConditions.isNamedIgnoringCase(Tire.class, "spare").fulfilled(null, Tire.class, null, new Named() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Named.class;
            }
            
            @Override
            public String value() {
                return "notspare";
            }
            
        }, classLoader));
    }
    
    @Test
    public void annotationIsPresent() {
        assertTrue(BindingConditions.annotationIsPresent(Seat.class, Singleton.class).fulfilled(null, Seat.class, null, new Singleton() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Singleton.class;
            }
            
        }, classLoader));
    }
    
    @Test
    public void annotationNotPresent() {
        assertFalse(BindingConditions.annotationIsPresent(Seat.class, Inject.class).fulfilled(null, Seat.class, null, null, classLoader));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void notAllAnnotationsArePresent() {
        assertFalse(BindingConditions.allAnnotationsArePresent(Seat.class, Singleton.class, Inject.class).fulfilled(null, Seat.class, null, null, classLoader));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void allAnnotationsArePresent() {
        assertTrue(BindingConditions.allAnnotationsArePresent(Seat.class, Singleton.class).fulfilled(null, Seat.class, null, null, classLoader));
        assertTrue(BindingConditions.allAnnotationsArePresent(Drivers.class, Retention.class, Qualifier.class).fulfilled(null, Drivers.class, null, null,
                classLoader));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void anyAnnotationsIsPresent() {
        assertTrue(BindingConditions.anyAnnotationIsPresent(Seat.class, Singleton.class, Inject.class).fulfilled(null, Seat.class, null, null, classLoader));
        assertTrue(BindingConditions.anyAnnotationIsPresent(Seat.class, Singleton.class, Drivers.class).fulfilled(null, Seat.class, null, null, classLoader));
        assertTrue(BindingConditions.anyAnnotationIsPresent(Seat.class, Singleton.class).fulfilled(null, Seat.class, null, null, classLoader));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void anyAnnotationsIsNotPresent() {
        assertFalse(BindingConditions.anyAnnotationIsPresent(Seat.class, Inject.class, Qualifier.class).fulfilled(null, Seat.class, null, null, classLoader));
        assertFalse(BindingConditions.anyAnnotationIsPresent(Seat.class, Drivers.class).fulfilled(null, Seat.class, null, null, classLoader));
        assertFalse(BindingConditions.anyAnnotationIsPresent(Seat.class).fulfilled(null, Seat.class, null, null, classLoader));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void and() {
        assertTrue(BindingConditions.and(confirming).fulfilled(null, Object.class, null, null, classLoader));
        assertFalse(BindingConditions.and(nonConfirming).fulfilled(null, Object.class, null, null, classLoader));
        assertTrue(BindingConditions.and(confirming, confirming).fulfilled(null, Object.class, null, null, classLoader));
        assertFalse(BindingConditions.and(nonConfirming, nonConfirming).fulfilled(null, Object.class, null, null, classLoader));
        assertFalse(BindingConditions.and(confirming, nonConfirming).fulfilled(null, Object.class, null, null, classLoader));
        assertFalse(BindingConditions.and(nonConfirming, confirming).fulfilled(null, Object.class, null, null, classLoader));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void or() {
        assertTrue(BindingConditions.or(confirming).fulfilled(null, Object.class, null, null, classLoader));
        assertFalse(BindingConditions.or(nonConfirming).fulfilled(null, Object.class, null, null, classLoader));
        assertTrue(BindingConditions.or(confirming, confirming).fulfilled(null, Object.class, null, null, classLoader));
        assertFalse(BindingConditions.or(nonConfirming, nonConfirming).fulfilled(null, Object.class, null, null, classLoader));
        assertTrue(BindingConditions.or(confirming, nonConfirming).fulfilled(null, Object.class, null, null, classLoader));
        assertTrue(BindingConditions.or(nonConfirming, confirming).fulfilled(null, Object.class, null, null, classLoader));
    }
    
    @Test
    public void xor() {
        assertFalse(BindingConditions.xor(confirming, confirming).fulfilled(null, Object.class, null, null, classLoader));
        assertFalse(BindingConditions.xor(nonConfirming, nonConfirming).fulfilled(null, Object.class, null, null, classLoader));
        assertTrue(BindingConditions.xor(confirming, nonConfirming).fulfilled(null, Object.class, null, null, classLoader));
        assertTrue(BindingConditions.xor(nonConfirming, confirming).fulfilled(null, Object.class, null, null, classLoader));
    }
    
}
