package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.prologx.PrologUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class LogicMatchSerializer extends DynamicSerializer<LogicMatch> {


    public LogicMatchSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(mimeType, mapper);
    }

    @Override
    public Object toDynamicObject(LogicMatch object) {
        final Map<String, Object> matchMap = new HashMap<String, Object>();

        matchMap.put("tuple", object.getTuple().map(t -> getSerializer(LogicTuple.class).toDynamicObject(t)).orElse(null));

        matchMap.put("template", getSerializer(LogicTemplate.class).toDynamicObject(object.getTemplate()));

        matchMap.put("match", object.isMatching());

        final Map<String, Object> map = object.toMap().entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> PrologUtils.termToDynamicObject(entry.getValue())
                        )
                );

        matchMap.put("map", Collections.unmodifiableMap(map));

        return matchMap;
    }
}
