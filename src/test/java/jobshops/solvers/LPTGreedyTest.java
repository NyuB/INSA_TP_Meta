package jobshops.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.solvers.greedy.GreedySolver;
import jobshop.solvers.Mode;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class LPTGreedyTest extends SolverTest {
	@Override
	public GreedySolver getSolver() {
		return new GreedySolver(Mode.LPT);
	}

	@Test
	public void testInstanceAAA1() throws IOException{
		Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));
		Result result = getSolver().solve(instance, -1);
	}
}
