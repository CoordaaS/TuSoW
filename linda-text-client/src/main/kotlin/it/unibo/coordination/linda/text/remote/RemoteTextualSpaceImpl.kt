package it.unibo.coordination.linda.text.remote

import it.unibo.coordination.linda.remote.AbstractRemoteTupleSpace
import it.unibo.coordination.linda.text.RegexTemplate
import it.unibo.coordination.linda.text.RegularMatch
import it.unibo.coordination.linda.text.StringTuple
import java.net.URL

internal class RemoteTextualSpaceImpl(service: URL, name: String) : RemoteTextualSpace, AbstractRemoteTupleSpace<StringTuple, RegexTemplate, Any, String, RegularMatch>(service, name) {
    override val tupleSpaceType: String
        get() = "textual"

    override val tupleClass: Class<StringTuple>
        get() = StringTuple::class.java

    override val templateClass: Class<RegexTemplate>
        get() = RegexTemplate::class.java

    override val matchClass: Class<RegularMatch>
        get() = RegularMatch::class.java
}