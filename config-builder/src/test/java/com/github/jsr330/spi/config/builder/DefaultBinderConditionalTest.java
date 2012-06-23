package com.github.jsr330.spi.config.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.jsr330.instance.TypeContainer;
import com.github.jsr330.instance.TypeContainer.InstanceMode;
import com.github.jsr330.spi.ClassInjector;
import com.github.jsr330.spi.TypeConfig;

public class DefaultBinderConditionalTest {
    
    InitialBinder<Object> defaultBinder;
    BindingCondition<?> nonConfirming = new BindingCondition<Object>() {
        
        @Override
        public boolean fulfilled(ClassInjector injector, Class<Object> type, Map<String, Class<? extends Object>[]> inheritanceTree, Annotation qualifier,
                ClassLoader classLoader) {
            return false;
        }
        
    };
    BindingCondition<?> confirming = new BindingCondition<Object>() {
        
        @Override
        public boolean fulfilled(ClassInjector injector, Class<Object> type, Map<String, Class<? extends Object>[]> inheritanceTree, Annotation qualifier,
                ClassLoader classLoader) {
            return true;
        }
        
    };
    
    @Before
    public void init() {
        defaultBinder = new DefaultBinder<Object>();
    }
    
    @Test
    public void nullConditional() {
        try {
            defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when(null);
            fail();
        } catch (NullPointerException exception) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void conditionalInterfaceNonConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) nonConfirming)
                .build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNull(container);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void conditionalInterfaceConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) confirming)
                .build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void conditionalSingletonInterfaceNonConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).asSingleton(TestImplementation1.class)
                .when((BindingCondition<TestInterface>) nonConfirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNull(container);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void conditionalSingletonInterfaceConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).asSingleton(TestImplementation1.class)
                .when((BindingCondition<TestInterface>) confirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void conditionalSimpleTypeNonConfirming() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .when((BindingCondition<TestImplementation1>) nonConfirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNull(container);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void conditionalSimpleTypeConfirming() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .when((BindingCondition<TestImplementation1>) confirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void conditionalSingletonSimpleTypeNonConfirming() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .when((BindingCondition<TestImplementation1>) nonConfirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNull(container);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void conditionalSingletonSimpleTypeConfirming() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .when((BindingCondition<TestImplementation1>) confirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
}
