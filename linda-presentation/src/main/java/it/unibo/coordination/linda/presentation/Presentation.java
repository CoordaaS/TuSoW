package it.unibo.coordination.linda.presentation;

import alice.tuprolog.Term;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unibo.coordination.linda.core.TupleSpace;
import it.unibo.coordination.linda.core.events.TupleSpaceEvent;
import it.unibo.coordination.linda.logic.InspectableLogicSpace;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringTuple;
import it.unibo.coordination.prologx.PrologUtils;
import org.jooq.lambda.function.Function2;
import org.jooq.lambda.function.Function3;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Presentation {

    static {
    }

    private static final Map<Tuple2<Class<?>, MIMETypes>, Serializer<?>> serializers = new HashMap<>();
    private static final Map<Tuple2<Class<?>, MIMETypes>, Deserializer<?>> deserializers = new HashMap<>();

    private static final Map<MIMETypes, ObjectMapper> mappers = Map.of(
        MIMETypes.APPLICATION_JSON, createMapper(ObjectMapper.class),
        MIMETypes.APPLICATION_YAML, createMapper(YAMLMapper.class),
        MIMETypes.APPLICATION_XML, createMapper(XmlMapper.class)
    );

    public static <OM extends ObjectMapper> OM createMapper(Class<OM> mapperClass) {
        try {
            final OM mapper = mapperClass.getConstructor().newInstance();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> Serializer<T> getSerializer(Class<T> type, MIMETypes mimeType) {
        var result = serializers.get(Tuple.tuple(type, mimeType));
        if (result == null) {
            throw new IllegalArgumentException("Class-MIMEType combo not supported: " + type.getName() + " --> " + mimeType);
        }
        return (Serializer<T>) result;
    }

    public static <T> Deserializer<T> getDeserializer(Class<T> type, MIMETypes mimeType) {
        var result = deserializers.get(Tuple.tuple(type, mimeType));
        if (result == null) {
            throw new IllegalArgumentException("Class-MIMEType combo not supported: " + type.getName() + " <-- " + mimeType);
        }
        return (Deserializer<T>) result;
    }

    public static <T> void register(Class<T> type, Serializer<? super T> serializer) {
        final Tuple2<Class<?>, MIMETypes> key = Tuple.tuple(type, serializer.getSupportedMIMEType());
        if (serializers.containsKey(key)) {
            throw new IllegalArgumentException("Class-MIMEType combo already registered: " + type.getName() + " --> " + serializer.getSupportedMIMEType());
        }
        serializers.put(key, serializer);
    }

    public static <T> void registerSimpleSerializers(Class<T> type) {
        registerSimpleSerializers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML));
    }

    public static <T> void registerSimpleSerializers(Class<T> type, EnumSet<MIMETypes> types) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, new SimpleSerializer<>(t, mapper));
        }
    }

    public static <T> void registerDynamicSerializers(Class<T> type, Function3<Class<T>, MIMETypes, T, Object> f) {
        registerDynamicSerializers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML), f);
    }

    public static <T> void registerDynamicSerializers(Class<T> type, EnumSet<MIMETypes> types, Function3<Class<T>, MIMETypes, T, Object> f) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, new DynamicSerializer<T>(t, mapper) {
                @Override
                public Object toDynamicObject(T object) {
                    return f.apply(type, getSupportedMIMEType(), object);
                }
            });
        }
    }

    public static <T> void registerDynamicSerializers(Class<T> type, Function2<MIMETypes, ObjectMapper, DynamicSerializer<T>> f) {
        registerDynamicSerializers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML), f);
    }

    public static <T> void registerDynamicSerializers(Class<T> type, EnumSet<MIMETypes> types, Function2<MIMETypes, ObjectMapper, DynamicSerializer<T>> f) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, f.apply(t, mapper));
        }
    }

    public static <T> void register(Class<T> type, Deserializer<? extends T> deserializer) {
        final Tuple2<Class<?>, MIMETypes> key = Tuple.tuple(type, deserializer.getSupportedMIMEType());
        if (deserializers.containsKey(key)) {
            throw new IllegalArgumentException("Class-MIMEType combo already registered: " + type.getName() + " <-- " + deserializer.getSupportedMIMEType());
        }
        deserializers.put(key, deserializer);
    }

    public static <T> void registerSimpleDeserializers(Class<T> type) {
        registerSimpleDeserializers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML));
    }

    public static <T> void registerSimpleDeserializers(Class<T> type, EnumSet<MIMETypes> types) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, new SimpleDeserializer<>(type, t, mapper));
        }
    }

    public static <T> void registerDynamicDeserializers(Class<T> type, Function3<Class<T>, MIMETypes, Object, T> f) {
        registerDynamicDeserializers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML), f);
    }

    public static <T> void registerDynamicDeserializers(Class<T> type, EnumSet<MIMETypes> types, Function3<Class<T>, MIMETypes, Object, T> f) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, new DynamicDeserializer<T>(type, t, mapper) {

                @Override
                public T fromDynamicObject(Object dynamicObject) {
                    return f.apply(type, t, dynamicObject);
                }
            });
        }
    }

    public static <T> void registerDynamicDeserializers(Class<T> type, Function2<MIMETypes, ObjectMapper, DynamicDeserializer<T>> f) {
        registerDynamicDeserializers(type, EnumSet.of(MIMETypes.APPLICATION_JSON, MIMETypes.APPLICATION_YAML, MIMETypes.APPLICATION_XML), f);
    }

    public static <T> void registerDynamicDeserializers(Class<T> type, EnumSet<MIMETypes> types, Function2<MIMETypes, ObjectMapper, DynamicDeserializer<T>> f) {
        for (var t : types) {
            final var mapper = mappers.get(t);
            if (mapper == null) throw new IllegalArgumentException("No mapper for MIMEType " + t);
            register(type, f.apply(t, mapper));
        }
    }

    static {
        registerDynamicSerializers(LogicTemplate.class, (klass, targetType, template) ->
                PrologUtils.termToDynamicObject(template.getTemplate()));
        registerDynamicSerializers(LogicTuple.class, (klass, targetType, tuple) ->
                PrologUtils.termToDynamicObject(tuple.getValue()));
        registerDynamicSerializers(LogicMatch.class, LogicMatchSerializer::new);
        registerDynamicSerializers(Term.class, (klass, targetType, term) ->
                PrologUtils.termToDynamicObject(term));
        registerDynamicSerializers(TupleSpaceEvent.class, (mimeType, mapper) -> new TupleSpaceEventSerializer(mimeType, mapper, LogicTuple.class, LogicTemplate.class));

        registerDynamicDeserializers(LogicTemplate.class, (klass, targetType, obj) ->
                LogicTemplate.of(PrologUtils.dynamicObjectToTerm(obj)));
        registerDynamicDeserializers(LogicTuple.class, (klass, targetType, obj) ->
                LogicTuple.of(PrologUtils.dynamicObjectToTerm(obj)));
        registerDynamicDeserializers(LogicMatch.class, LogicMatchDeserializer::new);
        registerDynamicDeserializers(Term.class, (klass, targetType, obj) ->
                PrologUtils.dynamicObjectToTerm(obj));
        registerDynamicDeserializers(TupleSpaceEvent.class, (mimeType, mapper) -> new TupleSpaceEventDeserializer(mimeType, mapper, LogicTuple.class, LogicTemplate.class) {
            @Override
            protected TupleSpace getSpace(String tupleSpaceName) {
                return InspectableLogicSpace.create(tupleSpaceName);
            }
        });


        registerDynamicSerializers(StringTuple.class, StringTupleSerializer::new);
        registerDynamicSerializers(RegexTemplate.class, RegexTemplateSerializer::new);
        registerDynamicSerializers(RegularMatch.class, RegularMatchSerializer::new);

        registerDynamicDeserializers(StringTuple.class, StringTupleDeserializer::new);
        registerDynamicDeserializers(RegexTemplate.class, RegexTemplateDeserializer::new);
        registerDynamicDeserializers(RegularMatch.class, RegularMatchDeserializer::new);
    }
}
