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

	I compute(int batchSize, int reference, double proba, long deadline){
		ArrayList<I> population = new ArrayList<>();
		int[] scores = new int[batchSize];
		Random random = new Random();
		for(int i=0;i<batchSize;i++){
			population.add(this.populator.generate());
		}
		ArrayList<I> candidates = new ArrayList<>();
		int iter =0;
		while (System.currentTimeMillis()<deadline) {
			Collections.sort(population,(a,b)-> evaluator.evaluate(b,reference)- evaluator.evaluate(a,reference));
			I candidate = population.get(0);
			candidates.add(candidate);
			ArrayList<I> nextPopulation = new ArrayList<>();

			for(int i=0;i<population.size()/2;i++){
				//Binary tournament
				int aa = random.nextInt(population.size());
				int ab = random.nextInt(population.size());
				int ba = random.nextInt(population.size());
				int bb = random.nextInt(population.size());
				I a = population.get(Integer.min(aa,ab));
				I b = population.get(Integer.min(ba,bb));
				nextPopulation.add(mutator.mutate(crosser.cross(a, b), proba));
			}

			for(int i=0;i<population.size()/2;i++){
				//Best fit
				int aa = random.nextInt(population.size()/4);
				int bb = random.nextInt(population.size()/4);
				I a = population.get(aa);
				I b = population.get(bb);
				nextPopulation.add(mutator.mutate(crosser.cross(a, b), proba));
			}
			iter++;
			population = nextPopulation;
		}
		Collections.sort(candidates,(a,b)-> evaluator.evaluate(b,reference)- evaluator.evaluate(a,reference));
		for(I i :candidates){
			if(validator.validate(i))return i;
		}
		System.out.println("No valid selected");
		return this.populator.generate();
	}




}
