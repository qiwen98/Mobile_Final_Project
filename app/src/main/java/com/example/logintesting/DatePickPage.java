package com.example.logintesting;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class DatePickPage extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener{

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference TimeCapsuleRef=db.collection("TimeCapsuleBook");
    String TAG="DatePickPage";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datepicker);
        setTitle("                               Select Date");


        Log.d(TAG, "onCreate: initialise sucessfully");

        Button button=(Button)findViewById(R.id.select_date_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker=new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });



    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,year);

        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String currentDateString= DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        Log.d(TAG, "onDateSet: month"+c.get(month));
       // String currentDateString= c.get((Calendar.YEAR))+"-"+ c.get(month)+"-"+c.get((Calendar.DAY_OF_MONTH));

        TextView textView=(TextView) findViewById(R.id.textViewforSelectedDate);
        textView.setText(currentDateString);


        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try{
            Date date = simpleDateFormat.parse(simpleDateFormat.format(c.getTime()));
            Timestamp New_timestamp=new Timestamp(date);
            Timestamp CurrentTimeStamp=Timestamp.now();
            Log.d(TAG, "onDateselected: "+ New_timestamp+date);

            Button button=(Button)findViewById(R.id.send_date_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (New_timestamp.compareTo(CurrentTimeStamp)>=0)
                    {
                        RefreshinFirestore(New_timestamp);
                    }
                    else
                    {
                        showToast("Please select a future date");
                    }

                }
            });




        }
        catch (Exception e) {
            //The handling for the code
        }


    }

    private void RefreshinFirestore(Timestamp newTimestamp) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String TimecapsuleRefID=getIntent().getStringExtra("TimecapsuleRefID");
        ArrayList<String> receiverList= (ArrayList<String>) getIntent().getSerializableExtra("Receivers");


        TimeCapsuleRef.document(TimecapsuleRefID)
                .update("capsuleType","TimeCapsule",
                        "receiver", receiverList,
                        "validTimeStampForOpen",newTimestamp
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        showToast("Your Time Capsule is ready !!");
                        showToast(TimecapsuleRefID);

                        Intent intent = new Intent(DatePickPage.this, TimeCapsuleNavigatePage.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
}
