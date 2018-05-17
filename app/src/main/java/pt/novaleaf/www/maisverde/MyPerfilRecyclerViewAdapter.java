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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import static pt.novaleaf.www.maisverde.AlterarDadosActivity.adapter;
import static pt.novaleaf.www.maisverde.AlterarDadosActivity.arrayList;
import static pt.novaleaf.www.maisverde.AlterarDadosActivity.changed;
import static pt.novaleaf.www.maisverde.AlterarDadosActivity.mRecyclerViewPerfil;
import static pt.novaleaf.www.maisverde.AlterarDadosActivity.sharedPreferences;


/**
 * Created by Hugo Mochão on 16/05/2018.
 */

public class MyPerfilRecyclerViewAdapter extends RecyclerView.Adapter{

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

        ((PerfilHolder )holder).bind(perfilItem);

    }

    private class PerfilHolder extends RecyclerView.ViewHolder {
        TextView titulo, caracterizacao;
        LinearLayout layout;

        PerfilHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.linearPessoal);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
            caracterizacao = (TextView) itemView.findViewById(R.id.caracterizacao);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int itemPosition = mRecyclerViewPerfil.getChildLayoutPosition(view);
                    PerfilItem item = mPerfilList.get(itemPosition);

                    switch (itemPosition) {
                        case 1:
                            setDialog("Alterar o Email", "Introduza o novo endereço de email", 1);
                            return true;
                        case 4:
                            setDialog("Morada", "Introduza a sua morada", 4);
                            return true;
                        case 5:
                            setDialog("Morada Complementar", "Introduza a sua morada complementar", 5);
                            return true;
                        case 6:
                            setDialog("Localidade", "Introduza a sua localidade", 6);
                            return true;
                        case 7:
                            setDialog("Código Postal", "Introduza o seu código postal", 7);
                            return true;
                        case 8:
                            setDialog("Telefone", "Introduza o seu número de telefone", 8);
                            return true;
                        case 9:
                            setDialog("Telemovel", "Introduza o seu número de telemovel", 9);
                            return true;
                        default:
                            return false;
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = mRecyclerViewPerfil.getChildLayoutPosition(view);
                    PerfilItem item = mPerfilList.get(itemPosition);

                    switch (itemPosition){
                        case 10:
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

            // Format the stored timestamp into a readable String using method.
            caracterizacao.setText(perfilItem.getDescricao());
        }

        public void setDialog(String titulo, String mensagem, final int index) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle(titulo);
            alertDialog.setMessage(mensagem);

            final EditText input = new EditText(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            String tipo="";
            String nomeCampo="";
            switch (index){
                case 1:
                    tipo = "email";
                    nomeCampo = "Email: ";
                    break;
                case 4:
                    tipo = "firstaddress";
                    nomeCampo = "Morada principal: ";
                    break;
                case 5:
                    tipo = "complementaryaddress";
                    nomeCampo = "Morada complementar: ";
                    break;
                case 6:
                    tipo = "locality";
                    nomeCampo = "Localidade: ";
                    break;
                case 7:
                    tipo = "postalcode";
                    nomeCampo = "Código Postal: ";
                    break;
                case 8:
                    tipo = "telephone";
                    nomeCampo = "Telefone: ";
                    break;
                case 9:
                    tipo = "mobile_phone";
                    nomeCampo = "Telemovel: ";
                    break;
                default:
            }

            Log.i("tipo", tipo);
            final String finalTipo = tipo;
            final String finalNomeCampo = nomeCampo;
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            changed = true;
                            String campo = input.getText().toString();
                            Log.i("finaltipo", finalTipo);
                            arrayList.remove(index);
                            arrayList.add(index, new PerfilItem(finalNomeCampo, campo));
                            adapter.notifyDataSetChanged();
                            SharedPreferences.Editor ed = sharedPreferences.edit();
                            ed.putString(finalTipo, campo);
                            ed.commit();
                        }
                    });

            alertDialog.setNegativeButton("Cancelar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }


    }

}
