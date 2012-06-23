package com.github.jsr330.spi.config.builder;

import static com.github.jsr330.spi.config.builder.Constructors.constructor;
import static com.github.jsr330.spi.config.builder.Constructors.defaultConstructor;
import static com.github.jsr330.spi.config.builder.Methods.factoryMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.jsr330.instance.TypeContainer;
import com.github.jsr330.instance.TypeContainer.InstanceMode;
import com.github.jsr330.spi.TypeConfig;

public class DefaultBinderInstancingTest {
    
    InitialBinder<Object> defaultBinder;
    
    @Before
    public void init() {
        defaultBinder = new DefaultBinder<Object>();
    }
    
    @Test
    public void usingDefaultConstructor() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(defaultConstructor(TestImplementation1.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertEquals(defaultConstructor(TestImplementation1.class), container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void usingCustomConstructor() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(constructor(TestImplementation1.class, String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertEquals(constructor(TestImplementation1.class, String.class, String.class), container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void usingNotExistingConstructor() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(constructor(TestImplementation1.class, String.class, Integer.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingDefaultConstructor() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(defaultConstructor(TestImplementation1.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertEquals(defaultConstructor(TestImplementation1.class), container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingCustomConstructor() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(constructor(TestImplementation1.class, String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertEquals(constructor(TestImplementation1.class, String.class, String.class), container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingNotExistingConstructor() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(constructor(TestImplementation1.class, String.class, Integer.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void usingStaticFactoryMethodWithoutParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class).using(factoryMethod(TestImplementation1.class))
                .build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.FACTORY_METHOD, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertEquals(factoryMethod(TestImplementation1.class), container.getFactoryMethod());
    }
    
    @Test
    public void usingStaticFactoryMethodWithParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.FACTORY_METHOD, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertEquals(factoryMethod(TestImplementation1.class, String.class, String.class), container.getFactoryMethod());
    }
    
    @Test
    public void usingUnexistingMethod() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, String.class, Integer.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void usingNamedStaticFactoryMethodWithoutParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance1")).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.FACTORY_METHOD, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertEquals(factoryMethod(TestImplementation1.class, "instance1"), container.getFactoryMethod());
    }
    
    @Test
    public void usingNamedStaticFactoryMethodWithParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance2", String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.FACTORY_METHOD, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertEquals(factoryMethod(TestImplementation1.class, "instance2", String.class, String.class), container.getFactoryMethod());
    }
    
    @Test
    public void usingUnexistingNamedMethodWithoutParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance123")).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void usingUnexistingNamedMethodWithParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance123", String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void usingNonStaticMethodWithoutParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance3")).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void usingNonStaticMethodWithParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).as(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance4", String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertFalse(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingStaticFactoryMethodWithoutParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.FACTORY_METHOD, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertEquals(factoryMethod(TestImplementation1.class), container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingStaticFactoryMethodWithParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.FACTORY_METHOD, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertEquals(factoryMethod(TestImplementation1.class, String.class, String.class), container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingUnexistingMethod() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, String.class, Integer.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingNamedStaticFactoryMethodWithoutParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance1")).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.FACTORY_METHOD, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertEquals(factoryMethod(TestImplementation1.class, "instance1"), container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingNamedStaticFactoryMethodWithParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance2", String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.FACTORY_METHOD, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertEquals(factoryMethod(TestImplementation1.class, "instance2", String.class, String.class), container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingUnexistingNamedMethodWithoutParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance123")).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingUnexistingNamedMethodWithParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance123", String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingNonStaticMethodWithoutParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance3")).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
    @Test
    public void singletonUsingNonStaticMethodWithParameters() {
        TypeConfig config = defaultBinder.instance(TestImplementation1.class).asSingleton(TestImplementation1.class)
                .using(factoryMethod(TestImplementation1.class, "instance4", String.class, String.class)).build();
        TypeContainer container;
        
        container = config.getTypeContainer(null, TestImplementation1.class, null, null, null);
        assertNotNull(container);
        assertEquals(TestImplementation1.class, container.getType());
        assertTrue(container.isSingleton());
        assertEquals(InstanceMode.CONSTRUCTOR, container.getInstanceMode());
        assertNull(container.getConstructor());
        assertNull(container.getFactoryMethod());
    }
    
}
