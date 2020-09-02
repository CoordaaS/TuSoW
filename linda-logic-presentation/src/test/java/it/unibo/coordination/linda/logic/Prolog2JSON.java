package it.unibo.coordination.linda.logic;

import it.unibo.presentation.MIMETypes;
import it.unibo.tuprolog.core.Term;
import it.unibo.tuprolog.core.parsing.TermParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Prolog2JSON {

    private final static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        while (true) {
            try {
                System.out.print("prolog > ");
                final String line;
                line = reader.readLine();
                final Term term = TermParser.getWithDefaultOperators().parseTerm(line);
                System.out.println();
                System.out.println("json > " + term);
                System.out.println();
                final String yaml = Presentation.INSTANCE.serializerOf(Term.class, MIMETypes.APPLICATION_JSON).toString(term);
                System.out.print("json > ");
                System.out.println(yaml.trim().replace("\n", "\njson > "));
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
