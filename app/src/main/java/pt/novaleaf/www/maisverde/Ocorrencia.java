package pt.novaleaf.www.maisverde;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hugo Mochão on 27/04/2018.
 */

public class Ocorrencia {

    String titulo;
    int imgId;

    public static final List<Ocorrencia> items = new ArrayList<>();

    public Ocorrencia(String tit, int img){
        titulo = tit;
        imgId = img;
    }

    public String getTitulo(){
        return titulo;
    }

    public int getImgId(){
        return imgId;
    }
}
