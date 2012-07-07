package com.github.jsr330.spi.config.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsr330.instance.TypeContainer;
import com.github.jsr330.instance.TypeContainer.InstanceMode;
import com.github.jsr330.spi.ClassInjector;
import com.github.jsr330.spi.TypeConfig;

/**
 * The DefaultBinder combines all binder types and a {@link TypeConfig}.
 */
public class DefaultBinder<T> implements LinkingBinder<T>, InitialBinder<T>, TypeBinder<T>, ConditionalBinder<T>, InstancingBinder<T>, TypeConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBinder.class);
    
    protected Map<String, TypeContainerConfig<?>> configs = new HashMap<String, TypeContainerConfig<?>>();
    protected TypeContainerConfig<?> currentConfig;
    
    /**
     * Checks if an implementation is not abstract or an interface.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void checkImplementation() {
        if (currentConfig.getProvider() != null) {
            return;
        }
        
        if (currentConfig.getImplementation() == null) {
            if (currentConfig.getType().isInterface() || Modifier.isAbstract(currentConfig.getType().getModifiers())) {
                throw new BinderException("can't instance interface or abstract class without a implementation specified.");
            } else {
                currentConfig.setImplementation((Class) currentConfig.getType());
            }
        } else {
            if (currentConfig.getImplementation().isInterface() || Modifier.isAbstract(currentConfig.getImplementation().getModifiers())) {
                throw new BinderException("can't instance interface or abstract class.");
            }
        }
    }
    
    /**
     * Gets itself.
     */
    @Override
    public TypeConfig build() {
        checkImplementation();
        LOGGER.debug("build - returning {}", this);
        return this;
    }
    
    /**
     * Puts the specified type to the list of types configured and makes it the current type to configure.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <V> TypeBinder<V> instance(Class<V> type) {
        currentConfig = new TypeContainerConfig<V>(type);
        configs.put(currentConfig.getType().getName(), currentConfig);
        LOGGER.debug("instance - type {}", type);
        return (TypeBinder<V>) this;
    }
    
    /**
     * Does the bitwise and to the previous condition.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public LinkingBinder<T> and(BindingCondition<T> condition) {
        if (condition == null) {
            throw new NullPointerException("condition must not be null.");
        }
        currentConfig.setCondition((BindingCondition) BindingConditions.and((BindingCondition<T>) currentConfig.getCondition(), condition));
        LOGGER.debug("and - condition {}", condition);
        return this;
    }
    
    /**
     * Does the bitwise xor to the previous condition.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public LinkingBinder<T> xor(BindingCondition<T> condition) {
        if (condition == null) {
            throw new NullPointerException("condition must not be null.");
        }
        currentConfig.setCondition((BindingCondition) BindingConditions.xor((BindingCondition<T>) currentConfig.getCondition(), condition));
        LOGGER.debug("xor - condition {}", condition);
        return this;
    }
    
    /**
     * Does the bitwise or to the previous condition.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public LinkingBinder<T> or(BindingCondition<T> condition) {
        if (condition == null) {
            throw new NullPointerException("condition must not be null.");
        }
        currentConfig.setCondition((BindingCondition) BindingConditions.or((BindingCondition<T>) currentConfig.getCondition(), condition));
        LOGGER.debug("or - condition {}", condition);
        return this;
    }
    
    /**
     * Marks the current type as singleton if it is instantiable.
     */
    @Override
    public InstancingBinder<T> asSingleton() {
        currentConfig.setSingleton(true);
        checkImplementation();
        LOGGER.debug("asSingleton");
        return this;
    }
    
    /**
     * Marks the current type as singleton with the specified implementation.
     */
    @Override
    public InstancingBinder<T> asSingleton(Class<? extends T> type) {
        as(type);
        return asSingleton();
    }
    
    /**
     * defines the implementation for the current type.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public InstancingBinder<T> as(Class<? extends T> type) {
        currentConfig.setImplementation((Class) type);
        checkImplementation();
        LOGGER.debug("as - type {}", type);
        return this;
    }
    
    /**
     * Assigns the provider for the current type.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ConditionalBinder<T> with(Provider<? extends T> provider) {
        if (provider == null) {
            throw new NullPointerException("provider must not be null.");
        }
        currentConfig.setProvider((Provider) provider);
        checkImplementation();
        LOGGER.debug("with - provider {}", provider);
        return this;
    }
    
    /**
     * Assigns the condition of the current configuration.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public LinkingBinder<T> when(BindingCondition<T> condition) {
        if (condition == null) {
            throw new NullPointerException("condition must not be null.");
        }
        currentConfig.setCondition((BindingCondition) condition);
        LOGGER.debug("when - condition {}", condition);
        return this;
    }
    
    /**
     * Assigns the constructor to use.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ConditionalBinder<T> using(Constructor<T> constructor) {
        currentConfig.setConstructor((Constructor) constructor);
        LOGGER.debug("using - constructor {}", constructor);
        return this;
    }
    
    /**
     * Assigns the static factory method to use.
     */
    @Override
    public ConditionalBinder<T> using(Method factoryMethod) {
        currentConfig.setFactoryMethod(factoryMethod);
        LOGGER.debug("using - factoryMethod {}", factoryMethod);
        return this;
    }
    
    /**
     * Gets a provider that uses the specified {@link ClassInjector}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <V> Provider<V> getProvider(final ClassInjector injector, final Class<V> type, final Map<String, Class<? extends V>[]> inheritanceTree,
            final Annotation qualifier, final ClassLoader classLoader) {
        TypeContainerConfig<V> config = (TypeContainerConfig<V>) configs.get(type.getName());
        
        LOGGER.debug("getProvider - type {}, qualifier {}", type, qualifier);
        
        if (config != null) {
            LOGGER.debug("getProvider - config exists");
            if (config.getCondition() != null && !config.getCondition().fulfilled(injector, type, inheritanceTree, qualifier, classLoader)) {
                LOGGER.debug("getProvider - condition not fulfilled");
                return null;
            }
            LOGGER.debug("getProvider - returning new provider");
            
            return new Provider<V>() {
                
                @Override
                public V get() {
                    return injector.instance(type, inheritanceTree, classLoader, null, qualifier);
                }
                
            };
        }
        
        return null;
    }
    
    /**
     * Gets the container type for the specified type if it is in the list of configured types.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <V> TypeContainer getTypeContainer(ClassInjector injector, Class<V> type, Map<String, Class<? extends V>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader) {
        TypeContainerConfig<V> config = (TypeContainerConfig<V>) configs.get(type.getName());
        TypeContainer container;
        
        LOGGER.debug("getTypeContainer - type {}, qualifier {}", type, qualifier);
        
        if (config != null && (config.getImplementation() != null || config.getProvider() != null)) {
            LOGGER.debug("getTypeContainer - config exists");
            if (config.getCondition() != null && !config.getCondition().fulfilled(injector, type, inheritanceTree, qualifier, classLoader)) {
                LOGGER.debug("getTypeContainer - condition not fulfilled");
                return null;
            }
            
            container = new TypeContainer(null, null);
            
            if (config.getFactoryMethod() != null) {
                LOGGER.debug("getTypeContainer - using factory method");
                container.setType(config.getImplementation());
                container.gatherInformation();
                container.setSingleton(config.isSingleton());
                
                container.setInstanceMode(InstanceMode.FACTORY_METHOD);
                container.setFactoryMethod(config.getFactoryMethod());
                container.setConstructor(null);
                container.setProvider(null);
            } else if (config.getProvider() != null) {
                LOGGER.debug("getTypeContainer - using provider");
                container.setInstanceMode(InstanceMode.PROVIDER);
                container.setProvider(config.getProvider());
                container.setFactoryMethod(null);
                container.setConstructor(null);
            } else {
                LOGGER.debug("getTypeContainer - using constructor");
                container.setType(config.getImplementation());
                container.gatherInformation();
                container.setSingleton(config.isSingleton());
                
                container.setInstanceMode(InstanceMode.CONSTRUCTOR);
                container.setConstructor(config.getConstructor());
                container.setFactoryMethod(null);
                container.setProvider(null);
            }
            
            return container;
        }
        
        return null;
    }
    
}
