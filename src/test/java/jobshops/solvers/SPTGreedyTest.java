package jobshops.solvers;

import jobshop.Solver;
import jobshop.solvers.GreedySolver;

public class SPTGreedyTest extends SolverTest {
	@Override
	public GreedySolver getSolver() {
		return new GreedySolver(GreedySolver.Mode.SPT);
	}
}
