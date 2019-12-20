package com.example.magisterka;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LabelListActivity extends AppCompatActivity {
    private Button addLabel, updateLabel, deleteLabel;
    private Intent addLabelIntent, updateLabelIntent;
    private RecyclerView rv;
    private List<Label> lp;
    private DBHelper myDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.labellist);
        addLabel = findViewById(R.id.addLabel);
        updateLabel = findViewById(R.id.updateLabel);
        deleteLabel = findViewById(R.id.deleteLabel);
        addLabelIntent = new Intent(this, AddLabel.class);
        updateLabelIntent = new Intent(this, UpdateLabel.class);
        rv = findViewById(R.id.rv1);
        LinearLayoutManager ln = new LinearLayoutManager(this);
        rv.setLayoutManager(ln);

        myDb = new DBHelper(this);
        //myDb.onUpgrade(myDb, 0, 0);
        //myDb.insertData("test1", null, 0);



        Cursor res = myDb.getAllData();
        if(res.getCount() == 0) {
            //error
        }

        lp  = new ArrayList<>();
        int i=0;
        while(res.moveToNext()){
            lp.add(new Label(res.getString(0), null, 0));
            i++;
        }



        LabelAdapter pa = new LabelAdapter(this, lp, myDb);
        rv.setAdapter(pa);


        addLabel.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddLabelActivity();
            }
        });
        
        updateLabel.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUpdateLabelActivity();
            }
        });
        
        deleteLabel.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteChoosenLabel();
            }
        });
    }

    private void deleteChoosenLabel() {
    }

    private void goToUpdateLabelActivity() {
        startActivity(updateLabelIntent);
    }

    private void goToAddLabelActivity() {
        startActivity(addLabelIntent);
    }
}
