package pt.novaleaf.www.maisverde;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import pt.novaleaf.www.maisverde.OcorrenciaFragment.OnListFragmentInteractionListener;
import pt.novaleaf.www.maisverde.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.imagem.setImageResource(mValues.get(position).getImgId());
        holder.titulo.setText(mValues.get(position).getTitulo());

        holder.mFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagem;
        public TextView titulo;
        public FrameLayout mFrame;

        public ViewHolder(View v){
            super(v);
            imagem = (ImageView) v.findViewById(R.id.image);
            titulo = (TextView) v.findViewById(R.id.content);
            mFrame = (FrameLayout) v.findViewById(R.id.frame);
        }
    }
}
