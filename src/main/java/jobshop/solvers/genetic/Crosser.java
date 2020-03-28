package jobshop.solvers.genetic;

public interface Crosser<I> {
	I cross(I individual,I other);
}
