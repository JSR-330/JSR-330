package com.github.jsr330;

import junit.framework.Test;

import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;

import com.github.jsr330.Injector;

public class MyTck {
    
    public static Test suite() {
        Car car = new Injector().getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
    
}
