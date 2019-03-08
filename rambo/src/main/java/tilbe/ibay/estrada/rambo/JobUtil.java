package tilbe.ibay.estrada.rambo;

/**
 * Created by IJTilbe on 08/03/2019.
 */

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

public class JobUtil {
    public static ArrayList sort(ArrayList<Job> jobList){
        Collections.sort(jobList, (j1, j2) -> {
            LocalTime t1 = j1.getArrivalTime();
            LocalTime t2 = j2.getArrivalTime();
            int value;
            if (t1.isAfter(t2)) value = 1;
            else if (t1.isBefore(t2)) value = -1;
            else value = 0;
            return value;
        });

        return jobList;
    }
}
