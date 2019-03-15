package tilbe.ibay.estrada.rambo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * A simple {@link Fragment} subclass.
 */
public class FixedPartitionFragment extends Fragment {
    Button addRow;
    Button minusRow;
    Button addJob;
    Button fixedButton;

    EditText jobSizeEt;
    TextView fixedResultTv;
    TextView jobQueueTv;
    TextView clearQueueTv;

    ArrayList<EditText> editTextList;
    Stack<TableRow> tableRowStack;
    Queue<Integer> job;

    RadioButton firstFitRb;

    String result;

    int counter = 0;
    public FixedPartitionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fixed_partition, container, false);
        editTextList = new ArrayList<>();
        tableRowStack = new Stack<>();
        job = new LinkedList<>();

        addRow = view.findViewById(R.id.plus);
        minusRow = view.findViewById(R.id.minus);
        addJob = view.findViewById(R.id.addjob);
        fixedButton = view.findViewById(R.id.fixed_btn);
        jobSizeEt = view.findViewById(R.id.job_size_et);
        jobQueueTv = view.findViewById(R.id.job_queue_tv);
        clearQueueTv = view.findViewById(R.id.clear_tv);
        fixedResultTv = view.findViewById(R.id.fixed_result);
        firstFitRb = view.findViewById(R.id.first_fit_rb);

        addRow.setOnClickListener(v -> {
            // get a reference for the TableLayout
            TableLayout table = getView().findViewById(R.id.TableLayout01);

            TableRow row = new TableRow(getContext());

            counter++;

            TextView t = new TextView(getContext());
            t.setText("Partition " + counter);
            t.setBackgroundResource(R.drawable.border);
            t.setLayoutParams(new TableRow.LayoutParams(spToPx(100, getContext()), TableRow.LayoutParams.MATCH_PARENT));
            t.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            t.setGravity(Gravity.CENTER);
            t.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            EditText e = new EditText(getContext());
            e.setHint("memory");
            e.setInputType(InputType.TYPE_CLASS_NUMBER);
            e.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            e.setBackgroundResource(R.drawable.border);
            e.setLayoutParams(new TableRow.LayoutParams(spToPx(120, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
            e.setGravity(Gravity.CENTER);
            e.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            row.addView(t);
            row.addView(e);

            tableRowStack.push(row);
            editTextList.add(e);

            table.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            e.requestFocus();
        });

        minusRow.setOnClickListener(v -> {
            if (counter > 0) {
                TableLayout table = getView().findViewById(R.id.TableLayout01);
                TableRow row = tableRowStack.peek();
                table.removeView(row);
                tableRowStack.pop();
                editTextList.remove(editTextList.size()-1);
                counter--;
            } else{
                Toast.makeText(getContext(),"No rows left",
                        Toast.LENGTH_SHORT).show();
            }
        });

        addJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                job.add(Integer.parseInt(jobSizeEt.getText().toString()));
                String jobQueueString = job.toString();
                jobQueueTv.setText("Job Queue: " + jobQueueString);
                jobSizeEt.setText("");
            }
        });

        fixedButton.setOnClickListener(v -> {
            if (checkInputValidity()){
                int[] partition = new int[editTextList.size()];
                ArrayList<EditText> temp = editTextList;
                for (int i = 0; i < temp.size(); i++) {
                    partition[i] = Integer.parseInt(temp.get(i).getText().toString());
                }

                if (firstFitRb.isChecked()){

                    System.out.println(job.toString() + "--------------job---------------");
                    for (int i = 0; i < partition.length; i++) {
                        System.out.print(partition[i] + " ");
                    }
                    System.out.println("--------------partition--------------");

                    firstFit(job, partition);
                } else{
                    bestFit(job, partition);
                }
            } else {
                Toast.makeText(getContext(), "Invalid. Check job queue and partitions.", Toast.LENGTH_LONG).show();
            }
        });

        clearQueueTv.setOnClickListener(v -> {
            job.clear();
            jobQueueTv.setText("Job Queue: empty");
        });

        return view;
    }

    boolean checkInputValidity(){
        boolean emptyPartition = editTextList.isEmpty();
        boolean emptyJobQueue = job.isEmpty();

        for (int i = 0; i < editTextList.size(); i++) {
            if(editTextList.get(i).getText().toString().isEmpty())
                return false;
        }

        if(emptyJobQueue || emptyPartition) {
            return false;
        } else {
            return true;
        }
    }

    public static int spToPx(int sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    void firstFit(Queue<Integer> job, int[] partition){
        Queue<Integer> jobQueue = (Queue<Integer>)deepCopy(job);
        int[] allocation = new int[partition.length];
        Arrays.fill(allocation, 0);

        //n is the number of partitions
        int n = partition.length;
        int temp;

        for (int i = 0; i < n; i++) {
            temp = jobQueue.peek();
            System.out.println("queue: " + temp);
            for (int j = 0; j < n; j++) {
                if ((allocation[j] == 0) && (partition[j] > temp)){
                    allocation[j] = temp;
                    jobQueue.poll();
                    break;
                }
            }
        }
        computeAllocationStats(partition, allocation, n);
    }

    void bestFit(Queue<Integer> job, int[] partition){
        Queue<Integer> jobQueue = (Queue<Integer>)deepCopy(job);
        int[] allocation = new int[partition.length];
        Arrays.fill(allocation, 0);

        //n is the number of partitions
        int n = partition.length;
        int temp;

        int best = Integer.MAX_VALUE;
        int bestIndex = -1;

        for (int i = 0; i < n; i++) {
            temp = jobQueue.peek();
            System.out.println("queue: " + temp);
            for (int j = 0; j < n; j++) {
                if ((partition[j] >= temp) && (partition[j] < best) && (allocation[j] == 0)) {
                    best = partition[j];
                    bestIndex = j;
                }
            }
            if (bestIndex != -1){
                allocation[bestIndex] = temp;
                jobQueue.poll();
            }
            bestIndex = -1;
            best = Integer.MAX_VALUE;
        }

        computeAllocationStats(partition, allocation, n);
    }

    void computeAllocationStats(int[] partition, int[] allocation, int n){
        int inFrag = 0;
        int exFrag = 0;
        int memAvail = 0;
        int memAlloc = 0;
        float memUtil = 0;

        DecimalFormat df = new DecimalFormat("0.00");

        for (int i = 0; i < n; i++) {
            if (allocation[i] > 0) {
                inFrag += partition[i] - allocation[i];
            } else {
                exFrag += partition[i];
            }
            memAvail += partition[i];
            memAlloc += allocation[i];
        }

        memUtil = (float)memAlloc/(float)memAvail * 100;

        //Output only
        System.out.println("Partition");
        for (int j = 0; j < n; j++) {
            System.out.print(partition[j] + " ");

        }
        System.out.println();

        System.out.println("Allocation");
        String allocationPrint = "Allocation: ";
        for (int j = 0; j < n; j++) {
            if (allocation[j] == 0) {
                System.out.print("x ");
                allocationPrint += "x ";
            } else {
                System.out.print(allocation[j] + " ");
                allocationPrint += String.valueOf(allocation[j]) + " ";
            }
        }
        System.out.println();
        System.out.println("--------");
        System.out.println("Internal fragmentation: " + inFrag);
        System.out.println("External fragmentation: " + exFrag);
        System.out.println("Memory Utilization: " + df.format(memUtil) + "%");

        result = allocationPrint;
        result += "\n\n" +"Internal fragmentation: " + inFrag;
        result += "\n" + "External fragmentation: " + exFrag;
        result += "\n" + "Memory Utilization: " + df.format(memUtil) + "%";

        new AlertDialog.Builder(getContext())
                .setTitle("Result")
                .setMessage(result)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> fixedResultTv.setText(result))
                .show();
    }

    private Object deepCopy(Object object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
            outputStrm.writeObject(object);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return objInputStream.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



