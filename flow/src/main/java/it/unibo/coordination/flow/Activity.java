package it.unibo.coordination.flow;

import java.util.Optional;

public interface Activity<A, C, R> {
    Continuation<C> onBegin(A argument) throws Exception;

    Continuation<C> onStep(C context) throws Exception;

    R onEnd(C context, Optional<Exception> exception) throws Exception;

    Continuation<C> onError(C context, Exception exception);
}
