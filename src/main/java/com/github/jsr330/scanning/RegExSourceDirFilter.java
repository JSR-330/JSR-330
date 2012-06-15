package com.github.jsr330.scanning;

import java.net.URI;
import java.util.regex.Pattern;

import com.github.jsr330.GenericFilter;

public class RegExSourceDirFilter implements GenericFilter<URI> {
    
    protected Pattern regex;
    
    public RegExSourceDirFilter(String regex) {
        this.regex = Pattern.compile(regex);
    }
    
    public RegExSourceDirFilter(Pattern regex) {
        this.regex = regex;
    }
    
    @Override
    public boolean filter(URI value) {
        return regex.matcher(value.toString()).matches();
    }
    
}
