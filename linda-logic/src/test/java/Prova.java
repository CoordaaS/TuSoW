import it.unibo.coordination.linda.logic.LogicSpace;
import it.unibo.coordination.linda.logic.LogicTemplate;

import java.util.concurrent.ExecutionException;

public class Prova {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var ts = LogicSpace.deterministic("");

        var t = ts.takeAnyTuple(LogicTemplate.of("f(X)"), LogicTemplate.of("g(X)"));

        Thread.sleep(1000);
        System.out.println(t);
        System.out.println(ts.getSize().get());

        ts.write("g(2)").get();

        Thread.sleep(1000);
        System.out.println(t);
        System.out.println(ts.getSize().get());

        ts.write("f(1)").get();

        System.out.println(t.get());
        System.out.println(ts.getSize().get());
    }
}
