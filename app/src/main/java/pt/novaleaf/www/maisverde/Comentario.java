package pt.novaleaf.www.maisverde;

/**
 * Created by Hugo Mochão on 16/05/2018.
 */

public class Comentario {

    private String mensagem;
    private String autor;
    private String tempo;
    private int origem;

    public Comentario(String mensagem, String autor, String tempo, int origem){
        this.mensagem = mensagem;
        this.autor = autor;
        this.tempo =tempo;
        this.origem = origem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getAutor() {
        return autor;
    }

    public String getTempo() {
        return tempo;
    }

    public int getOrigem() {
        return origem;
    }
}
