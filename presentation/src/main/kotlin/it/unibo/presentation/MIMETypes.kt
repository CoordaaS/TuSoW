package it.unibo.presentation

import java.util.*

enum class MIMETypes(val type: String, val subtype: String) {
    APPLICATION_JSON("application", "json"),
    APPLICATION_YAML("application", "yaml"),
    APPLICATION_XML("application", "xml"),
    APPLICATION_PROLOG("application", "prolog"),
    TEXT_HTML("text", "html"),
    TEXT_PLAIN("text", "plain"), ANY("*", "*"),
    APPLICATION_ANY("application", "*");

    override fun toString(): String {
        return "$type/$subtype"
    }

    fun matches(other: String?): Boolean {
        return match(this, other)
    }

    companion object {

        val XML_JSON_YAML = EnumSet.of(APPLICATION_XML, APPLICATION_JSON, APPLICATION_YAML)

        @JvmStatic
        fun match(mime: MIMETypes, other: String?): Boolean {
            if (other == null || !other.contains("/")) return false
            val parts = other.split("/").toTypedArray()
            return if (parts.size != 2) false else match(mime, parts[0], parts[1])
        }

        @JvmStatic
        fun match(mime: MIMETypes, type: String, subtype: String): Boolean {
            return ((sequenceOf(mime.type, type).any { "*" == it } || mime.type.equals(type, ignoreCase = true))
                    && (sequenceOf(mime.subtype, subtype).any { "*" == it }  || mime.subtype.equals(subtype, ignoreCase = true)))
        }

        @JvmStatic
        fun parse(string: String?): MIMETypes {
            return sequenceOf(*values())
                    .find { it.toString().equals(string, ignoreCase = true) }
                    ?: throw IllegalArgumentException(string)
        }
    }

}