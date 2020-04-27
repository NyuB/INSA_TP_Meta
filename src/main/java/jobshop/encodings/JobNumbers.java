package jobshop.encodings;

import jobshop.Encoding;
import jobshop.Instance;
import jobshop.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Représentation par numéro de job.
 */
public class JobNumbers extends Encoding {

	public static JobNumbers fromSchedule(Schedule schedule) {
		JobNumbers res = new JobNumbers(schedule.pb);
		ArrayList<Task> taskQ = new ArrayList<>();
		for (int j = 0; j < res.instance.numJobs; j++) {
			taskQ.add(new Task(j, 0));
		}
		for (int i = 0; i < res.instance.numMachines * res.instance.numJobs; i++) {
			Task next = taskQ.get(0);
			int t_min = schedule.startTime(next);
			for (Task item : taskQ) {
				if (schedule.startTime(item) < t_min) {
					t_min = schedule.startTime(item);
					next = item;
				}
			}
			taskQ.remove(next);
			res.jobs[res.nextToSet++] = next.job;
			if (next.task < res.instance.numTasks - 1) {
				taskQ.add(new Task(next.job, next.task + 1));
			}
		}
		return res;
	}

	public static boolean same(JobNumbers a, JobNumbers b){
		for(int i=0;i<a.jobs.length;i++){
			if(a.jobs[i]!=b.jobs[i])return false;
		}
		return true;
	}

	/**
	 * A numJobs * numTasks array containing the representation by job numbers.
	 */
	public final int[] jobs;

	/**
	 * In case the encoding is only partially filled, indicates the index of first
	 * element of `jobs` that has not been set yet.
	 */
	public int nextToSet = 0;

	public JobNumbers(Instance instance) {
		super(instance);
		jobs = new int[instance.numJobs * instance.numMachines];
		Arrays.fill(jobs, -1);
	}

	public void swap(int a,int b){
		int aux= this.jobs[a];
		this.jobs[a] = this.jobs[b];
		this.jobs[b] = aux;
	}

	@Override
	public Schedule toSchedule() {
		// time at which each machine is going to be freed
		int[] nextFreeTimeResource = new int[instance.numMachines];

		// for each job, the first task that has not yet been scheduled
		int[] nextTask = new int[instance.numJobs];

		// for each task, its start time
		int[][] startTimes = new int[instance.numJobs][instance.numTasks];

		// compute the earliest start time for every task of every job
		for (int job : jobs) {
			int task = nextTask[job];
			int machine = instance.machine(job, task);
			// earliest start time for this task
			int est = task == 0 ? 0 : startTimes[job][task - 1] + instance.duration(job, task - 1);
			est = Math.max(est, nextFreeTimeResource[machine]);

			startTimes[job][task] = est;
			nextFreeTimeResource[machine] = est + instance.duration(job, task);
			nextTask[job] = task + 1;
		}

		return new Schedule(instance, startTimes);
	}



	@Override
	public String toString() {
		return Arrays.toString(Arrays.copyOfRange(jobs, 0, nextToSet));
	}


	@Override
	public JobNumbers clone()  {
		JobNumbers res = new JobNumbers(this.instance);
		for(int i=0;i<this.nextToSet;i++){
			res.jobs[i]=this.jobs[i];
		}
		res.nextToSet = this.nextToSet;
		return res;
	}

	public JobNumbers clone(int jobInc){
		JobNumbers res = this.clone();
		res.jobs[res.nextToSet++] = jobInc;
		return res;
	}

	public static JobNumbers generateRandom(Instance instance,Random random){
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

	public String toIdentifier(){
		StringBuilder sb = new StringBuilder();
		for(int j:this.jobs){
			sb.append(j);
		}
		return sb.toString();
	}

	/**
	 * For "explorer type" solvers(gradient,taboo...)
	 * @return A list corresponding to all possible exchange between two jobs in the representation
	 */
	public List<JobNumbers> generateAllSwaps(){
		ArrayList<JobNumbers> res = new ArrayList<>();
		for(int a=0;a<this.instance.totalOps()-1;a++){
			for(int b=a+1;b<instance.totalOps();b++){
				if(this.jobs[a]!=this.jobs[b]) {
					JobNumbers clone = this.clone();
					clone.jobs[a] = this.jobs[b];
					clone.jobs[b] = this.jobs[a];
					res.add(clone);
				}
			}
		}
		return res;
	}


	//TODO to optimize exhaustive search
	public int currentMakespan(){
		int res=0;
		int[] tasks = new int[instance.numJobs];
		for(int i=0;i<nextToSet;i++){
			res+=instance.duration(jobs[i],tasks[jobs[i]]);
			tasks[jobs[i]]++;
		}
		return res;
	}


}
