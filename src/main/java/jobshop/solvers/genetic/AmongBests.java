package jobshop.solvers.genetic;

import java.util.List;
import java.util.Random;

public class AmongBests<I> implements Selector<I> {
	private int cutoffNumerator;
	private int cutoffDenominator;
	private Random random;
	public AmongBests(Random random, int cutoffNumerator, int cutoffDenominator) {
		if(cutoffNumerator > cutoffDenominator || cutoffNumerator <0 ) {
			throw new RuntimeException("Invalid proportion specifications in Selector");
		}
		this.random = random;
		this.cutoffNumerator = cutoffNumerator;
		this.cutoffDenominator = cutoffDenominator;
	}

	@Override
	public void select(Evaluator<I> evaluator, Mutator<I> mutator, Crosser<I> crosser, List<I> population, List<I> toFill, int nbToSelect) {
		int limit = (population.size() * this.cutoffNumerator)/this.cutoffDenominator;
		for(int i=0;i<nbToSelect;i++){
			int a = random.nextInt(limit);
			int b = random.nextInt(limit);
			toFill.add(mutator.mutate(crosser.cross(population.get(a), population.get(b))));
		}

	}
}
