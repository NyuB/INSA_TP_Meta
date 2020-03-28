package jobshops.solvers;

import jobshop.Instance;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.solvers.genetic.GeneticSolverResource;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class GeneticTest extends SolverTest {
	@Override
	public Solver getSolver() {
		return new GeneticSolverResource(0.5,100);
	}

	@Test
	public void generateTest() throws IOException {
		for(String name : new String[] {"aaa1","la01","la02"}) {
			Instance instance = Instance.fromFile(Paths.get("instances/" + name));
			for (int i = 0; i < 100; i++) {
				//ResourceOrder resourceOrder = instance.generate();
				//Assert.assertTrue(resourceOrder.toSchedule().isValid());
			}
		}
	}

	@Test
	public void crossTest() throws IOException {
		for(String name : new String[] {"aaa1","la01","la02"}) {
			Instance instance = Instance.fromFile(Paths.get("instances/" + name));
			for(int i=0;i<100;i++){
				//ResourceOrder a = instance.generate();
				//ResourceOrder b = instance.generate();
				//Assert.assertTrue(instance.cross(a,b).toSchedule().isValid());

			}
		}
	}



}
