package org.uvigo.dm2_juego_del_ajedrez.ui;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.uvigo.dm2_juego_del_ajedrez.R;
import org.uvigo.dm2_juego_del_ajedrez.core.Profile;

import java.util.List;

/**
 * Clase adaptador para manejar los eventos sobre el ListView de los perfiles.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class ProfileArrayAdapter  extends ArrayAdapter<Profile> {

    /**
     * Construye e inicializa un adaptador para los perfiles
     *
     * @param context contexto actual
     * @param objects objetos para representar en el ListView
     */
    public ProfileArrayAdapter(@NonNull Context context, List<Profile> objects) {
        super(context, 0, objects);
    }

    class ViewHolder {
        LinearLayout linearLayoutProfile;
        LinearLayout backButtonLayoutSpace;
        LinearLayout backButtonLayout;
        ImageButton backButton;
        TextView textViewName;
        TextView textViewPoints;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder= null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.profile_listview, null);

            viewHolder.backButton = convertView.findViewById(R.id.backButton);
            viewHolder.backButtonLayoutSpace = convertView.findViewById(R.id.backButtonLayoutSpace);
            viewHolder.backButtonLayout = convertView.findViewById(R.id.backButtonLayout);
            viewHolder.linearLayoutProfile = convertView.findViewById(R.id.linearLayoutProfile);
            viewHolder.textViewName =convertView.findViewById(R.id.textViewName);
            viewHolder.textViewPoints = convertView.findViewById(R.id.points_counter);

            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.textViewName.setText(getItem(position).getName());
        viewHolder.textViewPoints.setText(getItem(position).getPoints());

        return convertView;
    }
}