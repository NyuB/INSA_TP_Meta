package jobshop.solvers.genetic;
import jobshop.Instance;
import jobshop.Result;
import jobshop.encodings.JobNumbers;
import jobshop.encodings.Task;
import jobshop.solvers.GreedySolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GeneticSolverJobs extends GeneticSolver<JobNumbers>{
	private Random random = new Random();
	//For seeding
	private JobNumbers spt;
	private JobNumbers lpt;

	public GeneticSolverJobs(double proba, int batchsize,double seedingProportion) {
		super(proba, batchsize,seedingProportion);

	}

	@Override
	public Result solve(Instance instance, long deadline) {
		spt = JobNumbers.fromSchedule(new GreedySolver(GreedySolver.Mode.SPT).solve(instance, 0).schedule);
		lpt = JobNumbers.fromSchedule(new GreedySolver(GreedySolver.Mode.LPT).solve(instance, 0).schedule);
		return super.solve(instance, deadline);
	}

	@Override
	public JobNumbers cross(JobNumbers individual, JobNumbers other) {
		int split = random.nextInt(instance.totalOps());
		JobNumbers res = individual.clone();
		int[] available = new int[instance.numJobs];
		Arrays.fill(available, instance.numTasks);
		for(int i=0;i<split;i++){
			available[res.jobs[i]]--;
		}
		for(int i=split;i<instance.totalOps();i++){
			int jobID = other.jobs[i];
			while(available[jobID]==0){
				jobID++;
				jobID%=instance.numJobs;
			}
			res.jobs[i]=jobID;
			available[jobID]--;
		}
		return res;
	}

	@Override
	public int evaluate(JobNumbers individual) {
		return -individual.toSchedule().makespan();
	}

	@Override
	public JobNumbers mutate(JobNumbers individual, double proba) {
		JobNumbers res = individual.clone();
		if (random.nextDouble() <= proba) {
			int j1 = random.nextInt(instance.numJobs * instance.numTasks);
			int j2 = random.nextInt(instance.numJobs * instance.numTasks);
			res.swap(j1, j2);
		}
		return res;
	}

	@Override
	public JobNumbers generate() {

		if(random.nextDouble()<=seedingProportion){//Seeding
			return (random.nextDouble()<=0.5)?spt:lpt;
		}
		else {//Generating randomly
			ArrayList<Task> todo = new ArrayList<>();
			JobNumbers individual = new JobNumbers(instance);
			for (int j = 0; j < instance.numJobs; j++) {
				todo.add(new Task(j, 0));
			}
			while (todo.size() > 0) {
				int index = random.nextInt(todo.size());
				Task task = todo.remove(index);
				individual.jobs[individual.nextToSet++] = task.job;
				if (task.task < instance.numTasks - 1) {
					todo.add(new Task(task.job, task.task + 1));
				}

			}
			return individual;
		}
	}

	@Override
	public boolean validate(JobNumbers individual) {
		return individual.toSchedule().isValid();
	}
}
