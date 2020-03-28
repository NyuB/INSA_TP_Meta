package jobshop.solvers.genetic;

public interface Mutator<I> {
	I mutate(I individual,double proba);
}
