package it.unibo.cooordination.respect.core

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface SpecificationTemplate<T : Tuple<T>, TT: Template<T>,  ST : SpecificationTuple<T, TT, ST>> : Template<ST>