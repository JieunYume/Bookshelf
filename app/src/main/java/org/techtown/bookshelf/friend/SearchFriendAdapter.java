package org.techtown.bookshelf.friend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.user.MainActivity;

import java.util.ArrayList;

public class SearchFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private ArrayList<Friend> filteredList =new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public Friend getItem(int position){
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend3, parent, false);
        return new SearchFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SearchFriendViewHolder) holder).onBind(filteredList.get(position));
    }

    public void setFilteredList(ArrayList<Friend> filteredList) {
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    class SearchFriendViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView id;
        Button addButton;

        public SearchFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.friend_image3);
            id = (TextView) itemView.findViewById(R.id.friend_id3);
            addButton=(Button) itemView.findViewById(R.id.addButton);
        }

        void onBind(Friend item){
                Glide.with(itemView)
                        .load(item.getImage())
                        .placeholder(R.drawable.icon_search)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(image);
            id.setText(item.getId());
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String UserId = ((MainActivity)MainActivity.mContext).getUserId();
                    firebaseDatabase.getReference("user").child(UserId).child("friendList").child(item.getId()).setValue(item.getId());
                    ((MainActivity) MainActivity.mContext).dispFriendProfile();
                    Toast.makeText(v.getContext(), "친구추가가 완료되었습니다!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}

