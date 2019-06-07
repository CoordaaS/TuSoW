//import it.unibo.coordination.linda.logic.

import it.unibo.coordination.linda.logic.remote.RemoteLogicSpace;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class Prova {
    public static void main(String[] args) throws ExecutionException, InterruptedException, MalformedURLException {
        RemoteLogicSpace ts = RemoteLogicSpace.of("127.0.0.1", 8080, "default");
//        RemoteLogicSpaceImpl ts = new RemoteLogicSpaceImpl(new URL("http", "localhost", 8080, ""), "default");
        ts.write("f(a)").get();
    }
}
