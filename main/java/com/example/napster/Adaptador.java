
package com.example.napster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Adaptador extends BaseAdapter {
    ArrayList<ClaseVideos> claseVideos_cpr;
    Context c_cpr;

    public Adaptador(ArrayList<ClaseVideos> claseVideos_cpr, Context c_cpr) {
        this.c_cpr = c_cpr;
        this.claseVideos_cpr = claseVideos_cpr;
    }

    @Override
    public int getCount() {
        return claseVideos_cpr.size();
    }

    @Override
    public Object getItem(int i) {
        return claseVideos_cpr.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) c_cpr.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vista_elemento = inflater.inflate(R.layout.elementolista, viewGroup, false);

        // Asignar el título de la canción
        TextView texto = vista_elemento.findViewById(R.id.textoVista);
        texto.setText(claseVideos_cpr.get(i).getTitulo());

        // Asignar la carátula
        ImageView imagenCaratula = vista_elemento.findViewById(R.id.imagenCaratula);

        // Verificar si la carátula está disponible
        byte[] caratula = claseVideos_cpr.get(i).getCaratula();
        if (caratula != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(caratula, 0, caratula.length);
            imagenCaratula.setImageBitmap(bitmap);  // Mostrar la carátula en el ImageView
        } else {
            imagenCaratula.setImageResource(R.drawable.default_album_art);  // Imagen predeterminada si no hay carátula
        }

        // Botón de reproducir
        Button bPlay = vista_elemento.findViewById(R.id.play);
        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = claseVideos_cpr.get(i).getTitulo();
                byte[] caratula = claseVideos_cpr.get(i).getCaratula();
                Log.d("Adaptador", "Título: " + titulo);
                Log.d("Adaptador", "Carátula: " + (caratula != null ? "Existe" : "Es nula"));

                Intent intent = new Intent(c_cpr, ReproductorActivity.class);
                intent.putExtra("NOMBRE_CANCION", claseVideos_cpr.get(i).getArchivo().getName());
                intent.putExtra("CARATULA_CANCION", claseVideos_cpr.get(i).getCaratula());
                c_cpr.startActivity(intent);

            }
        });

        return vista_elemento;
    }

}