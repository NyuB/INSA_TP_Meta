package jobshop.solvers.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public abstract class GeneticAlgorithm<I> {
	protected Mutator<I> mutator;
	protected Evaluator<I> evaluator;
	protected Crosser<I> crosser;
	protected Populator<I> populator;
	protected Validator<I> validator;
	protected Selector<I> selector;

	public void setMutator(Mutator<I> mutator) {
		this.mutator = mutator;
	}

	public void setEvaluator(Evaluator<I> evaluator) {
		this.evaluator = evaluator;
	}

	public void setCrosser(Crosser<I> crosser) {
		this.crosser = crosser;
	}

	public void setPopulator(Populator<I> populator) {
		this.populator = populator;
	}

	public void setValidator(Validator<I> validator) {
		this.validator = validator;
	}

	public void setSelector(Selector<I> selector) {
		this.selector = selector;
	}

	I compute(int populationSize, double mutationRate, long deadline){
		ArrayList<I> candidates = new ArrayList<>();
		while(System.currentTimeMillis()<deadline) {
			//System.out.println("Restart");
			ArrayList<I> population = new ArrayList<>();
			populator.populate(population,populationSize);
			this.mutator.setMutationRate(mutationRate);
			int iter =0;
			int best = Integer.MIN_VALUE;
			while (iter<1000 && System.currentTimeMillis() < deadline) {
				evaluator.sort(population);
				I candidate = population.get(0);
				int eval = evaluator.evaluate(candidate);
				if (eval > best) {
					best = eval;
					candidates.add(candidate);
					//System.out.println("New best " + best + " found at iteration " + iter + " " + (deadline - System.currentTimeMillis()) + "ms");
					iter = 0;
				} else {
					iter++;
				}
				ArrayList<I> nextPopulation = new ArrayList<>();
				selector.select(evaluator, mutator, crosser, population, nextPopulation, population.size());
				population = nextPopulation;
			}

		}
		evaluator.sort(candidates);
		for (I i : candidates) {
			if (validator.validate(i)) return i;
		}
		System.out.println("No valid selected");
		return this.populator.generate();
	}




}
