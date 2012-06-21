package com.github.jsr330.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

public class InheritanceAnalyserTest {
    
    InheritanceAnalyser inheritanceAnalyser;
    
    @Before
    public void init() {
        inheritanceAnalyser = new InheritanceAnalyser();
    }
    
    @Test
    public void analyse_NoClasses() {
        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
        Map<String, Class<?>[]> inheritances;
        
        inheritances = inheritanceAnalyser.analyse(classes);
        
        assertNotNull(inheritances);
        assertEquals(0, inheritances.size());
    }
    
    @Test
    public void analyse_NoInheritances() {
        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
        Map<String, Class<?>[]> inheritances;
        
        classes.put(Object.class.getName(), Object.class);
        classes.put(CharSequence.class.getName(), CharSequence.class);
        classes.put(Runnable.class.getName(), Runnable.class);
        
        inheritances = inheritanceAnalyser.analyse(classes);
        
        assertNotNull(inheritances);
        assertEquals(0, inheritances.size());
    }
    
    @Test
    public void analyse_Inheritances() {
        // need TreeMap for Sorting
        Map<String, Class<?>> classes = new TreeMap<String, Class<?>>();
        Map<String, Class<?>[]> inheritances;
        
        classes.put(Object.class.getName(), Object.class);
        classes.put(CharSequence.class.getName(), CharSequence.class);
        classes.put(Runnable.class.getName(), Runnable.class);
        classes.put(Thread.class.getName(), Thread.class);
        classes.put(String.class.getName(), String.class);
        
        inheritances = inheritanceAnalyser.analyse(classes);
        
        assertNotNull(inheritances);
        assertEquals(3, inheritances.size());
        assertEquals(Thread.class, inheritances.get(Runnable.class.getName())[0]);
        assertEquals(String.class, inheritances.get(CharSequence.class.getName())[0]);
        assertEquals(String.class, inheritances.get(Object.class.getName())[0]);
        assertEquals(Thread.class, inheritances.get(Object.class.getName())[1]);
    }
    
}
