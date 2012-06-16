package com.github.jsr330.analysis;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InheritanceAnalyser implements ClassAnalyser<Map<String, Class<?>[]>> {
    
    private static final String[] EMPTY_STRING_ARRAY = new String[] {};
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[] {};
    private static final Logger LOGGER = LoggerFactory.getLogger(InheritanceAnalyser.class);
    
    @Override
    public Map<String, Class<?>[]> analyse(Map<String, Class<?>> classes) {
        Map<String, Class<?>[]> inheritances = new TreeMap<String, Class<?>[]>();
        List<Class<?>> classList = new ArrayList<Class<?>>();
        String[] classNames = classes.keySet().toArray(EMPTY_STRING_ARRAY);
        Class<?> type;
        
        for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
            classList.clear();
            LOGGER.debug("analyse - looking for {}", entry.getKey());
            for (String className : classNames) {
                if (!entry.getKey().equals(className)) {
                    type = classes.get(className);
                    if (entry.getValue().isAssignableFrom(type) && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
                        classList.add(type);
                        LOGGER.debug("analyse - found {}", type);
                    }
                }
            }
            if (!classList.isEmpty()) {
                inheritances.put(entry.getKey(), classList.toArray(EMPTY_CLASS_ARRAY));
            }
        }
        
        return inheritances;
    }
    
}
