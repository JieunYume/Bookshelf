package org.techtown.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class BookAdapter extends BaseAdapter  {
    ArrayList<Book> items = new ArrayList<Book>();
    private Context context;


    
    //파이어베이스 가져오기
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("book");;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        Book book = items.get(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_book, parent, false);
        }

        //리스트뷰에 데이터 뿌리는 작업 :ㅇ
        ImageView bookimage = convertView.findViewById(R.id.bookimage);
        TextView title = convertView.findViewById(R.id.title);
        TextView author = convertView.findViewById(R.id.author);
        TextView publisher = convertView.findViewById(R.id.publisher);
        TextView genre = convertView.findViewById(R.id.genre);
        Button choice_btn=convertView.findViewById(R.id.choice_btn);

        if(book.getBookimage_address()!="") {
            Glide.with(convertView)
                    .load(book.getBookimage_address())
                    .placeholder(R.drawable.icon_search)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(bookimage);
        }

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        genre.setText(book.getGenre());
        //유저가 선택 버튼을 누르면 transmit_push_to_main()
        choice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BookSearchActivity)BookSearchActivity.mContext).transmit_push_to_main(position);
            }
        });

        return convertView;
    }

    public void addItem(Book item){
        items.add(item);
    }
}
