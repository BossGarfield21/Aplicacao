package pt.novaleaf.www.maisverde;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyItemGrupoFragmentRecyclerViewAdapter extends RecyclerView.Adapter<MyItemGrupoFragmentRecyclerViewAdapter.ViewHolder>{

    private final List<Grupo> mValues;
    private final ItemGruposFragment.OnListFragmentInteractionListener mListener;

    public MyItemGrupoFragmentRecyclerViewAdapter(List<Grupo> items, ItemGruposFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public MyItemGrupoFragmentRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_grupos, parent, false);
        return new MyItemGrupoFragmentRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MyItemGrupoFragmentRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.mNomeGrupo.setText(mValues.get(position).getName());
        holder.mPrivacyGrupo.setText(mValues.get(position).getPrivacy());

        holder.mContraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onGrupoInteraction(mValues.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mNomeGrupo;
        public TextView mPrivacyGrupo;
        public ConstraintLayout mContraintLayout;

        public ViewHolder(View v){
            super(v);

            mNomeGrupo = (TextView) v.findViewById(R.id.textNomeGrupo);
            mPrivacyGrupo = (TextView) v.findViewById(R.id.textPrivacy);
            mContraintLayout = (ConstraintLayout) v.findViewById(R.id.constraintLayoutItemGrupo);

        }
    }
}
