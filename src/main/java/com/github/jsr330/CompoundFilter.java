package com.github.jsr330;


public class CompoundFilter<T> implements GenericFilter<T> {
    
    public enum Mode {
        AND, OR, XOR
    }
    
    protected GenericFilter<T>[] filters;
    protected Mode mode;
    
    public CompoundFilter(Mode mode, GenericFilter<T>... filters) {
        this.filters = filters;
        this.mode = mode;
    }
    
    public Mode getMode() {
        return mode;
    }
    
    @Override
    public boolean filter(T value) {
        if (filters.length > 0) {
            if (mode == Mode.AND) {
                for (GenericFilter<T> filter : filters) {
                    if (!filter.filter(value)) {
                        return false;
                    }
                }
            } else if (mode == Mode.OR) {
                for (GenericFilter<T> filter : filters) {
                    if (filter.filter(value)) {
                        return true;
                    }
                }
                return false;
            } else if (mode == Mode.XOR && filters.length == 2) {
                return filters[0].filter(value) != filters[1].filter(value);
            }
        }
        
        return true;
    }
    
}
