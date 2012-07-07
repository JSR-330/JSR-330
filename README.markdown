# JSR-330 - Dependency Injection for Java
http://jsr-330.github.com/JSR-330/

This is a simple and easy to integrate implementation of JSR-330 (http://jcp.org/en/jsr/detail?id=330).
One can instance an ``Injector`` and instantly go for DI. This implementation passes the Technology Compatibility Kit (TCK).

```java
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;

public class MyTck {
    
    public static Test suite() {
        Car car = new Injector().getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
    
}
```

## License

Apache 2.0 License (http://www.apache.org/licenses/LICENSE-2.0)

## Integration

```xml
<dependency>
    <groupId>com.github.jsr-330</groupId>
    <artifactId>core</artifactId>
    <version>1.4.0</version>
</dependency>
```

## How it works

1. scan the classpath
2. introspect the classes found in the classpath
3. inject all static fields
4. inject all static methods
5. prepare for inject of non-static fields and methods

## Filters

One can influence the way the classpath is scanned and the classes are introspected via filters:

```java
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;

public class MyTck {
    
    public static Test suite() {
        ClassAnalyser<Map<String, Class<?>[]>> analyser = new InheritanceAnalyser();
        ClassInjector instancer = new DefaultClassInjector();
        ClassScanner scanner = new DefaultClassScanner(new RegExSourceDirFilter(".*javax\\.inject-tck-1\\.jar"), null);
        
        Injector injector = new Injector(Thread.currentThread().getContextClassLoader(), scanner, analyser, instancer);
        Car car = injector.getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
    
}
```

- a ``ClassAnalyser`` is responsible for providing information needed for injection to the ``ClassInjector``
- a ``ClassScanner`` is responsible for getting a list of classes
- a ``ClassInjector`` is responsible for the actual injection of static and non-static class members

A ``ClassScanner`` can be composed of other ``ClassScanner`` instances.
The advantage is that you can instance a base-ClassScanner, which loads a basic set of classes (this can be a performance penalty)
and sub-ClassScanner for every jar file that will be dynamically loaded during runtime.

One can filter classes during analysis too. E.g. to only analyse the classes of a specific package.

This implementation is made to fast at runtime and not for only be executed at start-up.
E.g. for a servlet-container that loads applications who need a fast DI implementation and not a full-fledged CDI implementation.

## Choose an implementation

By default the JSR-330 specification determines the class of the object injected into another
by the type of the field or parameter that is to be injected and an additional Qualifier.

e.g.:

```java
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Drivers {
}

public class Bean {
    
    @Drivers
    private Seat leftSeat;
    private Seat rightSeat;
    
}

public class Seat {
    ...
}

public class DriversSeat extends Seat {
    ...
}

```

``leftSeat`` would be injected with an instance of DriversSeat and ``rightSeat`` with Seat
because the name of the annotation Drivers in combination with the type to inject (Seat) results in DriversSeat.
One can alternatively use the ``@Named`` annotation to determine the correct type to inject.

So far the specification!

There are three stages of configuration in this project:

* writing a ``TypeDeterminator`` which merely chooses one of the possibilities given to it (together with some extra information)
* writing a ``TypeConfig`` which merely offers a configuration for a specific type (it doesn't have to deliver a configuration for each type)
* writing a ``ClassInjector`` which does all the stuff needed to be a valid DI implementation.

### TypeDeterminators

```java
public class MyTypeDeterminator implements TypeDeterminator {
    
    @Override
    public Class<?> determineClass(Class<?> type, Class<?>[] candidates, Annotation qualifier, ClassLoader classLoader) {
        
        // if a qualifier is present and there are implementations or subtypes
        if (qualifier != null && qualifier instanceof MyOwnAnnotation && candidates != null) {
            for (Class<?> candidate : candidates) {
                // MyOwnAnnotation gets you the correct classname of the implementation
                if (candidate.getName().equals(((MyOwnAnnotation)annotation).getClassname())) {
                    return candidate;
                }
            }
        }
        
        return type;
    }
    
}

ClassAnalyser<Map<String, Class<?>[]>> analyser = new InheritanceAnalyser();
ClassInjector instancer = new DefaultClassInjector();
ClassScanner scanner = new DefaultClassScanner();

instancer.setTypeDeterminator(new MyTypeDeterminator());

Injector injector = new Injector(Thread.currentThread().getContextClassLoader(), scanner, analyser, instancer);

```

### TypeConfigs

#### ConfigBuilder

The ``ConfigBuilder`` is a [Google-Guice](http://code.google.com/p/google-guice/)-like configuration tool.
The semantic is as follows:

```java
import com.github.jsr330.spi.config.builder.ConfigBuilder;

new ConfigBuilder()
    .instance(type)
    .( as(type) | asSingleton() | asSingleton(type) | with(Provider<type>) )
    .( when(condition) | using(constructor) | using(method) )
    .( when(condition) | and(condition) | or(condition) | xor(condition) )
    .build();
```

An example for passing the TCK:

```java
import static com.github.jsr330.spi.config.builder.BindingConditions.*;
import com.github.jsr330.spi.config.builder.*;

public class ConfigBuilderTck {
    
    public static Test suite() {
        InitialBinder<?> binder;
        ConfigBuilder builder = new ConfigBuilder();
        
        ...
        
        binder = builder.get();
        
        binder.instance(Car.class).as(Convertible.class);
        binder.instance(Seat.class).as(DriversSeat.class).when(qualifierIs(Seat.class, Drivers.class));
        binder.instance(Tire.class).as(SpareTire.class).when(isNamed(Tire.class, "spare"));
        
        instancer = new DefaultClassInjector(binder.build());
        
        Injector injector = new Injector(Thread.currentThread().getContextClassLoader(), scanner, analyser, instancer);
        
        ...
    }
    
}
```

#### JSON Config

If you like to express you bindings via JSON you can do it like this:

```json
[
    {
    	"org.atinject.tck.auto.Car" : {"as" : "org.atinject.tck.auto.Convertible"}
    },
    {
    	"org.atinject.tck.auto.Seat" : {
    	    "as" : "org.atinject.tck.auto.DriversSeat",
    	    "when" : "$qualifierIs(org.atinject.tck.auto.Drivers)"
    	}
    },
    {
    	"org.atinject.tck.auto.Tire" : {
    	    "as" : "org.atinject.tck.auto.accessories.SpareTire",
    	    "when" : "$isNamed(spare)"
    	}
    }
]
```

```java
import com.github.jsr330.spi.config.json.JsonConfig;

public class JsonConfigTck {
    
    public static Test suite() throws Exception {
        ClassAnalyser<Map<String, Class<?>[]>> analyser = new InheritanceAnalyser();
        ClassInjector instancer;
        ClassScanner scanner = new DefaultClassScanner(new RegExSourceDirFilter(".*javax\\.inject-tck-1\\.jar"), null);
        
        JsonConfig config = new JsonConfig(new File("./src/test/resources/tck.json"));
        
        instancer = new DefaultClassInjector(config.getConfig(Thread.currentThread().getContextClassLoader()));
        
        Injector injector = new Injector(Thread.currentThread().getContextClassLoader(), scanner, analyser, instancer);
        Car car = injector.getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
}
```

At the moment the only conditions available are those defined in ``com.github.jsr330.spi.config.builder.BindingConditions`` except ``and``, ``or`` and ``xor``.
Also nested conditions are not yet available. The JSON Config module is based on the ConfigBuilder module explained above.

#### XML Config

The XML configuration works as same as the JSON configuration.

```java
import com.github.jsr330.spi.config.xml.XmlConfig.XmlConfig;

public class XmlConfigTck {
    
    public static Test suite() throws Exception {
        ClassAnalyser<Map<String, Class<?>[]>> analyser = new InheritanceAnalyser();
        ClassInjector instancer;
        ClassScanner scanner = new DefaultClassScanner(new RegExSourceDirFilter(".*javax\\.inject-tck-1\\.jar"), null);
        
        XmlConfig config = new XmlConfig(new File("./src/test/resources/tck.xml"));
        
        instancer = new DefaultClassInjector(config.getConfig(Thread.currentThread().getContextClassLoader()));
        
        Injector injector = new Injector(Thread.currentThread().getContextClassLoader(), scanner, analyser, instancer);
        Car car = injector.getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
    
}
```

```xml
<?xml version="1.0"?>
<config xmlns="http://jsr-330.github.com" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://jsr-330.github.com jsr-330-xml-config.xsd">

    <instance classname="org.atinject.tck.auto.Car">
        <as classname="org.atinject.tck.auto.Convertible"/>
    </instance>

    <instance classname="org.atinject.tck.auto.Seat">
        <as classname="org.atinject.tck.auto.DriversSeat"/>
        <when>
            <qualifierIs classname="org.atinject.tck.auto.Drivers"/>
        </when>
    </instance>

    <instance classname="org.atinject.tck.auto.Tire">
        <as classname="org.atinject.tck.auto.accessories.SpareTire"/>
        <when>
            <isNamed value="spare"/>
        </when>
    </instance>

</config>
```

The XSD file can be found under ``src/main/resources`` or in the root of the jar file.

#### Write your own TypeConfig

A TypeConfig is simply a repository for type configurations and provider. A ``ClassInjector`` asks a TypeConfig for a provider or a TypeContainer and,
if not returning a ``null`` value, use the result to instantiate a specific type. The TypeConfig don't have to be complete.
It doesn't need to return a configuration for every Type the ClassInjector asks for. So you can only configure the some special types,
like a specific type as a singleton, and leave the rest to the automatic type binding mechanism (assuming using the DefaultClassInjector).

```java
public interface TypeConfig {
    
    <T> Provider<T> getProvider(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader);
    
    <T> TypeContainer getTypeContainer(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader);
    
}

public class ExampleTypeConfig implements TypeConfig {
    
    @Override
    public <V> Provider<V> getProvider(final ClassInjector injector, final Class<V> type, final Map<String, Class<? extends V>[]> inheritanceTree,
            final Annotation qualifier, final ClassLoader classLoader) {
        
        // pretty simple implementation: just redirect the instantiation request to the ClassInjector
        return new Provider<V>() {
            
            @Override
            public V get() {
                return injector.instance(type, inheritanceTree, classLoader, null, qualifier);
            }
            
        };
    }
    
    @Override
    public <T> TypeContainer getTypeContainer(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader) {
        TypeContainer container;
        Class<?> implementation;
        
        // if MyInterface is wanted
        if (type.getName().equals("my.package.MyInterface")) {
            container = new TypeContainer(null, null);
            
            // get the standard information
            container.gatherInformation();
            
            try {
                implementation = Class.forName("my.package.MyBean", false, classLoader);
                
                // set the new values: MyBean (which implements MyInterface), singleton, using default (no args) constructor
                container.setType(implementation);
                container.setSingleton(true);
                
                container.setInstanceMode(InstanceMode.CONSTRUCTOR);
                try {
                    container.setConstructor(implementation.getDeclaredConstructor());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                container.setFactoryMethod(null);
                container.setProvider(null);
                
                return container;
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
        
        return null;
    }
    
}
```

#### ClassInjectors

The default implementation of a ``ClassInjector`` is ``com.github.jsr330.instance.DefaultClassInjector``.
It supports ``TypeDeterminators`` and ``TypeConfigs`` as well as automatic type binding.
