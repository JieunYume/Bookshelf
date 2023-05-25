package org.techtown.bookshelf.book.display.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.MainActivity;
import org.techtown.bookshelf.book.display.BookSelected;

import java.util.ArrayList;

public class FriendBookshelfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BookShelfAdapter {
    ArrayList<BookSelected> bookList = new ArrayList<>();
    String FriendId;

    public FriendBookshelfAdapter(String FriendId) {
        this.FriendId = FriendId;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookshelf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).onBind(bookList.get(position));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setBookList(ArrayList<BookSelected> bookList) {
        this.bookList = bookList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookIV;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookIV=itemView.findViewById(R.id.shelf_bookimage);
            title = itemView.findViewById(R.id.shelf_title);
        }

        void onBind(BookSelected item){

            bookIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("book",item.getTitle());
                    ((MainActivity)MainActivity.mContext).dispBookFragment(FriendId, item);
                }
            });
            title.setText(item.getTitle());
        }
    }



}
