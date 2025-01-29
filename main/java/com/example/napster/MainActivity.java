package com.example.napster;



import android.Manifest;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int READ_MEDIA = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES
    };

    private ListView listaCanciones_cpr;
    private ArrayList<ClaseVideos> canciones_cpr; // Lista de canciones


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Solicitar permisos de almacenamiento
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, READ_MEDIA);
        } else {
            // Android 12 y versiones anteriores
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, READ_MEDIA);
        }

        listaCanciones_cpr = findViewById(R.id.listaCanciones);
        canciones_cpr = new ArrayList<>();

        // Configurar clic en las canciones para abrir el reproductor
        listaCanciones_cpr.setOnItemClickListener((parent, view, position, id) -> {
            ClaseVideos cancionSeleccionada = canciones_cpr.get(position);

            Intent intent = new Intent(MainActivity.this, ReproductorActivity.class);

            // Pasar el título
            if (cancionSeleccionada.getTitulo() != null) {
                intent.putExtra("TITULO_CANCION", cancionSeleccionada.getTitulo());
            } else {
                intent.putExtra("TITULO_CANCION", "Sin título");
            }

            // Pasar la carátula
            intent.putExtra("CARATULA_CANCION", cancionSeleccionada.getCaratula());

            startActivity(intent);
        });

    }

    private void cargarCancionesDesdeDownload() {
        canciones_cpr.clear();

        File carpetaDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (carpetaDownload.exists() && carpetaDownload.isDirectory()) {
            File[] archivos = carpetaDownload.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.getName().toLowerCase().endsWith(".mp3")) {
                        // Leer los metadatos del MP3
                        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                        try {
                            metadataRetriever.setDataSource(archivo.getAbsolutePath());

                            // Obtener título
                            String titulo = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            if (titulo == null || titulo.isEmpty()) {
                                titulo = archivo.getName().replace(".mp3", ""); // Si no hay título, usar el nombre del archivo
                            }

                            // Obtener carátula
                            byte[] caratula = metadataRetriever.getEmbeddedPicture();

                            // Agregar canción a la lista
                            canciones_cpr.add(new ClaseVideos(titulo, caratula, archivo));
                        } catch (Exception e) {
                            Log.e("MainActivity", "Error al leer metadatos del archivo: " + archivo.getName(), e);
                        }
                    }
                }
            }
        }

        if (canciones_cpr.isEmpty()) {
            Toast.makeText(this, "No se encontraron canciones en la carpeta Download", Toast.LENGTH_LONG).show();
        } else {
            Adaptador adaptador = new Adaptador(canciones_cpr, this);
            listaCanciones_cpr.setAdapter(adaptador);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_MEDIA) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                cargarCancionesDesdeDownload(); // Cargar canciones automáticamente
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
