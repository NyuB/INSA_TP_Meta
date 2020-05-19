package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.JobNumbers;

import java.util.ArrayList;
import java.util.Arrays;

public class ExhaustSolver implements Solver {
	public Schedule deepDive(JobNumbers jobNumbers,int[] tasks){
		if (jobNumbers.nextToSet == jobNumbers.instance.totalOps()) {
			return jobNumbers.toSchedule();
		}
		else{
			Schedule res=null;
			int best = Integer.MAX_VALUE;
			for(int i=0;i<jobNumbers.instance.numJobs;i++){
				if(tasks[i]>0){
					tasks[i]--;
					jobNumbers.jobs[jobNumbers.nextToSet++]=i;
					Schedule schedule = deepDive(jobNumbers, tasks);
					int m = schedule.makespan();
					if (m < best) {
						best = m;
						res= schedule;
					}
					tasks[i]++;
					jobNumbers.nextToSet--;
					jobNumbers.jobs[jobNumbers.nextToSet]=-1;
				}
			}
			return res;
		}
	}

	@Override
	public Result solve(Instance instance, long deadline) {
		 int tasks[] =new int[instance.numJobs];
		 Arrays.fill(tasks,instance.numTasks);
		 return new Result(instance, deepDive(new JobNumbers(instance),tasks), Result.ExitCause.Blocked);
	}
}
