package jobshop.solvers.genetic;

import java.util.List;
import java.util.Random;

public class BinaryTournament<I> implements Selector<I> {
	private Random random;
	public BinaryTournament(Random random) {
		this.random = random;
	}

	@Override
	public void select(Evaluator<I> evaluator, Mutator<I> mutator, Crosser<I> crosser, List<I> population, List<I> toFill, int nbToSelect) {
		for(int i = 0;i<nbToSelect;i++){
			int aa = random.nextInt(population.size());
			int ab = random.nextInt(population.size());
			int ba = random.nextInt(population.size());
			int bb = random.nextInt(population.size());
			I indA = population.get(Integer.min(aa,ab));
			I indB = population.get(Integer.min(ba,bb));
			toFill.add(mutator.mutate(crosser.cross(indA,indB)));
		}

	}
}
