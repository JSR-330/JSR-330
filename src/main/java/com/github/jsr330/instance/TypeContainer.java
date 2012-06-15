package com.github.jsr330.instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.inject.Inject;

class TypeContainer {
    
    private static final Comparator<Method> METHOD_COMPARATOR = new Comparator<Method>() {
        
        @Override
        public int compare(Method method1, Method method2) {
            return method1.toGenericString().compareTo(method2.toGenericString());
        }
        
    };
    private static Set<String> INJECTED_STATIC_MEMBERS = new TreeSet<String>();
    
    protected InjectionSet[] injectionSets;
    protected Class<?> type;
    protected Constructor<?> constructor;
    
    public TypeContainer(Class<?> type, Constructor<?> constructor) {
        this.type = type;
        this.constructor = constructor;
    }
    
    protected void fillTypeContainer() {
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
        
        this.injectionSets = injectionSets.toArray(new InjectionSet[] {});
        
        fillFields();
        fillMethods();
    }
    
    protected void fillMethods() {
        List<Method> methods = new ArrayList<Method>();
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
                if (Modifier.isStatic(method.getModifiers()) && INJECTED_STATIC_MEMBERS.contains(method.toString())) {
                    continue;
                }
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
            set.methods = methods.toArray(new Method[] {});
        }
        
        for (InjectionSet set : injectionSets) {
            methods.clear();
            for (Method method : set.methods) {
                if (!toRemove.contains(method)) {
                    methods.add(method);
                    if (Modifier.isStatic(method.getModifiers())) {
                        INJECTED_STATIC_MEMBERS.add(method.toString());
                    }
                }
            }
            set.methods = methods.toArray(new Method[] {});
            Arrays.sort(set.methods, METHOD_COMPARATOR);
        }
    }
    
    protected boolean isSamePackage(Method oldMethod, Method method) {
        return oldMethod.getDeclaringClass().getPackage().equals(method.getDeclaringClass().getPackage());
    }
    
    protected void fillFields() {
        Field[] fields;
        List<Field> list = new ArrayList<Field>();
        
        for (InjectionSet set : injectionSets) {
            list.clear();
            fields = set.type.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Inject.class) && !Modifier.isFinal(field.getModifiers())) {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        if (Modifier.isStatic(field.getModifiers()) && INJECTED_STATIC_MEMBERS.contains(field.toString())) {
                            continue;
                        }
                        
                        list.add(field);
                        if (Modifier.isStatic(field.getModifiers())) {
                            INJECTED_STATIC_MEMBERS.add(field.toString());
                        }
                    }
                }
            }
            set.fields = list.toArray(new Field[] {});
        }
    }
    
}