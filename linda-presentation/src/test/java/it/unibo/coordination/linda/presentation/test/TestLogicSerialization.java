package it.unibo.coordination.linda.presentation.test;

import alice.tuprolog.Term;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.linda.presentation.MIMETypes;
import it.unibo.coordination.linda.presentation.Presentation;
import it.unibo.coordination.linda.test.TestBaseLinda;
import it.unibo.coordination.linda.test.TestMatch;
import it.unibo.coordination.linda.test.TupleTemplateFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestLogicSerialization extends TestBaseLinda<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    private YAMLMapper yamlMapper;
    private ObjectMapper jsonMapper;

    public TestLogicSerialization() {
        super(new LogicTupleTemplateFactory());
    }

    @Before
    public void setUp() {
        yamlMapper = Presentation.createMapper(YAMLMapper.class);
        jsonMapper = Presentation.createMapper(ObjectMapper.class);
    }

    @Test
    public void testLogicTupleSerialisationToYAML() throws IOException {
        final var tuple = LogicTuple.of("f(1, \"2\", '3', d, e(f), g(h, [4, i]), [x, y, Z])");
        final var resultString =
                "fun: \"f\"\n" +
                "args:\n" +
                "- 1\n" +
                "- \"2\"\n" +
                "- \"3\"\n" +
                "- \"d\"\n" +
                "- fun: \"e\"\n" +
                "  args:\n" +
                "  - \"f\"\n" +
                "- fun: \"g\"\n" +
                "  args:\n" +
                "  - \"h\"\n" +
                "  - \n" +
                "    - 4\n" +
                "    - \"i\"\n" +
                "- \n" +
                "  - \"x\"\n" +
                "  - \"y\"\n" +
                "  - var: \"Z\"\n" +
                "    val: null";

        final var result = yamlMapper.readTree(resultString);

        final var serialised = Presentation.getSerializer(LogicTuple.class, MIMETypes.APPLICATION_YAML).toString(tuple);
        final var deserialised = yamlMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTupleSerialisationToJSON() throws IOException {
        final var tuple = LogicTuple.of("f(1, \"2\", '3', d, e(f), g(h, [4, i]), [x, y, Z])");
        final var resultString = "{\"fun\":\"f\",\"args\":[1,\"2\",\"3\",\"d\",{\"fun\":\"e\",\"args\":[\"f\"]},{\"fun\":\"g\",\"args\":[\"h\",[4,\"i\"]]},[\"x\",\"y\",{\"var\":\"Z\",\"val\":null}]]}";

        final var result = jsonMapper.readTree(resultString);

        final var serialised = Presentation.getSerializer(LogicTuple.class, MIMETypes.APPLICATION_JSON).toString(tuple);
        final var deserialised = jsonMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTuplesSerialisationToYAML() throws IOException {
        final var tuples = Stream.of("a(1)", "b(2)", "c(3)").map(LogicTuple::of).collect(Collectors.toList());
        final var resultString =
                "- fun: \"a\"\n" +
                "  args:\n" +
                "  - 1\n" +
                "- fun: \"b\"\n" +
                "  args:\n" +
                "  - 2\n" +
                "- fun: \"c\"\n" +
                "  args:\n" +
                "  - 3";

        final var result = yamlMapper.readTree(resultString);

        final var serialised = Presentation.getSerializer(LogicTuple.class, MIMETypes.APPLICATION_YAML).toString(tuples);
        final var deserialised = yamlMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTuplesSerialisationToJSON() throws IOException {
        final var tuples = Stream.of("a(1)", "b(2)", "c(3)").map(LogicTuple::of).collect(Collectors.toList());
        final var resultString = "[{\"fun\":\"a\",\"args\":[1]},{\"fun\":\"b\",\"args\":[2]},{\"fun\":\"c\",\"args\":[3]}]";

        final var result = jsonMapper.readTree(resultString);

        final var serialised = Presentation.getSerializer(LogicTuple.class, MIMETypes.APPLICATION_JSON).toString(tuples);
        final var deserialised = jsonMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }
}
