package it.unibo.coordination.flow;

import alice.tuprolog.*;

import java.util.Objects;

public final class AgentId {

    private static final Prolog PROLOG = new Prolog();
    private static final Term TEMPLATE = Term.createTerm("agent_id(LN, EN)");
    private final String name;
    private final String environmentName;

    AgentId(String name, String environmentName) {
        this.name = Objects.requireNonNull(name);
        this.environmentName = environmentName;
    }

    AgentId(String name) {
        this(name, null);
    }

    public static Term getTemplate() {
        return TEMPLATE;
    }

    public static AgentId fromTerm(Term term) {
        final SolveInfo si = PROLOG.solve(new Struct("=", TEMPLATE, term));
        try {
            final String localName = ((Struct) si.getVarValue("LN")).getName();
            final String envName = ((Struct) si.getVarValue("EN")).getName();
            return new AgentId(localName, envName);
        } catch (NoSolutionException e) {
            throw new IllegalArgumentException(term.toString(), e);
        }
    }

    public static AgentId parse(String string) {
        if (Objects.requireNonNull(string).contains("@")) {
            final String[] parts = string.split("@");
            return new AgentId(parts[0], parts[1]);
        } else {
            return new AgentId(string);
        }
    }

    public boolean isLocal() {
        return environmentName == null;
    }

    public String getLocalName() {
        return name;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getCompleteName() {
        if (isLocal()) {
            return getLocalName();
        }
        return getLocalName() + "@" + getEnvironmentName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentId agentId = (AgentId) o;
        return Objects.equals(name, agentId.name) &&
                Objects.equals(getEnvironmentName(), agentId.getEnvironmentName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getEnvironmentName());
    }

    @Override
    public String toString() {
        return name + (environmentName == null ? "" : "@" + environmentName);
    }

    public Struct toTerm() {
        return new Struct("agent_id", new Struct(getLocalName()), getEnvironmentName() == null ? new Var() : new Struct(getEnvironmentName()));
    }
}
