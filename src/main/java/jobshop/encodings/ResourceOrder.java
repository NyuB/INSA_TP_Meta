package jobshop.encodings;

import jobshop.Encoding;
import jobshop.Instance;
import jobshop.Schedule;

import java.util.ArrayList;

public class ResourceOrder extends Encoding {

	public static ResourceOrder fromSchedule(Schedule schedule){
		ResourceOrder res = new ResourceOrder(schedule.pb);
		ArrayList<Task> taskQ = new ArrayList<>();
		for(int j=0;j<res.instance.numJobs;j++){
			taskQ.add(new Task(j, 0));
		}
		for (int i = 0; i < res.instance.numMachines * res.instance.numJobs;i++) {
			Task next = taskQ.get(0);
			int t_min = schedule.startTime(next);
			for(Task item : taskQ){
				if(schedule.startTime(item)<t_min){
					t_min = schedule.startTime(item);
					next = item;
				}
			}
			taskQ.remove(next);
			int mID = res.instance.machine(next);
			res.insertTask(next, mID);
			if(next.task<res.instance.numTasks-1){
				taskQ.add(new Task(next.job,next.task+1));
			}
		}
		return res;
	}

	private Task[][] order;

	public ResourceOrder(Instance instance) {
		super(instance);
		this.order = new Task[instance.numMachines][instance.numJobs];
	}

	public void insertTask(Task task,int machine){
		int t = 0;
		while(t<this.instance.numJobs && this.order[machine][t] != null){
			t++;
		}
		this.order[machine][t] = task;
	}
	public void swap(int j1,int j2,int m){
		Task aux = this.order[m][j1];
		this.order[m][j1]=this.order[m][j2];
		this.order[m][j2]=aux;
	}
	@Override
	public Schedule toSchedule() {
		int[] machinesTime = new int[instance.numMachines];
		int[] machinesNext = new int[instance.numMachines];
		int[] jobsTime = new int[instance.numJobs];
		int[] jobsNext = new int[instance.numJobs];
		int[][] startTimes = new int[instance.numJobs][instance.numTasks];

		for (int i = 0; i < instance.numTasks * instance.numJobs; i++) {
			boolean done = false;
			int mID = 0;
			while (!done && mID < instance.numMachines) {
				int nextTask = machinesNext[mID];
				if (nextTask < this.instance.numJobs) {
					Task task = this.order[mID][nextTask];
					if (jobsNext[task.job] == task.task) {
						int time = Integer.max(machinesTime[mID], jobsTime[task.job]);
						startTimes[task.job][task.task] = time;
						time += instance.duration(task);
						machinesTime[mID] = time;
						jobsTime[task.job] = time;
						machinesNext[mID]++;
						jobsNext[task.job]++;
						done = true;
					}
				}
				mID++;
			}
		}
		return new Schedule(instance, startTimes);
	}

	public Task[][] getOrder() {
		return order;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\n");
		sb.append("| |");
		for (int i = 0; i < instance.numJobs; i++) {
			sb.append("  ");
			sb.append(i);
			sb.append("  |");
		}
		sb.append("\n");
		for (int j = 0; j < instance.numMachines; j++) {
			sb.append("|");
			sb.append(j);
			sb.append("|");
			for (int i = 0; i < instance.numJobs; i++) {
				Task task = order[j][i];
				sb.append("(");
				sb.append(task.job);
				sb.append(",");
				sb.append(task.task);
				sb.append(")|");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public ResourceOrder clone() {
		ResourceOrder res = new ResourceOrder(this.instance);
		for(int m=0;m<this.instance.numMachines;m++){
			for(int j=0;j<this.instance.numJobs;j++){
				res.order[m][j]=this.order[m][j];
			}
		}
		return res;
	}
}
