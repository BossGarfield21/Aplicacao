package pt.novaleaf.www.maisverde;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<MyPostRecyclerViewAdapter.ViewHolder> {

    private final List<Post> mValues;
    private final PostFragment.OnListFragmentInteractionListener mListener;

    public MyPostRecyclerViewAdapter(List<Post> items, PostFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
                }
            }
        });

        holder.mImageComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCommentInteraction(mValues.get(position));
                }
            }
        });


        holder.mTextNumLikes.setText(String.valueOf(mValues.get(position).getLikes()) + " likes");
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

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mLinearComentarios;
        public ImageView mImagePost;
        public TextView mTextNumLikes;
        public TextView mTextNumComments;
        public TextView mMessage;
        public TextView mAutor;
        public TextView mHour;
        public ImageView mImageComments;
        public ImageView mImageGosto;

        public ViewHolder(View v) {
            super(v);
            mImageGosto = (ImageView) v.findViewById(R.id.imagePostLike);
            mImageComments = (ImageView) v.findViewById(R.id.imagePostComment);
            mImagePost = (ImageView) v.findViewById(R.id.imagePostAuthor);
            mMessage = (TextView) v.findViewById(R.id.textPostMessage);
            mTextNumLikes = (TextView) v.findViewById(R.id.textPostLikes);
            mTextNumComments = (TextView) v.findViewById(R.id.textPostComments);
            mHour = (TextView) v.findViewById(R.id.textPostHour);
            mAutor = (TextView) v.findViewById(R.id.textPostAutor);
            mLinearComentarios = (LinearLayout) v.findViewById(R.id.linLikes);


        }
    }
}
