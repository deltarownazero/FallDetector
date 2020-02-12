package com.example.magisterka;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateLabel extends AppCompatActivity {
    Button save, uploadImage;
    EditText name, description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editlabel);
        save = findViewById(R.id.saveLabel);
       // uploadImage = findViewById(R.id.uploadImage);
        name = findViewById(R.id.editName);
        description = findViewById(R.id.editDescription);


        save.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLabel();
            }
        });

    }

    private void saveLabel() {
    }

}
