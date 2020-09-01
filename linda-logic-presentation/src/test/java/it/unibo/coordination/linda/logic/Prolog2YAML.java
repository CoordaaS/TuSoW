package it.unibo.coordination.linda.logic;

import it.unibo.presentation.MIMETypes;
import it.unibo.tuprolog.core.Term;
import it.unibo.tuprolog.core.parsing.TermParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Prolog2YAML {

    private final static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        while (true) {
            try {
                System.out.print("prolog > ");
                final String line;
                line = reader.readLine();
                final Term term = TermParser.getWithDefaultOperators().parseTerm(line);
                System.out.println();
                System.out.println("prolog > " + term);
                System.out.println();
                final String yaml = Presentation.INSTANCE.serializerOf(Term.class, MIMETypes.APPLICATION_YAML).toString(term);
                System.out.print("yaml > ");
                System.out.println(yaml.trim().replace("\n", "\nyaml > "));
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
