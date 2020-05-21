package jobshop.solvers.descent;

import jobshop.Instance;
import jobshop.Result;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.List;

public class TabooSolver extends DescentSolver {
	private int[][][][] tabooMat;

	private int nbIterations;
	private int tabooSize;

	public TabooSolver(boolean loopWithRandomSeed,int tabooSize) {
		super(loopWithRandomSeed);
		this.tabooSize = tabooSize;
	}

	private int taboo(int job, int task,int j,int t) {
		return this.tabooMat[job][task][j][t];
	}
	private int taboo(Swap s) {
		Task a = this.currentPoint.getOrder()[s.machine][s.t1];
		Task b = this.currentPoint.getOrder()[s.machine][s.t2];
		return this.taboo(a.job,a.task, b.job,b.task);
	}

	private void tabboInc(Swap s){
		Task a = this.currentPoint.getOrder()[s.machine][s.t1];
		Task b = this.currentPoint.getOrder()[s.machine][s.t2];
		this.tabooMat[a.job][a.task][b.job][b.task] = this.nbIterations+this.tabooSize;
		this.tabooMat[b.job][b.task][a.job][a.task] = this.nbIterations+this.tabooSize;
	}

	private void init(Instance instance) {
		this.tabooMat = new int[instance.numJobs][instance.numTasks][instance.numJobs][instance.numTasks];
		for(int job=0;job<instance.numJobs;job++){
			for(int task = 0;task<instance.numTasks;task++){
				for(int j = 0;j<instance.numJobs;j++) {
					for (int t = 0; t < instance.numTasks; t++) {
						tabooMat[job][task][j][t] = 0;
					}
				}
			}
		}
		this.nbIterations = 0;
	}

	@Override
	protected boolean update(List<Swap> neighborhood) {
		int min = Integer.MAX_VALUE;
		ResourceOrder next = currentPoint;
		this.nbIterations++;
		Swap elected = null;

		for(Swap s : neighborhood){
			ResourceOrder aux = currentPoint.copy();
			s.applyOn(aux);
			int score = aux.toSchedule().makespan();
			if(score<bestScore || nbIterations>taboo(s)) {//the taboo method returns the minimal iteration number required to choose this swap again
				if (score < min) {
					next = aux;
					min = score;
					elected = s;
					if (score < bestScore) {
						bestScore = score;
						bestCandidate = aux;
					}
				}
			}
		}
		if(elected!=null){
			tabboInc(elected);//the tabooInc method reset the minimal iteration number required to choose this swap again
			currentPoint = next;
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public Result solve(Instance instance, long deadline) {
		this.init(instance);
		return super.solve(instance, deadline);
	}
}
