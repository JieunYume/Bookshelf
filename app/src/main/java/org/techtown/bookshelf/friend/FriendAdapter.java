package org.techtown.bookshelf.friend;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.MainActivity;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Friend> friendList=new ArrayList<>();
    private int viewType=0;

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public Friend getItem(int position){
        return friendList.get(position);
    }

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Log.e("viewType", String.valueOf(viewType));

        if(viewType == FriendViewType.HORIZONTAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend1, parent, false);
            return new HorizontalViewHolder(view);
        } else if(viewType == FriendViewType.VERTICAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend2, parent, false);
            return new VerticalViewHolder(view);
        } else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend1, parent, false);
            return new HorizontalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HorizontalViewHolder) {
            ((HorizontalViewHolder) holder).onBind(friendList.get(position));
        }
        else if(holder instanceof VerticalViewHolder) {
            ((VerticalViewHolder) holder).onBind(friendList.get(position));
        }
        else {
            ((HorizontalViewHolder) holder).onBind(friendList.get(position));
        }
    }

    public void setFriendList(ArrayList<Friend> friendlist){
        this.friendList = friendlist;
        notifyDataSetChanged();
    }

    public ArrayList<Friend> getFriendList() {
        return friendList;
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView id;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.friend_image);
            id = (TextView) itemView.findViewById(R.id.friend_id);
        }

        void onBind(Friend item){
            Glide.with(itemView)
                    .load(item.getImage())
                    .placeholder(R.drawable.icon_search)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(image);
            id.setText(item.getId());
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // MainActivity에서 Fragment를 변경해줘야 함!
                    ((MainActivity)MainActivity.mContext).changeFragment(item.getId());
                }
            });
        }
    }
    class VerticalViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView id;

        public VerticalViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.friend_Image2);
            id = (TextView) itemView.findViewById(R.id.friend_id2);
        }

        void onBind(Friend item){
            Glide.with(itemView)
                    .load(item.getImage())
                    .placeholder(R.drawable.icon_search)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(image);
            id.setText(item.getId());
        }
    }

}

