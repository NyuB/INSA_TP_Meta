package jobshops.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public  abstract class SolverTest {
	public abstract Solver getSolver();

	public void checkSimpleSolve(Instance instance){
		Solver solver = getSolver();
		Result result = solver.solve(instance,System.currentTimeMillis()+10);
		Assert.assertTrue(result.schedule.isValid());
		System.out.println("Result for solver  "+solver.getClass().getSimpleName()+" : "+result.schedule.makespan());
	}

	@Test
	public void testSimpleSolve() throws IOException {
		Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));
		checkSimpleSolve(instance);
		instance = Instance.fromFile(Paths.get("instances/ft10"));
		checkSimpleSolve(instance);
		instance = Instance.fromFile(Paths.get("instances/ft20"));
		checkSimpleSolve(instance);
		instance = Instance.fromFile(Paths.get("instances/la01"));
		checkSimpleSolve(instance);

	}
}
