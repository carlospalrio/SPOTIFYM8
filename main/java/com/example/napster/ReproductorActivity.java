package com.example.napster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReproductorActivity extends AppCompatActivity {

    private static final String TAG = "ReproductorActivity";
    private MediaPlayer mediaPlayer_cpr;
    private TextView tituloCancion_cpr;
    private ImageButton botonPlay_cpr, botonReiniciar_cpr, botonPausar_cpr, botonSubirVolumen_cpr, botonBajarVolumen_cpr;
    private Button botonVolver_cpr;
    private ProgressBar barraProgreso_cpr;
    private ImageView imagenCaratula_cpr;

    private String nombreArchivo_cpr; // Nombre de la canción seleccionada
    private File archivoCancion_cpr; // Archivo de la canción en la carpeta Downloads

    private MediaObserver observador = null;
    private AudioManager audioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        // Inicializar vistas
        tituloCancion_cpr = findViewById(R.id.tituloCancion);
        botonPlay_cpr = findViewById(R.id.botonPlay);
        botonReiniciar_cpr = findViewById(R.id.botonReiniciar);
        botonPausar_cpr = findViewById(R.id.botonPausar);
        botonVolver_cpr = findViewById(R.id.botonVolver);
        barraProgreso_cpr= findViewById(R.id.barraProgreso);
        botonSubirVolumen_cpr = findViewById(R.id.botonSubirVolumen);
        botonBajarVolumen_cpr = findViewById(R.id.botonBajarVolumen);
        imagenCaratula_cpr = findViewById(R.id.imagenCaratula);



        // Inicializar AudioManager
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Obtener el nombre del archivo desde el Intent
        nombreArchivo_cpr = getIntent().getStringExtra("NOMBRE_CANCION");
        byte[] caratulaCancion = getIntent().getByteArrayExtra("CARATULA_CANCION");

        if (nombreArchivo_cpr != null) {
            tituloCancion_cpr.setText(nombreArchivo_cpr); // Mostrar el nombre de la canción
        } else {
            Toast.makeText(this, "No se recibió el nombre de la canción", Toast.LENGTH_LONG).show();
            Log.e("asdasdasdasdasda", "El nombre del archivo no fue proporcionado en el Intent.");
            finish(); // Finalizar la actividad si no hay nombre de archivo
            return;
        }

        // Obtener la carátula de la canción
        if (caratulaCancion != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(caratulaCancion, 0, caratulaCancion.length);
            imagenCaratula_cpr.setImageBitmap(bitmap);  // Mostrar la carátula
        } else {
            imagenCaratula_cpr.setImageResource(R.drawable.default_album_art);  // Imagen predeterminada si no hay carátula
        }
        configurarBotones(); // Configurar acciones de los botones
    }

    private void configurarBotones() {
        // Botón Play
        botonPlay_cpr.setOnClickListener(v -> {
            if (mediaPlayer_cpr != null) {
                if (!mediaPlayer_cpr.isPlaying()) {
                    mediaPlayer_cpr.start(); // Reanudar la reproducción
                    Toast.makeText(this, "Reproducción reanudada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "La canción ya está en reproducción", Toast.LENGTH_SHORT).show();
                }
            } else {
                reproducirCancion(); // Reproducir desde el principio
            }
        });

        // Botón Subir Volumen
        botonSubirVolumen_cpr.setOnClickListener(v -> {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (currentVolume < maxVolume) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + 1, AudioManager.FLAG_SHOW_UI);
            } else {
                Toast.makeText(this, "Volumen ya está al máximo", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Bajar Volumen
        botonBajarVolumen_cpr.setOnClickListener(v -> {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (currentVolume > 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume - 1, AudioManager.FLAG_SHOW_UI);
            } else {
                Toast.makeText(this, "Volumen ya está en el mínimo", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Reiniciar
        botonReiniciar_cpr.setOnClickListener(v -> {
            if (mediaPlayer_cpr != null) {
                mediaPlayer_cpr.seekTo(0); // Volver al inicio de la canción
                mediaPlayer_cpr.start(); // Iniciar reproducción desde el principio
                Toast.makeText(this, "Canción reiniciada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Reproduce la canción antes de reiniciarla", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Pausar
        botonPausar_cpr.setOnClickListener(v -> {
            if (mediaPlayer_cpr != null && mediaPlayer_cpr.isPlaying()) {
                mediaPlayer_cpr.pause(); // Pausar la reproducción
                Toast.makeText(this, "Canción pausada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No hay ninguna canción en reproducción", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Volver
        botonVolver_cpr.setOnClickListener(v -> {
            detenerMediaPlayer(); // Detener y liberar el MediaPlayer
            finish(); // Finalizar la actividad
        });

    }




private void reproducirCancion() {
    archivoCancion_cpr = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nombreArchivo_cpr);

    if (!nombreArchivo_cpr.endsWith(".mp3")) {
        archivoCancion_cpr = new File(archivoCancion_cpr.getAbsolutePath() + ".mp3");
    }

    if (!archivoCancion_cpr.exists()) {
        Toast.makeText(this, "El archivo no existe: " + archivoCancion_cpr.getAbsolutePath(), Toast.LENGTH_LONG).show();
        Log.e(TAG, "Archivo no encontrado: " + archivoCancion_cpr.getAbsolutePath());
        return;
    }


    if (mediaPlayer_cpr != null && mediaPlayer_cpr.isPlaying()) {
            Toast.makeText(this, "La canción ya está en reproducción", Toast.LENGTH_SHORT).show();
            return;
        }

        detenerMediaPlayer(); // Liberar recursos si ya había un MediaPlayer

        Uri uri = Uri.fromFile(archivoCancion_cpr);
        try {
            mediaPlayer_cpr = MediaPlayer.create(this, uri); // Inicializar el MediaPlayer
            mediaPlayer_cpr.start(); // Iniciar reproducción
            Toast.makeText(this, "Reproduciendo canción", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Reproduciendo canción: " + archivoCancion_cpr.getAbsolutePath());

            iniciarBarraProgreso(); // Iniciar la barra de progreso

            // Configurar evento cuando la canción finalice
            mediaPlayer_cpr.setOnCompletionListener(mp -> {
                detenerBarraProgreso(); // Detener el hilo de la barra de progreso
                Toast.makeText(this, "Canción finalizada", Toast.LENGTH_SHORT).show();
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error al reproducir la canción", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error al inicializar MediaPlayer", e);
        }
    }

    private void iniciarBarraProgreso() {
        barraProgreso_cpr.setMax(100); // Asegúrate de que la barra de progreso tenga un valor máximo
        detenerBarraProgreso(); // Detener cualquier hilo anterior
        observador = new MediaObserver();
        new Thread(observador).start();
    }

    private void detenerMediaPlayer() {
        if (observador != null) {
            observador.stop(); // Detener el hilo de la barra de progreso
        }
        if (mediaPlayer_cpr != null) {
            mediaPlayer_cpr.stop();
            mediaPlayer_cpr.release();
            mediaPlayer_cpr = null;
        }
    }

    private void detenerBarraProgreso() {
        if (observador != null) {
            observador.stop();
            observador = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerMediaPlayer(); // Detener y liberar recursos del MediaPlayer
    }

    // Thread encargado de actualizar la barra de progreso
    private class MediaObserver implements Runnable {
        private final AtomicBoolean stop = new AtomicBoolean(false);

        public void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while (!stop.get()) {
                if (mediaPlayer_cpr != null) {
                    try {
                        if (mediaPlayer_cpr.isPlaying()) {
                            barraProgreso_cpr.setProgress((int) ((double) mediaPlayer_cpr.getCurrentPosition() / mediaPlayer_cpr.getDuration() * 100));
                        }
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "MediaPlayer no está en un estado válido: " + e.getMessage());
                        stop();
                    }
                }
                try {
                    Thread.sleep(1000); // Actualizar cada segundo
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
