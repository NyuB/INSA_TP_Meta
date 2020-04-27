package jobshop.solvers.taboo;

import jobshop.encodings.JobNumbers;

import java.util.ArrayList;
import java.util.HashSet;

public class JobsTabooQueue {
	private ArrayList<JobNumbers> queue = new ArrayList<>();
	private HashSet<String> set = new HashSet<>();
	private int sizeLimit;
	public JobsTabooQueue(int sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public void add(JobNumbers jobNumbers) {
		this.set.add(jobNumbers.toIdentifier());
		this.queue.add(jobNumbers);
		if (this.queue.size() > sizeLimit) {
			this.set.remove(this.queue.remove(0).toIdentifier());
		}
	}

	public boolean contains(JobNumbers jobNumbers){
		return this.set.contains(jobNumbers.toIdentifier());
	}
}
