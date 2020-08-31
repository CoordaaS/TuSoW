package it.unibo.cooordination.respect.core

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple

interface TupleCentreExternalAPI<
        T : Tuple<T>,
        TT : Template<T>,
        K,
        V,
        M : Match<T, TT, K, V>,
        ST : SpecificationTuple<T, TT, ST>,
        STT : SpecificationTemplate<T, TT, ST>,
        SM : Match<ST, STT, K, V>
        > {
    fun out(agentID: AgentID, tuple: T): Promise<T>
    fun `in`(agentID: AgentID, template: TT): Promise<M>
    fun rd(agentID: AgentID, template: TT): Promise<M>
    fun no(agentID: AgentID, template: TT): Promise<M>

    fun outS(agentID: AgentID, specificationTuple: ST): Promise<ST>
    fun inS(agentID: AgentID, specificationTemplate: STT): Promise<SM>
    fun rdS(agentID: AgentID, specificationTemplate: STT): Promise<SM>
    fun noS(agentID: AgentID, specificationTemplate: STT): Promise<SM>
}