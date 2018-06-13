package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class EventoFragment extends Fragment {
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private EventoFragment.OnListFragmentInteractionListener mListener;
    private RecyclerView myRecyclerView;
    private static List<Evento> list =  new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventoFragment() {
    }

    // TODO: Customize parameter initialization

    public static EventoFragment newInstance(int columnCount) {
        EventoFragment fragment = new EventoFragment();
        updateList();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento_list, container, false);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            myRecyclerView.setAdapter(new MyEventoRecyclerViewAdapter(list, mListener));
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OcorrenciaFragment.OnListFragmentInteractionListener) {
            mListener = (EventoFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public static void updateList(){
        list.add(new Evento("limpar mata", "bombeiroTuga", 2901921, 392791, 8272,
                null, null, null, "2", "caparica", "bina", "hoje vamos limpar a mata\n" +
                "venham connosco limpar que isto esta tudo sujo\n" +
                "é essencial toda a ajuda"));
        list.add(new Evento("limpar terreno", "admin", 2901921, 392791, 8272,
                null, null, null, "2", "leiria", "bina", "hoje vamos limpar leiria\n" +
                "venham a leiria limpar\n" +
                "é essencial toda a ajuda"));

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLikeInteraction(Evento item);

        void onCommentInteraction(Evento item);

        void onFavoritoInteraction(Evento item);

        void onImagemInteraction(Evento item);
    }

}

