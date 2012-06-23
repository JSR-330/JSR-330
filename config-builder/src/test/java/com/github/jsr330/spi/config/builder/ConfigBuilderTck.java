/**
 * Copyright 2012 the contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.github.jsr330.spi.config.builder;

import static com.github.jsr330.spi.config.builder.BindingConditions.isNamed;
import static com.github.jsr330.spi.config.builder.BindingConditions.qualifierIs;

import java.util.Map;

import junit.framework.Test;

import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.accessories.SpareTire;

import com.github.jsr330.Injector;
import com.github.jsr330.analysis.InheritanceAnalyser;
import com.github.jsr330.instance.DefaultClassInjector;
import com.github.jsr330.scanning.DefaultClassScanner;
import com.github.jsr330.scanning.RegExSourceDirFilter;
import com.github.jsr330.spi.ClassAnalyser;
import com.github.jsr330.spi.ClassInjector;
import com.github.jsr330.spi.ClassScanner;

public class ConfigBuilderTck {
    
    public static Test suite() {
        InitialBinder<?> binder;
        ConfigBuilder builder = new ConfigBuilder();
        
        ClassAnalyser<Map<String, Class<?>[]>> analyser = new InheritanceAnalyser();
        ClassInjector instancer;
        ClassScanner scanner = new DefaultClassScanner(new RegExSourceDirFilter(".*javax\\.inject-tck-1\\.jar"), null);
        
        binder = builder.get();
        
        binder.instance(Car.class).as(Convertible.class);
        binder.instance(Seat.class).as(DriversSeat.class).when(qualifierIs(Seat.class, Drivers.class));
        binder.instance(Tire.class).as(SpareTire.class).when(isNamed(Tire.class, "spare"));
        
        instancer = new DefaultClassInjector(binder.build());
        
        Injector injector = new Injector(Thread.currentThread().getContextClassLoader(), scanner, analyser, instancer);
        Car car = injector.getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
    
}
