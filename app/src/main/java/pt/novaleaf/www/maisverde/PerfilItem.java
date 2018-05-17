package pt.novaleaf.www.maisverde;

/**
 * Created by Hugo Mochão on 16/05/2018.
 */

public class PerfilItem {

    private String campo;
    private String descricao;

    public PerfilItem(String campo, String descricao){
        this.campo = campo;
        this.descricao = descricao;
    }

    public String getCampo() {
        return campo;
    }

    public String getDescricao() {
        return descricao;
    }
}
