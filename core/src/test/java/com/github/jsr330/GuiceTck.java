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
package com.github.jsr330;

import javax.inject.Named;

import junit.framework.Test;

import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.Engine;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;
import org.junit.Ignore;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

@Ignore
public class GuiceTck {
    
    public static class MyModule extends AbstractModule {
        
        @Override
        protected void configure() {
            bind(Car.class).to(Convertible.class);
            bind(Seat.class).annotatedWith(Drivers.class).to(DriversSeat.class);
            bind(Engine.class).to(V8Engine.class);
            bind(Cupholder.class);
            bind(Tire.class);
            bind(FuelTank.class);
            requestStaticInjection(Convertible.class, SpareTire.class);
        }
        
        @Provides
        @Named("spare")
        Tire provideSpareTire(SpareTire spare) {
            return spare;
        }
        
    }
    
    public static Test suite() {
        Injector injector = Guice.createInjector(new MyModule());
        Car car = injector.getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
    
}
