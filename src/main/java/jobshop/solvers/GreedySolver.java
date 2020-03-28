package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.Collections;
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

	public boolean priority(Task better,Task worse,Instance instance){
		if(this.mode==Mode.SPT){
			return instance.duration(better) < instance.duration(worse);
		}
		else {
			return instance.duration(better) > instance.duration(worse);
		}
	}
	@Override
	public Result solve(Instance instance, long deadline) {
		ResourceOrder resourceOrder = new ResourceOrder(instance);
		ArrayList<Task> taskQ = new ArrayList<>();
		int[] machinesTime = new int[instance.numMachines];
		int[] jobsTime = new int[instance.numJobs];
		for(int j=0;j<instance.numJobs;j++){
			taskQ.add(new Task(j, 0));
		}
		while(taskQ.size()>0){
			Task next = null;
			int t_min = Integer.MAX_VALUE;
			int index=-1;
			for (int i =0;i<taskQ.size();i++) {
				Task item = taskQ.get(i);
				int t =Integer.max(jobsTime[item.job],machinesTime[instance.machine(item)]);
				if(t < t_min){
					next = item;
					t_min = t;
					index=i;
				}
				else if(t==t_min){
					if(this.priority(item,next,instance)){
						next=item;
						index=i;
					}
				}
				else{
					break;
				}
			}
			taskQ.remove(index);
			resourceOrder.insertTask(next, instance.machine(next));
			if(next.task<instance.numTasks-1){
				taskQ.add(new Task(next.job,next.task+1));
			}
			t_min += instance.duration(next);
			machinesTime[instance.machine(next)] = t_min;
			jobsTime[next.job] = t_min;
			Collections.sort(taskQ, new Comparator<Task>() {
				@Override
				public int compare(Task o1, Task o2) {
					int t1 = Integer.max(jobsTime[o1.job], machinesTime[instance.machine(o1)]);
					int t2 = Integer.max(jobsTime[o2.job], machinesTime[instance.machine(o2)]);
					return t1-t2;
				}
			});
		}
		return new Result(instance, resourceOrder.toSchedule(), Result.ExitCause.Blocked);
	}
}
