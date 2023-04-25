package org.uvigo.hamminggolay.ui;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.uvigo.hamminggolay.R;
import org.uvigo.hamminggolay.core.Profile;
import org.uvigo.hamminggolay.core.Skin;
import org.uvigo.hamminggolay.core.Uploader;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase adaptador para manejar los eventos sobre el ListView de las skins.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class SkinArrayAdapter extends ArrayAdapter<Skin> {
    boolean mode;
    ArrayList<Profile> profiles;
    Profile selectedProfile= MainActivity.getSelectedProfile();

    /**
     * Construye e inicializa un adaptador para las skins
     *
     * @param context   contexto actual
     * @param objects   objetos para representar en el ListView
     * @param mode      tipo de skin: "true" para una skin de tablero, "false" para una skin de
     *                  pieza
     */
    public SkinArrayAdapter(@NonNull Context context, List<Skin> objects, boolean mode) {
        super(context, 0, objects);
        this.mode=mode;
        profiles=Uploader.loadProfiles(getContext());

    }

    class ViewHolder {
        LinearLayout linearLayoutSkins;
        LinearLayout backButtonLayoutSpace;
        LinearLayout backButtonLayout;
        ImageButton backButton;
        ImageView iv_SkinPhoto;
        TextView textViewName;
        CheckBox useSkin;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder= null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.skin_listview, null);

            viewHolder.backButton = convertView.findViewById(R.id.backButton);
            viewHolder.backButtonLayoutSpace = convertView.findViewById(R.id.backButtonLayoutSpace);
            viewHolder.backButtonLayout = convertView.findViewById(R.id.backButtonLayout);
            viewHolder.linearLayoutSkins = convertView.findViewById(R.id.linearLayoutSkins);
            viewHolder.iv_SkinPhoto = convertView.findViewById(R.id.iv_SkinPhoto);
            viewHolder.textViewName =convertView.findViewById(R.id.textViewName);
            viewHolder.useSkin= convertView.findViewById(R.id.useSkin);

            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.textViewName.setText(getItem(position).getName());

        Skin skin= getItem(position);

        viewHolder.iv_SkinPhoto.setImageBitmap(Uploader.bitmapFromAssets(getContext(),skin.getImagePath()+".png"));

        viewHolder.useSkin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String imagePath=getItem(position).getImagePath();

                if(mode){
                    selectedProfile.setSkinBoardName(imagePath);
                    Toast.makeText(SkinArrayAdapter.super.getContext(), "El perfil "+selectedProfile.getName()+" ha cambiado su skin de tablero a "+imagePath, Toast.LENGTH_SHORT).show();
                }else{
                    selectedProfile.setSkinPieceName(imagePath);
                    Toast.makeText(SkinArrayAdapter.super.getContext(), "El perfil "+selectedProfile.getName()+" ha cambiado su skin de pieza a "+imagePath, Toast.LENGTH_SHORT).show();
                }
                updateProfiles();
                notifyDataSetChanged();
            }
        });

        notifyDataSetChanged();
        return convertView;
    }

    /**
     * Actualiza la lista de skins
     */
    public void updateProfiles(){

        ArrayList<Profile> tempProfiles= profiles;
        Profile tempSP=null;

        for(Profile pr: profiles){
            if(selectedProfile.getName().equals(pr.getName())){
                //Quitamos el selected profile
                tempSP=pr;
            }
        }

        if(tempSP!=null){
            tempProfiles.remove(tempSP);
            tempProfiles.add(selectedProfile);
        }

        Uploader.saveProfiles(getContext(),tempProfiles);
        Uploader.changeGlobalSelectedProfile(SkinArrayAdapter.super.getContext(),selectedProfile);
    }
}
