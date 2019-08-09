package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.string.StringTuple;

import java.util.Map;

class StringTupleSerializer extends DynamicSerializer<StringTuple> {

    public StringTupleSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(mimeType, mapper);
    }

    @Override
    public Object toDynamicObject(StringTuple object) {
        return Map.of("tuple", object.getTuple());
    }
}
