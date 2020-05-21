package jobshop.solvers.descent;

import jobshop.encodings.ResourceOrder;

import java.util.List;

public class BasicDescentSolver extends DescentSolver {
	public BasicDescentSolver(boolean loopWithRandomSeed) {
		super(loopWithRandomSeed);
	}

	protected boolean update(List<DescentSolver.Swap> neighborhood)  {
		DescentSolver.Swap elected = null;
		ResourceOrder next = currentPoint;
		for (DescentSolver.Swap s : neighborhood) {
			ResourceOrder aux = currentPoint.copy();
			s.applyOn(aux);
			int score = aux.toSchedule().makespan();
			if (score < bestScore) {
				bestScore = score;
				bestCandidate = aux;
				next = aux;
				elected =s;
			}
		}
		if(elected!=null){
			currentPoint = next;
			return true;
		}
		else{
			return false;
		}
	}
}
