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

class TypeContainer {
    
    private static final Field[] EMPTY_FIELD_ARRAY = new Field[] {};
    private static final Method[] EMPTY_METHOD_ARRAY = new Method[] {};
    private static final InjectionSet[] EMPTY_INJECTIONSET_ARRAY = new InjectionSet[] {};
    
    protected InjectionSet[] injectionSets;
    protected Class<?> type;
    protected Constructor<?> constructor;
    
    public TypeContainer(Class<?> type, Constructor<?> constructor) {
        this.type = type;
        this.constructor = constructor;
    }
    
    protected void gatherInformation() {
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
        
        getFieldInformation();
        getMethodInformation();
    }
    
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
                pckName = method.getDeclaringClass().getPackage().getName() + ' ';
                
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
    
    protected boolean isSamePackage(Method oldMethod, Method method) {
        return oldMethod.getDeclaringClass().getPackage().equals(method.getDeclaringClass().getPackage());
    }
    
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
        builder.append("]");
        return builder.toString();
    }
    
}