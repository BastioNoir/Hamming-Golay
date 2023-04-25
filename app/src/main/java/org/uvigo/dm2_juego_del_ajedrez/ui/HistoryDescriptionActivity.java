package org.uvigo.dm2_juego_del_ajedrez.ui;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.dm2_juego_del_ajedrez.R;

/**
 * Actividad que muestra los movimientos almacenados en un historial.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class HistoryDescriptionActivity extends AppCompatActivity {
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showhistory);

        String name= (String)getIntent().getSerializableExtra("name");
        String description= (String)getIntent().getSerializableExtra("description");

        //BACKBUTTON
        backButton = this.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryDescriptionActivity.this.setResult( HistoryActivity.RESULT_CANCELED );
                HistoryDescriptionActivity.this.finish();
            }
        });

        TextView historyName= findViewById(R.id.textViewNameHistory);
        TextView historyDescription = findViewById(R.id.textViewDescription);

        historyName.setText(name);
        historyDescription.setText(description);
    }
}