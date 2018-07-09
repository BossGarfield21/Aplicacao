package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MyEventoRecyclerViewAdapter extends RecyclerView.Adapter<MyEventoRecyclerViewAdapter.ViewHolder> {
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MyEventoRecyclerViewAdapter.ViewHolder holder, final int position) {
        //holder.mImageReport.setImageResource(mValues.get(position).getImgId());
        //holder.titulo.setText(mValues.get(position).getName());

        holder.titulo.setText(mValues.get(position).getName());

        if (mValues.get(position).isInteresse())
            holder.mImageInteresse.setImageResource(R.drawable.ic_if_star_285661);

        holder.mLinearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onLocationInteraction(mValues.get(position));
                }
            }
        });


        holder.mLinearInteresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    if (!mValues.get(position).isInteresse()) {
                        holder.mImageInteresse.setImageResource(R.drawable.ic_if_star_285661);
                    } else {

                        holder.mImageInteresse.setImageResource(R.drawable.ic_star_border_black_24dp);
                    }
                    mListener.onLikeInteraction(mValues.get(position));
                    mValues.get(position).setInteresse();

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

        long time = mValues.get(position).getMeetupDate();

        if (time != 0) {
            SimpleDateFormat simpleDateFormatMes = new SimpleDateFormat("MMM", Locale.UK);
            SimpleDateFormat simpleDateFormatDia = new SimpleDateFormat("dd", Locale.UK);
            String dataMes = simpleDateFormatMes.format(new Date(time));
            String dataDia = simpleDateFormatDia.format(new Date(time));
            holder.mMes.setText(dataMes.toUpperCase());
            holder.mDia.setText(dataDia.toUpperCase());
        }

        if (mValues.get(position).getImage_uri() != null && mValues.get(position).getBitmap() != null) {
            holder.mImageReport.setAdjustViewBounds(true);

            Bitmap bitmap = BitmapFactory.decodeByteArray(mValues.get(position).getBitmap(),
                    0, mValues.get(position).getBitmap().length);
            holder.mImageReport.setMaxHeight(bitmap.getHeight());
            holder.mImageReport.setMaxWidth(bitmap.getWidth());
            holder.mImageReport.setMinimumHeight(bitmap.getHeight());
            holder.mImageReport.setMinimumWidth(bitmap.getWidth());
            holder.mImageReport.setImageBitmap(bitmap);

            //receberImagemVolley(holder, position);
        } else if (mValues.get(position).getImageID() != 0) {
            //holder.mImageReport.setAdjustViewBounds(false);

            holder.mImageReport.setImageResource(mValues.get(position).getImageID());

        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageReport;
        public TextView titulo;
        public TextView mDia;
        public TextView mMes;
        public LinearLayout mLinearInteresse;
        public LinearLayout mLinearLocation;
        public ImageView mImageInteresse;

        public ViewHolder(View v) {
            super(v);
            mImageReport = (ImageView) v.findViewById(R.id.fragmentevent_img);
            titulo = (TextView) v.findViewById(R.id.tituloEventTextView);
            mDia = (TextView) v.findViewById(R.id.textViewDia);
            mMes = (TextView) v.findViewById(R.id.textViewMes);
            mLinearInteresse = (LinearLayout) v.findViewById(R.id.linearInteresse);
            mLinearLocation = (LinearLayout) v.findViewById(R.id.linearLocation);
            mImageInteresse = (ImageView) v.findViewById(R.id.starEvento);
        }
    }

}

