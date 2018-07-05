package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static pt.novaleaf.www.maisverde.PerfilActivity.adapter;
import static pt.novaleaf.www.maisverde.PerfilActivity.arrayList;
import static pt.novaleaf.www.maisverde.PerfilActivity.changed;
import static pt.novaleaf.www.maisverde.PerfilActivity.mRecyclerViewPerfil;


/**
 * Created by Hugo Mochão on 16/05/2018.
 */

public class MyPerfilRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<PerfilItem> mPerfilList;

    public MyPerfilRecyclerViewAdapter(Context context, List<PerfilItem> perfil) {
        mContext = context;
        mPerfilList = perfil;
    }


    @Override
    public int getItemCount() {
        return mPerfilList.size();
    }


    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_area_pessoal, parent, false);
        return new MyPerfilRecyclerViewAdapter.PerfilHolder(view);


    }


    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PerfilItem perfilItem = mPerfilList.get(position);

        ((PerfilHolder) holder).bind(perfilItem);

    }

    private class PerfilHolder extends RecyclerView.ViewHolder {
        TextView titulo, caracterizacao;
        LinearLayout layout;

        PerfilHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.linearPessoal);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
            caracterizacao = (TextView) itemView.findViewById(R.id.caracterizacao);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = mRecyclerViewPerfil.getChildLayoutPosition(view);
                    PerfilItem item = mPerfilList.get(itemPosition);

                    switch (itemPosition) {
                        case 9:
                            AlertDialog.Builder changepass = new AlertDialog.Builder(mContext);
                            changepass.setTitle("Mudar password");
                            changepass
                                    .setMessage("Quer mudar a password?")
                                    .setCancelable(true)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(mContext, AlterarPassActivity.class);
                                            mContext.startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                            AlertDialog alertDialog = changepass.create();
                            alertDialog.show();
                            return;
                        default:
                            return;
                    }


                }
            });


        }

        public void bind(final PerfilItem perfilItem) {
            titulo.setText(perfilItem.getCampo());

            caracterizacao.setText(perfilItem.getDescricao());
        }


    }

}
