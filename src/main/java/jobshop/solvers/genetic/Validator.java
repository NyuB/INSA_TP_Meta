package jobshop.solvers.genetic;

public interface Validator<I> {
	boolean validate(I individual);
}
