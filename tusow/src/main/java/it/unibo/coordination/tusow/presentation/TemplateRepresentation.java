package it.unibo.coordination.tusow.presentation;

import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.logic.LogicTemplate;

public interface TemplateRepresentation extends Template, Representation {
    static TemplateRepresentation wrap(Template tuple) {
        if (tuple instanceof TemplateRepresentation) {
            return (TemplateRepresentation) tuple;
        } else if (tuple instanceof LogicTemplate) {
            return LogicTemplateRepresentation.wrap((LogicTemplate) tuple);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
