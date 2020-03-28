package jobshop.solvers.genetic;

import jobshop.Schedule;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;
import java.util.ArrayList;
import java.util.Random;


public class GeneticSolverResource extends GeneticSolver<ResourceOrder>  {

	public GeneticSolverResource(double proba, int batchsize) {
		super(proba, batchsize,0);
	}
	@Override
	public ResourceOrder cross(ResourceOrder individual, ResourceOrder other) {
		Random random = new Random();
		int split = random.nextInt(instance.numMachines);
		ResourceOrder res = new ResourceOrder(instance);
		for(int j=0;j<instance.numJobs;j++) {
			for (int m = 0; m < split; m++) {
				res.getOrder()[m][j] = individual.getOrder()[m][j];
			}
			for(int m=split;m<instance.numMachines;m++){
				res.getOrder()[m][j] = other.getOrder()[m][j];
			}
		}
		return res;
	}

	@Override
	public int evaluate(ResourceOrder individual) {
		Schedule schedule = individual.toSchedule();
		int val = schedule.makespan();
		return (schedule.isValid()) ? -val : -10 * val;
	}

	@Override
	public ResourceOrder mutate(ResourceOrder individual,double proba) {
		ResourceOrder res = individual.clone();

		Random random = new Random();
		for (int m = 0; m < instance.numMachines; m++) {
			double p = random.nextDouble();
			if(p<=proba){
				int j1 = random.nextInt(instance.numJobs);
				int j2 = random.nextInt(instance.numJobs);
				res.swap(j1, j2, m);
			}
		}
		return res;
	}

	@Override
	public ResourceOrder generate() {
		Random random = new Random();
		ResourceOrder individual = new ResourceOrder(instance);
		ArrayList<Task> todo = new ArrayList<>();
		for(int j=0;j<instance.numJobs;j++){
			todo.add(new Task(j,0));
		}
		for(int i=0;i<instance.numJobs*instance.numTasks;i++){
			int index = random.nextInt(todo.size());
			Task task = todo.remove(index);
			individual.insertTask(task, instance.machine(task));
			if (task.task < instance.numTasks-1) {
				todo.add(new Task(task.job, task.task + 1));
			}
		}
		return individual;
	}

	@Override
	public boolean validate(ResourceOrder individual) {
		return individual.toSchedule().isValid();
	}
}
