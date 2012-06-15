package com.github.jsr330;

import java.util.Map;

import junit.framework.Test;

import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;

import com.github.jsr330.analysis.ClassAnalyser;
import com.github.jsr330.analysis.InheritanceAnalyser;
import com.github.jsr330.instance.ClassInstancer;
import com.github.jsr330.instance.DefaultClassInstancer;
import com.github.jsr330.scanning.ClassScanner;
import com.github.jsr330.scanning.DefaultClassScanner;
import com.github.jsr330.scanning.RegExSourceDirFilter;

public class MyTck {
    
    public static Test suite() {
        ClassAnalyser<Map<String, Class<?>[]>> analyser = new InheritanceAnalyser();
        ClassInstancer instancer = new DefaultClassInstancer();
        ClassScanner scanner = new DefaultClassScanner(new RegExSourceDirFilter(".*javax\\.inject-tck-1\\.jar"), null);
        
        Injector injector = new Injector(Thread.currentThread().getContextClassLoader(), scanner, analyser, instancer);
        Car car = injector.getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
    
}
