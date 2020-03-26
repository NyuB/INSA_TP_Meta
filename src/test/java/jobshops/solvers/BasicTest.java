package jobshops.solvers;

import jobshop.Instance;
import jobshop.Solver;
import jobshop.solvers.BasicSolver;

public class BasicTest extends SolverTest {
	@Override
	public Solver getSolver() {
		return new BasicSolver();
	}
}
