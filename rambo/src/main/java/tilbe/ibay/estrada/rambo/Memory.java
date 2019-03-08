package tilbe.ibay.estrada.rambo;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 *
 * @author IJTilbe
 */
public class Memory{
    int memoryAvailable;
    int remainingMemory;
    ArrayList<Partition> partitionList;
    ArrayList<Job> jobList;
    public Memory(int memoryAvailable, String compaction) {
        this.memoryAvailable = memoryAvailable;
        remainingMemory = memoryAvailable;
        if (compaction.equals("without")) {
            partitionList = new ArrayList<>();
            partitionList.add(new Partition(memoryAvailable));
        } else {
            jobList = new ArrayList<>();
        }

    }

    public int getMemoryAvailable() {
        return memoryAvailable;
    }

    private class Partition{
        int memorySize;
        Job job;

        public Partition(Job job) {
            this.job = job;
            this.memorySize = job.getSize();
        }

        public Partition(int memorySize) {
            this.job = null;
            this.memorySize = memorySize;
        }

    }

    //for adding partitions with compaction
    public Job addPartitionWith(Job job, LocalTime time){
        if (memoryAvailable >= job.getSize()) {
            memoryAvailable -= job.getSize();
            jobList.add(job);
            job.setStatus("allocated");
            job.setTimeStarted(time);
            job.setTimeFinished(time.plusMinutes(job.getRunTime()));
            job.setWaitingTime(Math.toIntExact(Duration.between(job.getArrivalTime(), job.getTimeStarted()).toMinutes()));
            job.setWhenAllocated(memoryAvailable);
            System.out.println("Update: Job No " + job.getJobNo() + " Allocated");
        } else {
            System.out.println("Insufficient memory to create new partition");
        }
        return job;
    }

    public Job addPartitionWithout(Job job, LocalTime time){
        if (memoryAvailable > 0) {
            System.out.println("Job No " + job.getJobNo() + ": Finding free partitions...");
            for (int i = 0; i < partitionList.size(); i++) {
                Partition p = partitionList.get(i);
                if((p.memorySize > job.getSize()) && (p.job == null)){
                    p.job = job;
                    int memoryRemaining = p.memorySize - job.getSize();

                    if(memoryRemaining > 0)
                        partitionList.add(i+1, new Partition(memoryRemaining));

                    p.memorySize = job.getSize();
                    memoryAvailable -= job.getSize();
                    job.setStatus("allocated");
                    job.setTimeStarted(time);
                    job.setTimeFinished(time.plusMinutes(job.getRunTime()));
                    job.setWaitingTime(Math.toIntExact(Duration.between(job.getArrivalTime(), job.getTimeStarted()).toMinutes()));
                    job.setWhenAllocated(memoryAvailable);
                    System.out.println("Update: Job No " + job.getJobNo() + " Allocated");
                    break;
                }
            }
        } else {
            System.out.println("Insufficient memory to create new partition");
        }
        return job;
    }

    public void updateJobWithout(LocalTime currentTime){

        for (int i = 0; i < partitionList.size(); i++) {
            Job j = partitionList.get(i).job;
            if ((j != null)) {
                if ((j.getStatus().equals("allocated")) && j.getTimeFinished().isBefore(currentTime.plusMinutes(1))) {
                    partitionList.get(i).job.setStatus("done");
                    memoryAvailable += j.getSize();
                    partitionList.get(i).job = null;

                }
            }
        }

    }

    public void updateJobWith(LocalTime currentTime){
        if (!jobList.isEmpty()) {
            for (int i = 0; i < jobList.size(); i++) {
                Job j = jobList.get(i);
                if ((j != null)) {
                    if ((j.getStatus().equals("allocated")) && j.getTimeFinished().isBefore(currentTime.plusMinutes(1))) {
                        jobList.get(i).setStatus("done");
                        memoryAvailable += j.getSize();
                    }
                }
            }
        }
    }

    public void mergeEmptyPartitions(){
        if (partitionList.size() > 2) {
            int partitionListSize = partitionList.size() - 1;
            for (int i = 0; i < partitionListSize; i++) {
                if((partitionList.get(i).job == null) && (partitionList.get(i+1).job == null)){
                    int temp = partitionList.get(i).memorySize + partitionList.get(i+1).memorySize;
                    partitionList.get(i).memorySize = temp;
                    partitionList.remove(i+1);
                    partitionListSize = partitionList.size() - 1;
                    i = 0;
                }
            }

        }

    }

}
