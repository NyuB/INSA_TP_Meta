package jobshop.solvers.greedy;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;
import jobshop.solvers.Mode;

import java.util.ArrayList;



public class GreedySolver implements Solver {
	private Mode mode;

	public GreedySolver(Mode mode) {
		this.mode = mode;
	}

	public boolean priority(Task better, Task worse, Instance instance) {
		if (this.mode == Mode.SPT || this.mode == Mode.EST_SPT) {
			return instance.duration(better) < instance.duration(worse);
		} else if(this.mode == Mode.LPT ||this.mode == Mode.EST_LPT){
			return instance.duration(better) > instance.duration(worse);
		} else if(this.mode == Mode.LRPT || this.mode == Mode.EST_LRPT){
			return instance.durationFromTask(better)>instance.durationFromTask(worse);
		}
		else if(this.mode == Mode.SRPT || this.mode == Mode.EST_SRPT){
			return instance.durationFromTask(better)<instance.durationFromTask(worse);
		}
		else{
			throw new NullPointerException("Please set a valid mode for this solver");
		}
	}

	@Override
	public Result solve(Instance instance, long deadline) {
		ResourceOrder resourceOrder = new ResourceOrder(instance);
		ArrayList<Task> taskQ = new ArrayList<>();//Available tasks sorted by earliest start time possible
		int[] machinesTime = new int[instance.numMachines];
		int[] jobsTime = new int[instance.numJobs];
		for (int j = 0; j < instance.numJobs; j++) {
			taskQ.add(new Task(j, 0));
		}
		while (taskQ.size() > 0) {
			Task next = taskQ.get(0);
			int t_min = Integer.max(jobsTime[next.job], machinesTime[instance.machine(next)]);
			int index = 0;
			for (int i = 1; i < taskQ.size(); i++) {
				Task item = taskQ.get(i);
				int t = Integer.max(jobsTime[item.job], machinesTime[instance.machine(item)]);
				//When not in EST mode, starting time does not impact selection
				if (t == t_min || (this.mode !=Mode.EST_SPT && this.mode!=Mode.EST_LPT && this.mode != Mode.EST_SRPT && this.mode != Mode.EST_LRPT)) {
					if (this.priority(item, next, instance)) {
						next = item;
						index = i;
					}
				} else {
					break;
				}
			}
			taskQ.remove(index);
			resourceOrder.insertTask(next, instance.machine(next));
			if (next.task < instance.numTasks - 1) {
				taskQ.add(new Task(next.job, next.task + 1));
			}
			t_min += instance.duration(next);
			machinesTime[instance.machine(next)] = t_min;
			jobsTime[next.job] = t_min;

			if (this.mode == Mode.EST_SPT ||this.mode == Mode.EST_LPT || this.mode == Mode.EST_SRPT ||this.mode == Mode.EST_LRPT) {//When in EST mode, sort the next tasks by starting time
				taskQ.sort((o1, o2) -> {//Sort the tasklist by earliest possible start time
					int t1 = Integer.max(jobsTime[o1.job], machinesTime[instance.machine(o1)]);
					int t2 = Integer.max(jobsTime[o2.job], machinesTime[instance.machine(o2)]);
					return t1 - t2;
				});
			}
		}
		return new Result(instance, resourceOrder.toSchedule(), Result.ExitCause.Blocked);
	}
}
