package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import pt.novaleaf.www.maisverde.OcorrenciaFragment.OnListFragmentInteractionListener;

import java.util.HashMap;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ocorrencia} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * Adaptador para o recycler view
 */
public class MyOcorrenciaRecyclerViewAdapter extends RecyclerView.Adapter<MyOcorrenciaRecyclerViewAdapter.ViewHolder> {

    private final List<Ocorrencia> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyOcorrenciaRecyclerViewAdapter(List<Ocorrencia> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ocorrencia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mImageReport.setImageResource(mValues.get(position).getImgId());
        holder.titulo.setText(mValues.get(position).getTitulo());

        holder.mLinearGosto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    if (!mValues.get(position).isLiked()) {
                        holder.mTextGosto.setTextColor(0xFF429844);
                        holder.mImageGosto.setImageResource(R.drawable.ic_favorite_green_24dp);
                    }
                    else {
                        holder.mTextGosto.setTextColor(Color.BLACK);
                        holder.mImageGosto.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                    mValues.get(position).like();
                    mListener.onLikeInteraction(mValues.get(position));
                }
            }
        });

        holder.mLinearComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCommentInteraction(mValues.get(position));
                }
            }
        });

        holder.mLinearFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.

                    if (!mValues.get(position).isFavorito()) {
                        holder.mTextFavorito.setTextColor(0xFF429844);
                        holder.mImageFavorito.setImageResource(R.drawable.ic_star_green_24dp);
                    }
                    else {
                        holder.mTextFavorito.setTextColor(Color.BLACK);
                        holder.mImageFavorito.setImageResource(R.drawable.ic_star_border_black_24dp);

                    }
                    mValues.get(position).favorito();
                    mListener.onFavoritoInteraction(mValues.get(position));

                }
            }
        });

        holder.mLinearInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCommentInteraction(mValues.get(position));
                }
            }
        });

        holder.mImageReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onImagemInteraction(mValues.get(position));
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageReport;
        public TextView titulo;
        public RelativeLayout mRelative;
        public LinearLayout mLinearGosto;
        public LinearLayout mLinearFavorito;
        public LinearLayout mLinearComentar;
        public LinearLayout mLinearInfo;
        public TextView mTextGosto;
        public TextView mTextFavorito;
        public ImageView mImageFavorito;
        public ImageView mImageGosto;

        public ViewHolder(View v){
            super(v);
            mImageReport = (ImageView) v.findViewById(R.id.imageReport);
            titulo = (TextView) v.findViewById(R.id.tituloReport);
            mRelative = (RelativeLayout) v.findViewById(R.id.relative);
            mLinearGosto = (LinearLayout) v.findViewById(R.id.linearGosto);
            mLinearFavorito = (LinearLayout) v.findViewById(R.id.linearFavorito);
            mLinearComentar = (LinearLayout) v.findViewById(R.id.linearComentar);
            mLinearInfo = (LinearLayout) v.findViewById(R.id.linearInfo);
            mTextGosto = (TextView) v.findViewById(R.id.textGosto);
            mTextFavorito = (TextView) v.findViewById(R.id.textFavorito);
            mImageFavorito = (ImageView) v.findViewById(R.id.imageFavorito);
            mImageGosto = (ImageView) v.findViewById(R.id.imageGosto);
        }
    }
}
