package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringTuple;

import java.util.HashMap;

class RegularMatchSerializer extends DynamicSerializer<RegularMatch> {


    public RegularMatchSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(mimeType, mapper);
    }

    @Override
    public Object toDynamicObject(RegularMatch object) {
        final var matchMap = new HashMap<String, Object>();

        matchMap.put("tuple", object.getTuple().map(StringTuple::getTuple).orElse(null));
        matchMap.put("template", object.getTemplate().getTemplate().pattern());
        matchMap.put("match", object.isMatching());
        matchMap.put("map", object.toMap());

        return matchMap;
    }
}
