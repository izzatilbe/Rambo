package tilbe.ibay.estrada.rambo;

/**
 * Created by IJTilbe on 08/03/2019.
 */

import java.time.LocalTime;

public class Job {
    private int jobNo;
    private int size;
    private LocalTime arrivalTime;
    private int runTime;

    private LocalTime timeStarted;
    private LocalTime timeFinished;
    private int waitingTime;
    private String status;
    private int whenAllocated; //memory available when job was allocated

    public Job(int jobNo, int size, LocalTime arrivalTime, int runTime) {
        this.jobNo = jobNo;
        this.size = size;
        this.arrivalTime = arrivalTime;
        this.runTime = runTime;
        timeStarted = LocalTime.MIN;
        timeFinished = LocalTime.MIN;
        whenAllocated = 0;
        status = "waiting";
    }

    public Job(Job job) {
        this.jobNo = job.getJobNo();
        this.size = job.getSize();
        this.arrivalTime = job.getArrivalTime();
        this.runTime = job.getRunTime();
        timeStarted = LocalTime.MIN;
        timeFinished = LocalTime.MIN;
        whenAllocated = 0;
        status = "waiting";
    }

    public int getJobNo() {
        return jobNo;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public int getRunTime() {
        return runTime;
    }

    public int getSize() {
        return size;
    }

    public LocalTime getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(LocalTime timeStarted) {
        this.timeStarted = timeStarted;
    }

    public LocalTime getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(LocalTime timeFinished) {
        this.timeFinished = timeFinished;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getWhenAllocated() {
        return whenAllocated;
    }

    public void setWhenAllocated(int whenAllocated) {
        this.whenAllocated = whenAllocated;
    }
}
