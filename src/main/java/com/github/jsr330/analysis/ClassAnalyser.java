package com.github.jsr330.analysis;

import java.util.Map;

public interface ClassAnalyser<T> {
    
    T analyse(Map<String, Class<?>> classes);
    
}
