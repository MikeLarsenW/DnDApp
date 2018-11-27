package com.example.mike.dnd_mobile_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import java.util.ArrayList;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    public ArrayList<String> charNameList;
    Context context;
    DnDCharacter storedCharacter;


    public CharacterAdapter(Context context, ArrayList<String> charNames) {
       this.context = context;
       charNameList = charNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View charView = inflater.inflate(R.layout.character_list_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(charView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final String character = charNameList.get(i);
        viewHolder.viewName.setText(character);
        Log.d(TAG, "onBindViewHolder: called");

        Gson gson = new Gson();
        String json = viewHolder.sharedpreferences.getString(character, "");
        storedCharacter = gson.fromJson(json, DnDCharacter.class);

        String previouslyEncodedImage = storedCharacter.getCharImage();
        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            viewHolder.viewImage.setImageBitmap(bitmap);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ArrayListPosition", Integer.toString(i));
                String character = charNameList.get(i);
                Log.d("in array list based on clicked", character);

                Intent intent = new Intent(v.getContext(), CharacterView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("key", character);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return charNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView viewName;
        LinearLayout viewLayout;
        ImageView viewImage;
        SharedPreferences sharedpreferences;
        public static final String userPREFERENCES = "CharsCreated";

        public ViewHolder(View itemView) {
            super(itemView);
            viewName = itemView.findViewById(R.id.character);
            viewLayout = itemView.findViewById(R.id.charListLayout);
            viewImage = itemView.findViewById(R.id.charImage);
            sharedpreferences = context.getSharedPreferences(userPREFERENCES, Context.MODE_PRIVATE);
        }
    }
}
