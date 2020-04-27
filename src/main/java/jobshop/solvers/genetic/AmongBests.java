package jobshop.solvers.genetic;

import java.util.List;
import java.util.Random;

public class AmongBests<I> implements Selector<I> {
	private int cutoffNum;
	private int cutoffDen;
	private Random random;
	public AmongBests(Random random, int cutoffNum, int cutoffDen) {
		if(cutoffNum> cutoffDen || cutoffNum<0 ) {
			throw new RuntimeException("Invalid proportion specifications in Selector");
		}
		this.random = random;
		this.cutoffNum = cutoffNum;
		this.cutoffDen = cutoffDen;
	}

	@Override
	public void select(Evaluator<I> evaluator, Mutator<I> mutator, Crosser<I> crosser, List<I> population, List<I> toFill, int nbToSelect) {
		int limit = (population.size() * this.cutoffNum)/this.cutoffDen;
		for(int i=0;i<nbToSelect;i++){
			int a = random.nextInt(limit);
			int b = random.nextInt(limit);
			toFill.add(mutator.mutate(crosser.cross(population.get(a), population.get(b))));
		}

	}
}
