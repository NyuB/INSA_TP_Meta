package jobshop.solvers.genetic;

import jobshop.Encoding;
import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;

public abstract class GeneticSolver<I extends Encoding> extends GeneticAlgorithm<I> implements Solver ,Evaluator<I>, Mutator<I>, Populator<I> ,Validator<I>,Crosser<I> {
	protected Instance instance;
	private double proba;
	private int batchsize;

	public GeneticSolver(double proba, int batchsize) {
		this.proba = proba;
		this.batchsize = batchsize;
	}

	@Override
	public Result solve(Instance instance, long deadline) {
		this.instance = instance;
		this.mutator = this;
		this.crosser=this;
		this.evaluator=this;
		this.populator=this;
		this.validator = this;
		return new Result(instance,this.compute(batchsize, 0,proba, deadline).toSchedule(), Result.ExitCause.Timeout);
	}
}
