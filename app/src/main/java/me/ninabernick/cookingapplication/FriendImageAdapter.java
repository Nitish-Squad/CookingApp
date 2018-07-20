package me.ninabernick.cookingapplication;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendImageAdapter extends RecyclerView.Adapter<FriendImageAdapter.ViewHolder> {


    FriendProfileListener listener;
    ArrayList<ParseUser> friends = new ArrayList<>();
    Context context;

    public interface FriendProfileListener {
        void thumbnailClicked(ParseUser friend);
    }

    public FriendImageAdapter(ArrayList<ParseUser> myFriends, FriendProfileListener fListener) {

        friends.clear();
        friends.addAll(myFriends);
        listener = fListener;

    }

    @NonNull
    @Override
    public FriendImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View friendView = inflater.inflate(R.layout.item_friend_profile, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(friendView, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendImageAdapter.ViewHolder viewHolder, int position) {
       ParseUser friend = friends.get(position);
       viewHolder.tvName.setText(friend.getString("name"));
       Glide.with(context).load(friend.getString("profileImageURL")).into(viewHolder.ivProfileThumb);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName;
        private ImageView ivProfileThumb;
        FriendProfileListener listener;

        public ViewHolder(View itemView, FriendProfileListener fListener) {
            super(itemView);
            listener = fListener;
            tvName = (TextView) itemView.findViewById(R.id.tvFriendName);
            ivProfileThumb = (ImageView) itemView.findViewById(R.id.ivProfileThumb);
            itemView.setOnClickListener(this);


        }

        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.thumbnailClicked(friends.get(getAdapterPosition()));
            }
        }
    }
}
