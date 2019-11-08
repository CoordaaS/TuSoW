package it.unibo.coordination.linda.text;

class RegexTemplateImpl implements RegexTemplate {

    private final com.google.code.regexp.Pattern pattern;

    public RegexTemplateImpl(String pattern) {
        this.pattern = com.google.code.regexp.Pattern.compile(pattern);
    }

    public RegexTemplateImpl(com.google.code.regexp.Pattern pattern) {
        this.pattern = pattern;
    }

    public RegexTemplateImpl(java.util.regex.Pattern pattern) {
        this.pattern = com.google.code.regexp.Pattern.compile(pattern.pattern());
    }

    @Override
    public java.util.regex.Pattern getTemplate() {
        return pattern.pattern();
    }

    @Override
    public RegularMatch matchWith(StringTuple tuple) {
        return new RegularMatchImpl(this, pattern.matcher(tuple.getValue()), tuple);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegexTemplate that = (RegexTemplate) o;
        return RegexTemplate.equals(this, that);
    }

    @Override
    public int hashCode() {
        return RegexTemplate.hashCode(this);
    }

    @Override
    public String toString() {
        return "/" + pattern + "/";
    }
}
