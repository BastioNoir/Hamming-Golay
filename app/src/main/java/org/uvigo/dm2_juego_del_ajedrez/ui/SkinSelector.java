package org.uvigo.dm2_juego_del_ajedrez.ui;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.dm2_juego_del_ajedrez.R;

/**
 * Actividad que muestra las dos opciones para acceder a las listas de skins: skins de tablero y
 * skins de piezas.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class SkinSelector extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skin_selector);

        Button boardSkins= (Button)findViewById(R.id.boardSkinSelection);
        Button pieceSkins= (Button)findViewById(R.id.pieceSkinSelection);

        boardSkins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subActividad = new Intent( SkinSelector.this, SkinsActivity.class );
                subActividad.putExtra("mode",true); //Enviamos el modo de juego
                activityResultLauncher.launch(subActividad);
            }
        });

        pieceSkins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subActividad = new Intent( SkinSelector.this, SkinsActivity.class );
                subActividad.putExtra("mode",false); //Enviamos el modo de juego
                activityResultLauncher.launch(subActividad);
            }
        });

        ActivityResultContract<Intent, ActivityResult> contract = new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback = new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
            }
        };

        ImageButton backButton= (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinSelector.this.setResult( MainActivity.RESULT_CANCELED );
                SkinSelector.this.finish();
            }
        });

        this.activityResultLauncher = this.registerForActivityResult(contract, callback);
    }
}
