package jobshop.solvers.genetic;

import jobshop.Instance;
import jobshop.Result;
import jobshop.encodings.JobNumbers;
import jobshop.solvers.descent.TabooSolver;
import jobshop.solvers.greedy.GreedySolver;
import jobshop.solvers.Mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneticSolverJobs extends GeneticSolver<JobNumbers> {
	private Random random = new Random();
	//For seeding
	private ArrayList<JobNumbers> seeds;

	public GeneticSolverJobs(double proba, int batchsize, double seedingProportion) {
		super(proba, batchsize, seedingProportion);

	}

	@Override
	public Result solve(Instance instance, long deadline) {
		seeds = new ArrayList<>();
		seeds.add(JobNumbers.fromSchedule(new GreedySolver(Mode.EST_SRPT).solve(instance, 0).schedule));
		seeds.add(JobNumbers.fromSchedule(new GreedySolver(Mode.EST_LRPT).solve(instance, 0).schedule));
		long available = (deadline-System.currentTimeMillis())/100;
		long start = System.currentTimeMillis();
		for(int i = 1;i<=3;i++){
			seeds.add(JobNumbers.fromSchedule(new TabooSolver(true,30).solve(instance, start+i*5*available).schedule));
		}
		for(int i = 4;i<=6;i++){
			seeds.add(JobNumbers.fromSchedule(new TabooSolver(true,30).solve(instance, start+i*5*available).schedule));
		}
		return super.solve(instance, deadline);
	}

	@Override
	public void setMutationRate(double mutationRate) {
		super.setMutationRate(mutationRate);
	}

	@Override
	public JobNumbers cross(JobNumbers individual, JobNumbers other) {
		int split = random.nextInt(instance.totalOps());
		JobNumbers res = individual.clone();
		int[] available = new int[instance.numJobs];
		Arrays.fill(available, instance.numTasks);
		for (int i = 0; i < split; i++) {
			available[res.jobs[i]]--;
		}
		for (int i = split; i < instance.totalOps(); i++) {
			int jobID = other.jobs[i];
			while (available[jobID] == 0) {
				jobID++;
				jobID %= instance.numJobs;
			}
			res.jobs[i] = jobID;
			available[jobID]--;
		}
		return res;
	}

	@Override
	public int evaluate(JobNumbers individual) {
		return -individual.toSchedule().makespan();
	}

	@Override
	public JobNumbers mutate(JobNumbers individual) {
		if(random.nextDouble()<=this.mutationRate) {
			JobNumbers res = individual.clone();
			int j1 = random.nextInt(instance.numJobs * instance.numTasks);
			int j2 = random.nextInt(instance.numJobs * instance.numTasks);
			res.swap(j1, j2);
			return res;
		}
		return individual;
	}

	@Override
	public JobNumbers generate() {
		if (random.nextDouble() <= seedingProportion) {//Seeding
			return seeds.get(random.nextInt(seeds.size()));
		} else {//Generating randomly
			return JobNumbers.generateRandom(instance, random);
		}
	}

	@Override
	public void select(Evaluator<JobNumbers> evaluator, Mutator<JobNumbers> mutator, Crosser<JobNumbers> crosser, List<JobNumbers> population, List<JobNumbers> toFill, int nbToSelect) {
		new AmongBests<JobNumbers>(this.random, 1, 4).select(evaluator,mutator,crosser,population,toFill,3*nbToSelect/4);
		new BinaryTournament<JobNumbers>(this.random).select(evaluator,mutator,crosser,population,toFill,nbToSelect/4);

	}

	@Override
	public boolean validate(JobNumbers individual) {
		return individual.toSchedule().isValid();
	}
}
