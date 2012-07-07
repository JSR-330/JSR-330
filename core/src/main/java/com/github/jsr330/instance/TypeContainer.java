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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * This is an information container for classes.
 */
public class TypeContainer {
    
    public enum InstanceMode {
        CONSTRUCTOR, FACTORY_METHOD, PROVIDER
    }
    
    private static final Field[] EMPTY_FIELD_ARRAY = new Field[] {};
    private static final Method[] EMPTY_METHOD_ARRAY = new Method[] {};
    private static final InjectionSet[] EMPTY_INJECTIONSET_ARRAY = new InjectionSet[] {};
    
    /**
     * The injection information in inheritance order (base class to subclass).
     */
    protected InjectionSet[] injectionSets;
    /**
     * The type to inject.
     */
    protected Class<?> type;
    /**
     * The constructor to instantiate the bean.
     */
    protected Constructor<?> constructor;
    /**
     * The provider to instantiate the bean.
     */
    protected Provider<?> provider;
    /**
     * The (static) method to instantiate the bean.
     */
    protected Method factoryMethod;
    /**
     * Indicates that this bean is a singleton.
     */
    protected boolean singleton = false;
    /**
     * The mode for the instantiation (method, constructor or provider).
     */
    protected InstanceMode instanceMode = InstanceMode.CONSTRUCTOR;
    
    public TypeContainer(Class<?> type, Constructor<?> constructor) {
        this.type = type;
        this.constructor = constructor;
    }
    
    /**
     * Collects some basic information about the type (base classes, injection sets).
     */
    public void gatherInformation() {
        Stack<Class<?>> hierarchie;
        Class<?> parent;
        InjectionSet injectionSet;
        List<InjectionSet> injectionSets = new ArrayList<InjectionSet>();
        
        hierarchie = new Stack<Class<?>>();
        hierarchie.push(parent = type);
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
        
        this.injectionSets = injectionSets.toArray(EMPTY_INJECTIONSET_ARRAY);
        
        singleton = type.isAnnotationPresent(Singleton.class);
        getFieldInformation();
        getMethodInformation();
    }
    
    /**
     * Iterates through every injection set in order of inheritance to determine with method should be invoked when injection values.
     */
    protected void getMethodInformation() {
        List<Method> methods = new ArrayList<Method>();
        List<Method> staticMethods = new ArrayList<Method>();
        List<Method> toRemove = new ArrayList<Method>();
        Map<String, Method> map = new HashMap<String, Method>();
        StringBuilder tmp = new StringBuilder();
        String pckKey, shortKey;
        boolean candidate;
        Method oldMethod;
        int mod;
        String pckName;
        
        for (InjectionSet set : injectionSets) {
            methods.clear();
            for (Method method : set.type.getDeclaredMethods()) {
                candidate = method.isAnnotationPresent(Inject.class) && !Modifier.isAbstract(method.getModifiers());
                pckName = getPackageName(method) + ' ';
                
                tmp.delete(0, tmp.length());
                tmp.append(method.getReturnType()).append(' ').append(method.getName()).append(' ').append(Arrays.toString(method.getParameterTypes()));
                shortKey = tmp.toString();
                tmp.insert(0, pckName);
                pckKey = tmp.toString();
                
                if (map.containsKey(pckKey) && (oldMethod = map.get(pckKey)) != null) {
                    mod = oldMethod.getModifiers();
                    if (!(Modifier.isPrivate(mod) || Modifier.isStatic(mod) || mod == 0) || isSamePackage(oldMethod, method)) {
                        toRemove.add(map.get(pckKey));
                    }
                } else if (map.containsKey(shortKey) && (oldMethod = map.get(shortKey)) != null) {
                    mod = oldMethod.getModifiers();
                    if (Modifier.isPublic(mod) || Modifier.isProtected(mod)) {
                        toRemove.add(map.get(shortKey));
                    }
                }
                
                if (candidate) {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    
                    map.put(pckKey, method);
                    map.put(shortKey, method);
                    methods.add(method);
                }
            }
            set.methods = methods.toArray(EMPTY_METHOD_ARRAY);
        }
        
        for (InjectionSet set : injectionSets) {
            methods.clear();
            staticMethods.clear();
            for (Method method : set.methods) {
                if (!toRemove.contains(method)) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        staticMethods.add(method);
                    } else {
                        methods.add(method);
                    }
                }
            }
            
            set.methods = methods.toArray(EMPTY_METHOD_ARRAY);
            set.staticMethods = staticMethods.toArray(EMPTY_METHOD_ARRAY);
        }
    }
    
    /**
     * Gets the name of the package out of the method.
     */
    protected String getPackageName(Method method) {
        String name;
        int index = (name = method.getDeclaringClass().getName()).lastIndexOf('.');
        
        if (index == -1) {
            return "";
        } else {
            return name.substring(0, index);
        }
    }
    
    /**
     * Indicates if the two method are in the same package (not necessarily code base).
     * This is to avoid the removal of methods that are package private and not overridden by same-named methods in different-packaged subclasses.
     */
    protected boolean isSamePackage(Method oldMethod, Method method) {
        return oldMethod.getDeclaringClass().getPackage().equals(method.getDeclaringClass().getPackage());
    }
    
    /**
     * Iterates through all non-static fields in inheritance order.
     */
    protected void getFieldInformation() {
        Field[] fields;
        List<Field> objectFields = new ArrayList<Field>();
        List<Field> staticFields = new ArrayList<Field>();
        
        for (InjectionSet set : injectionSets) {
            objectFields.clear();
            staticFields.clear();
            fields = set.type.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Inject.class) && !Modifier.isFinal(field.getModifiers())) {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        
                        if (Modifier.isStatic(field.getModifiers())) {
                            staticFields.add(field);
                        } else {
                            objectFields.add(field);
                        }
                    }
                }
            }
            
            set.fields = objectFields.toArray(EMPTY_FIELD_ARRAY);
            set.staticFields = staticFields.toArray(EMPTY_FIELD_ARRAY);
        }
    }
    
    public Provider<?> getProvider() {
        return provider;
    }
    
    public void setProvider(Provider<?> provider) {
        this.provider = provider;
    }
    
    public InjectionSet[] getInjectionSets() {
        return injectionSets;
    }
    
    public void setInjectionSets(InjectionSet[] injectionSets) {
        this.injectionSets = injectionSets;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public void setType(Class<?> type) {
        this.type = type;
    }
    
    public Constructor<?> getConstructor() {
        return constructor;
    }
    
    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }
    
    public boolean isSingleton() {
        return singleton;
    }
    
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
    
    public Method getFactoryMethod() {
        return factoryMethod;
    }
    
    public void setFactoryMethod(Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }
    
    public InstanceMode getInstanceMode() {
        return instanceMode;
    }
    
    public void setInstanceMode(InstanceMode instanceMode) {
        this.instanceMode = instanceMode;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName());
        builder.append(" [injectionSets=");
        builder.append(Arrays.toString(injectionSets));
        builder.append(",\ntype=");
        builder.append(type);
        builder.append(",\nconstructor=");
        builder.append(constructor);
        builder.append(",\nfactoryMethod=");
        builder.append(factoryMethod);
        builder.append(",\nsingleton=");
        builder.append(singleton);
        builder.append(",\ninstanceMode=");
        builder.append(instanceMode);
        builder.append("]");
        return builder.toString();
    }
    
}