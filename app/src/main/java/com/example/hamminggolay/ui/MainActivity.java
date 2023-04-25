package com.example.hamminggolay.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hamminggolay.R;

import com.example.hamminggolay.core.G23Decrypter;
import com.example.hamminggolay.core.G24Decrypter;
import com.example.hamminggolay.core.HammingDecrypter;

public class MainActivity extends AppCompatActivity {
    HammingDecrypter hDecrypter;
    G23Decrypter g23Decrypter;
    G24Decrypter g24Decrypter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hDecrypter= new HammingDecrypter();
        g23Decrypter= new G23Decrypter();
        g24Decrypter= new G24Decrypter();

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

    private void parametersSelector(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(type + " parameters");

        if(type.equals("H")){
            TextView txtP1= new TextView(this);
            txtP1.setText("n: ");
            EditText etP1 = new EditText(this);

            TextView txtP2= new TextView(this);
            txtP1.setText("k: ");
            EditText etP2 = new EditText(this);

            builder.setView(etP1);
            builder.setPositiveButton("Act", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String profileName = editText.getText().toString();
                    profiles.get(position).setName(profileName);
                    profileArrayAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }else if(type.equals("G23")){

        }else{

        }
    }
}