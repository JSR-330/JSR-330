package com.github.jsr330.spi.config.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;

import com.github.jsr330.instance.TypeContainer;
import com.github.jsr330.instance.TypeContainer.InstanceMode;
import com.github.jsr330.spi.TypeConfig;

public class DefaultBinderTypedTest {
    
    InitialBinder<Object> defaultBinder;
    
    @Before
    public void init() {
        defaultBinder = new DefaultBinder<Object>();
    }
    
    @Test
    public void bindingIncomplete() {
        try {
            defaultBinder.instance(TestInterface.class).build();
            fail();
        } catch (BinderException e) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @Test
    public void interfaceAsImplementation() {
        try {
            defaultBinder.instance(TestInterface.class).as(TestInterface.class);
            fail();
        } catch (BinderException e) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @Test
    public void interfaceAsImplementationAndSingleton() {
        try {
            defaultBinder.instance(TestInterface.class).asSingleton(TestInterface.class);
            fail();
        } catch (BinderException e) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @Test
    public void interfaceAsSingleton() {
        try {
            defaultBinder.instance(TestInterface.class).asSingleton();
            fail();
        } catch (BinderException e) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @Test
    public void simpleBinding() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).as(TestImplementation1.class).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @Test
    public void simpleBindingAsSingleton() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).asSingleton(TestImplementation1.class).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @Test
    public void simpleTypeAsSingleton() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton().build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @Test
    public void simpleType() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @Test
    public void simpleTypeInheritance() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation2.class).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation2.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
    }
    
    @Test
    public void interfaceWithNullProvider() {
        try {
            defaultBinder.instance(TestInterface.class).with(null);
            fail();
        } catch (NullPointerException exception) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @Test
    public void simpleTypeWithNullProvider() {
        try {
            defaultBinder.instance(TestImplementation1.class).with(null);
            fail();
        } catch (NullPointerException exception) {
        } catch (Throwable t) {
            fail();
        }
    }
    
    @Test
    public void interfaceWithProvider() {
        TypeConfig config = defaultBinder.instance(TestInterface.class).with(new Provider<TestInterface>() {
            
            @Override
            public TestInterface get() {
                return new TestImplementation1();
            }
            
        }).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestInterface.class, null, null, null);
        assertNotNull(container);
        assertNotNull(container.getProvider());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.PROVIDER, container.getInstanceMode());
    }
    
    @Test
    public void simpleTypeWithProvider() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).with(new Provider<TestImplementation1>() {
            
            @Override
            public TestImplementation1 get() {
                return new TestImplementation1();
            }
            
        }).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertNotNull(container.getProvider());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.PROVIDER, container.getInstanceMode());
    }
    
}
