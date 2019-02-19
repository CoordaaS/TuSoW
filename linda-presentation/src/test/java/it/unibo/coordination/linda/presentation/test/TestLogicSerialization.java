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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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

    @Test
    public void testLogicTemplateSerialisationToYAML() throws IOException {
        final var template = LogicTemplate.of("f(1, A, B, C, D, g(E, [F | G]), H)");
        final var resultString =
                "fun: \"f\"\n" +
                "args:\n" +
                "- 1\n" +
                "- var: \"A\"\n" +
                "  val: null\n" +
                "- var: \"B\"\n" +
                "  val: null\n" +
                "- var: \"C\"\n" +
                "  val: null\n" +
                "- var: \"D\"\n" +
                "  val: null\n" +
                "- fun: \"g\"\n" +
                "  args:\n" +
                "  - var: \"E\"\n" +
                "    val: null\n" +
                "  - fun: \".\"\n" +
                "    args:\n" +
                "    - var: \"F\"\n" +
                "      val: null\n" +
                "    - var: \"G\"\n" +
                "      val: null\n" +
                "- var: \"H\"\n" +
                "  val: null";

        final var result = yamlMapper.readTree(resultString);

        final var serialised = Presentation.getSerializer(LogicTemplate.class, MIMETypes.APPLICATION_YAML).toString(template);
        final var deserialised = yamlMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTemplateSerialisationToJSON() throws IOException {
        final var template = LogicTemplate.of("f(1, A, B, C, D, g(E, [F | G]), H)");
        final var resultString = "{\"fun\":\"f\",\"args\":[1,{\"var\":\"A\",\"val\":null},{\"var\":\"B\",\"val\":null},{\"var\":\"C\",\"val\":null},{\"var\":\"D\",\"val\":null},{\"fun\":\"g\",\"args\":[{\"var\":\"E\",\"val\":null},{\"fun\":\".\",\"args\":[{\"var\":\"F\",\"val\":null},{\"var\":\"G\",\"val\":null}]}]},{\"var\":\"H\",\"val\":null}]}";

        final var result = jsonMapper.readTree(resultString);

        final var serialised = Presentation.getSerializer(LogicTemplate.class, MIMETypes.APPLICATION_JSON).toString(template);
        final var deserialised = jsonMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTemplatesSerialisationToYAML() throws IOException {
        final var templates = Stream.of("a(A)", "b(B)", "c(C)").map(LogicTemplate::of).collect(Collectors.toList());
        final var resultString =
                "- fun: \"a\"\n" +
                "  args:\n" +
                "  - var: A\n" +
                "    val: null\n" +
                "- fun: \"b\"\n" +
                "  args:\n" +
                "  - var: B\n" +
                "    val: null\n" +
                "- fun: \"c\"\n" +
                "  args:\n" +
                "  - var: C\n" +
                "    val: null\n";

        final var result = yamlMapper.readTree(resultString);

        final var serialised = Presentation.getSerializer(LogicTemplate.class, MIMETypes.APPLICATION_YAML).toString(templates);
        final var deserialised = yamlMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTemplatesSerialisationToJSON() throws IOException {
        final var templates = Stream.of("a(A)", "b(B)", "c(C)").map(LogicTemplate::of).collect(Collectors.toList());
        final var resultString = "[{\"fun\":\"a\",\"args\":[{\"var\":\"A\",\"val\":null}]},{\"fun\":\"b\",\"args\":[{\"var\":\"B\",\"val\":null}]},{\"fun\":\"c\",\"args\":[{\"var\":\"C\",\"val\":null}]}]";

        final var result = jsonMapper.readTree(resultString);

        final var serialised = Presentation.getSerializer(LogicTemplate.class, MIMETypes.APPLICATION_JSON).toString(templates);
        final var deserialised = jsonMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }
}
