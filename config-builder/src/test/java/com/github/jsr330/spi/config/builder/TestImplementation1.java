package com.github.jsr330.spi.config.builder;

public class TestImplementation1 implements TestInterface {
    
    public static TestImplementation1 instance1() {
        return new TestImplementation1();
    }
    
    public static TestImplementation1 instance2(String setting1, String setting2) {
        return new TestImplementation1(setting1, setting2);
    }
    
    public TestImplementation1 instance3() {
        return new TestImplementation1();
    }
    
    public TestImplementation1 instance4(String setting1, String setting2) {
        return new TestImplementation1(setting1, setting2);
    }
    
    public TestImplementation1() {
    }
    
    public TestImplementation1(String setting1, String setting2) {
    }
    
}