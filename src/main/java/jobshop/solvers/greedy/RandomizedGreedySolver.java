package jobshop.solvers.greedy;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.Random;

public class RandomizedGreedySolver implements Solver {
	private Mode mode;
	public RandomizedGreedySolver(Mode mode) {
		this.mode = mode;
	}
	@Override
	public Result solve(Instance instance, long deadline) {
		ResourceOrder result = null;

		while (result == null || System.currentTimeMillis()<deadline) {
			ResourceOrder resourceOrder = new ResourceOrder(instance);
			ArrayList<Task> taskQ = new ArrayList<>();//Available tasks sorted by earliest start time possible
			int[] machinesTime = new int[instance.numMachines];
			int[] jobsTime = new int[instance.numJobs];
			for (int j = 0; j < instance.numJobs; j++) {
				taskQ.add(new Task(j, 0));
			}
			while (taskQ.size() > 0) {
				int totalHeuristic = 0;
				int t_min = Integer.max(jobsTime[taskQ.get(0).job], machinesTime[instance.machine(taskQ.get(0))]);
				ArrayList<Task> candidates = new ArrayList<>();
				ArrayList<Integer> chances = new ArrayList<>();
				for (int i = 0; i < taskQ.size(); i++) {
					Task item = taskQ.get(i);
					int t = Integer.max(jobsTime[item.job], machinesTime[instance.machine(item)]);
					if (t == t_min) {
						candidates.add(item);
						chances.add(totalHeuristic);
						t_min = t;
						totalHeuristic += instance.duration(item);
					} else {
						break;
					}
				}
				if(this.mode==Mode.SPT){
					ArrayList<Integer> transfo = new ArrayList<>();
					int total = 0;
					for(int i=0;i<chances.size();i++){
						transfo.add(total+totalHeuristic-instance.duration(candidates.get(i)));
						total+=totalHeuristic-instance.duration(candidates.get(i));
					}
					chances = transfo;
				}


				Task next = null;
				int index=-1;
				int proba = new Random().nextInt(totalHeuristic);
				for(int i=0;i<candidates.size()-1;i++){
					int p = chances.get(i);
					int psuiv = chances.get(i+1);
					if(p<= proba && proba < psuiv){
						index=i;
						next = candidates.get(i);
						break;
					}
				}
				if(next==null){
					index = candidates.size()-1;
					next = candidates.get(index);
				}


				taskQ.remove(index);
				resourceOrder.insertTask(next, instance.machine(next));
				if (next.task < instance.numTasks - 1) {
					taskQ.add(new Task(next.job, next.task + 1));
				}
				t_min += instance.duration(next);
				machinesTime[instance.machine(next)] = t_min;
				jobsTime[next.job] = t_min;
				taskQ.sort((o1, o2) -> {//Sort the tasklist by earliest possible start time
					int t1 = Integer.max(jobsTime[o1.job], machinesTime[instance.machine(o1)]);
					int t2 = Integer.max(jobsTime[o2.job], machinesTime[instance.machine(o2)]);
					return t1 - t2;
				});
			}
			if(result == null || resourceOrder.toSchedule().makespan()<result.toSchedule().makespan()){
				result = resourceOrder;
			}
		}
		return new Result(instance, result.toSchedule(), Result.ExitCause.Blocked);
	}
}
