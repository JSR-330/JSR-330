package com.github.jsr330.spi.config.xml;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.jsr330.spi.config.builder.BindingCondition;
import com.github.jsr330.spi.config.builder.BindingConditions;
import com.github.jsr330.spi.config.builder.ConditionalBinder;
import com.github.jsr330.spi.config.builder.ConfigBuilder;
import com.github.jsr330.spi.config.builder.Constructors;
import com.github.jsr330.spi.config.builder.InstancingBinder;
import com.github.jsr330.spi.config.builder.LinkingBinder;
import com.github.jsr330.spi.config.builder.Methods;
import com.github.jsr330.spi.config.builder.TypeBinder;

/**
 * This {@link ContentHandler} parses a XML file to configure the {@link ConfigBuilder} passed to the constructor.
 */
public class ConfigHandler extends DefaultHandler {
    
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[] {};
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHandler.class);
    
    protected ClassLoader loader;
    protected ConfigBuilder builder;
    protected TypeBinder<?> typeBinder;
    protected ConditionalBinder<?> conditionalBinder = null;
    protected LinkingBinder<?> linkingBinder = null;
    protected List<Class<?>> classes = new ArrayList<Class<?>>();
    protected BindingCondition<?> condition;
    protected Class<?> typeToInstance;
    protected String instancing;
    protected String methodName;
    
    public ConfigHandler(ClassLoader loader, ConfigBuilder builder) {
        this.loader = loader;
        this.builder = builder;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String tmp = attributes.getValue("classname");
        Class<?> type = null;
        
        super.startElement(uri, localName, qName, attributes);
        
        if (tmp != null && tmp.trim().length() > 0) {
            try {
                type = Class.forName(tmp, false, loader);
            } catch (ClassNotFoundException exception) {
                LOGGER.debug("error while getting class.", exception);
            }
        }
        
        if (localName.equalsIgnoreCase("instance")) {
            typeToInstance = type;
            typeBinder = builder.get().instance(type);
        } else if (localName.equalsIgnoreCase("as")) {
            conditionalBinder = typeBinder.as((Class) type);
        } else if (localName.equalsIgnoreCase("asSingleton")) {
            if (type == null) {
                conditionalBinder = typeBinder.asSingleton();
            } else {
                conditionalBinder = typeBinder.as((Class) type);
            }
        } else if (localName.equalsIgnoreCase("with")) {
            try {
                conditionalBinder = typeBinder.with((Provider) type.newInstance());
            } catch (Exception exception) {
                LOGGER.debug("error while instancing provider.", exception);
            }
        } else if (localName.equalsIgnoreCase("annotation") || localName.equalsIgnoreCase("parameter")) {
            classes.add(type);
        } else if (localName.equalsIgnoreCase("annotationIsPresent")) {
            condition = BindingConditions.annotationIsPresent(typeToInstance, (Class) type);
        } else if (localName.equalsIgnoreCase("qualifierIs")) {
            condition = BindingConditions.qualifierIs(typeToInstance, (Class) type);
        } else if (localName.equalsIgnoreCase("isNamed")) {
            condition = BindingConditions.isNamed(typeToInstance, attributes.getValue("value"));
        } else if (localName.equalsIgnoreCase("isNamedIgnoringCase")) {
            condition = BindingConditions.isNamedIgnoringCase(typeToInstance, attributes.getValue("value"));
        } else if (localName.equalsIgnoreCase("using")) {
            instancing = attributes.getValue("type");
            methodName = attributes.getValue("name");
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        
        if (localName.equalsIgnoreCase("when")) {
            linkingBinder = conditionalBinder.when((BindingCondition) condition);
        } else if (localName.equalsIgnoreCase("and")) {
            linkingBinder = linkingBinder.and((BindingCondition) condition);
        } else if (localName.equalsIgnoreCase("or")) {
            linkingBinder = linkingBinder.or((BindingCondition) condition);
        } else if (localName.equalsIgnoreCase("xor")) {
            linkingBinder = linkingBinder.xor((BindingCondition) condition);
        } else if (localName.equalsIgnoreCase("using") && conditionalBinder instanceof InstancingBinder) {
            if (instancing.equalsIgnoreCase("method")) {
                if (methodName == null || methodName.trim().length() == 0) {
                    conditionalBinder = ((InstancingBinder) conditionalBinder).using(Methods.factoryMethod(typeToInstance, classes.toArray(EMPTY_CLASS_ARRAY)));
                } else {
                    conditionalBinder = ((InstancingBinder) conditionalBinder).using(Methods.factoryMethod(typeToInstance, methodName,
                            classes.toArray(EMPTY_CLASS_ARRAY)));
                }
            } else if (instancing.equalsIgnoreCase("constructor")) {
                conditionalBinder = ((InstancingBinder) conditionalBinder).using(Constructors.constructor(typeToInstance, classes.toArray(EMPTY_CLASS_ARRAY)));
            }
            classes.clear();
        } else if (localName.equalsIgnoreCase("allAnnotationsArePresent")) {
            condition = BindingConditions.allAnnotationsArePresent(typeToInstance, (Class[]) classes.toArray(EMPTY_CLASS_ARRAY));
            classes.clear();
        } else if (localName.equalsIgnoreCase("anyAnnotationIsPresent")) {
            condition = BindingConditions.anyAnnotationIsPresent(typeToInstance, (Class[]) classes.toArray(EMPTY_CLASS_ARRAY));
            classes.clear();
        }
    }
    
}