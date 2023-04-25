package com.example.hamminggolay.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hamminggolay.ui.R;
import com.example.hamminggolay.core.G23Decrypter;
import com.example.hamminggolay.core.G24Decrypter;
import com.example.hamminggolay.core.HammingDecrypter;

public class MainActivity extends AppCompatActivity {
    HammingDecrypter hDecrypter;
    G23Decrypter g23Decrypter;
    G24Decrypter g24Decrypter;

    EditText etEDT;
    EditText etECT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hDecrypter= new HammingDecrypter();
        g23Decrypter= new G23Decrypter();
        g24Decrypter= new G24Decrypter();

        etEDT= findViewById(R.id.edTextDCT);
        etECT= findViewById(R.id.edTextECT);

        Button hammingButton = findViewById(R.id.HammingButton);
        Button golay23Button = findViewById(R.id.Golay23Button);
        Button golay24Button = findViewById(R.id.Golay24Button);

        hammingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parametersSelector("H");
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
                    if(!etP1.equals(null) && !etP2.equals(null)){
                        hDecrypter.setN(Integer.parseInt(etP1.getText().toString()));
                        hDecrypter.setK(Integer.parseInt(etP2.getText().toString()));

                        hDecrypter.decode(transformData(etEDT.getText().toString()));
                    }
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }else if(type.equals("G23")){

        }else{

        }
    }

    public int[] transformData(String input){
        int[] output= new int[]{};
        for(int i=0; i<input.length();i++){
            output[i]=Character.getNumericValue(input.charAt(i));
        }
        Log.e("","Input: "+input+", Output: "+output);
        return output;
    }
}