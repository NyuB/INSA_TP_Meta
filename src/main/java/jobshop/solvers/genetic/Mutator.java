package jobshop.solvers.genetic;

public interface Mutator<I> {

	void setMutationRate(double mutationRate);

	I mutate(I individual);
}
