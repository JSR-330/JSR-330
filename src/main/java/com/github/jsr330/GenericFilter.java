package com.github.jsr330;

public interface GenericFilter<T> {
    
    boolean filter(T value);
    
}
