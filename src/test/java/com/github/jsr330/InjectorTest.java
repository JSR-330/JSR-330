package com.github.jsr330;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.jsr330.Injector;

@RunWith(MockitoJUnitRunner.class)
public class InjectorTest {
    
    @Mock
    Object mock;
    
    @Test
    public void mainTest() {
        new Injector();
    }
    
}
