package com.github.jsr330;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassAnalyser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassAnalyser.class);
    
    public Map<String, List<Class<?>>> getInheritanceTree(Map<String, Class<?>> classes) {
        Map<String, List<Class<?>>> inheritances = new TreeMap<String, List<Class<?>>>();
        List<Class<?>> tmp;
        Set<String> classNames = classes.keySet();
        Class<?> type;
        
        for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
            tmp = null;
            LOGGER.debug("getInheritanceTree - looking for {}", entry.getKey());
            for (String className : classNames) {
                if (!entry.getKey().equals(className)) {
                    type = classes.get(className);
                    if (entry.getValue().isAssignableFrom(type) && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
                        if (tmp == null) {
                            tmp = new ArrayList<Class<?>>();
                        }
                        tmp.add(type);
                        LOGGER.debug("getInheritanceTree - found {}", type);
                    }
                }
            }
            if (tmp != null && !tmp.isEmpty()) {
                inheritances.put(entry.getKey(), tmp);
            }
        }
        
        return inheritances;
    }
    
}
