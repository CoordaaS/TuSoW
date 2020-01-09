package it.unibo.coordination.linda.logic;

import alice.tuprolog.Term;
import it.unibo.presentation.MIMETypes;

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
                final Term term = Term.createTerm(line);
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
