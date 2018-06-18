package pt.novaleaf.www.maisverde;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hugo Mochão on 27/04/2018.
 */

public class Ocorrencia {

    String titulo;
    int imgId;
    boolean liked;
    boolean favorito;
    int risco;
    String hora;

    public static final List<Ocorrencia> items = new ArrayList<>();

    public Ocorrencia(){

    }

    public Ocorrencia(String tit, int img, int risco, String hora){
        titulo = tit;
        imgId = img;
        favorito = false;
        liked = false;
        this.risco = risco;
        this.hora = hora;
    }

    public String getTitulo(){
        return titulo;
    }

    public int getImgId(){
        return imgId;
    }

    public int getRisco() {
        return risco;
    }

    public String getHora() {
        return hora;
    }

    public void like(){
        liked = !liked;
    }

    public void favorito(){
        favorito = !favorito;
    }

    public boolean isFavorito(){
        return favorito;
    }

    public boolean isLiked() {
        return liked;
    }
}
