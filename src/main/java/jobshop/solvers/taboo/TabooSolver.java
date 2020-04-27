package jobshop.solvers.taboo;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.JobNumbers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TabooSolver implements Solver {
	private int qSize;

	@Override
	public Result solve(Instance instance, long deadline) {
		Random random = new Random();
		JobNumbers current = JobNumbers.generateRandom(instance, random);
		JobsTabooQueue queue = new JobsTabooQueue(150);
		queue.add(current);
		int best = current.toSchedule().makespan();
		JobNumbers selected = current;
		int iter =0;
		while (System.currentTimeMillis() < deadline) {
			iter++;
			List<JobNumbers> neighbours = current.generateAllSwaps();
			JobNumbers next = null;
			int min = Integer.MAX_VALUE;
			for (JobNumbers candidate : neighbours) {
				if (queue.contains(candidate)) {
					int m = candidate.toSchedule().makespan();
					if (m <= min) {
						next = candidate;
						min = m;
						if (min < best) {
							selected = next;
							best = min;
						}
					}
				}
			}
			if (next == null) {
				next = JobNumbers.generateRandom(instance, random);
			}
			queue.add(next);
			current = next;
		}
		System.out.println("Iter # " + iter);
		return new Result(instance, selected.toSchedule(), Result.ExitCause.Timeout);
	}
}
