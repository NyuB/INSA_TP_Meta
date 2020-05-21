package jobshop.solvers.descent;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;
import jobshop.solvers.greedy.GreedySolver;
import jobshop.solvers.Mode;
import jobshop.solvers.greedy.RandomizedGreedySolver;

import java.util.ArrayList;
import java.util.List;

public abstract class DescentSolver implements Solver {
	protected ResourceOrder currentPoint;
	protected ResourceOrder bestCandidate;
	protected int bestScore;
	private boolean loopWithRandomSeed;


	public DescentSolver(boolean loopWithRandomSeed) {
		this.loopWithRandomSeed = loopWithRandomSeed;
	}

	/**
	 * A block represents a subsequence of the critical path such that all tasks in it execute on the same machine.
	 * This class identifies a block in a ResourceOrder representation.
	 * <p>
	 * Consider the solution in ResourceOrder representation
	 * machine 0 : (0,1) (1,2) (2,2)
	 * machine 1 : (0,2) (2,1) (1,1)
	 * machine 2 : ...
	 * <p>
	 * The block with : machine = 1, firstTask= 0 and lastTask = 1
	 * Represent the task sequence : [(0,2) (2,1)]
	 */
	static class Block {
		/**
		 * machine on which the block is identified
		 */
		final int machine;
		/**
		 * index of the first task of the block
		 */
		final int firstTask;
		/**
		 * index of the last task of the block
		 */
		final int lastTask;

		Block(int machine, int firstTask, int lastTask) {
			this.machine = machine;
			this.firstTask = firstTask;
			this.lastTask = lastTask;
		}
	}

	/**
	 * Represents a swap of two tasks on the same machine in a ResourceOrder encoding.
	 * <p>
	 * Consider the solution in ResourceOrder representation
	 * machine 0 : (0,1) (1,2) (2,2)
	 * machine 1 : (0,2) (2,1) (1,1)
	 * machine 2 : ...
	 * <p>
	 * The swap with : machine = 1, t1= 0 and t2 = 1
	 * Represents inversion of the two tasks : (0,2) and (2,1)
	 * Applying this swap on the above resource order should result in the following one :
	 * machine 0 : (0,1) (1,2) (2,2)
	 * machine 1 : (2,1) (0,2) (1,1)
	 * machine 2 : ...
	 */
	static class Swap {
		// machine on which to perform the swap
		final int machine;
		// index of one task to be swapped
		final int t1;
		// index of the other task to be swapped
		final int t2;

		Swap(int machine, int t1, int t2) {
			this.machine = machine;
			this.t1 = t1;
			this.t2 = t2;
		}

		/**
		 * Apply this swap on the given resource order, transforming it into a new solution.
		 */
		public void applyOn(ResourceOrder order) {
			order.swap(t1, t2, machine);
		}
	}

	/**
	 * @return the neighbourhood of the current point
	 */
	protected List<Swap> scan() {
		List<Block> blocks = blocksOfCriticalPath(currentPoint);
		List<Swap> neighborhood = new ArrayList<>();
		for (Block b : blocks) {
			neighborhood.addAll(neighbors(b));
		}
		return neighborhood;
	}

	protected abstract boolean update(List<Swap> neighborhood) ;

	@Override
	public Result solve(Instance instance, long deadline) {
		Result result = new GreedySolver(Mode.EST_LRPT).solve(instance, deadline);
		currentPoint = ResourceOrder.fromSchedule(result.schedule);
		Schedule bestResult  = result.schedule;
		boolean restart = true;
		while(restart) {
			bestCandidate = currentPoint;
			bestScore = result.schedule.makespan();
			boolean iterAgain = true;
			while (iterAgain && System.currentTimeMillis() < deadline) {
				iterAgain = this.update(this.scan());
			}
			Schedule candidate = bestCandidate.toSchedule();
			if(bestCandidate.toSchedule().makespan()<bestResult.makespan()){
				bestResult = candidate;
			}
			if(loopWithRandomSeed && System.currentTimeMillis()<deadline){
				currentPoint = new RandomizedGreedySolver(Mode.LRPT).randomGreedySolution(instance);
			}
			else {
				restart = false;
			}
		}
		return new Result(instance, bestResult, (deadline > System.currentTimeMillis()) ? Result.ExitCause.Blocked : Result.ExitCause.Timeout);
	}

	/**
	 * Returns a list of all blocks of the critical path.
	 */
	List<Block> blocksOfCriticalPath(ResourceOrder order) {
		List<Task> path = order.toSchedule().criticalPath();
		List<Block> res = new ArrayList<>();
		int currentMachine = -1;
		int start = -1;
		int end = -1;
		for (int i = 0; i < path.size(); i++) {
			Task task = path.get(i);
			int machine = order.instance.machine(task);
			if (currentMachine == -1) {
				currentMachine = machine;
				start = i;
				end = i;
			} else if (currentMachine == machine) {
				end = i;
			} else {
				if (end - start > 0) {
					int startIndex = order.indexOn(path.get(start), currentMachine);
					int endIndex = order.indexOn(path.get(end), currentMachine);
					assert startIndex >= 0;
					assert endIndex >= 0;
					res.add(new Block(currentMachine, startIndex, endIndex));
				}
				start = i;
				end = i;
				currentMachine = machine;
			}
		}
		if (end - start > 0) {
			int startIndex = order.indexOn(path.get(start), currentMachine);
			int endIndex = order.indexOn(path.get(end), currentMachine);
			assert startIndex >= 0;
			assert endIndex >= 0;
			res.add(new Block(currentMachine, startIndex, endIndex));
		}
		return res;
	}

	/**
	 * For a given block, return the possible swaps for the Nowicki and Smutnicki neighborhood
	 */
	List<Swap> neighbors(Block block) {
		List<Swap> res = new ArrayList<>();
		res.add(new Swap(block.machine, block.firstTask, block.firstTask + 1));
		if (block.lastTask != block.firstTask + 1) res.add(new Swap(block.machine, block.lastTask - 1, block.lastTask));
		return res;
	}

}