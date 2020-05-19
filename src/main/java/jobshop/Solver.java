package jobshop;

import java.io.*;

public interface Solver {


    Result solve(Instance instance, long deadline);

}
