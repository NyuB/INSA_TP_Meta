package jobshop;

import jobshop.encodings.JobNumbers;
import jobshop.encodings.ResourceOrder;

import java.io.IOException;
import java.nio.file.Paths;

public class CombiCalc {
	public static void main(String[] args) throws IOException {
		Instance ft06 = Instance.fromFile(Paths.get("instances/ft06"));
		int[][] padding = new int[ft06.numJobs][ft06.numTasks];
		Schedule schedule = new Schedule(ft06,padding);
		ResourceOrder resourceOrder = new ResourceOrder(ft06);
		JobNumbers jobNumbers = new JobNumbers(ft06);
		long dMax = 0;

		for(int j = 0;j<schedule.times.length;j++){
			for(int t=0;t<schedule.times[j].length;t++){
				dMax+=schedule.pb.duration(j,t);
			}
		}
		System.out.println("SEARCH SPACE SIZE");
		System.out.println("BY JOB NUMBERS :    "+jobNumbers.researchSpaceEval());
		System.out.println("BY RESOURCE ORDER : "+resourceOrder.researchSpaceEval());
		System.out.println("schedule = " + dMax);
		System.out.println("BY SCHEDULE :       "+schedule.researchSpaceEval());

	}
}
