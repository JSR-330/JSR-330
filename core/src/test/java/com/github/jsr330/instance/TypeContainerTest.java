package com.github.jsr330.instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Test;

public class TypeContainerTest {
    
    public static class Bean1 {
        
        static final String notContained1_7 = "";
        @Inject
        static final String notContained1_8 = "";
        static String notContained1_9;
        @Inject
        static String contained1_3;
        
        final String notContained1_10 = "";
        @Inject
        final String notContained1_11 = "";
        String notContained1_12;
        @Inject
        String contained1_4;
        
        static void notContained1_6() {
        }
        
        @Inject
        static void contained1_2() {
        }
        
        void notContained1_3() {
        }
        
        @Inject
        void contained1_1() {
        }
        
    }
    
    public static class Bean2 extends Bean1 {
        
        static final String notContained2_7 = "";
        @Inject
        static final String notContained2_8 = "";
        static String notContained2_9;
        @Inject
        static String contained2_3;
        
        final String notContained2_10 = "";
        @Inject
        final String notContained2_11 = "";
        String notContained2_12;
        @Inject
        String contained2_4;
        
        static void notContained2_6() {
        }
        
        @Inject
        static void contained2_2() {
        }
        
        void notContained2_3() {
        }
        
        @Inject
        void contained2_1() {
        }
        
    }
    
    public static class Bean3 extends Bean2 {
        
        static final String notContained3_7 = "";
        @Inject
        static final String notContained3_8 = "";
        static String notContained3_9;
        @Inject
        static String contained3_3;
        
        final String notContained3_10 = "";
        @Inject
        final String notContained3_11 = "";
        String notContained3_12;
        @Inject
        String contained3_4;
        
        static void notContained3_6() {
        }
        
        @Inject
        static void contained3_2() {
        }
        
        void notContained3_3() {
        }
        
        @Inject
        void contained3_1() {
        }
        
    }
    
    @Singleton
    public static class Bean4 extends Bean3 {
        
        static final String notContained4_7 = "";
        @Inject
        static final String notContained4_8 = "";
        static String notContained4_9;
        @Inject
        static String contained4_3;
        
        final String notContained4_10 = "";
        @Inject
        final String notContained4_11 = "";
        String notContained4_12;
        @Inject
        String contained4_4;
        
        static void notContained4_6() {
        }
        
        @Inject
        static void contained4_2() {
        }
        
        void notContained4_3() {
        }
        
        @Inject
        void contained4_1() {
        }
        
    }
    
    TypeContainer typeContainer;
    
    @SuppressWarnings("unchecked")
    @Test
    public void gatherInformation_NoneSingleton() {
        Constructor<Bean3> ctor = null;
        
        for (Constructor<?> tmp : Bean3.class.getDeclaredConstructors()) {
            if (tmp.getParameterTypes().length == 0) {
                ctor = (Constructor<Bean3>) tmp;
                break;
            }
        }
        
        typeContainer = new TypeContainer(Bean3.class, ctor);
        
        assertNull(typeContainer.injectionSets);
        
        typeContainer.gatherInformation();
        
        assertNotNull(typeContainer.injectionSets);
        assertEquals(3, typeContainer.injectionSets.length);
        assertEquals(Bean1.class, typeContainer.injectionSets[0].type);
        assertEquals(Bean2.class, typeContainer.injectionSets[1].type);
        assertEquals(Bean3.class, typeContainer.injectionSets[2].type);
        assertFalse(typeContainer.singleton);
        
        assertEquals(1, typeContainer.injectionSets[0].methods.length);
        assertEquals("contained1_1", typeContainer.injectionSets[0].methods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[0].staticMethods.length);
        assertEquals("contained1_2", typeContainer.injectionSets[0].staticMethods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[0].staticFields.length);
        assertEquals("contained1_3", typeContainer.injectionSets[0].staticFields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[0].fields.length);
        assertEquals("contained1_4", typeContainer.injectionSets[0].fields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[1].methods.length);
        assertEquals("contained2_1", typeContainer.injectionSets[1].methods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[1].staticMethods.length);
        assertEquals("contained2_2", typeContainer.injectionSets[1].staticMethods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[1].staticFields.length);
        assertEquals("contained2_3", typeContainer.injectionSets[1].staticFields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[1].fields.length);
        assertEquals("contained2_4", typeContainer.injectionSets[1].fields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[2].methods.length);
        assertEquals("contained3_1", typeContainer.injectionSets[2].methods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[2].staticMethods.length);
        assertEquals("contained3_2", typeContainer.injectionSets[2].staticMethods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[2].staticFields.length);
        assertEquals("contained3_3", typeContainer.injectionSets[2].staticFields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[2].fields.length);
        assertEquals("contained3_4", typeContainer.injectionSets[2].fields[0].getName());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void gatherInformation_Singleton() {
        Constructor<Bean4> ctor = null;
        
        for (Constructor<?> tmp : Bean4.class.getDeclaredConstructors()) {
            if (tmp.getParameterTypes().length == 0) {
                ctor = (Constructor<Bean4>) tmp;
                break;
            }
        }
        
        typeContainer = new TypeContainer(Bean4.class, ctor);
        
        assertNull(typeContainer.injectionSets);
        
        typeContainer.gatherInformation();
        
        assertNotNull(typeContainer.injectionSets);
        assertEquals(4, typeContainer.injectionSets.length);
        assertEquals(Bean1.class, typeContainer.injectionSets[0].type);
        assertEquals(Bean2.class, typeContainer.injectionSets[1].type);
        assertEquals(Bean3.class, typeContainer.injectionSets[2].type);
        assertEquals(Bean4.class, typeContainer.injectionSets[3].type);
        assertTrue(typeContainer.singleton);
        
        assertEquals(1, typeContainer.injectionSets[0].methods.length);
        assertEquals("contained1_1", typeContainer.injectionSets[0].methods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[0].staticMethods.length);
        assertEquals("contained1_2", typeContainer.injectionSets[0].staticMethods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[0].staticFields.length);
        assertEquals("contained1_3", typeContainer.injectionSets[0].staticFields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[0].fields.length);
        assertEquals("contained1_4", typeContainer.injectionSets[0].fields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[1].methods.length);
        assertEquals("contained2_1", typeContainer.injectionSets[1].methods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[1].staticMethods.length);
        assertEquals("contained2_2", typeContainer.injectionSets[1].staticMethods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[1].staticFields.length);
        assertEquals("contained2_3", typeContainer.injectionSets[1].staticFields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[1].fields.length);
        assertEquals("contained2_4", typeContainer.injectionSets[1].fields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[2].methods.length);
        assertEquals("contained3_1", typeContainer.injectionSets[2].methods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[2].staticMethods.length);
        assertEquals("contained3_2", typeContainer.injectionSets[2].staticMethods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[2].staticFields.length);
        assertEquals("contained3_3", typeContainer.injectionSets[2].staticFields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[2].fields.length);
        assertEquals("contained3_4", typeContainer.injectionSets[2].fields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[3].methods.length);
        assertEquals("contained4_1", typeContainer.injectionSets[3].methods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[3].staticMethods.length);
        assertEquals("contained4_2", typeContainer.injectionSets[3].staticMethods[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[3].staticFields.length);
        assertEquals("contained4_3", typeContainer.injectionSets[3].staticFields[0].getName());
        
        assertEquals(1, typeContainer.injectionSets[3].fields.length);
        assertEquals("contained4_4", typeContainer.injectionSets[3].fields[0].getName());
    }
    
}
