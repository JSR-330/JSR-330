package com.github.jsr330.scanning;

import java.util.Map;

public interface ClassScanner {
    
    Map<String, Class<?>> scan(ClassLoader loader);
    
}
