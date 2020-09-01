package it.unibo.coordination.linda.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import it.unibo.coordination.linda.test.TestBaseLinda;
import it.unibo.presentation.MIMETypes;
import it.unibo.tuprolog.core.Term;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
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
        yamlMapper = Presentation.INSTANCE.getMapper(MIMETypes.APPLICATION_YAML);
        jsonMapper = Presentation.INSTANCE.getMapper(MIMETypes.APPLICATION_JSON);
    }

    @Test
    public void testLogicTupleSerialisationToYAML() throws IOException {
        final LogicTuple tuple = LogicTuple.of("f(1, \"2\", '3', d, e(f), g(h, [4, i]), [x, y, Z])");
        final String resultString =
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
                "  - list: \n" +
                "    - 4\n" +
                "    - \"i\"\n" +
                "- list: \n" +
                "  - \"x\"\n" +
                "  - \"y\"\n" +
                "  - var: \"Z\"\n";

        final JsonNode result = yamlMapper.readTree(resultString);

        final String serialised = Presentation.INSTANCE.serializerOf(LogicTuple.class, MIMETypes.APPLICATION_YAML).toString(tuple);
        final JsonNode deserialised = yamlMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTupleSerialisationToJSON() throws IOException {
        final LogicTuple tuple = LogicTuple.of("f(1, \"2\", '3', d, e(f), g(h, [4, i]), [x, y, Z])");
        final String resultString = "{\"fun\":\"f\",\"args\":[1,\"2\",\"3\",\"d\",{\"fun\":\"e\",\"args\":[\"f\"]},{\"fun\":\"g\",\"args\":[\"h\",{\"list\":[4,\"i\"]}]},{\"list\":[\"x\",\"y\",{\"var\":\"Z\"}]}]}";

        final JsonNode result = jsonMapper.readTree(resultString);

        final String serialised = Presentation.INSTANCE.serializerOf(LogicTuple.class, MIMETypes.APPLICATION_JSON).toString(tuple);
        final JsonNode deserialised = jsonMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTuplesSerialisationToYAML() throws IOException {
        final List<LogicTuple> tuples = Stream.of("a(1)", "b(2)", "c(3)").map(LogicTuple::of).collect(Collectors.toList());
        final String resultString =
                "- fun: \"a\"\n" +
                "  args:\n" +
                "  - 1\n" +
                "- fun: \"b\"\n" +
                "  args:\n" +
                "  - 2\n" +
                "- fun: \"c\"\n" +
                "  args:\n" +
                "  - 3";

        final JsonNode result = yamlMapper.readTree(resultString);

        final String serialised = Presentation.INSTANCE.serializerOf(LogicTuple.class, MIMETypes.APPLICATION_YAML).toString(tuples);
        final JsonNode deserialised = yamlMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTuplesSerialisationToJSON() throws IOException {
        final List<LogicTuple> tuples = Stream.of("a(1)", "b(2)", "c(3)").map(LogicTuple::of).collect(Collectors.toList());
        final String resultString = "[{\"fun\":\"a\",\"args\":[1]},{\"fun\":\"b\",\"args\":[2]},{\"fun\":\"c\",\"args\":[3]}]";

        final JsonNode result = jsonMapper.readTree(resultString);

        final String serialised = Presentation.INSTANCE.serializerOf(LogicTuple.class, MIMETypes.APPLICATION_JSON).toString(tuples);
        final JsonNode deserialised = jsonMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTemplateSerialisationToYAML() throws IOException {
        final LogicTemplate template = LogicTemplate.of("f(1, A, B, C, D, g(E, [F | G]), H)");
        final String resultString =
                "fun: \"f\"\n" +
                "args:\n" +
                        "- 1\n" +
                        "- var: \"A\"\n" +
                        "- var: \"B\"\n" +
                        "- var: \"C\"\n" +
                        "- var: \"D\"\n" +
                        "- fun: \"g\"\n" +
                        "  args:\n" +
                        "  - var: \"E\"\n" +
                        "  - list: \n" +
                        "    - var: \"F\"\n" +
                        "    tail: \n" +
                        "      var: \"G\"\n" +
                        "- var: \"H\"\n";

        final JsonNode result = yamlMapper.readTree(resultString);

        final String serialised = Presentation.INSTANCE.serializerOf(LogicTemplate.class, MIMETypes.APPLICATION_YAML).toString(template);
        final JsonNode deserialised = yamlMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTemplateSerialisationToJSON() throws IOException {
        final LogicTemplate template = LogicTemplate.of("f(1, A, B, C, D, g(E, [F | G]), H)");
        final String resultString = "{\"fun\":\"f\",\"args\":[1,{\"var\":\"A\"},{\"var\":\"B\"},{\"var\":\"C\"},{\"var\":\"D\"},{\"fun\":\"g\",\"args\":[{\"var\":\"E\"},{\"list\":[{\"var\":\"F\"}],\"tail\":{\"var\":\"G\"}}]},{\"var\":\"H\"}]}";

        final JsonNode result = jsonMapper.readTree(resultString);

        final String serialised = Presentation.INSTANCE.serializerOf(LogicTemplate.class, MIMETypes.APPLICATION_JSON).toString(template);
        final JsonNode deserialised = jsonMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTemplatesSerialisationToYAML() throws IOException {
        final List<LogicTemplate> templates = Stream.of("a(A)", "b(B)", "c(C)").map(LogicTemplate::of).collect(Collectors.toList());
        final String resultString =
                "- fun: \"a\"\n" +
                "  args:\n" +
                "  - var: A\n" +
                "- fun: \"b\"\n" +
                "  args:\n" +
                "  - var: B\n" +
                "- fun: \"c\"\n" +
                "  args:\n" +
                "  - var: C\n";

        final JsonNode result = yamlMapper.readTree(resultString);

        final String serialised = Presentation.INSTANCE.serializerOf(LogicTemplate.class, MIMETypes.APPLICATION_YAML).toString(templates);
        final JsonNode deserialised = yamlMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }

    @Test
    public void testLogicTemplatesSerialisationToJSON() throws IOException {
        final List<LogicTemplate> templates = Stream.of("a(A)", "b(B)", "c(C)").map(LogicTemplate::of).collect(Collectors.toList());
        final String resultString = "[{\"fun\":\"a\",\"args\":[{\"var\":\"A\"}]},{\"fun\":\"b\",\"args\":[{\"var\":\"B\"}]},{\"fun\":\"c\",\"args\":[{\"var\":\"C\"}]}]";

        final JsonNode result = jsonMapper.readTree(resultString);

        final String serialised = Presentation.INSTANCE.serializerOf(LogicTemplate.class, MIMETypes.APPLICATION_JSON).toString(templates);
        final JsonNode deserialised = jsonMapper.readTree(serialised);

        Assert.assertEquals(result, deserialised);

    }
}
