package jobshop.encodings;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.solvers.BasicSolver;
import jobshop.solvers.RandomSolver;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class EncodingTests {

    @Test
    public void testJobNumbers() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        // numéro de jobs : 1 2 2 1 1 2 (cf exercices)
        JobNumbers enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        Schedule sched = enc.toSchedule();
        System.out.println(sched);
        assert sched.isValid();
        assert sched.makespan() == 12;

        // numéro de jobs : 1 1 2 2 1 2
        enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        sched = enc.toSchedule();
        assert sched.isValid();
        assert sched.makespan() == 14;
        JobNumbers reverse = JobNumbers.fromSchedule(sched);
        Schedule back = reverse.toSchedule();
        Assert.assertTrue(back.isValid());
        Assert.assertEquals(sched.makespan(), back.makespan());
    }

    @Test
    public void testResourceOrder() throws IOException{
        // load the aaa1 instance
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        ResourceOrder resourceOrder = new ResourceOrder(instance);
        resourceOrder.getOrder()[0][0] = new Task(0,0);
        resourceOrder.getOrder()[1][0] = new Task(1,0);
        resourceOrder.getOrder()[2][0] = new Task(0,2);
        resourceOrder.getOrder()[0][1] = new Task(1,1);
        resourceOrder.getOrder()[1][1] = new Task(0,1);
        resourceOrder.getOrder()[2][1] = new Task(1,2);

        Schedule sched = resourceOrder.toSchedule();
        Assert.assertTrue(sched.isValid());
        Assert.assertEquals(12, sched.makespan());
        ResourceOrder reverse = ResourceOrder.fromSchedule(sched);
        Schedule back = reverse.toSchedule();
        Assert.assertTrue(back.isValid());
        Assert.assertEquals(sched.makespan(), back.makespan());
    }

    @Test
    public void conversionTestResourceToJob() throws IOException{
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        // numéro de jobs : 1 2 2 1 1 2 (cf exercices)
        JobNumbers enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        Schedule sched = enc.toSchedule();
        System.out.println(sched);
        assert sched.isValid();
        assert sched.makespan() == 12;

        // numéro de jobs : 1 1 2 2 1 2
        enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        sched = enc.toSchedule();
        Assert.assertTrue(sched.isValid());
        Assert.assertEquals(14, sched.makespan());
        ResourceOrder reverse = ResourceOrder.fromSchedule(sched);
        Schedule back = reverse.toSchedule();
        Assert.assertTrue(back.isValid());
        Assert.assertEquals(sched.makespan(), back.makespan());
    }

    @Test
    public void conversionTestJobToResource() throws IOException{
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        ResourceOrder resourceOrder = new ResourceOrder(instance);
        resourceOrder.getOrder()[0][0] = new Task(0,0);
        resourceOrder.getOrder()[1][0] = new Task(1,0);
        resourceOrder.getOrder()[2][0] = new Task(0,2);
        resourceOrder.getOrder()[0][1] = new Task(1,1);
        resourceOrder.getOrder()[1][1] = new Task(0,1);
        resourceOrder.getOrder()[2][1] = new Task(1,2);

        Schedule sched = resourceOrder.toSchedule();
        Assert.assertTrue(sched.isValid());
        Assert.assertEquals(12, sched.makespan());
        JobNumbers reverse = JobNumbers.fromSchedule(sched);
        Schedule back = reverse.toSchedule();
        Assert.assertTrue(back.isValid());
        Assert.assertEquals(sched.makespan(), back.makespan());

    }

    public void testBasicSolverOnInstance(String instanceFile) throws IOException{
        Instance instance = Instance.fromFile(Paths.get(instanceFile));
        Solver solver = new BasicSolver();
        Result result = solver.solve(instance, System.currentTimeMillis() + 10);
        Assert.assertTrue(result.schedule.isValid());

        JobNumbers jobNumbers = JobNumbers.fromSchedule(result.schedule);
        Schedule jobSchedule = jobNumbers.toSchedule();
        ResourceOrder resourceOrder = ResourceOrder.fromSchedule(result.schedule);
        Schedule resourceSchedule = resourceOrder.toSchedule();

        Assert.assertTrue(jobSchedule.isValid());
        Assert.assertTrue(resourceSchedule.isValid());
        Assert.assertEquals(jobSchedule.makespan(),resourceSchedule.makespan());
        Assert.assertEquals(result.schedule.makespan(),resourceSchedule.makespan());

    }
    public void testRandomSolverOnInstance(String instanceFile) throws IOException{
        Instance instance = Instance.fromFile(Paths.get(instanceFile));
        Solver solver = new RandomSolver();
        Result result = solver.solve(instance, System.currentTimeMillis() + 10);
        Assert.assertTrue(result.schedule.isValid());

        JobNumbers jobNumbers = JobNumbers.fromSchedule(result.schedule);
        Schedule jobSchedule = jobNumbers.toSchedule();
        ResourceOrder resourceOrder = ResourceOrder.fromSchedule(result.schedule);
        Schedule resourceSchedule = resourceOrder.toSchedule();

        Assert.assertTrue(jobSchedule.isValid());
        Assert.assertTrue(resourceSchedule.isValid());
        Assert.assertEquals(jobSchedule.makespan(),resourceSchedule.makespan());
        Assert.assertEquals(result.schedule.makespan(),resourceSchedule.makespan());

    }
    @Test
    public void testMultipleInstances() throws IOException{
        String[] instances = new String[] {"instances/aaa1","instances/aaa1","instances/la01","instances/la12","instances/orb10","instances/swv01","instances/swv02",
                "instances/ta67","instances/yn4","instances/ta80","instances/ta04","instances/ta03","instances/orb08","instances/la36"};
        for(String s :instances){
            testBasicSolverOnInstance(s);
            testRandomSolverOnInstance(s);
        }
    }


    @Test
    public void testBasicSolver() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        // build a solution that should be equal to the result of BasicSolver
        JobNumbers enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        Schedule sched = enc.toSchedule();
        assert sched.isValid();
        assert sched.makespan() == 12;

        Solver solver = new BasicSolver();
        Result result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.schedule.isValid();
        assert result.schedule.makespan() == sched.makespan(); // should have the same makespan
    }

}
