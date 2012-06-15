package com.github.jsr330.analysis;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InheritanceAnalyser implements ClassAnalyser<Map<String, Class<?>[]>> {
    
    private static final Class<?>[] EMPTY_ARRAY = new Class<?>[] {};
    private static final Logger LOGGER = LoggerFactory.getLogger(InheritanceAnalyser.class);
    
    @Override
    public Map<String, Class<?>[]> analyse(Map<String, Class<?>> classes) {
        Map<String, Class<?>[]> inheritances = new TreeMap<String, Class<?>[]>();
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
                inheritances.put(entry.getKey(), tmp.toArray(EMPTY_ARRAY));
            }
        }
        
        return inheritances;
    }
    
}
