package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.prologx.PrologUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LogicMatchSerializer extends DynamicSerializer<LogicMatch> {


    public LogicMatchSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(mimeType, mapper);
    }

    @Override
    public Object toDynamicObject(LogicMatch object) {
        final var matchMap = new HashMap<String, Object>();

        matchMap.put("tuple", object.getTuple().map(t -> getMarshaller(LogicTuple.class).toDynamicObject(t)).orElse(null));

        matchMap.put("template", getMarshaller(LogicTemplate.class).toDynamicObject(object.getTemplate()));

        matchMap.put("match", object.isMatching());

        final var map = object.toMap().entrySet().stream()
                .collect(
                        Collectors.toUnmodifiableMap(
                                Map.Entry::getKey,
                                entry -> PrologUtils.termToDynamicObject(entry.getValue())
                        )
                );

        matchMap.put("map", map);

        return matchMap;
    }
}
