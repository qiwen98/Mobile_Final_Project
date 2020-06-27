package com.example.logintesting;

import android.os.Bundle;
import android.renderscript.Script;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewTimeCapsuleActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_time_capsule);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Time Capsule");

        editTextTitle=findViewById(R.id.edit_text_title);
        editTextDescription=findViewById(R.id.edit_text_description);
       numberPickerPriority=findViewById(R.id.number_picker_priority);

       numberPickerPriority.setMinValue(1);
       numberPickerPriority.setMaxValue(10);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_time_capsule_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
       
    }

    private void saveNote() {
        String title=editTextTitle.getText().toString();
        String description=editTextDescription.getText().toString();
        int priority=numberPickerPriority.getValue();

        if(title.trim().isEmpty()|| description.trim().isEmpty())
        {
            Toast.makeText(this,"Please insert title and desciption",Toast.LENGTH_LONG).show();
            return;
        }
        CollectionReference timecapsuleRef= FirebaseFirestore.getInstance()
                .collection("TimeCapsuleBook");
        timecapsuleRef.add(new TimeCapsule(title,description,priority));
        Toast.makeText(this,"Note added",Toast.LENGTH_LONG).show();
        finish();

    }

}