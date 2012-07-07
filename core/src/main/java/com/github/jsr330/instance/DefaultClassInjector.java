/**
 * Copyright 2012 the contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.github.jsr330.instance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Qualifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsr330.instance.TypeContainer.InstanceMode;
import com.github.jsr330.spi.ClassInjector;
import com.github.jsr330.spi.TypeConfig;
import com.github.jsr330.spi.TypeDeterminator;

/**
 * This ClassInjector instances type with respect to the {@link TypeConfig} and the {@link TypeDeterminator} assigned.
 */
public class DefaultClassInjector implements ClassInjector {
    
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[] {};
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClassInjector.class);
    
    protected Map<String, Provider<?>> providers = new TreeMap<String, Provider<?>>();
    protected Map<String, Object> singletons = new TreeMap<String, Object>();
    protected TypeDeterminator typeDeterminator = new DefaultTypeDeterminator();
    protected Map<String, TypeContainer> types = new TreeMap<String, TypeContainer>();
    protected TypeConfig config;
    
    public DefaultClassInjector() {
    }
    
    public DefaultClassInjector(TypeConfig config) {
        this.config = config;
    }
    
    public DefaultClassInjector(TypeDeterminator typeDeterminator) {
        this.typeDeterminator = typeDeterminator;
    }
    
    public DefaultClassInjector(TypeConfig config, TypeDeterminator typeDeterminator) {
        this.config = config;
        this.typeDeterminator = typeDeterminator;
    }
    
    @Override
    public void setTypeConfig(TypeConfig config) {
        this.config = config;
    }
    
    /**
     * Injects the static members in inheritance order.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void injectStaticMembers(Map<String, Class<?>> classes, Map<String, Class<?>[]> inheritanceTree, ClassLoader classLoader) {
        TypeContainer typeContainer;
        Map<String, Class<? extends Object>[]> castedInheritanceTree = new HashMap<String, Class<? extends Object>[]>(inheritanceTree);
        
        for (Map.Entry<String, Class<?>> type : classes.entrySet()) {
            typeContainer = generateTypeContainer((Class<Object>) type.getValue(), castedInheritanceTree, null, classLoader);
            for (InjectionSet set : typeContainer.getInjectionSets()) {
                injectStaticFields(set, (Object) null, castedInheritanceTree, classLoader);
                injectStaticMethods(set, (Object) null, castedInheritanceTree, classLoader);
            }
        }
    }
    
    /**
     * Instances the specified type.
     * If a provider is wanted a {@link SimpleProvider} is returned with the corresponding generic as type.
     * If the type is declared a singleton an already instanced bean of that type is returned.
     * Otherwise the whole injection stack will be processed.
     * This method caches it's results.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T instance(Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader, Class<?>[] generics, Annotation qualifier) {
        T inst = null;
        Provider<T> provider;
        Class<? extends T>[] candidates;
        TypeContainer typeContainer;
        
        LOGGER.debug("instance - gets instance for {} with generics {}", type, generics);
        
        if (type.isAssignableFrom(Provider.class)) {
            if (config == null || (provider = config.getProvider(this, type, inheritanceTree, qualifier, classLoader)) == null) {
                provider = new SimpleProvider<T>((Class<T>) generics[0], this, inheritanceTree, qualifier, classLoader);
            }
            LOGGER.debug("instance - return provider {} for {}", provider, generics[0]);
            return (T) provider;
        }
        
        candidates = inheritanceTree.get(type.getName());
        type = typeDeterminator.determineClass(type, candidates, qualifier, classLoader);
        LOGGER.debug("instance - got type {} due to annotations", type);
        
        if (singletons.containsKey(type.getName())) {
            LOGGER.debug("instance - deliver singleton {}", singletons.get(type.getName()));
            return (T) singletons.get(type.getName());
        } else {
            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                candidates = inheritanceTree.get(type.getName());
                if (candidates != null && candidates.length > 0) {
                    if (candidates.length == 1) {
                        inst = instance((Class<T>) candidates[0], inheritanceTree, classLoader, null, qualifier);
                    }
                }
                
                if (inst == null) {
                    inst = instance(type, inheritanceTree, classLoader, null, qualifier);
                }
            } else {
                if ((typeContainer = types.get(type.getName())) == null) {
                    typeContainer = generateTypeContainer(type, inheritanceTree, qualifier, classLoader);
                }
                
                if (typeContainer != null
                        && (typeContainer.getInstanceMode() == InstanceMode.CONSTRUCTOR && typeContainer.getConstructor() != null
                                || typeContainer.getInstanceMode() == InstanceMode.FACTORY_METHOD && typeContainer.getFactoryMethod() != null || typeContainer
                                .getInstanceMode() == InstanceMode.PROVIDER && typeContainer.getProvider() != null)) {
                    try {
                        if (typeContainer.getInstanceMode() == InstanceMode.FACTORY_METHOD && typeContainer.getFactoryMethod() != null) {
                            inst = (T) typeContainer.getFactoryMethod().invoke(null,
                                    getArguments(typeContainer.getFactoryMethod(), inheritanceTree, classLoader));
                        } else if (typeContainer.getInstanceMode() == InstanceMode.PROVIDER && typeContainer.getProvider() != null) {
                            inst = (T) typeContainer.getProvider().get();
                        } else {
                            inst = (T) typeContainer.getConstructor().newInstance(getArguments(typeContainer.getConstructor(), inheritanceTree, classLoader));
                        }
                        injectTypeContainer(typeContainer, inst, inheritanceTree, classLoader);
                        
                        if (typeContainer.isSingleton() && !singletons.containsKey(type.getName())) {
                            singletons.put(type.getName(), inst);
                        }
                    } catch (Exception exception) {
                        LOGGER.debug("error while instancing type", exception);
                    }
                }
            }
        }
        
        return inst;
    }
    
    /**
     * Gets the {@link TypeContainer} for the specified type. Asking the {@link TypeConfig} assigned before the entire investigation stack will be processed.
     */
    protected <T> TypeContainer generateTypeContainer(Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader) {
        Constructor<?> ctor;
        TypeContainer typeContainer = null;
        
        if (type != null) {
            if (config == null || (typeContainer = config.getTypeContainer(this, type, inheritanceTree, qualifier, classLoader)) == null) {
                ctor = getInjectableConstructor(type);
                if (ctor == null) {
                    ctor = getDefaultConstructor(type);
                }
                
                typeContainer = new TypeContainer(type, ctor);
                typeContainer.gatherInformation();
            }
            types.put(type.getName(), typeContainer);
        }
        
        return typeContainer;
    }
    
    /**
     * Do the entire non-static injection of the bean.
     */
    protected <T> void injectTypeContainer(TypeContainer typeContainer, Object inst, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader) {
        for (InjectionSet set : typeContainer.getInjectionSets()) {
            injectFields(set, inst, inheritanceTree, classLoader);
            injectMethods(set, inst, inheritanceTree, classLoader);
        }
    }
    
    /**
     * Injects the static fields of the specified type.
     */
    protected <T> void injectStaticFields(InjectionSet set, Object inst, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader) {
        injectFields(set, inst, inheritanceTree, classLoader, true);
    }
    
    /**
     * Injects the non-static fields of the specified bean.
     */
    protected <T> void injectFields(InjectionSet set, Object inst, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader) {
        injectFields(set, inst, inheritanceTree, classLoader, false);
    }
    
    /**
     * Injects the fields of the specified type / bean.
     */
    @SuppressWarnings("unchecked")
    protected <T> void injectFields(InjectionSet set, Object inst, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader,
            boolean onlyStatic) {
        ParameterizedType parameterizedType;
        Class<T> objectType;
        Class<?>[] generics;
        Class<? extends T>[] candidates;
        Annotation qualifier;
        
        for (Field field : onlyStatic ? set.getStaticFields() : set.getFields()) {
            try {
                LOGGER.debug("injectFields - field injected {} of {}", field, set.getType());
                if (field.getGenericType() instanceof ParameterizedType) {
                    parameterizedType = (ParameterizedType) field.getGenericType();
                    generics = getGenericTypes(parameterizedType);
                    objectType = null;
                    if (parameterizedType.getRawType() instanceof Class) {
                        objectType = (Class<T>) parameterizedType.getRawType();
                    }
                    
                    if (objectType != null) {
                        candidates = inheritanceTree.get(objectType.getName());
                        qualifier = getQualifier(field.getAnnotations());
                        objectType = typeDeterminator.determineClass(objectType, candidates, qualifier, classLoader);
                        field.set(inst, instance(objectType, inheritanceTree, classLoader, generics, qualifier));
                    }
                } else if (field.getGenericType() instanceof Class) {
                    qualifier = getQualifier(field.getAnnotations());
                    field.set(inst, instance((Class<T>) field.getGenericType(), inheritanceTree, classLoader, null, qualifier));
                }
            } catch (Exception exception) {
                LOGGER.debug("Error while injecting field", exception);
            }
        }
    }
    
    /**
     * Injects the static methods of the specified type.
     */
    protected <T> void injectStaticMethods(InjectionSet set, Object inst, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader) {
        injectMethods(set, inst, inheritanceTree, classLoader, true);
    }
    
    /**
     * Injects the non-static methods of the specified bean.
     */
    protected <T> void injectMethods(InjectionSet set, Object inst, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader) {
        injectMethods(set, inst, inheritanceTree, classLoader, false);
    }
    
    /**
     * Injects the methods of the specified type / bean.
     */
    protected <T> void injectMethods(InjectionSet set, Object inst, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader,
            boolean onlyStatic) {
        Object[] arguments;
        
        for (Method method : onlyStatic ? set.getStaticMethods() : set.getMethods()) {
            try {
                LOGGER.debug("injectMethods - method injected {} of {}", method, set.getType());
                arguments = getArguments(method, inheritanceTree, classLoader);
                if (arguments != null) {
                    method.invoke(inst, arguments);
                } else {
                    method.invoke(inst);
                }
            } catch (Exception exception) {
                LOGGER.debug("error while invoking method", exception);
            }
        }
    }
    
    /**
     * Gets the arguments for the method.
     */
    protected <T> Object[] getArguments(Method method, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader) {
        return getArguments(inheritanceTree, method.getParameterTypes(), method.getGenericParameterTypes(), method.getParameterAnnotations(), classLoader);
    }
    
    /**
     * Gets the arguments for the constructor.
     */
    protected <T> Object[] getArguments(Constructor<?> ctor, Map<String, Class<? extends T>[]> inheritanceTree, ClassLoader classLoader) {
        return getArguments(inheritanceTree, ctor.getParameterTypes(), ctor.getGenericParameterTypes(), ctor.getParameterAnnotations(), classLoader);
    }
    
    /**
     * Gets the arguments for the method / constructor.
     */
    @SuppressWarnings("unchecked")
    protected <T> Object[] getArguments(Map<String, Class<? extends T>[]> inheritanceTree, Class<?>[] parameters, Type[] generics, Annotation[][] annotations,
            ClassLoader classLoader) {
        Object[] arguments = null;
        Class<?>[] genericTypes;
        Class<?>[] candidates;
        int index = 0;
        Class<T> cls;
        Annotation qualifier;
        
        if (parameters.length > 0) {
            arguments = new Object[parameters.length];
            for (Class<?> parameterType : parameters) {
                genericTypes = null;
                if (ParameterizedType.class.isAssignableFrom(generics[index].getClass())) {
                    genericTypes = getGenericTypes((ParameterizedType) generics[index]);
                }
                
                candidates = inheritanceTree.get(parameterType.getName());
                qualifier = getQualifier(annotations[index]);
                cls = typeDeterminator.determineClass((Class<T>) parameterType, (Class<? extends T>[]) candidates, qualifier, classLoader);
                arguments[index] = instance(cls, inheritanceTree, classLoader, genericTypes, qualifier);
                index++;
            }
        }
        
        return arguments;
    }
    
    /**
     * Gets the annotation that is assignable from {@link Qualifier}.
     */
    protected Annotation getQualifier(Annotation[] annotations) {
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                    return annotation;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets the constructor annotated with {@link Inject}.
     */
    protected Constructor<?> getInjectableConstructor(Class<?> type) {
        Constructor<?>[] ctors = type.getDeclaredConstructors();
        
        for (Constructor<?> ctor : ctors) {
            if (ctor.isAnnotationPresent(Inject.class)) {
                LOGGER.debug("getInjectableConstructor - constructor found for {} {}", type, ctor);
                if (!ctor.isAccessible()) {
                    ctor.setAccessible(true);
                }
                return ctor;
            }
        }
        
        return null;
    }
    
    /**
     * Gets the default no-args constructor.
     */
    protected Constructor<?> getDefaultConstructor(Class<?> type) {
        Constructor<?>[] ctors = type.getDeclaredConstructors();
        
        for (Constructor<?> ctor : ctors) {
            if (ctor.getParameterTypes().length == 0) {
                if (!ctor.isAccessible()) {
                    ctor.setAccessible(true);
                }
                LOGGER.debug("getDefaultConstructor - got constructor for {}", type);
                return ctor;
            }
        }
        
        return null;
    }
    
    /**
     * Gets the type-classes of the parameterizedType.
     */
    protected Class<?>[] getGenericTypes(ParameterizedType type) {
        List<Class<?>> list = new ArrayList<Class<?>>();
        
        for (Type tmp : type.getActualTypeArguments()) {
            if (tmp instanceof Class) {
                list.add((Class<?>) tmp);
            }
        }
        
        return list.toArray(EMPTY_CLASS_ARRAY);
    }
    
    public TypeDeterminator getTypeDeterminator() {
        return typeDeterminator;
    }
    
    public void setTypeDeterminator(TypeDeterminator typeDeterminator) {
        this.typeDeterminator = typeDeterminator;
    }
    
}
