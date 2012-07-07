package com.github.jsr330.spi.config.json;

import static com.github.jsr330.spi.config.builder.BindingConditions.allAnnotationsArePresent;
import static com.github.jsr330.spi.config.builder.BindingConditions.annotationIsPresent;
import static com.github.jsr330.spi.config.builder.BindingConditions.isNamed;
import static com.github.jsr330.spi.config.builder.BindingConditions.isNamedIgnoringCase;
import static com.github.jsr330.spi.config.builder.BindingConditions.qualifierIs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsr330.spi.TypeConfig;
import com.github.jsr330.spi.config.builder.Binder;
import com.github.jsr330.spi.config.builder.BinderException;
import com.github.jsr330.spi.config.builder.BindingCondition;
import com.github.jsr330.spi.config.builder.BindingConditions;
import com.github.jsr330.spi.config.builder.ConditionalBinder;
import com.github.jsr330.spi.config.builder.ConfigBuilder;
import com.github.jsr330.spi.config.builder.InitialBinder;
import com.github.jsr330.spi.config.builder.InstancingBinder;
import com.github.jsr330.spi.config.builder.LinkingBinder;
import com.github.jsr330.spi.config.builder.TypeBinder;

/**
 * This is a {@link ConfigBuilder}-based, Json-sources {@link TypeConfig}-Builder.
 */
