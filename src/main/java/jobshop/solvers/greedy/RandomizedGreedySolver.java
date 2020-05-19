package jobshop.solvers.greedy;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;
import jobshop.solvers.Mode;

import java.util.ArrayList;
import java.util.Random;

/**
 * This solver is by default in EST mode. It chooses the next task randomly, each task having a different probability of election based on its duration.
 */
public class RandomizedGreedySolver implements Solver {
	private Mode mode;
	public RandomizedGreedySolver(Mode mode) {
		this.mode = mode;
	}

	private int score(Task candidate,Instance instance){
		return (this.mode==Mode.SPT || this.mode==Mode.LPT)?instance.duration(candidate):instance.durationFromTask(candidate);
	}

	public ResourceOrder randomGreedySolution(Instance instance){
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
			ArrayList<Task> candidates = new ArrayList<>();//All tasks available to start at the same earliest time
			ArrayList<Integer> chances = new ArrayList<>();//The intervall assigned to each available task
			for (int i = 0; i < taskQ.size(); i++) {
				Task item = taskQ.get(i);
				int t = Integer.max(jobsTime[item.job], machinesTime[instance.machine(item)]);
				if (t == t_min) {
					candidates.add(item);
					chances.add(totalHeuristic);
					t_min = t;
					totalHeuristic += score(item,instance);
				} else {
					break;
				}
			}
			if(this.mode==Mode.SPT ||this.mode == Mode.SRPT){
				ArrayList<Integer> transfo = new ArrayList<>();
				int total = 0;
				for(int i=0;i<chances.size();i++){
					transfo.add(total+totalHeuristic-score(candidates.get(i),instance));
					total+=totalHeuristic-score(candidates.get(i),instance);
				}
				chances = transfo;
			}


			Task next = null;
			int index=-1;
			if(totalHeuristic>0) {
				int proba = new Random().nextInt(totalHeuristic);
				for (int i = 0; i < candidates.size() - 1; i++) {
					int p = chances.get(i);//lower bound of the interval attributed to candidate i
					int psuiv = chances.get(i + 1);
					if (p <= proba && proba < psuiv) {//Random value belongs to this candidate's interval
						index = i;
						next = candidates.get(i);
						break;
					}
				}
			}
			if(next==null){//pick the last candidate
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
		return resourceOrder;
	}

	@Override
	public Result solve(Instance instance, long deadline) {
		ResourceOrder result = null;

		while (result == null || System.currentTimeMillis()<deadline) {
			ResourceOrder resourceOrder = randomGreedySolution(instance);
			if(result == null || resourceOrder.toSchedule().makespan()<result.toSchedule().makespan()){
				result = resourceOrder;
			}
		}
		return new Result(instance, result.toSchedule(), Result.ExitCause.Blocked);
	}
}
