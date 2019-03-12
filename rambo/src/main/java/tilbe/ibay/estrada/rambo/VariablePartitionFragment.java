package tilbe.ibay.estrada.rambo;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

/**
 * A simple {@link Fragment} subclass.
 */
public class VariablePartitionFragment extends Fragment {
    Button addRow;
    Button minusRow;
    Button variableBtn;
    RadioButton withRb;
    EditText memoryEt;

    Stack<TableRow> tableRowStack;
    ArrayList<EditText> et_memoryList;
    ArrayList<EditText> et_runTimeList;
    ArrayList<EditText> et_arrivalTimeList;
    ArrayList<Job> jobList_input;
    ArrayList<Job> jobList;

    int counter;
    int jobsDone;

    public VariablePartitionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        counter = 0;
        View view = inflater.inflate(R.layout.fragment_variable_partition, container, false);

        tableRowStack = new Stack<>();
        et_memoryList = new ArrayList<>();
        et_runTimeList = new ArrayList<>();
        et_arrivalTimeList = new ArrayList<>();

        addRow = view.findViewById(R.id.plus_2);
        minusRow = view.findViewById(R.id.minus_2);
        variableBtn = view.findViewById(R.id.variable_btn);
        withRb = view.findViewById(R.id.with_rb);
        memoryEt = view.findViewById(R.id.memory_et);

