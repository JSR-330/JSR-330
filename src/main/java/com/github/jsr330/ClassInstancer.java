package com.github.jsr330;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassInstancer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassInstancer.class);
    private static final Comparator<Method> METHOD_COMPARATOR = new Comparator<Method>() {
        
        @Override
        public int compare(Method method1, Method method2) {
            return method1.toGenericString().compareTo(method2.toGenericString());
        }
        
    };
    
    protected class InjectionSet {
        
        Method[] methods;
        Field[] fields;
        Class<?> type;
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            
            builder.append("type=");
            builder.append(type);
            builder.append(", fields=");
            builder.append(Arrays.toString(fields));
            builder.append(", methods=");
            builder.append(Arrays.toString(methods));
            
            return builder.toString();
        }
        
    }
    
    protected class TypeContainer {
        
        Constructor<?> constructor;
        InjectionSet[] injectionSets;
        Class<?> type;
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            
            builder.append("type=");
            builder.append(type);
            builder.append(", constructor=");
            builder.append(constructor);
            builder.append(", injectionSets=");
            builder.append(Arrays.toString(injectionSets));
            
            return builder.toString();
        }
        
    }
    
    protected Map<String, Provider<?>> providers = new TreeMap<String, Provider<?>>();
    protected Map<String, Object> singletons = new TreeMap<String, Object>();
    
    @SuppressWarnings("unchecked")
    public <T> T instance(Class<T> type, Map<String, List<Class<?>>> inheritanceTree, ClassLoader classLoader) {
        return (T) instance(type, null, null, inheritanceTree, classLoader);
    }
    
    protected Object instance(Class<?> type, Class<?>[] generics, Annotation[] annotations, Map<String, List<Class<?>>> inheritanceTree, ClassLoader classLoader) {
        Constructor<?> ctor = null;
        Object inst = null;
        Provider<?> provider;
        List<Class<?>> candidates;
        TypeContainer typeContainer;
        
        LOGGER.debug("instance - gets instance for {} with generics {}", type, generics);
        
        if (type.isAssignableFrom(Provider.class)) {
            provider = new SimpleProvider(generics[0], this, inheritanceTree, annotations, classLoader);
            LOGGER.debug("instance - return provider {} for {}", provider, generics[0]);
            return provider;
        }
        
        type = getQualifiedClass(type, annotations, inheritanceTree, classLoader);
        LOGGER.debug("instance - got type {} due to annotations", type);
        
        if (singletons.containsKey(type.getName())) {
            LOGGER.debug("instance - deliver singleton {}", singletons.get(type.getName()));
            return singletons.get(type.getName());
        } else {
            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                candidates = inheritanceTree.get(type.getName());
                if (candidates != null && !candidates.isEmpty()) {
                    if (candidates.size() == 1) {
                        inst = instance(candidates.get(0), null, annotations, inheritanceTree, classLoader);
                    }
                }
                
                if (inst == null) {
                    inst = instance(type, null, annotations, inheritanceTree, classLoader);
                }
            } else {
                ctor = getInjectableConstructor(type);
                if (ctor == null) {
                    ctor = getDefaultConstructor(type);
                }
                
                if (ctor != null) {
                    try {
                        inst = ctor.newInstance(getArguments(ctor, inheritanceTree, classLoader));
                    } catch (Exception exception) {
                        LOGGER.debug("error while instantiating class with constructor {}", ctor, exception);
                    }
                    
                    typeContainer = new TypeContainer();
                    typeContainer.type = type;
                    typeContainer.constructor = ctor;
                    
                    fillTypeContainer(typeContainer);
                    injectTypeContainer(typeContainer, inst, inheritanceTree, classLoader);
                    
                    if (isAnnotatedWith(Singleton.class, type) && !singletons.containsKey(type.getName())) {
                        singletons.put(type.getName(), inst);
                    }
                }
            }
        }
        
        return inst;
    }
    
    protected void fillTypeContainer(TypeContainer typeContainer) {
        Stack<Class<?>> hierarchie;
        Class<?> parent;
        InjectionSet injectionSet;
        List<InjectionSet> injectionSets = new ArrayList<InjectionSet>();
        
        hierarchie = new Stack<Class<?>>();
        hierarchie.push(parent = typeContainer.type);
        while ((parent = parent.getSuperclass()) != null) {
            if (!parent.equals(Object.class)) {
                hierarchie.push(parent);
            }
        }
        
        while (!hierarchie.isEmpty()) {
            parent = hierarchie.pop();
            if (!parent.equals(Object.class)) {
                injectionSet = new InjectionSet();
                injectionSet.type = parent;
                injectionSets.add(injectionSet);
            }
        }
        
        typeContainer.injectionSets = injectionSets.toArray(new InjectionSet[] {});
        
        fillFields(typeContainer);
        fillMethods(typeContainer);
    }
    
    protected void fillMethods(TypeContainer typeContainer) {
        List<Method> methods = new ArrayList<Method>();
        List<Method> toRemove = new ArrayList<Method>();
        Map<String, Method> map = new HashMap<String, Method>();
        StringBuilder key = new StringBuilder();
        String tmp;
        boolean candidate;
        Method oldMethod;
        int mod;
        
        for (InjectionSet set : typeContainer.injectionSets) {
            methods.clear();
            for (Method method : set.type.getDeclaredMethods()) {
                key.delete(0, key.length());
                key.append(method.getReturnType()).append(' ').append(method.getName()).append(' ').append(Arrays.toString(method.getParameterTypes()));
                tmp = key.toString();
                
                candidate = method.isAnnotationPresent(Inject.class) && !Modifier.isAbstract(method.getModifiers());
                
                if (map.containsKey(tmp)) {
                    if ((oldMethod = map.get(tmp)) != null) {
                        mod = oldMethod.getModifiers();
                        if (!(Modifier.isPrivate(mod) || Modifier.isStatic(mod) || mod == 0)) {
                            toRemove.add(map.get(tmp));
                        }
                    }
                }
                
                if (candidate) {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    
                    map.put(tmp, method);
                    methods.add(method);
                }
            }
            set.methods = methods.toArray(new Method[] {});
        }
        
        for (InjectionSet set : typeContainer.injectionSets) {
            methods.clear();
            for (Method method : set.methods) {
                if (!toRemove.contains(method)) {
                    methods.add(method);
                }
            }
            set.methods = methods.toArray(new Method[] {});
            Arrays.sort(set.methods, METHOD_COMPARATOR);
        }
    }
    
    protected void fillFields(TypeContainer typeContainer) {
        Field[] fields;
        List<Field> list = new ArrayList<Field>();
        
        for (InjectionSet set : typeContainer.injectionSets) {
            list.clear();
            fields = set.type.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Inject.class) && !Modifier.isFinal(field.getModifiers())) {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        list.add(field);
                    }
                }
            }
            set.fields = list.toArray(new Field[] {});
        }
    }
    
    protected void injectTypeContainer(TypeContainer typeContainer, Object inst, Map<String, List<Class<?>>> inheritanceTree, ClassLoader classLoader) {
        for (InjectionSet set : typeContainer.injectionSets) {
            injectFields(set, inst, inheritanceTree, classLoader);
            injectMethods(set, inst, inheritanceTree, classLoader);
        }
    }
    
    protected void injectFields(InjectionSet set, Object inst, Map<String, List<Class<?>>> inheritanceTree, ClassLoader classLoader) {
        ParameterizedType parameterizedType;
        Class<?> objectType;
        Class<?>[] generics;
        
        for (Field field : set.fields) {
            try {
                LOGGER.debug("injectFields - field injected {} of {}", field, set.type);
                if (field.getGenericType() instanceof ParameterizedType) {
                    parameterizedType = (ParameterizedType) field.getGenericType();
                    generics = getGenericTypes(parameterizedType);
                    objectType = null;
                    if (parameterizedType.getRawType() instanceof Class) {
                        objectType = (Class<?>) parameterizedType.getRawType();
                    }
                    
                    if (objectType != null) {
                        objectType = getQualifiedClass(objectType, field.getAnnotations(), inheritanceTree, classLoader);
                        field.set(inst, instance(objectType, generics, field.getAnnotations(), inheritanceTree, classLoader));
                    }
                } else if (field.getGenericType() instanceof Class) {
                    Object tmp = instance((Class<?>) field.getGenericType(), null, field.getAnnotations(), inheritanceTree, classLoader);
                    field.set(inst, tmp);
                }
            } catch (Exception exception) {
                LOGGER.debug("Error while injecting field", exception);
            }
        }
    }
    
    protected void injectMethods(InjectionSet set, Object inst, Map<String, List<Class<?>>> inheritanceTree, ClassLoader classLoader) {
        Object[] arguments;
        
        for (Method method : set.methods) {
            try {
                LOGGER.debug("injectMethods - method injected {} of {}", method, set.type);
                arguments = getArguments(method, inheritanceTree, classLoader);
                if (method.getName().equals("supertypeStaticMethodInjection")) {
                    System.out.println(method.toString());
                }
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
    
    protected Object[] getArguments(Method method, Map<String, List<Class<?>>> inheritanceTree, ClassLoader classLoader) {
        return getArguments(inheritanceTree, method.getParameterTypes(), method.getGenericParameterTypes(), method.getParameterAnnotations(), classLoader);
    }
    
    protected Object[] getArguments(Constructor<?> ctor, Map<String, List<Class<?>>> inheritanceTree, ClassLoader classLoader) {
        return getArguments(inheritanceTree, ctor.getParameterTypes(), ctor.getGenericParameterTypes(), ctor.getParameterAnnotations(), classLoader);
    }
    
    protected Object[] getArguments(Map<String, List<Class<?>>> inheritanceTree, Class<?>[] parameters, Type[] generics, Annotation[][] annotations,
            ClassLoader classLoader) {
        Object[] arguments = null;
        Class<?>[] genericTypes;
        int index = 0;
        Class<?> cls;
        
        if (parameters.length > 0) {
            arguments = new Object[parameters.length];
            for (Class<?> parameterType : parameters) {
                genericTypes = null;
                if (ParameterizedType.class.isAssignableFrom(generics[index].getClass())) {
                    genericTypes = getGenericTypes((ParameterizedType) generics[index]);
                }
                
                cls = getQualifiedClass(parameterType, annotations[index], inheritanceTree, classLoader);
                arguments[index] = instance(cls, genericTypes, annotations[index], inheritanceTree, classLoader);
                index++;
            }
        }
        
        return arguments;
    }
    
    protected Class<?> getQualifiedClass(Class<?> type, Annotation[] annotations, Map<String, List<Class<?>>> inheritanceTree, ClassLoader classLoader) {
        String namePrefix = "";
        List<Class<?>> candidates = inheritanceTree.get(type.getName());
        
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (isAnnotatedWith(Qualifier.class, annotation.annotationType())) {
                    if (annotation instanceof Named) {
                        namePrefix = ((Named) annotation).value();
                    } else {
                        namePrefix = annotation.annotationType().getSimpleName();
                    }
                    
                    if (candidates != null) {
                        for (Class<?> candi : candidates) {
                            if (candi.getSimpleName().toLowerCase().startsWith(namePrefix.toLowerCase())) {
                                LOGGER.debug("getQualifiedClass - returning decent {} for {}", candi, type);
                                return candi;
                            }
                        }
                    }
                }
            }
        }
        
        LOGGER.debug("getQualifiedClass - returning {} for {}", type, type);
        return type;
    }
    
    protected boolean isAnnotatedWith(Class<?> annotation, Class<?> type) {
        for (Annotation tmp : type.getAnnotations()) {
            if (tmp.annotationType().equals(annotation)) {
                return true;
            }
        }
        
        return false;
    }
    
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
    
    protected Class<?>[] getGenericTypes(ParameterizedType type) {
        List<Class<?>> list = new ArrayList<Class<?>>();
        
        for (Type tmp : type.getActualTypeArguments()) {
            if (tmp instanceof Class) {
                list.add((Class<?>) tmp);
            }
        }
        
        return list.toArray(new Class<?>[] {});
    }
    
}
