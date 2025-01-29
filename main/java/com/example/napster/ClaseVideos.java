package com.example.napster;

import java.io.File;

public class ClaseVideos {
    private String titulo_cpr; // Título de la canción
    private byte[] caratula_cpr;
    private File archivo_cpr;// Carátula del álbum

    public ClaseVideos(String titulo_cpr, byte[] caratula_cpr, File archivo_cpr) {
        this.titulo_cpr = titulo_cpr;
        this.caratula_cpr = caratula_cpr;
        this.archivo_cpr = archivo_cpr;
    }

    public String getTitulo() {
        return titulo_cpr;
    }

    public byte[] getCaratula() {
        return caratula_cpr;
    }
    public File getArchivo() {
        return archivo_cpr;
    }

}
