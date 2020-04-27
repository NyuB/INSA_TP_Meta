package jobshop.solvers.genetic;

import java.util.List;

public interface Selector<I> {
	/**
	 * @param evaluator Assigns a score to and individual
	 * @param mutator Randomly alters an individual
	 * @param crosser Randomly mix two individuals
	 * @param population A population sorted by descending score of interest(logically ordered by evaluator but not necessarily)
	 * @param toFill The next population to fill with selected individuals
	 * @param nbToSelect The number of individuals to select
	 */
	void select(Evaluator<I> evaluator,Mutator<I> mutator, Crosser<I> crosser,List<I> population,List<I> toFill,int nbToSelect);

}
