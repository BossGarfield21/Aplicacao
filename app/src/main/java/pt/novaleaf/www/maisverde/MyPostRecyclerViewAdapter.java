package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<MyPostRecyclerViewAdapter.ViewHolder> {

    private final List<Post> mValues;
    private final PostFragment.OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public MyPostRecyclerViewAdapter(List<Post> items, PostFragment.OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_posts, parent, false);
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
        //holder.mImagePost.setImageResource(mValues.get(position).getImgId());
        holder.mAutor.setText(mValues.get(position).getAuthor());

        holder.mMessage.setText(mValues.get(position).getMessage());

        if (mValues.get(position).isLiked()) {
            holder.mImageGosto.setImageResource(R.drawable.ic_favorite_green_24dp);
        } else {
            holder.mImageGosto.setImageResource(R.drawable.ic_favorite_border_black_24dp);
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
                    mValues.get(position).like();
                    mListener.onLikeInteraction(mValues.get(position));

                    holder.mTextNumLikes.setText(String.valueOf(mValues.get(position).getLikes()));
                }
            }
        });

        holder.mLinearComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCommentInteraction(mValues.get(position));
                }
            }
        });


        holder.mTextNumLikes.setText(String.valueOf(mValues.get(position).getLikes()));


        if (mValues.get(position).getComments().size() != 1)
            holder.mTextNumComments.setText(String.valueOf(mValues.get(position).getComments().size()) + " comentários");
        else
            holder.mTextNumComments.setText(String.valueOf(mValues.get(position).getComments().size()) + " comentário");


        //holder.mLinearComentarios.setClickable(true);
        holder.mLinearComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCommentInteraction(mValues.get(position));
                }
            }
        });

        holder.mImageComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCommentInteraction(mValues.get(position));
                }
            }
        });

        long time = mValues.get(position).getCreationDate();

        if (time != 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
            String data = simpleDateFormat.format(new Date(time));
            holder.mHour.setText(data);
        }

        if (mValues.get(position).getBitmapUser() != null) {
            holder.mImageUser.setImageBitmap(BitmapFactory.decodeByteArray(mValues.get(position).getBitmapUser(),
                    0, mValues.get(position).getBitmapUser().length));
        } else {
            holder.mImageUser.setImageResource(R.drawable.ic_person_black_24dp);
        }

        if (mValues.get(position).getBitmap() != null) {
            holder.mImagePost.setImageBitmap(BitmapFactory.decodeByteArray(mValues.get(position).getBitmap(),
                    0, mValues.get(position).getBitmap().length));
            holder.mImagePost.setAdjustViewBounds(true);
        }


        holder.mImageDef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    if (mValues.get(position).getAuthor().equals(mContext.
                            getSharedPreferences("Prefs", Context.MODE_PRIVATE).getString("username", "")))
                        mListener.onEditInteraction(mValues.get(position), holder.mImageDef);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mLinearComentarios;
        public ImageView mImagePost;
        public ImageView mImageUser;
        public TextView mTextNumLikes;
        public TextView mTextNumComments;
        public TextView mMessage;
        public TextView mAutor;
        public TextView mHour;
        public ImageView mImageGosto;
        public ImageView mImageComentar;
        public ImageView mImageDef;

        public ViewHolder(View v) {
            super(v);
            mImageGosto = (ImageView) v.findViewById(R.id.imageGosto);
            mImageComentar = (ImageView) v.findViewById(R.id.imageComentar);
            mImageDef = (ImageView) v.findViewById(R.id.def);
            mImageUser = (ImageView) v.findViewById(R.id.imagePostAuthor);
            mImagePost = (ImageView) v.findViewById(R.id.imagePost);
            mMessage = (TextView) v.findViewById(R.id.textPostMessage);
            mTextNumLikes = (TextView) v.findViewById(R.id.textPostLikes);
            mTextNumComments = (TextView) v.findViewById(R.id.comentarios);
            mHour = (TextView) v.findViewById(R.id.textPostHour);
            mAutor = (TextView) v.findViewById(R.id.textPostAutor);
            mLinearComentarios = (LinearLayout) v.findViewById(R.id.linearInfo);


        }
    }
}
