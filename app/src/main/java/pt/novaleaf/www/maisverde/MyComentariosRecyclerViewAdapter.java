package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static pt.novaleaf.www.maisverde.ComentariosActivity.comentarios;
import static pt.novaleaf.www.maisverde.ComentariosActivity.mMessageAdapter;

/**
 * Created by Hugo Mochão on 16/05/2018.
 */

public class MyComentariosRecyclerViewAdapter extends RecyclerView.Adapter {

        private static final int VIEW_TYPE_MESSAGE_SENT = 1;
        private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

        private Context mContext;
        private List<Comentario> mMessageList;

        public MyComentariosRecyclerViewAdapter(Context context, List<Comentario> messageList) {
            mContext = context;
            mMessageList = messageList;
        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }

        // Determines the appropriate ViewType according to the sender of the message.
        @Override
        public int getItemViewType(int position) {
            Comentario comentario = mMessageList.get(position);
            Log.i("ORIGEM", comentario.getOrigem() + " posiçao");

            if (comentario.getOrigem()==1) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

        // Inflates the appropriate layout according to the ViewType.
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_comentarioenviado, parent, false);
                return new SentMessageHolder(view);
            } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_comentarionormal, parent, false);
                return new ReceivedMessageHolder(view);
            }

            return null;
        }

        // Passes the message object to a ViewHolder so that the contents can be bound to UI.
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Comentario comentario = mMessageList.get(position);


            switch (holder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SentMessageHolder) holder).bind(comentario);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder) holder).bind(comentario);
            }
        }

        class SentMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText;

            SentMessageHolder(View itemView) {
                super(itemView);

                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);



            }

            void bind(final Comentario comentario) {
                messageText.setText(comentario.getMessage());
                messageText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                        alert.setTitle("Apagar Comentário");
                        alert
                                .setMessage("Quer apagar o comentário?")
                                .setCancelable(true)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    comentarios.remove(comentario);
                                    mMessageAdapter.notifyDataSetChanged();

                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                        return true;
                    }
                });

                // Format the stored timestamp into a readable String using method.
                timeText.setText(comentario.getCreation_date()+"");
            }

        }

        private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText, nameText;
            ImageView profileImage;

            ReceivedMessageHolder(View itemView) {
                super(itemView);

                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);
                nameText = (TextView) itemView.findViewById(R.id.text_message_name);
                profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            }

            void bind(Comentario comentario) {
                messageText.setText(comentario.getMessage());

                // Format the stored timestamp into a readable String using method.
                timeText.setText(comentario.getCreation_date()+"");

                nameText.setText(comentario.getAuthor());

            }
        }

}

