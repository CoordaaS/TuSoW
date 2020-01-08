package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.text.StringTuple;

import static it.unibo.coordination.utils.CollectionUtils.mapOf;

class StringTupleSerializer extends DynamicSerializer<StringTuple> {

    public StringTupleSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(mimeType, mapper);
    }

    @Override
    public Object toDynamicObject(StringTuple object) {
        return mapOf("tuple", object.getValue());
    }
}
