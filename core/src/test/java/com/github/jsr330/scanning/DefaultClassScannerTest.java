package com.github.jsr330.scanning;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.jsr330.GenericFilter;
import com.github.jsr330.spi.ClassScanner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultClassScannerTest {
    
    DefaultClassScanner defaultClassScanner;
    @Mock
    ClassScanner parent;
    @Mock
    GenericFilter<URI> sourceDirFilter;
    @Mock
    GenericFilter<String> classNameFilter;
    @Captor
    ArgumentCaptor<URI> sourceDirFilterParameter;
    @Captor
    ArgumentCaptor<String> classNameFilterParameter;
    
    @Before
    public void init() {
        when(sourceDirFilter.filter(any(URI.class))).thenReturn(true);
        when(classNameFilter.filter(any(String.class))).thenReturn(true);
        
        defaultClassScanner = new DefaultClassScanner();
    }
    
    @Test
    public void defaultConstructor() {
        assertNull(defaultClassScanner.parent);
        assertNull(defaultClassScanner.classNameFilter);
        assertNull(defaultClassScanner.sourceDirFilter);
    }
    
    @Test
    public void parentConstructor() {
        defaultClassScanner = new DefaultClassScanner(parent);
        assertSame(parent, defaultClassScanner.parent);
        assertNull(defaultClassScanner.classNameFilter);
        assertNull(defaultClassScanner.sourceDirFilter);
    }
    
    @Test
    public void filterConstructor() {
        defaultClassScanner = new DefaultClassScanner(sourceDirFilter, classNameFilter);
        assertNull(defaultClassScanner.parent);
        assertSame(classNameFilter, defaultClassScanner.classNameFilter);
        assertSame(sourceDirFilter, defaultClassScanner.sourceDirFilter);
    }
    
    @Test
    public void filterAndParentConstructor() {
        defaultClassScanner = new DefaultClassScanner(parent, sourceDirFilter, classNameFilter);
        assertSame(parent, defaultClassScanner.parent);
        assertSame(classNameFilter, defaultClassScanner.classNameFilter);
        assertSame(sourceDirFilter, defaultClassScanner.sourceDirFilter);
    }
    
    @Test
    public void scanWithoutParentAndFilter() {
        Map<String, Class<?>> classes = defaultClassScanner.scan(Thread.currentThread().getContextClassLoader());
        
        verifyZeroInteractions(parent);
        verifyZeroInteractions(classNameFilter);
        verifyZeroInteractions(sourceDirFilter);
        
        assertTrue(classes.containsKey(Test.class.getName()));
    }
    
    @Test
    public void scanWithoutFilter() {
        defaultClassScanner = new DefaultClassScanner(parent);
        Map<String, Class<?>> classes = defaultClassScanner.scan(Thread.currentThread().getContextClassLoader());
        
        verifyZeroInteractions(classNameFilter);
        verifyZeroInteractions(sourceDirFilter);
        
        assertTrue(classes.containsKey(Test.class.getName()));
        verify(parent).scan(Thread.currentThread().getContextClassLoader());
    }
    
    @Test
    public void scanWithoutParent() throws Exception {
        boolean scannedJUnit = false;
        boolean scannedTest = false;
        
        defaultClassScanner = new DefaultClassScanner(sourceDirFilter, classNameFilter);
        Map<String, Class<?>> classes = defaultClassScanner.scan(Thread.currentThread().getContextClassLoader());
        
        verifyZeroInteractions(parent);
        
        assertTrue(classes.containsKey(Test.class.getName()));
        verify(sourceDirFilter, atLeast(1)).filter(sourceDirFilterParameter.capture());
        verify(classNameFilter, atLeast(1)).filter(classNameFilterParameter.capture());
        
        for (URI uri : sourceDirFilterParameter.getAllValues()) {
            if (uri.toURL().toExternalForm().matches(".*junit-.*\\.jar")) {
                scannedJUnit = true;
                break;
            }
        }
        if (!scannedJUnit) {
            fail();
        }
        
        for (String name : classNameFilterParameter.getAllValues()) {
            if (name.equals(Test.class.getName())) {
                scannedTest = true;
                break;
            }
        }
        if (!scannedTest) {
            fail();
        }
    }
    
    @Test
    public void scanWithParentAndFilter() throws Exception {
        boolean scannedJUnit = false;
        boolean scannedTest = false;
        
        defaultClassScanner = new DefaultClassScanner(parent, sourceDirFilter, classNameFilter);
        Map<String, Class<?>> classes = defaultClassScanner.scan(Thread.currentThread().getContextClassLoader());
        
        assertTrue(classes.containsKey(Test.class.getName()));
        verify(parent).scan(Thread.currentThread().getContextClassLoader());
        verify(sourceDirFilter, atLeast(1)).filter(sourceDirFilterParameter.capture());
        verify(classNameFilter, atLeast(1)).filter(classNameFilterParameter.capture());
        
        for (URI uri : sourceDirFilterParameter.getAllValues()) {
            if (uri.toURL().toExternalForm().matches(".*junit-.*\\.jar")) {
                scannedJUnit = true;
                break;
            }
        }
        if (!scannedJUnit) {
            fail();
        }
        
        for (String name : classNameFilterParameter.getAllValues()) {
            if (name.equals(Test.class.getName())) {
                scannedTest = true;
                break;
            }
        }
        if (!scannedTest) {
            fail();
        }
    }
    
}
