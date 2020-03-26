package jobshop.encodings;

import jobshop.Encoding;
import jobshop.Instance;
import jobshop.Schedule;

public class RessourceOrder extends Encoding {

	public RessourceOrder(Instance instance) {
		super(instance);
	}

	@Override
	public Schedule toSchedule() {
		//TODO
		return null;
	}
}
