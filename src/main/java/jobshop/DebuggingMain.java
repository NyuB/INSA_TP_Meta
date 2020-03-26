package jobshop;

import jobshop.encodings.JobNumbers;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.io.IOException;
import java.nio.file.Paths;

public class DebuggingMain {

    public static void main(String[] args) {
        try {
            // load the aaa1 instance
            Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

            // construit une solution dans la représentation par
            // numéro de jobs : [0 1 1 0 0 1]
            // Note : cette solution a aussi été vue dans les exercices (section 3.3)
            //        mais on commençait à compter à 1 ce qui donnait [1 2 2 1 1 2]
            JobNumbers enc = new JobNumbers(instance);
            enc.jobs[enc.nextToSet++] = 0;//Job 0 [0] = t0,3 => t0 (0, 3)
            enc.jobs[enc.nextToSet++] = 1;//Job 1 [0] = t1,2 => t1 (0, 2)
            enc.jobs[enc.nextToSet++] = 1;//Job 1 [1] = t0,2 => t0 (3, 5)
            enc.jobs[enc.nextToSet++] = 0;//Job 0 [1] = t1,3 => t1 (3, 6)
            enc.jobs[enc.nextToSet++] = 0;//Job 0 [2] = t2, 2 => t2 (6, 8)
            enc.jobs[enc.nextToSet++] = 1;//Job 1 [2] = t2, 4 => t2 (8, 12)
            //Soit j0 et j1 commencent en 0, j0 finit en 8, j1 en 12
            //t0 et t1 commencent en 0 et finissent en 5 et 6
            //t2 commence en 6 et finit en 12

            System.out.println("\nENCODING: " + enc);
            Schedule sched = enc.toSchedule();
            System.out.println("SCHEDULE: " + sched);
            System.out.println("VALID: " + sched.isValid());
            System.out.println("MAKESPAN: " + sched.makespan());

            ResourceOrder resourceOrder = new ResourceOrder(instance);
            resourceOrder.getOrder()[0][0] = new Task(0,0);
            resourceOrder.getOrder()[1][0] = new Task(1,0);
            resourceOrder.getOrder()[2][0] = new Task(0,2);
            resourceOrder.getOrder()[0][1] = new Task(1,1);
            resourceOrder.getOrder()[1][1] = new Task(0,1);
            resourceOrder.getOrder()[2][1] = new Task(1,2);

            System.out.println("\nENCODING: " + resourceOrder);
            sched = resourceOrder.toSchedule();
            System.out.println("SCHEDULE: " + sched);
            System.out.println("VALID: " + sched.isValid());
            System.out.println("MAKESPAN: " + sched.makespan());

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
