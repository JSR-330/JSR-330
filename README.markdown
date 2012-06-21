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
    <version>1.1.0-SNAPSHOT</version>
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

This implementation provides you a way of determining the correct implementation for a specific type by using a ``TypeDeterminator``.
E.g.:

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
