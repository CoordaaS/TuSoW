package it.unibo.coordination.linda.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.coordination.linda.text.RegexTemplate;

import static it.unibo.coordination.utils.CollectionUtils.mapOf;

class RegexTemplateSerializer extends DynamicSerializer<RegexTemplate> {

    public RegexTemplateSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(mimeType, mapper);
    }

    @Override
    public Object toDynamicObject(RegexTemplate object) {
        return mapOf("template", object.getTemplate().pattern());
    }
}
