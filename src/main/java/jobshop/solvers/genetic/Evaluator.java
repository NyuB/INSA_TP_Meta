package jobshop.solvers.genetic;

public interface Evaluator<I> {
	int evaluate(I individual);
}
