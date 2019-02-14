package it.unibo.coordination.linda.presentation;

import alice.tuprolog.Term;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Prolog2YAML {

    private final static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
//        while (true) {
//            try {
//                System.out.print("prolog > ");
//                final String line;
//                line = reader.readLine();
//                final var term = Term.createTerm(line);
//                System.out.println();
//                System.out.println("prolog > " + term);
//                System.out.println();
//                final var yaml = Presentation.getSerializer(Term.class, MIMETypes.APPLICATION_YAML).toString(term);
//                System.out.print("yaml > ");
//                System.out.println(yaml.trim().replace("\n", "\nyaml > "));
//                System.out.println();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        Term term = Term.createTerm("f(1)");
        LogicTemplate template = LogicTemplate.of(term);
        LogicTuple tuple = LogicTuple.of(term);
        LogicMatch match = template.matchWith(tuple);
        System.out.println(match);
        String json = Presentation.getSerializer(LogicMatch.class, MIMETypes.APPLICATION_JSON).toString(match);
        match = Presentation.getDeserializer(LogicMatch.class, MIMETypes.APPLICATION_JSON).fromString(json);
        System.out.println(match);
    }
}
