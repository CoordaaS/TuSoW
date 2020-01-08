package it.unibo.coordination.linda.presentation;

import alice.tuprolog.Term;

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
                final Term term = Term.createTerm(line);
                System.out.println();
                System.out.println("json > " + term);
                System.out.println();
                final String yaml = Presentation.getSerializer(Term.class, MIMETypes.APPLICATION_JSON).toString(term);
                System.out.print("json > ");
                System.out.println(yaml.trim().replace("\n", "\njson > "));
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
