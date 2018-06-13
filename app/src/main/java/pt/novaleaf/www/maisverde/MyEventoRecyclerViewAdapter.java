package pt.novaleaf.www.maisverde;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class MyEventoRecyclerViewAdapter extends RecyclerView.Adapter<MyEventoRecyclerViewAdapter.ViewHolder>{
    private final List<Evento> mValues;
    private final EventoFragment.OnListFragmentInteractionListener mListener;

    public MyEventoRecyclerViewAdapter(List<Evento> items, EventoFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_evento, parent, false);
        return new MyEventoRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyEventoRecyclerViewAdapter.ViewHolder holder, final int position) {
        //holder.mImageReport.setImageResource(mValues.get(position).getImgId());
        //holder.titulo.setText(mValues.get(position).getName());

        holder.mLinearGosto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    if (!mValues.get(position).isIr()) {
                        holder.mTextGosto.setTextColor(0xFF429844);
                        holder.mImageGosto.setImageResource(R.drawable.ic_favorite_green_24dp);
                    }
                    else {
                        holder.mTextGosto.setTextColor(Color.BLACK);
                        holder.mImageGosto.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                    mValues.get(position).setIr();
                    mListener.onLikeInteraction(mValues.get(position));
                }
            }
        });


        holder.mLinearFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.

                    if (!mValues.get(position).isInteresse()) {
                        holder.mTextFavorito.setTextColor(0xFF429844);
                        holder.mImageFavorito.setImageResource(R.drawable.ic_star_green_24dp);
                    }
                    else {
                        holder.mTextFavorito.setTextColor(Color.BLACK);
                        holder.mImageFavorito.setImageResource(R.drawable.ic_star_border_black_24dp);

                    }
                    mValues.get(position).setInteresse();
                    mListener.onFavoritoInteraction(mValues.get(position));

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
        //public TextView titulo;
        //public RelativeLayout mRelative;
        public LinearLayout mLinearGosto;
        public LinearLayout mLinearFavorito;
        public LinearLayout mLinearInfo;
        public TextView mTextGosto;
        public TextView mTextFavorito;
        public ImageView mImageFavorito;
        public ImageView mImageGosto;

        public ViewHolder(View v){
            super(v);
            mImageReport = (ImageView) v.findViewById(R.id.imageReport);
            //titulo = (TextView) v.findViewById(R.id.tituloReport);
            //mRelative = (RelativeLayout) v.findViewById(R.id.relative);
            mLinearGosto = (LinearLayout) v.findViewById(R.id.linearVou);
            mLinearFavorito = (LinearLayout) v.findViewById(R.id.linearInteresse);
            mLinearInfo = (LinearLayout) v.findViewById(R.id.linInfo);
            mTextGosto = (TextView) v.findViewById(R.id.textVou);
            mTextFavorito = (TextView) v.findViewById(R.id.textInteresse);
            mImageFavorito = (ImageView) v.findViewById(R.id.imageFavorito);
            mImageGosto = (ImageView) v.findViewById(R.id.imageGosto);
        }
    }
}

