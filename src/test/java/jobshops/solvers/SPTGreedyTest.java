package jobshops.solvers;

import jobshop.solvers.greedy.GreedySolver;
import jobshop.solvers.greedy.Mode;

public class SPTGreedyTest extends SolverTest {
	@Override
	public GreedySolver getSolver() {
		return new GreedySolver(Mode.SPT);
	}
}
