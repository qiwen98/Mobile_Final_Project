package com.example.logintesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Choose_ar_or_time_capsule extends AppCompatActivity implements View.OnClickListener {

    private ImageButton ButtonForAR;

    private ImageButton ButtonForTime;
    private String TimecapsuleRef_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ar_or_time_capsule);

        ButtonForAR=findViewById(R.id.TimeCapsuleBut);
        ButtonForTime=findViewById(R.id.ArCapsuleBut);

        TimecapsuleRef_ID=getIntent().getStringExtra("TimecapsuleRefID");
        //Toast.makeText(Choose_ar_or_time_capsule.this,TimecapsuleRef_ID,Toast.LENGTH_LONG).show();
        ButtonForAR.setOnClickListener(this);
        ButtonForTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.TimeCapsuleBut:

                //go time capsule page

                Intent intent = new Intent(Choose_ar_or_time_capsule.this, Map_Activity.class);
                intent.putExtra("TimecapsuleRefID",  TimecapsuleRef_ID);
                startActivity(intent);
                break;

            case R.id.ArCapsuleBut:


                intent = new Intent(Choose_ar_or_time_capsule.this, ARactivity.class);
                intent.putExtra("TimecapsuleRefID",  TimecapsuleRef_ID);
                startActivity(intent);
                //go ar capsule page
               // finish();


                break;
        }

    }


}