package it.unibo.coordination.linda.logic;


import it.unibo.coordination.linda.core.OperationType;
import it.unibo.coordination.linda.core.PendingRequest;
import it.unibo.coordination.linda.core.RequestTypes;
import it.unibo.coordination.linda.core.events.OperationEvent;
import it.unibo.coordination.linda.core.events.PendingRequestEvent;
import it.unibo.coordination.linda.core.events.TupleEvent;
import it.unibo.coordination.linda.test.TestBaseLinda;
import it.unibo.presentation.Deserializer;
import it.unibo.presentation.MIMETypes;
import it.unibo.presentation.Serializer;
import it.unibo.presentation.TypeToken;
import it.unibo.tuprolog.core.Term;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.stream.Stream;

import static it.unibo.coordination.linda.logic.LogicTestsUtils.assertSerializationWorks;

@RunWith(Parameterized.class)
public class TestCoreSerialization extends TestBaseLinda<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    private Serializer<PendingRequest<LogicTuple, LogicTemplate>> pendingRequestSerializer;
    private Deserializer<PendingRequest<LogicTuple, LogicTemplate>> pendingRequestDeserializer;

    private Serializer<OperationEvent<LogicTuple, LogicTemplate>> operationEventSerializer;
    private Deserializer<OperationEvent<LogicTuple, LogicTemplate>> operationEventDeserializer;

    private Serializer<PendingRequestEvent<LogicTuple, LogicTemplate>> pendingRequestEventSerializer;
    private Deserializer<PendingRequestEvent<LogicTuple, LogicTemplate>> pendingRequestEventDeserializer;

    private Serializer<TupleEvent<LogicTuple, LogicTemplate>> tupleEventSerializer;
    private Deserializer<TupleEvent<LogicTuple, LogicTemplate>> tupleEventDeserializer;

    private static <X> TypeToken<X> typeToken(Class<X> mainType) {
        return new TypeToken<>(mainType, LogicTuple.class, LogicTemplate.class);
    }

    @Parameterized.Parameters
    public static Object[][] getParams() {
        return MIMETypes.JSON_YAML.stream().map(it -> new Object[]{it}).toArray(Object[][]::new);
    }

    private final MIMETypes mimeType;

    public TestCoreSerialization(MIMETypes mimeType) {
        super(new LogicTupleTemplateFactory());
        this.mimeType = mimeType;
    }

    @Before
    public void setUp() {
        pendingRequestSerializer = Presentation.INSTANCE.serializerOf(typeToken(PendingRequest.class), mimeType);
        pendingRequestDeserializer = Presentation.INSTANCE.deserializerOf(typeToken(PendingRequest.class), mimeType);

        operationEventSerializer = Presentation.INSTANCE.serializerOf(typeToken(OperationEvent.class), mimeType);
        operationEventDeserializer = Presentation.INSTANCE.deserializerOf(typeToken(OperationEvent.class), mimeType);

        pendingRequestEventSerializer = Presentation.INSTANCE.serializerOf(typeToken(PendingRequestEvent.class), mimeType);
        pendingRequestEventDeserializer = Presentation.INSTANCE.deserializerOf(typeToken(PendingRequestEvent.class), mimeType);

        tupleEventSerializer = Presentation.INSTANCE.serializerOf(typeToken(TupleEvent.class), mimeType);
        tupleEventDeserializer = Presentation.INSTANCE.deserializerOf(typeToken(TupleEvent.class), mimeType);
    }

    @Test
    public void testTupleEvent() {
        assertSerializationWorks(
                TupleEvent.beforeAbsent("name", getATemplate(), getATuple()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
        assertSerializationWorks(
                TupleEvent.afterAbsent("name0", getATemplate(), getATuple()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
        assertSerializationWorks(
                TupleEvent.beforeAbsent("name1", getATemplate()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
        assertSerializationWorks(
                TupleEvent.afterAbsent("name2", getATemplate()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
        assertSerializationWorks(
                TupleEvent.beforeReading("name3", getATuple()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
        assertSerializationWorks(
                TupleEvent.afterReading("name4", getATuple()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
        assertSerializationWorks(
                TupleEvent.beforeTaking("name5", getATuple()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
        assertSerializationWorks(
                TupleEvent.afterTaking("name6", getATuple()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
        assertSerializationWorks(
                TupleEvent.beforeWriting("name7", getATuple()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
        assertSerializationWorks(
                TupleEvent.afterWriting("name8", getATuple()),
                tupleEventSerializer,
                tupleEventDeserializer
        );
    }

    @Test
    public void testPendingRequestEvent() {
        assertSerializationWorks(
                PendingRequestEvent.of(
                        "name1",
                        PendingRequestEvent.Effect.RESUMING,
                        PendingRequest.of("id1", RequestTypes.ABSENT, getATemplate())
                ),
                pendingRequestEventSerializer,
                pendingRequestEventDeserializer
        );
        assertSerializationWorks(
                PendingRequestEvent.of(
                        "name2",
                        PendingRequestEvent.Effect.SUSPENDING,
                        PendingRequest.of("id1", RequestTypes.ABSENT, getATemplate())
                ),
                pendingRequestEventSerializer,
                pendingRequestEventDeserializer
        );
        assertSerializationWorks(
                PendingRequestEvent.of(
                        "name2",
                        PendingRequestEvent.Effect.RESUMING,
                        PendingRequest.of("id2", RequestTypes.ABSENT, getATemplate())
                ),
                pendingRequestEventSerializer,
                pendingRequestEventDeserializer
        );
        assertSerializationWorks(
                PendingRequestEvent.of(
                        "name2",
                        PendingRequestEvent.Effect.SUSPENDING,
                        PendingRequest.of("id2", RequestTypes.ABSENT, getATemplate())
                ),
                pendingRequestEventSerializer,
                pendingRequestEventDeserializer
        );
        assertSerializationWorks(
                PendingRequestEvent.of(
                        "name3",
                        PendingRequestEvent.Effect.RESUMING,
                        PendingRequest.of("id3", RequestTypes.ABSENT, getATemplate())
                ),
                pendingRequestEventSerializer,
                pendingRequestEventDeserializer
        );
        assertSerializationWorks(
                PendingRequestEvent.of(
                        "name3",
                        PendingRequestEvent.Effect.SUSPENDING,
                        PendingRequest.of("id3", RequestTypes.ABSENT, getATemplate())
                ),
                pendingRequestEventSerializer,
                pendingRequestEventDeserializer
        );
    }

    @Test
    public void testPendingRequest() {
        assertSerializationWorks(
                PendingRequest.of("id1", RequestTypes.ABSENT, getATemplate()),
                pendingRequestSerializer,
                pendingRequestDeserializer
        );
        assertSerializationWorks(
                PendingRequest.of("id2", RequestTypes.READ, getATemplate()),
                pendingRequestSerializer,
                pendingRequestDeserializer
        );
        assertSerializationWorks(
                PendingRequest.of("id3", RequestTypes.TAKE, getATemplate()),
                pendingRequestSerializer,
                pendingRequestDeserializer
        );
    }

    @Test
    public void testOperationEvent() {
        OperationEvent.Invocation<LogicTuple, LogicTemplate> value = OperationEvent.invocation(
                "tsName",
                OperationType.READ,
                Stream.empty(),
                Stream.of(getATemplate())
        );

        assertSerializationWorks(
                value,
                operationEventSerializer,
                operationEventDeserializer
        );

        assertSerializationWorks(
                value.toCompletion(Stream.of(getATuple()), Stream.empty()),
                operationEventSerializer,
                operationEventDeserializer
        );

        value = OperationEvent.invocation(
                "tsName1",
                OperationType.TAKE_ALL,
                getSomeTuplesOfTwoSorts().getValue0().stream(),
                Stream.of(getSomeTuplesOfTwoSorts().getValue1(), getSomeTuplesOfTwoSorts().getValue3())
        );

        assertSerializationWorks(
                value,
                operationEventSerializer,
                operationEventDeserializer
        );

        assertSerializationWorks(
                value.toCompletion(getSomeTuplesOfTwoSorts().getValue2().stream(), Stream.of(getATemplate())),
                operationEventSerializer,
                operationEventDeserializer
        );
    }
}
