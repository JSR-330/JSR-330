package com.github.jsr330.spi.config.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.jsr330.instance.TypeContainer;
import com.github.jsr330.instance.TypeContainer.InstanceMode;
import com.github.jsr330.spi.ClassInjector;
import com.github.jsr330.spi.TypeConfig;

public class DefaultBinderLinkingTest {
    
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
    
    @SuppressWarnings("unchecked")
    @Test
    public void nullAndCondition() {
        try {
            defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) confirming).and(null);
            fail();
        } catch (NullPointerException exception) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void nullOrCondition() {
        try {
            defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) confirming).or(null);
            fail();
        } catch (NullPointerException exception) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void nullXorCondition() {
        try {
            defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) confirming).xor(null);
            fail();
        } catch (NullPointerException exception) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void andConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) confirming)
                .and((BindingCondition<TestInterface>) confirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void andNonConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) confirming)
                .and((BindingCondition<TestInterface>) nonConfirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNull(container);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void orConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) confirming)
                .or((BindingCondition<TestInterface>) confirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void orNonConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) nonConfirming)
                .or((BindingCondition<TestInterface>) confirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void xorConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) confirming)
                .xor((BindingCondition<TestInterface>) confirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNull(container);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void xorNonConfirming() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).when((BindingCondition<TestInterface>) confirming)
                .xor((BindingCondition<TestInterface>) nonConfirming).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
}
