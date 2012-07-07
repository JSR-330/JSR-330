package com.github.jsr330.spi.config.builder;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Qualifier;

import com.github.jsr330.spi.ClassInjector;

/**
 * This is an utility class for some common condition for binding.
 */
public class BindingConditions {
    
    /**
     * Checks if an annotation is present for the instance that has to be instanced.
     */
    @SuppressWarnings("unchecked")
    public static <T> BindingCondition<T> annotationIsPresent(Class<? extends T> type, Class<? extends Annotation> annotation) {
        return anyAnnotationIsPresent(type, annotation);
    }
    
    /**
     * Checks if a type should be instanced using a specific {@link Qualifier}.
     */
    public static <T> BindingCondition<T> qualifierIs(Class<? extends T> type, final Class<? extends Annotation> expectedQualifier) {
        return new BindingCondition<T>() {
            
            @Override
            public boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
                    ClassLoader classLoader) {
                return qualifier != null && expectedQualifier.isAssignableFrom(qualifier.annotationType());
            }
            
        };
    }
    
    /**
     * Checks if a type should be instanced with a special named implementation.
     */
    public static <T> BindingCondition<T> isNamed(Class<? extends T> type, final CharSequence value) {
        return new BindingCondition<T>() {
            
            @Override
            public boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
                    ClassLoader classLoader) {
                return qualifier instanceof Named && ((Named) qualifier).value().equals(value);
            }
            
        };
    }
    
    /**
     * Checks if a type should be instanced with a special named implementation (ignoring the case).
     */
    public static <T> BindingCondition<T> isNamedIgnoringCase(Class<? extends T> type, final CharSequence value) {
        return new BindingCondition<T>() {
            
            @Override
            public boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
                    ClassLoader classLoader) {
                return qualifier instanceof Named && ((Named) qualifier).value().equalsIgnoreCase(value.toString());
            }
            
        };
    }
    
    /**
     * Checks if a type should be instanced with all specified annotations.
     */
    public static <T> BindingCondition<T> allAnnotationsArePresent(Class<? extends T> type, final Class<? extends Annotation>... annotations) {
        return new BindingCondition<T>() {
            
            @Override
            public boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
                    ClassLoader classLoader) {
                boolean exists = false;
                
                if (type != null && annotations != null && annotations.length > 0) {
                    for (Class<? extends Annotation> cmp : annotations) {
                        exists = false;
                        for (Annotation annotation : type.getAnnotations()) {
                            if (annotation.annotationType().equals(cmp)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            return false;
                        }
                        exists = true;
                    }
                    return true;
                }
                return false;
            }
            
        };
    }
    
    /**
     * Checks if a type should be instanced with any of the specified annotations.
     */
    public static <T> BindingCondition<T> anyAnnotationIsPresent(Class<? extends T> type, final Class<? extends Annotation>... annotations) {
        return new BindingCondition<T>() {
            
            @Override
            public boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
                    ClassLoader classLoader) {
                boolean exists = false;
                
                if (type != null && annotations != null && annotations.length > 0) {
                    for (Annotation annotation : type.getAnnotations()) {
                        exists = false;
                        for (Class<? extends Annotation> cmp : annotations) {
                            if (annotation.annotationType().equals(cmp)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            return exists;
                        }
                        exists = true;
                    }
                }
                return exists;
            }
            
        };
    }
    
    /**
     * Combines the specified conditions using a bitwise and.
     */
    public static <T> BindingCondition<T> and(final BindingCondition<T>... conditions) {
        return new BindingCondition<T>() {
            
            @Override
            public boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
                    ClassLoader classLoader) {
                if (conditions != null && conditions.length > 1) {
                    for (BindingCondition<T> condition : conditions) {
                        if (!condition.fulfilled(injector, type, inheritanceTree, qualifier, classLoader)) {
                            return false;
                        }
                    }
                } else if (conditions.length > 0) {
                    return conditions[0].fulfilled(injector, type, inheritanceTree, qualifier, classLoader);
                }
                return true;
            }
            
        };
    }
    
    /**
     * Combines the specified conditions using a bitwise or.
     */
    public static <T> BindingCondition<T> or(final BindingCondition<T>... conditions) {
        return new BindingCondition<T>() {
            
            @Override
            public boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
                    ClassLoader classLoader) {
                if (conditions != null && conditions.length > 1) {
                    for (BindingCondition<T> condition : conditions) {
                        if (condition.fulfilled(injector, type, inheritanceTree, qualifier, classLoader)) {
                            return true;
                        }
                    }
                    return false;
                } else if (conditions.length > 0) {
                    return conditions[0].fulfilled(injector, type, inheritanceTree, qualifier, classLoader);
                }
                return true;
            }
            
        };
    }
    
    /**
     * Combines the specified conditions using a bitwise xor.
     */
    public static <T> BindingCondition<T> xor(final BindingCondition<T> condition1, final BindingCondition<T> condition2) {
        return new BindingCondition<T>() {
            
            @Override
            public boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
                    ClassLoader classLoader) {
                if (condition1 != null && condition2 != null) {
                    return condition1.fulfilled(injector, type, inheritanceTree, qualifier, classLoader) != condition2.fulfilled(injector, type,
                            inheritanceTree, qualifier, classLoader);
                }
                return true;
            }
            
        };
    }
    
}
