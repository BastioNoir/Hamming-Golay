package com.example.hamminggolay.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hamminggolay.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etEDT= findViewById(R.id.edTextDCT);
        EditText etECT= findViewById(R.id.edTextECT);

        Button hammingButton = findViewById(R.id.HammingButton);
        Button golay23Button = findViewById(R.id.Golay23Button);
        Button golay24Button = findViewById(R.id.Golay24Button);

        hammingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent subActividad = new Intent( MainActivity.this, NewGameActivity.class );
//                subActividad.putExtra( "type", true);
//                activityResultLauncher.launch(subActividad);
            }
        });

        golay23Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent subActividad = new Intent( MainActivity.this, NewGameActivity.class );
//                subActividad.putExtra( "type", true);
//                activityResultLauncher.launch(subActividad);
            }
        });

        golay24Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent subActividad = new Intent( MainActivity.this, NewGameActivity.class );
//                subActividad.putExtra( "type", true);
//                activityResultLauncher.launch(subActividad);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.configuration_options, menu );
        return true;
    }
}