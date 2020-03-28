package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.JobNumbers;

import java.util.ArrayList;
import java.util.Arrays;

public class ExhaustSolver implements Solver {
	private class SearchItem {
		JobNumbers jobNumbers;
		int[] tasks;

		public SearchItem(JobNumbers jobNumbers, int[] tasks) {
			this.jobNumbers = jobNumbers;
			this.tasks = tasks;
		}
	}

	@Override
	public Result solve(Instance instance, long deadline) {
		ArrayList<SearchItem> stack = new ArrayList<>();
		int[] tasks = new int[instance.numJobs];
		Arrays.fill(tasks, instance.numTasks);
		SearchItem startItem = new SearchItem(new JobNumbers(instance), tasks);
		Schedule res = null;
		int best = Integer.MAX_VALUE;
		stack.add(startItem);
		while (stack.size() > 0) {
			SearchItem item = stack.remove(0);
			for (int j = 0; j < instance.numJobs; j++) {
				if (item.tasks[j] > 0) {
					JobNumbers inc = item.jobNumbers.clone(j);
					if (inc.nextToSet == inc.jobs.length) {
						Schedule s = inc.toSchedule();
						int m = s.makespan();
						if (m <= best) {
							best = m;
							res = s;
						}
					} else {
						int[] aux = Arrays.copyOf(item.tasks, item.tasks.length);
						aux[j]--;
						stack.add(0, new SearchItem(inc, aux));
					}
				}
			}
		}
		return new Result(instance, res, Result.ExitCause.Blocked);
	}
}
