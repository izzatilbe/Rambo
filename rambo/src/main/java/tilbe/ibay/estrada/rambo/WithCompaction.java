package tilbe.ibay.estrada.rambo;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 *
 * @author IJTilbe
 */
public class WithCompaction {

    /**
     * @param args the command line arguments
     */

    static int jobsDone = 0;
    public static void main(String[] args) {
        ArrayList<Job> jobList = new ArrayList<>();
        jobList.add(new Job(1, 60, LocalTime.parse("10:00"), 10));
        jobList.add(new Job(2, 100, LocalTime.parse("10:05"), 15));
        jobList.add(new Job(3, 50, LocalTime.parse("10:05"), 20));
        jobList.add(new Job(4, 50, LocalTime.parse("10:10"), 8));
        jobList.add(new Job(5, 30, LocalTime.parse("10:15"), 15));

        //jobList = JobUtil.sort(jobList);

        Memory memory = new Memory(200, "with");
        LocalTime currentTime = jobList.get(0).getArrivalTime();
        int totalTime = Math.toIntExact(Duration.between(jobList.get(0).getArrivalTime(),
                (jobList.get(jobList.size()-1).getArrivalTime())).toMinutes());

        System.out.println(totalTime);
        while (jobsDone < jobList.size()) {

            System.out.println("Current time: " + currentTime.toString());
            for (int i = 0; i < jobList.size(); i++) {
                Job job = jobList.get(i);

                //free partition with finished jobs
                memory.updateJobWith(currentTime);

                if(job.getStatus().equals("allocated") || job.getStatus().equals("done")) continue;
                //check if job did not arrive yet
                if (job.getArrivalTime().isAfter(currentTime)) {
                    System.out.println("Job " + job.getJobNo() + " has not arrived yet");
                    break;
                }
                else {
                    boolean allocated = false;

                    if(!jobList.get(i).getStatus().equals("done")){
                        job = memory.addPartitionWith(job, currentTime);
                        jobList.set(i, job);
                        if(job.getStatus().equals("allocated")){
                            allocated = true;
                        } else {
                            allocated = false;
                        }
                    }

                    if (!allocated){
                        System.out.println("No partitions available. Waiting...");
                        break;
                    }
                }

            }
            currentTime = currentTime.plusMinutes(1);
            jobsDone = getNumberJobsDone(jobList);
        }

        for (int i = 0; i < jobList.size(); i++) {
            Job j = jobList.get(i);
            System.out.println(j.getJobNo() + " " + j.getTimeStarted().toString() + " " + j.getTimeFinished().toString()
                    + " " + j.getWaitingTime() + " " + j.getWhenAllocated() + "K" + " " + j.getStatus());
        }
    }

    static int getNumberJobsDone(ArrayList<Job> jobs){
        int num = 0;
        for (Job j: jobs) {
            if(j.getStatus().equals("done")) num++;
        }
        return num;
    }

}
