package jobshop.solvers.genetic;

import java.util.ArrayList;

public interface Populator<I> {
	I generate();
	default void populate(ArrayList<I> toFill,int n){
		for(int i=0;i<n;i++){
			toFill.add(this.generate());
		}
	}


}
