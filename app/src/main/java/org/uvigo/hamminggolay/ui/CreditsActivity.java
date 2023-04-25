package org.uvigo.hamminggolay.ui;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import org.uvigo.hamminggolay.R;

/**
 * Actividad que muestra los creditos de la aplicacion.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class CreditsActivity extends AppCompatActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        backButton = this.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreditsActivity.this.setResult( MainActivity.RESULT_CANCELED );
                CreditsActivity.this.finish();
            }
        });
    }
}