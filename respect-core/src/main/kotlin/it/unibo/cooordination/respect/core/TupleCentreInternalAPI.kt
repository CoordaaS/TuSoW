package it.unibo.cooordination.respect.core

import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface TupleCentreInternalAPI<
        T : Tuple<T>,
        TT : Template<T>,
        K,
        V,
        M : Match<T, TT, K, V>,
        ST : SpecificationTuple<T, TT, ST>,
        STT : SpecificationTemplate<T, TT, ST>,
        SM : Match<ST, STT, K, V>
    > {
    fun out(tuple: T)
    fun `in`(template: TT): M
    fun rd(template: TT): M
    fun no(template: TT): M

    fun outS(specificationTuple: ST)
    fun inS(specificationTemplate: STT): SM
    fun rdS(specificationTemplate: STT): SM
    fun noS(specificationTemplate: STT): SM

    fun env(transducerID: TransducerID, key: String, value: T)
}