package com.github.jsr330.instance;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.jsr330.spi.TypeConfig;
import com.github.jsr330.spi.TypeDeterminator;

@RunWith(MockitoJUnitRunner.class)
public class DefaultClassInjectorTest {
    
    public static class Bean {
        
        @Inject
        static void injectionPoint() {
        }
        
    }
    
    DefaultClassInjector defaultClassInjector;
    @Mock
    TypeDeterminator typeDeterminator;
    @Mock
    TypeConfig typeConfig;
    @Mock
    TypeContainer typeContainer;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    InjectionSet injectionSet;
    Map<String, Class<?>> classes;
    Map<String, Class<?>[]> inheritanceTree;
    ClassLoader classLoader;
    
    @Before
    public void init() {
        classes = new HashMap<String, Class<?>>();
        inheritanceTree = new HashMap<String, Class<?>[]>();
        classLoader = Thread.currentThread().getContextClassLoader();
        typeContainer.injectionSets = new InjectionSet[] { injectionSet };
        
        when(injectionSet.getFields()).thenReturn(new Field[] {});
        when(injectionSet.getStaticFields()).thenReturn(new Field[] {});
        when(injectionSet.getMethods()).thenReturn(new Method[] {});
        when(injectionSet.getStaticMethods()).thenReturn(new Method[] {});
        
        defaultClassInjector = spy(new DefaultClassInjector());
    }
    
    @Test
    public void defaultConstructor() {
        assertNull(defaultClassInjector.config);
        assertNotNull(defaultClassInjector.typeDeterminator);
    }
    
    @Test
    public void constructorWithTypeDeterminator() {
        defaultClassInjector = new DefaultClassInjector(typeDeterminator);
        assertNull(defaultClassInjector.config);
        assertSame(typeDeterminator, defaultClassInjector.typeDeterminator);
    }
    
    @Test
    public void constructorWithTypeConfig() {
        defaultClassInjector = new DefaultClassInjector(typeConfig);
        assertSame(typeConfig, defaultClassInjector.config);
        assertNotNull(defaultClassInjector.typeDeterminator);
    }
    
    @Test
    public void constructorWithTypeConfigAndTypeDeterminator() {
        defaultClassInjector = new DefaultClassInjector(typeConfig, typeDeterminator);
        assertSame(typeConfig, defaultClassInjector.config);
        assertSame(typeDeterminator, defaultClassInjector.typeDeterminator);
    }
    
    @Test
    public void injectStaticMembers_NoClasses() {
        defaultClassInjector.injectStaticMembers(classes, inheritanceTree, classLoader);
        
        verifyZeroInteractions(typeConfig, typeDeterminator);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void injectStaticMembers_WithClasses() {
        classes.put(Bean.class.getName(), Bean.class);
        
        when(defaultClassInjector.generateTypeContainer(eq(Bean.class), any(Map.class), (Annotation) eq(null), same(classLoader))).thenReturn(typeContainer);
        defaultClassInjector.injectStaticMembers(classes, inheritanceTree, classLoader);
        
        verify(defaultClassInjector).generateTypeContainer(eq(Bean.class), any(Map.class), (Annotation) eq(null), same(classLoader));
        verify(defaultClassInjector).injectStaticFields(same(injectionSet), eq(null), any(Map.class), same(classLoader));
        verify(defaultClassInjector).injectStaticMethods(same(injectionSet), eq(null), any(Map.class), same(classLoader));
    }
    
    // TODO: test instance
    
}
