package com.github.jsr330.instance;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.jsr330.spi.ClassInjector;

@RunWith(MockitoJUnitRunner.class)
public class SimpleProviderTest {
    
    SimpleProvider<Runnable> simpleProvider;
    Class<Runnable> type = Runnable.class;
    @Mock
    ClassInjector instancer;
    @Mock
    Map<String, Class<? extends Runnable>[]> inheritanceTree;
    @Mock
    Annotation qualifier;
    @Mock
    ClassLoader classLoader;
    
    @Before
    public void init() {
        simpleProvider = new SimpleProvider<Runnable>(type, instancer, inheritanceTree, qualifier, classLoader);
    }
    
    @Test
    public void get() {
        simpleProvider.get();
        verify(instancer).instance(same(type), same(inheritanceTree), same(classLoader), (Class<?>[]) eq(null), same(qualifier));
    }
    
}
