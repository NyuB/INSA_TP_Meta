package jobshops.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.solvers.GreedySolver;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LPTGreedyTest extends SolverTest {
	@Override
	public GreedySolver getSolver() {
		return new GreedySolver(GreedySolver.Mode.LPT);
	}

	@Test
	public void testInstanceAAA1() throws IOException{
		Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));
		Result result = getSolver().solve(instance, -1);
	}
}
