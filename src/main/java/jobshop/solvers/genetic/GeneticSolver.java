package jobshop.solvers.genetic;

import jobshop.Encoding;
import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import java.util.List;

public abstract class GeneticSolver<I extends Encoding> extends GeneticAlgorithm<I> implements Solver, Evaluator<I>, Mutator<I>, Populator<I>, Validator<I>, Crosser<I> {
	protected Instance instance;
	protected double mutationRate;
	protected int populationSize;
	protected double seedingProportion;

	public void argsSetup(List<String> args) {
		System.out.println("GEN-PARAMETERS");
		for (String arg : args) {
			System.out.println(arg);
			String[] ops = arg.split("=");
			this.argSetup(ops[0], ops[1]);
		}
	}

	protected void argSetup(String left, String right) {
		switch (left) {
			case "mr":
				this.mutationRate = Double.valueOf(right);
				break;
			case "ps":
				this.populationSize = Integer.valueOf(right);
				break;
			case "sp":
				this.seedingProportion = Double.valueOf(right);
				break;
			default:
				break;

		}
	}

	public GeneticSolver(double mutationRate, int populationSize, double seedingProportion) {
		this.mutationRate = mutationRate;
		this.populationSize = populationSize;
		this.seedingProportion = seedingProportion;
	}

	@Override
	public Result solve(Instance instance, long deadline) {
		this.instance = instance;
		this.mutator = this.getMutator();
		this.crosser = this.getCrosser();
		this.evaluator = this.getEvaluator();
		this.populator = this.getPopulator();
		this.validator = this.getValidator();
		return new Result(instance, this.compute(populationSize, mutationRate, deadline).toSchedule(), Result.ExitCause.Timeout);
	}

	public Evaluator<I> getEvaluator() {
		return (evaluator != null) ? evaluator : this;
	}

	public void setEvaluator(Evaluator<I> evaluator) {
		this.evaluator = evaluator;
	}

	public Mutator<I> getMutator() {
		return (mutator != null) ? mutator : this;
	}

	public void setMutator(Mutator<I> mutator) {
		this.mutator = mutator;
	}

	public Populator<I> getPopulator() {
		return (populator != null) ? populator : this;
	}

	public void setPopulator(Populator<I> populator) {
		this.populator = populator;
	}

	public Validator<I> getValidator() {
		return (validator != null) ? validator : this;
	}

	public void setValidator(Validator<I> validator) {
		this.validator = validator;
	}

	public Crosser<I> getCrosser() {
		return (crosser != null) ? crosser : this;
	}

	public void setCrosser(Crosser<I> crosser) {
		this.crosser = crosser;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public double getSeedingProportion() {
		return seedingProportion;
	}

	public void setSeedingProportion(double seedingProportion) {
		this.seedingProportion = seedingProportion;
	}
}
