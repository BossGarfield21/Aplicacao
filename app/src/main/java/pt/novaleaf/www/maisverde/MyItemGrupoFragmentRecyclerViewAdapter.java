package pt.novaleaf.www.maisverde;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyItemGrupoFragmentRecyclerViewAdapter extends RecyclerView.Adapter<MyItemGrupoFragmentRecyclerViewAdapter.ViewHolder>
        implements Filterable{

    public static List<Grupo> mValues;
    private final ItemGruposFragment.OnListFragmentInteractionListener mListener;
    private String distrito;

    public MyItemGrupoFragmentRecyclerViewAdapter(List<Grupo> items, ItemGruposFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        distrito = "";
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
        holder.mNumPessoas.setText(mValues.get(position).getNumPessoas() + " pessoas");

        holder.mContraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onGrupoInteraction(mValues.get(position));
            }
        });

    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                // Create a FilterResults object
                FilterResults results = new FilterResults();

                // If the constraint (search string/pattern) is null
                // or its length is 0, i.e., its empty then
                // we just set the `values` property to the
                // original contacts list which contains all of them
                if (charSequence == null || charSequence.length() == 0) {
                    mValues = GruposListActivity.tempGrupos;
                    results.values = GruposListActivity.tempGrupos;
                    results.count = GruposListActivity.tempGrupos.size();
                } else {
                    // Some search copnstraint has been passed
                    // so let's filter accordingly
                    ArrayList<Grupo> filteredContacts = new ArrayList<>();

                    // We'll go through all the contacts and see
                    // if they contain the supplied string
                    for (Grupo grupo : GruposListActivity.grupos) {
                        if (grupo.getName().toUpperCase().contains( charSequence.toString().toUpperCase() )
                                && (distrito.isEmpty() || grupo.getDistrito().equals(distrito))) {
                            // if `contains` == true then add it
                            // to our filtered list
                            Log.d("adicionou", grupo.getName());
                            filteredContacts.add(grupo);
                        }
                    }

                    // Finally set the filtered values and size/count
                    results.values = filteredContacts;
                    results.count = filteredContacts.size();
                }

                // Return our FilterResults object
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ArrayList<Grupo> g =(ArrayList<Grupo>) filterResults.values;
                Log.d("RESULTS", g.size() + " BINA");
                mValues = (ArrayList<Grupo>) filterResults.values;
                Log.d("RESULTS", GruposListActivity.grupos.size() + " JORGINA");
                Log.d("RESULTS", mValues.size() + " NAO EMPINA");
                //GruposListActivity.grupos = new ArrayList<Grupo>( (ArrayList<Grupo>)filterResults.values);
                notifyDataSetChanged();
            }
        };
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mNomeGrupo;
        public TextView mPrivacyGrupo;
        public TextView mNumPessoas;
        public ConstraintLayout mContraintLayout;

        public ViewHolder(View v){
            super(v);

            mNomeGrupo = (TextView) v.findViewById(R.id.textNomeGrupo);
            mPrivacyGrupo = (TextView) v.findViewById(R.id.textPrivacy);
            mNumPessoas = (TextView) v.findViewById(R.id.textNumPessoas);
            mContraintLayout = (ConstraintLayout) v.findViewById(R.id.constraintLayoutItemGrupo);

        }
    }
}