        addRow.setOnClickListener(v -> {
            // get a reference for the TableLayout
            TableLayout table = getView().findViewById(R.id.TableLayout02);

            TableRow row = new TableRow(getContext());

            counter++;

            TextView t = new TextView(getContext());
            t.setText(String.valueOf(counter));
            t.setBackgroundResource(R.drawable.border);
            t.setLayoutParams(new TableRow.LayoutParams(spToPx(50, getContext()), TableRow.LayoutParams.MATCH_PARENT));
            t.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            t.setGravity(Gravity.CENTER);
            t.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            EditText e = new EditText(getContext());
            e.setHint("enter size");
            e.setInputType(InputType.TYPE_CLASS_NUMBER);
            e.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            e.setBackgroundResource(R.drawable.border);
            e.setLayoutParams(new TableRow.LayoutParams(spToPx(75, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
            e.setGravity(Gravity.CENTER);
            e.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            EditText e1 = new EditText(getContext());
            e1.setHint("run time");
            e1.setInputType(InputType.TYPE_CLASS_NUMBER);
            e1.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            e1.setBackgroundResource(R.drawable.border);
            e1.setLayoutParams(new TableRow.LayoutParams(spToPx(75, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
            e1.setGravity(Gravity.CENTER);
            e1.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            EditText e2 = new EditText(getContext());
            e2.setHint("arrival time");
            e2.setInputType(InputType.TYPE_NULL);

            e2.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            e2.setBackgroundResource(R.drawable.border);
            e2.setLayoutParams(new TableRow.LayoutParams(spToPx(100, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
            e2.setGravity(Gravity.CENTER);
            e2.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            e2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getContext(), (timePicker, selectedHour, selectedMinute) -> {
                            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                            e2.setText(time);

                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                }
            });


            row.addView(t);
            row.addView(e);
            row.addView(e2);
            row.addView(e1);

            tableRowStack.push(row);
            et_memoryList.add(e);
            et_runTimeList.add(e1);
            et_arrivalTimeList.add(e2);

            table.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            e.requestFocus();
        });

        minusRow.setOnClickListener(v -> {
            if (counter > 0) {
                TableLayout table = getView().findViewById(R.id.TableLayout02);
                TableRow row = tableRowStack.peek();
                table.removeView(row);
                tableRowStack.pop();
                et_memoryList.remove(et_memoryList.size()-1);
                et_runTimeList.remove(et_runTimeList.size()-1);
                et_arrivalTimeList.remove(et_arrivalTimeList.size()-1);
                counter--;
            } else{
                Toast.makeText(getContext(),"No rows left",
                        Toast.LENGTH_SHORT).show();
            }
        });

        variableBtn.setOnClickListener(v -> {
            jobsDone = 0;
            jobList_input = new ArrayList<>();
            jobList = new ArrayList<>(jobList_input.size());

            jobList_input.clear();
            jobList.clear();

            TableLayout table = getView().findViewById(R.id.TableLayout03);
            table.removeAllViews();
            if (checkInputValidity()) {
                for (int i = 0; i < tableRowStack.size(); i++) {
                    jobList_input.add(new Job(
                            i + 1,
                            Integer.parseInt(et_memoryList.get(i).getText().toString()),
                            LocalTime.parse(et_arrivalTimeList.get(i).getText().toString()),
                            Integer.parseInt(et_runTimeList.get(i).getText().toString())
                    ));
                }

                //deep clone of job input
                for (Job j : jobList_input) {
                    jobList.add(new Job(j));
                }

                jobList = JobUtil.sort(jobList);

                System.out.println("----------joblist------------");
                for (int i = 0; i < jobList.size(); i++) {
                    Job j = jobList.get(i);
                    System.out.println(j.getJobNo() + " " + String.valueOf(j.getSize()) + "K " + String.valueOf(j.getArrivalTime())
                            + " " + String.valueOf(j.getRunTime()));
                }

                if (withRb.isChecked()) {
                    withCompaction();
                } else {
                    withoutCompaction();
                }
            } else {
                Toast.makeText(getContext(), "Invalid. Check job table.", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public static int spToPx(int sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    boolean checkInputValidity(){
        return !(tableRowStack.isEmpty() || memoryEt.getText().toString().isEmpty());
    }

    int getNumberJobsDone(ArrayList<Job> jobs){
        int num = 0;
        for (Job j: jobs) {
            if(j.getStatus().equals("done")) num++;
        }
        return num;
    }

    void withoutCompaction(){
        Memory memory = new Memory(Integer.valueOf(memoryEt.getText().toString()), "without");
        LocalTime currentTime = jobList.get(0).getArrivalTime();

        while (jobsDone < jobList.size()) {

            System.out.println("Current time: " + currentTime.toString());
            for (int i = 0; i < jobList.size(); i++) {
                Job job = jobList.get(i);

                //free partition with finished jobs
                memory.updateJobWithout(currentTime);
                memory.mergeEmptyPartitions();

                if(job.getStatus().equals("allocated") || job.getStatus().equals("done")) continue;
                //check if job did not arrive yet
                if (job.getArrivalTime().isAfter(currentTime)) {
                    System.out.println("Job " + job.getJobNo() + " has not arrived yet");
                    break;
                }
                else {
                    boolean allocated = false;

                    if(!jobList.get(i).getStatus().equals("done")){
                        job = memory.addPartitionWithout(job, currentTime);
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

        showTable();
    }

    void withCompaction(){
        Memory memory = new Memory(Integer.valueOf(memoryEt.getText().toString()), "with");
        LocalTime currentTime = jobList.get(0).getArrivalTime();
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

        showTable();
    }

    void showTable() {
        TableLayout table = getView().findViewById(R.id.TableLayout03);

        TableRow row_h = new TableRow(getContext());

        TextView t1_h = new TextView(getContext());
        t1_h.setText("Job");
        t1_h.setGravity(View.TEXT_ALIGNMENT_CENTER);
        t1_h.setBackgroundResource(R.drawable.border);
        t1_h.setLayoutParams(new TableRow.LayoutParams(spToPx(50, getContext()), TableRow.LayoutParams.MATCH_PARENT));
        t1_h.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
        t1_h.setGravity(Gravity.CENTER);
        t1_h.setPadding(spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()));

        TextView t2_h = new TextView(getContext());
        t2_h.setText("Started");
        t2_h.setGravity(View.TEXT_ALIGNMENT_CENTER);
        t2_h.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
        t2_h.setBackgroundResource(R.drawable.border);
        t2_h.setLayoutParams(new TableRow.LayoutParams(spToPx(75, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
        t2_h.setGravity(Gravity.CENTER);
        t2_h.setPadding(spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()));

        TextView t3_h = new TextView(getContext());
        t3_h.setText("Finished");
        t3_h.setGravity(View.TEXT_ALIGNMENT_CENTER);
        t3_h.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
        t3_h.setBackgroundResource(R.drawable.border);
        t3_h.setLayoutParams(new TableRow.LayoutParams(spToPx(75, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
        t3_h.setGravity(Gravity.CENTER);
        t3_h.setPadding(spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()));

        TextView t5_h = new TextView(getContext());
        t5_h.setText("Wait");
        t5_h.setGravity(View.TEXT_ALIGNMENT_CENTER);
        t5_h.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
        t5_h.setBackgroundResource(R.drawable.border);
        t5_h.setLayoutParams(new TableRow.LayoutParams(spToPx(75, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
        t5_h.setGravity(Gravity.CENTER);
        t5_h.setPadding(spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()));

        TextView t4_h = new TextView(getContext());
        t4_h.setText("MAWJWA");
        t4_h.setGravity(View.TEXT_ALIGNMENT_CENTER);
        t4_h.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
        t4_h.setBackgroundResource(R.drawable.border);
        t4_h.setLayoutParams(new TableRow.LayoutParams(spToPx(100, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
        t4_h.setGravity(Gravity.CENTER);
        t4_h.setPadding(spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()),
                spToPx(8, getContext()));

        row_h.addView(t1_h);
        row_h.addView(t2_h);
        row_h.addView(t3_h);
        row_h.addView(t5_h);
        row_h.addView(t4_h);

        table.addView(row_h, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        for (Job j: jobList){
            TableRow row = new TableRow(getContext());

            TextView t1 = new TextView(getContext());
            t1.setText(String.valueOf(j.getJobNo()));
            t1.setBackgroundResource(R.drawable.border);
            t1.setLayoutParams(new TableRow.LayoutParams(spToPx(50, getContext()), TableRow.LayoutParams.MATCH_PARENT));
            t1.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            t1.setGravity(Gravity.CENTER);
            t1.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            TextView t2 = new TextView(getContext());
            t2.setText(String.valueOf(j.getTimeStarted()));
            t2.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            t2.setBackgroundResource(R.drawable.border);
            t2.setLayoutParams(new TableRow.LayoutParams(spToPx(75, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
            t2.setGravity(Gravity.CENTER);
            t2.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            TextView t3 = new TextView(getContext());
            t3.setText(String.valueOf(j.getTimeFinished()));
            t3.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            t3.setBackgroundResource(R.drawable.border);
            t3.setLayoutParams(new TableRow.LayoutParams(spToPx(75, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
            t3.setGravity(Gravity.CENTER);
            t3.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            TextView t5 = new TextView(getContext());
            t5.setText(String.valueOf(j.getWaitingTime()));
            t5.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            t5.setBackgroundResource(R.drawable.border);
            t5.setLayoutParams(new TableRow.LayoutParams(spToPx(75, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
            t5.setGravity(Gravity.CENTER);
            t5.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            TextView t4 = new TextView(getContext());
            t4.setText(String.valueOf(j.getWhenAllocated()));
            t4.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
            t4.setBackgroundResource(R.drawable.border);
            t4.setLayoutParams(new TableRow.LayoutParams(spToPx(100, getContext()), TableRow.LayoutParams.WRAP_CONTENT));
            t4.setGravity(Gravity.CENTER);
            t4.setPadding(spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()),
                    spToPx(8, getContext()));

            row.addView(t1);
            row.addView(t2);
            row.addView(t3);
            row.addView(t5);
            row.addView(t4);

            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
        table.setVisibility(TableLayout.VISIBLE);
    }

}

