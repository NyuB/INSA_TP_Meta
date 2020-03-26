package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.Comparator;

public class GreedySolver implements Solver {
	public enum Mode {
		SPT,
		LPT
	}

	private Mode mode;

	public GreedySolver(Mode mode) {
		this.mode = mode;
	}

	@Override
	public Result solve(Instance instance, long deadline) {
		ResourceOrder resourceOrder = new ResourceOrder(instance);
		ArrayList<Integer> jobsLeft = new ArrayList<>();
		for (int j = 0; j < instance.numJobs; jobsLeft.add(j++));
		int[] tasks = new int[instance.numJobs];
		for (int i = 0; i < instance.numJobs * instance.numMachines; i++) {
			int best = (this.mode == Mode.SPT) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
			int next = 0;
			int toSuppr = 0;
			for (int index =0;index<jobsLeft.size();index++){
				int j = jobsLeft.get(index);
				int t_task = instance.duration(j, tasks[j]);
				if ((this.mode == Mode.SPT && t_task <= best) || (this.mode == Mode.LPT && t_task >= best)) {
					best = t_task;
					next = j;
					toSuppr = index;
				}
			}
			int mID = instance.machine(next, tasks[next]);
			resourceOrder.insertTask(new Task(next, tasks[next]), mID);
			tasks[next]++;
			if(tasks[next]==instance.numTasks){
				jobsLeft.remove(toSuppr);
			}
		}
		return new Result(instance, resourceOrder.toSchedule(), Result.ExitCause.Blocked);
	}
}
