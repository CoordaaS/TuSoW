package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.prologx.PrologUtils;
import org.jooq.lambda.function.Function2;
import org.jooq.lambda.function.Function3;
import org.jooq.lambda.tuple.Tuple2;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.jooq.lambda.tuple.Tuple.tuple;

public class Presentation {

    static {
    }

    private static final Map<Tuple2<Class<?>, MIMETypes>, Marshaller<?>> marshallers = new HashMap<>();
    private static final Map<Tuple2<Class<?>, MIMETypes>, Unmarshaller<?>> unmarshallers = new HashMap<>();

    private static final Map<MIMETypes, ObjectMapper> mappers = Map.of(
        MIMETypes.APPLICATION_JSON, createMapper(ObjectMapper.class),
        MIMETypes.APPLICATION_YAML, createMapper(YAMLMapper.class),
        MIMETypes.APPLICATION_XML, createMapper(XmlMapper.class)
    );

    private static <OM extends ObjectMapper> OM createMapper(Class<OM> mapperClass) {
        try {
            final OM mapper = mapperClass.getConstructor().newInstance();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> Marshaller<T> getMarshaller(Class<T> type, MIMETypes mimeType) {
        var result = marshallers.get(tuple(type, mimeType));
        if (result == null) {
            throw new IllegalArgumentException("Class-MIMEType combo not supported: " + type.getName() + " --> " + mimeType);
        }
        return (Marshaller<T>) result;
    }

    public static <T> Unmarshaller<T> getUnmarshaller(Class<T> type, MIMETypes mimeType) {
        var result = unmarshallers.get(tuple(type, mimeType));
        if (result == null) {
            throw new IllegalArgumentException("Class-MIMEType combo not supported: " + type.getName() + " <-- " + mimeType);
        }
        return (Unmarshaller<T>) result;
    }

    public static <T> void register(Class<T> type, Marshaller<? super T> marshaller) {
        final Tuple2<Class<?>, MIMETypes> key = tuple(type, marshaller.getSupportedMIMEType());
        if (marshallers.containsKey(key)) {
            throw new IllegalArgumentException("Class-MIMEType combo already registered: " + type.getName() + " --> " + marshaller.getSupportedMIMEType());
        }
        marshallers.put(key, marshaller);
    }

    public static <T> void registerSimpleMarshallers(Class<T> type) {
        registerSimpleMarshallers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML));
    }

    public static <T> void registerSimpleMarshallers(Class<T> type, EnumSet<MIMETypes> types) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, new SimpleMarshaller<>(t, mapper));
        }
    }

    public static <T> void registerDynamicMarshallers(Class<T> type, Function3<Class<T>, MIMETypes, T, Object> f) {
        registerDynamicMarshallers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML), f);
    }

    public static <T> void registerDynamicMarshallers(Class<T> type, EnumSet<MIMETypes> types, Function3<Class<T>, MIMETypes, T, Object> f) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, new DynamicMarshaller<T>(t, mapper) {
                @Override
                public Object toDynamicObject(T object) {
                    return f.apply(type, getSupportedMIMEType(), object);
                }
            });
        }
    }

    public static <T> void registerDynamicMarshallers(Class<T> type, Function2<MIMETypes, ObjectMapper, DynamicMarshaller<T>> f) {
        registerDynamicMarshallers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML), f);
    }

    public static <T> void registerDynamicMarshallers(Class<T> type, EnumSet<MIMETypes> types, Function2<MIMETypes, ObjectMapper, DynamicMarshaller<T>> f) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, f.apply(t, mapper));
        }
    }

    public static <T> void register(Class<T> type, Unmarshaller<? extends T> unmarshaller) {
        final Tuple2<Class<?>, MIMETypes> key = tuple(type, unmarshaller.getSupportedMIMEType());
        if (unmarshallers.containsKey(key)) {
            throw new IllegalArgumentException("Class-MIMEType combo already registered: " + type.getName() + " <-- " + unmarshaller.getSupportedMIMEType());
        }
        unmarshallers.put(key, unmarshaller);
    }

    public static <T> void registerSimpleUnmarshallers(Class<T> type) {
        registerSimpleUnmarshallers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML));
    }

    public static <T> void registerSimpleUnmarshallers(Class<T> type, EnumSet<MIMETypes> types) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, new SimpleUnmarshaller<>(type, t, mapper));
        }
    }

    public static <T> void registerDynamicUnmarshallers(Class<T> type, Function3<Class<T>, MIMETypes, Object, T> f) {
        registerDynamicUnmarshallers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML), f);
    }

    public static <T> void registerDynamicUnmarshallers(Class<T> type, EnumSet<MIMETypes> types, Function3<Class<T>, MIMETypes, Object, T> f) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, new DynamicUnmarshaller<T>(type, t, mapper) {

                @Override
                public T fromDynamicObject(Object dynamicObject) {
                    return f.apply(type, t, dynamicObject);
                }
            });
        }
    }

    public static <T> void registerDynamicUnmarshallers(Class<T> type, Function2<MIMETypes, ObjectMapper, DynamicUnmarshaller<T>> f) {
        registerDynamicUnmarshallers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML), f);
    }

    public static <T> void registerDynamicUnmarshallers(Class<T> type, EnumSet<MIMETypes> types, Function2<MIMETypes, ObjectMapper, DynamicUnmarshaller<T>> f) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, f.apply(t, mapper));
        }
    }

    static {
        registerDynamicMarshallers(LogicTemplate.class, (klass, targetType, template) ->
                PrologUtils.termToDynamicObject(template.getTemplate()));
        registerDynamicMarshallers(LogicTuple.class, (klass, targetType, tuple) ->
                PrologUtils.termToDynamicObject(tuple.getTuple()));
        registerDynamicMarshallers(LogicMatch.class, LogicMatchMarshaller::new);

        registerDynamicUnmarshallers(LogicTemplate.class, (klass, targetType, obj) ->
                LogicTemplate.of(PrologUtils.dynamicObjectToTerm(obj)));
        registerDynamicUnmarshallers(LogicTuple.class, (klass, targetType, obj) ->
                LogicTuple.of(PrologUtils.dynamicObjectToTerm(obj)));
        registerDynamicUnmarshallers(LogicMatch.class, LogicMatchUnmarshaller::new);
    }
}
