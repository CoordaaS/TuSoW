import it.unibo.coordination.Engine;
import it.unibo.coordination.linda.logic.LogicSpace;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.testing.ActiveObject;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DiningPhilosophers {
    public static final int MAX_ROUND = 10;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final var numOfPhilosophers = 5000;

        final var ts = LogicSpace.deterministic("table");

        ts.writeAll(
                IntStream.range(0, numOfPhilosophers)
                    .mapToObj(i -> LogicTuple.of("chop(%d)", i))
                    .collect(Collectors.toList())
        ).get();

        final var philosophers = IntStream.range(0, numOfPhilosophers)
                .mapToObj(i -> new Philosopher(i, numOfPhilosophers, ts))
                .peek(ActiveObject::start)
                .collect(Collectors.toList());

        philosophers.forEach(ActiveObject::await);
        Engine.getDefaultEngine().shutdown();
    }

    static class Philosopher extends ActiveObject {

        private final int index;
        private final int total;
        private final LogicSpace tupleSpace;
        private int round = 0;

        public Philosopher(int index, int total, LogicSpace tupleSpace) {
            super("Philosopher-" + (index + 1) + "-of-" + total);
            this.index = index;
            this.total = total;
            this.tupleSpace = Objects.requireNonNull(tupleSpace);
        }

        private LogicTuple getFirstChop() {
            return LogicTuple.of("chop(%d)", index);
        }

        private LogicTuple getSecondChop() {
            return LogicTuple.of("chop(%d)", (index + 1) % total);
        }

        @Override
        protected void loop() throws Exception {
//            sleepFor(Duration.ofSeconds(1));
            log("think %d", round);
            tupleSpace.takeEach(getFirstChop().toTemplate(), getSecondChop().toTemplate()).get();
//            sleepFor(Duration.ofSeconds(1));
            log("eat %d", round++);
            tupleSpace.writeAll(getFirstChop(), getSecondChop()).get();

            if (round == MAX_ROUND) stop();
        }
    }
}