public class JsonConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonConfig.class);
    
    protected Reader source;
    protected ObjectMapper mapper = new ObjectMapper();
    
    public JsonConfig(String json) {
        source = new StringReader(json);
    }
    
    public JsonConfig(File file) throws FileNotFoundException {
        source = new InputStreamReader(new FileInputStream(file));
    }
    
    public JsonConfig(URL source) throws IOException {
        this.source = new InputStreamReader(source.openStream());
    }
    
    public JsonConfig(byte[] json) {
        source = new StringReader(new String(json));
    }
    
    public JsonConfig(byte[] json, Charset charset) {
        source = new StringReader(new String(json, charset));
    }
    
    public JsonConfig(InputStream source) {
        this.source = new InputStreamReader(source);
    }
    
    public JsonConfig(Reader source) {
        this.source = source;
    }
    
    /**
     * Gets the type configs out of the json source.
     * A wrong sequence will lead to a {@link BinderException}.
     */
    public TypeConfig getConfig(ClassLoader loader) throws JsonProcessingException, IOException {
        JsonNode node;
        ConfigBuilder builder = new ConfigBuilder();
        
        if (source != null) {
            try {
                node = mapper.readTree(source);
                if (node.isArray()) {
                    for (int i = 0; i < node.size(); i++) {
                        parseConfig(loader, builder.get(), node.get(i));
                    }
                    return builder.build();
                }
            } finally {
                try {
                    source.close();
                } catch (IOException exception) {
                    LOGGER.debug("error while closing reader.", exception);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Parses the Json file for {@link Binder} methods (the keys have the same name as the methods in the {@link Binder}-interfaces).
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void parseConfig(ClassLoader loader, InitialBinder<?> binder, JsonNode node) {
        JsonNode classConfig, typeNode, instancingNode, linkingNode, methodNode, constructorNode, parametersNode, conditionalNode;
        TypeBinder<?> typeBinder;
        ConditionalBinder<?> conditionalBinder = null;
        LinkingBinder<?> linkingBinder = null;
        String tmp, methodName;
        Iterator<String> names;
        Class<?>[] parameters = null;
        Class<?> implementation = null, type;
        
        if (node.isObject()) {
            tmp = node.fieldNames().next();
            classConfig = node.get(tmp);
            
            try {
                type = Class.forName(tmp, false, loader);
                typeBinder = binder.instance(type);
                names = classConfig.fieldNames();
                
                if (names.hasNext()) {
                    tmp = names.next();
                    typeNode = classConfig.get(tmp);
                    
                    if (tmp.equalsIgnoreCase("as")) {
                        implementation = Class.forName(typeNode.asText(), false, loader);
                        conditionalBinder = typeBinder.as((Class) implementation);
                    } else if (tmp.equalsIgnoreCase("asSingleton")) {
                        if (typeNode.isBoolean()) {
                            conditionalBinder = typeBinder.asSingleton();
                        } else {
                            implementation = Class.forName(typeNode.asText(), false, loader);
                            conditionalBinder = typeBinder.asSingleton((Class) implementation);
                        }
                    } else if (tmp.equalsIgnoreCase("with")) {
                        try {
                            conditionalBinder = typeBinder.with((Provider) Class.forName(typeNode.asText(), false, loader).newInstance());
                        } catch (Exception exception) {
                            LOGGER.debug("error while instancing provider.", exception);
                        }
                    }
                    
                    if (names.hasNext() && conditionalBinder != null) {
                        tmp = names.next();
                        instancingNode = classConfig.get(tmp);
                        
                        if (tmp.equalsIgnoreCase("using") && conditionalBinder instanceof InstancingBinder && implementation != null) {
                            methodNode = instancingNode.get("method");
                            constructorNode = instancingNode.get("constructor");
                            
                            if (methodNode != null) {
                                methodName = methodNode.get("name").asText();
                                if ((parametersNode = methodNode.get("parameters")).isArray()) {
                                    parameters = new Class<?>[parametersNode.size()];
                                    for (int i = 0; i < parametersNode.size(); i++) {
                                        parameters[i] = Class.forName(parametersNode.get(i).asText(), false, loader);
                                    }
                                }
                                
                                try {
                                    conditionalBinder = ((InstancingBinder<?>) conditionalBinder).using(implementation.getMethod(methodName, parameters));
                                } catch (Exception exception) {
                                    LOGGER.debug("error while getting factory method.", exception);
                                }
                            } else if (constructorNode != null) {
                                parameters = new Class<?>[constructorNode.size()];
                                for (int i = 0; i < constructorNode.size(); i++) {
                                    parameters[i] = Class.forName(constructorNode.get(i).asText(), false, loader);
                                }
                                
                                try {
                                    conditionalBinder = ((InstancingBinder<?>) conditionalBinder)
                                            .using((Constructor) implementation.getConstructor(parameters));
                                } catch (Exception exception) {
                                    LOGGER.debug("error while getting constructor.", exception);
                                }
                            }
                            
                            tmp = names.next();
                            instancingNode = classConfig.get(tmp);
                        }
                        
                        if (tmp != null && tmp.equalsIgnoreCase("when")) {
                            conditionalNode = classConfig.get(tmp);
                            tmp = conditionalNode.asText();
                            linkingBinder = conditionalBinder.when((BindingCondition) parseCondition(loader, tmp, type));
                        }
                        
                        if (linkingBinder != null) {
                            while (names.hasNext()) {
                                tmp = names.next();
                                linkingNode = classConfig.get(tmp);
                                
                                if (tmp.equalsIgnoreCase("and")) {
                                    linkingBinder.and((BindingCondition) parseCondition(loader, linkingNode.asText(), type));
                                } else if (tmp.equalsIgnoreCase("or")) {
                                    linkingBinder.or((BindingCondition) parseCondition(loader, linkingNode.asText(), type));
                                } else if (tmp.equalsIgnoreCase("xor")) {
                                    linkingBinder.xor((BindingCondition) parseCondition(loader, linkingNode.asText(), type));
                                }
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException exception) {
                LOGGER.debug("class not in the classpath", exception);
            }
        }
    }
    
    /**
     * Parses a condition with the following restrictions:
     * 
     * <ul>
     * <li>a condition need to start with "$&lt;condition&gt;(" and end with ")"</li>
     * <li>a condition needs to be a string or a comma-separated string</li>
     * <li>a condition can be one of the following commands:
     *     <ul>
     *     <li>annotationIsPresent (string)</li>
     *     <li>qualifierIs (string)</li>
     *     <li>isNamed (string)</li>
     *     <li>isNamedIgnoringCase (string)</li>
     *     <li>allAnnotationsArePresent (comma-separated string)</li>
     *     <li>anyAnnotationIsPresent (comma-separated string)</li>
     *     </ul>
     * </li>
     * </ul>
     * 
     * These command correspond to those in {@link BindingConditions}.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected BindingCondition<?> parseCondition(ClassLoader loader, String condition, Class<?> type) throws ClassNotFoundException {
        String[] values;
        Class<?>[] annotations;
        
        if (condition.startsWith("$")) {
            if (condition.startsWith("$annotationIsPresent(")) {
                condition = condition.substring("$annotationIsPresent(".length(), condition.length() - 1);
                return annotationIsPresent(type, (Class) Class.forName(condition, false, loader));
            } else if (condition.startsWith("$qualifierIs(")) {
                condition = condition.substring("$qualifierIs(".length(), condition.length() - 1);
                return qualifierIs(type, (Class) Class.forName(condition, false, loader));
            } else if (condition.startsWith("$isNamed(")) {
                condition = condition.substring("$isNamed(".length(), condition.length() - 1);
                return isNamed(type, condition);
            } else if (condition.startsWith("$isNamedIgnoringCase(")) {
                condition = condition.substring("$isNamedIgnoringCase(".length(), condition.length() - 1);
                return isNamedIgnoringCase(type, condition);
            } else if (condition.startsWith("$allAnnotationsArePresent(")) {
                condition = condition.substring("$allAnnotationsArePresent(".length(), condition.length() - 1);
                values = condition.split("\\s*,\\s*");
                annotations = new Class<?>[values.length];
                for (int i = 0; i < values.length; i++) {
                    annotations[i] = Class.forName(values[i], false, loader);
                }
                return allAnnotationsArePresent(type, (Class[]) annotations);
            } else if (condition.startsWith("$anyAnnotationIsPresent(")) {
                condition = condition.substring("$anyAnnotationIsPresent(".length(), condition.length() - 1);
                values = condition.split("\\s*,\\s*");
                annotations = new Class<?>[values.length];
                for (int i = 0; i < values.length; i++) {
                    annotations[i] = Class.forName(values[i], false, loader);
                }
                return allAnnotationsArePresent(type, (Class[]) annotations);
            }
        }
        
        return null;
    }
    
}
