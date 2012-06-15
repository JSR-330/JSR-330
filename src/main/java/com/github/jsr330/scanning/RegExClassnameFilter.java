package com.github.jsr330.scanning;

import java.util.regex.Pattern;

import com.github.jsr330.GenericFilter;

public class RegExClassnameFilter implements GenericFilter<String> {
    
    protected Pattern regex;
    
    public RegExClassnameFilter(String regex) {
        this.regex = Pattern.compile(regex);
    }
    
    public RegExClassnameFilter(Pattern regex) {
        this.regex = regex;
    }
    
    @Override
    public boolean filter(String value) {
        return regex.matcher(value).matches();
    }
    
}
