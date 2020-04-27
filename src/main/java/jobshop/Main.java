package jobshop;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import jobshop.solvers.*;
import jobshop.solvers.genetic.GeneticSolver;
import jobshop.solvers.genetic.GeneticSolverJobs;
import jobshop.solvers.greedy.GreedySolver;
import jobshop.solvers.greedy.Mode;
import jobshop.solvers.greedy.RandomizedGreedySolver;
import jobshop.solvers.taboo.TabooSolver;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class Main {

    /** All solvers available in this program */
    private static HashMap<String, Solver> solvers;
    static {
        solvers = new HashMap<>();
        solvers.put("exhaust", new ExhaustSolver());
        solvers.put("basic", new BasicSolver());
        solvers.put("random", new RandomSolver());
        solvers.put("spt", new GreedySolver(Mode.SPT));
        solvers.put("lpt", new GreedySolver(Mode.LPT));
        solvers.put("rspt", new RandomizedGreedySolver(Mode.SPT));
        solvers.put("rlpt", new RandomizedGreedySolver(Mode.LPT));

        solvers.put("geneticJ", new GeneticSolverJobs(0.5,50,0.33));
        solvers.put("taboo", new TabooSolver());
        // add new solvers here
    }


    public static void main(String[] args) throws IOException {
        ArgumentParser parser = ArgumentParsers.newFor("jsp-solver").build()
                .defaultHelp(true)
                .description("Solves jobshop problems.");

        parser.addArgument("-t", "--timeout")
                .setDefault(1L)
                .type(Long.class)
                .help("Solver timeout in seconds for each instance");
        parser.addArgument("--solver")
                .nargs("+")
                .required(true)
                .help("Solver(s) to use (space separated if more than one)");

        parser.addArgument("--instance")
                .nargs("+")
                .required(true)
                .help("Instance(s) to solve (space separated if more than one)");

        parser.addArgument("--genparams")
                .nargs("+")
                .required(false)
                .help("Parameters for genetic algorithms, format name=value, space separated if more than one");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        BufferedWriter reportWriter = new BufferedWriter(new FileWriter(new File("./report.txt")));
        PrintStream output = System.out;

        long solveTimeMs = ns.getLong("timeout") * 1000;

        List<String> solversToTest = ns.getList("solver");
        for(String solverName : solversToTest) {
            if(!solvers.containsKey(solverName)) {
                System.err.println("ERROR: Solver \"" + solverName + "\" is not avalaible.");
                System.err.println("       Available solvers: " + solvers.keySet().toString());
                System.err.println("       You can provide your own solvers by adding them to the `Main.solvers` HashMap.");
                System.exit(1);
            }
        }
        List<String> instances = ns.<String>getList("instance");
        for(String instanceName : instances) {
            if(!BestKnownResult.isKnown(instanceName)) {
                System.err.println("ERROR: instance \"" + instanceName + "\" is not avalaible.");
                System.err.println("       available instances: " + Arrays.toString(BestKnownResult.instances));
                System.exit(1);
            }
        }
        List<String> genParameters = ns.getList("genparams");
        ((GeneticSolver) (solvers.get("geneticJ"))).argsSetup(genParameters);

        float[] runtimes = new float[solversToTest.size()];
        float[] distances = new float[solversToTest.size()];

        try {
            output.print(  "                         ");;
            for(String s : solversToTest)
                output.printf("%-30s", s);
            output.println();
            output.print("instance size  best      ");
            for(String s : solversToTest) {
                output.print("runtime makespan ecart        ");
            }
            output.println();


        for(String instanceName : instances) {
            int bestKnown = BestKnownResult.of(instanceName);


            Path path = Paths.get("instances/", instanceName);
            Instance instance = Instance.fromFile(path);

            output.printf("%-8s %-5s %4d      ",instanceName, instance.numJobs +"x"+instance.numTasks, bestKnown);

            for(int solverId = 0 ; solverId < solversToTest.size() ; solverId++) {
                String solverName = solversToTest.get(solverId);
                Solver solver = solvers.get(solverName);
                long start = System.currentTimeMillis();
                long deadline = System.currentTimeMillis() + solveTimeMs;
                Result result = solver.solve(instance, deadline);
                long runtime = System.currentTimeMillis() - start;

                if(!result.schedule.isValid()) {
                    System.err.println("ERROR: solver returned an invalid schedule");
                    System.exit(1);
                }

                assert result.schedule.isValid();
                int makespan = result.schedule.makespan();
                float dist = 100f * (makespan - bestKnown) / (float) bestKnown;
                runtimes[solverId] += (float) runtime / (float) instances.size();
                distances[solverId] += dist / (float) instances.size();

                output.printf("%7d %8s %5.1f        ", runtime, makespan, dist);
                output.flush();
                reportWriter.write(instanceName+" "+solverName+" "+makespan+"\n"+result.schedule.toString()+"\n--------------------------\n");
            }
            output.println();

        }


        output.printf("%-8s %-5s %4s      ", "AVG", "-", "-");
        for(int solverId = 0 ; solverId < solversToTest.size() ; solverId++) {
            output.printf("%7.1f %8s %5.1f        ", runtimes[solverId], "-", distances[solverId]);
        }
        reportWriter.flush();



        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
