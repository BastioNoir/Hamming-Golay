package com.example.hamminggolay.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamminggolay.*;

import java.sql.SQLOutput;

public class MainActivity extends AppCompatActivity {
    GolayDecoder24 golay24Decoder;

    EditText etEDT;
    EditText etECT;

    String strEDT, strECT="null";

    /**Obtiene el contenido de la parte encriptada*/
    public String getECT(){
        System.out.println("EncryptedT: "+etECT.getText().toString());
        return etECT.getText().toString();
    }

    /**Modifica el contenido de decrypted*/
    public void setEDT(String input){
        System.out.println("OLD EncryptedT: "+this.strEDT+" NEW EDT: "+input);
        this.strEDT=input;
        etEDT.setText(input);
    }

    /**Modifica el contenido de encrypted, para examples*/
    public void setECT(String input){
        System.out.println("OLD EncryptedT: "+this.strEDT+" NEW ECT: "+input);
        this.strECT=input;
        etECT.setText(input);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        golay24Decoder = new GolayDecoder24();

        etEDT= findViewById(R.id.edTextEDT);
        etECT= findViewById(R.id.edTextECT);

        Button golay23Button = findViewById(R.id.Golay23Button);
        Button golay24Button = findViewById(R.id.Golay24Button);
        Button e1= findViewById(R.id.e1);
        Button e2= findViewById(R.id.e2);

        //EXAMPLE1
        e1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setECT("11010010111011111001001");
                Toast.makeText(MainActivity.this, "Charged 11010010111011111001001", Toast.LENGTH_SHORT).show();
            }
        });

        //EXAMPLE2
        e2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setECT("101111101111010010010010");
                Toast.makeText(MainActivity.this, "Charged 101111101111010010010010", Toast.LENGTH_SHORT).show();
            }
        });

        golay23Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setECT(etECT.getText().toString());
                Log.e("",etECT.getText().toString());
                parametersSelector("G23");
            }
        });

        golay24Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setECT(etECT.getText().toString());
                Log.e("",etECT.getText().toString());
                parametersSelector("G24");
            }
        });
    }

    private void parametersSelector(String type) throws NumberFormatException{
        if(type.equals("G23")){
            Log.w("","GOLAY 23");
            int[] toDecode= transformData(getECT(),23);
            Log.w("TODECODE",Integer.toString(toDecode.length));

            if(getECT().length()!=23){
                Toast.makeText(this, "Select another 23 bits word", Toast.LENGTH_SHORT).show();
            }else{
                setEDT(golay24Decoder.decodeWord(toDecode));
                Toast.makeText(this, "Decrypting...", Toast.LENGTH_SHORT).show();
            }
        }else{
            //G24
            Log.w("TODECODE","GOLAY 24");
            int[] toDecode= transformData(getECT(),24);

            Log.w("",Integer.toString(toDecode.length));
            if(getECT().length()!=24){
                Toast.makeText(this, "Select another 24 bits word", Toast.LENGTH_SHORT).show();
            }else{
                setEDT(golay24Decoder.decodeWord(toDecode));
                Toast.makeText(this, "Decrypting...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**Adapta la entrada al decoder, g23 23 bits, g24 24 bits*/
    public int[] transformData(String input, int mode){
        System.out.println("INPUT: "+input);

        if(mode==24 && input.length()==23){
            Toast.makeText(this, "Select a 24 bits word", Toast.LENGTH_SHORT).show();
            return new int[]{-1};
        }else{
            int[] output= new int[24]; //Siempre 24
            Log.w("Long Output: ",Integer.toString(output.length));

            int temp_value;
            int count_ones=0;

            for(int i=0; i<mode;i++){
                temp_value=Character.getNumericValue(input.charAt(i));
                output[i]=temp_value;
                if(temp_value==1){
                    //Se ha encontrado un 1
                    count_ones++;
                }
                System.out.println("Added "+temp_value);
            }

            //Sólo si 23
            if(mode==23){
                Log.w("","Mode 23 with "+count_ones+" ones");
                if(count_ones%2==0){
                    //Si hay 1 pares, añadimos 0
                    output[23]=0;
                }else{
                    output[23]=1;
                }
            }

            Log.e("","Input: "+input+", Output: ");
            for(int i=0;i<output.length;i++){
                System.out.print(output[i]);
            }
            System.out.println(" "+output[23]);
            return output;
        }
    }
}