package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.android.volley.VolleyLog;

import pt.novaleaf.www.maisverde.OcorrenciaFragment.OnListFragmentInteractionListener;
import utils.ByteRequest;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static pt.novaleaf.www.maisverde.LoginActivity.sharedPreferences;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ocorrencia} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * Adaptador para o recycler view
 */
public class MyOcorrenciaRecyclerViewAdapter extends RecyclerView.Adapter<MyOcorrenciaRecyclerViewAdapter.ViewHolder> implements Serializable {

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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //holder.mImageReport.setImageResource(mValues.get(position).getImgId());

        Ocorrencia ocorrencia = mValues.get(position);
        holder.titulo.setText(mValues.get(position).getName());

        if (mValues.get(position).isLiked()) {
            holder.mImageGosto.setImageResource(R.drawable.ic_favorite_green_24dp);
        }

        holder.mImageGosto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    if (!mValues.get(position).isLiked()) {
                        holder.mImageGosto.setImageResource(R.drawable.ic_favorite_green_24dp);

                    } else {
                        holder.mImageGosto.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                    }
                    mListener.onLikeInteraction(mValues.get(position));

                    mValues.get(position).like();
                    if (mValues.get(position).getLikes() != 1)
                        holder.mTextNumLikes.setText(String.valueOf(mValues.get(position).getLikes()) + " likes");
                    else
                        holder.mTextNumLikes.setText(String.valueOf(mValues.get(position).getLikes()) + " like");
                }
            }
        });

        holder.mImageComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCommentInteraction(mValues.get(position));
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

        if (mValues.get(position).getLikes() != 1)
            holder.mTextNumLikes.setText(String.valueOf(mValues.get(position).getLikes()) + " likes");
        else
            holder.mTextNumLikes.setText(String.valueOf(mValues.get(position).getLikes()) + " like");

        holder.username.setText(mValues.get(position).getOwner());
        long time = mValues.get(position).getCreationDate();

        if (time != 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
            String data = simpleDateFormat.format(new Date(time));
            holder.time.setText(data);
        }

        if (mValues.get(position).getImage_uri() != null && mValues.get(position).getBitmap() != null) {
            holder.mImageReport.setAdjustViewBounds(true);

            holder.mImageReport.setImageBitmap(BitmapFactory.decodeByteArray(mValues.get(position).getBitmap(),
                    0, mValues.get(position).getBitmap().length));

            //receberImagemVolley(holder, position);
        } else if (mValues.get(position).getImageID() != 0) {
            holder.mImageReport.setAdjustViewBounds(false);

            holder.mImageReport.setImageResource(mValues.get(position).getImageID());

        }

        holder.mRisco.setText(String.format("Risco\n%s", mValues.get(position).getRisk()));


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements Serializable {
        public ImageView mImageReport;
        public TextView titulo;
        public TextView username;
        public TextView time;
        public ConstraintLayout mRelative;
        public LinearLayout mLinearInfo;
        public TextView mTextNumLikes;
        public TextView mRisco;
        public ImageView mImageGosto;
        public ImageView mImageComentario;

        public ViewHolder(View v) {
            super(v);
            mImageReport = (ImageView) v.findViewById(R.id.imageReport);
            titulo = (TextView) v.findViewById(R.id.tituloReport);
            username = (TextView) v.findViewById(R.id.userName);
            time = (TextView) v.findViewById(R.id.time);
            mRisco = (TextView) v.findViewById(R.id.riscoCalculado);
            mRelative = (ConstraintLayout) v.findViewById(R.id.relative);
            mLinearInfo = (LinearLayout) v.findViewById(R.id.linearInfo);
            mTextNumLikes = (TextView) v.findViewById(R.id.likes);
            mImageGosto = (ImageView) v.findViewById(R.id.imageGosto);
            mImageComentario = (ImageView) v.findViewById(R.id.imageComentar);


        }
    }

    private void receberImagemVolley(final ViewHolder holder, final int position) {
        String tag_json_obj = "octect_request";
        Log.d("imgeuri", mValues.get(position).getImage_uri());
        String url = mValues.get(position).getImage_uri();


        final String token = sharedPreferences.getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                mValues.get(position).setBitmap(response);
                notifyDataSetChanged();
                holder.mImageReport.setImageBitmap(bitmap);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEMocorrencia", "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);

    }


}
