package com.example.logintesting;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    String TAG="DatePickerFragment";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LocalDateTime now = LocalDateTime.now();
        Calendar calendar=Calendar.getInstance();
        int year= now.getYear();
        int month=now.getMonthValue()-1;
        int day=now.getDayOfMonth();
        Log.d(TAG, "onCreateDialog: "+year+"  "+month+"   "+day);

        return new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener) getActivity(),year,month,day );
    }
}
