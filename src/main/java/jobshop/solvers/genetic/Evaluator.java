package jobshop.solvers.genetic;
import java.util.Collections;
import java.util.List;

public interface Evaluator<I> {
	int evaluate(I individual);
	default void sort(List<I> population){
		population.sort((a, b) -> this.evaluate(b) - this.evaluate(a));
	}
}
