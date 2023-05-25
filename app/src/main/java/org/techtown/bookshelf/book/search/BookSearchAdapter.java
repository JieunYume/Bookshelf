package org.techtown.bookshelf.book.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.bookshelf.R;

import java.util.ArrayList;



public class BookSearchAdapter extends RecyclerView.Adapter<BookSearchAdapter.ViewHolder> {

    private ArrayList<Book> bookList=new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(bookList.get(position));
    }

    public void setBookList(ArrayList<Book> list){
        this.bookList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        //리스트뷰에 데이터 뿌리는 작업 :ㅇ
        ImageView bookimage;
        TextView title;
        TextView author;
        TextView publisher;
        TextView genre;
        Button choice_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookimage = itemView.findViewById(R.id.bookimage);
            title = itemView.findViewById(R.id.list_title);
            author = itemView.findViewById(R.id.list_author);
            publisher = itemView.findViewById(R.id.search_publisher);
            genre = itemView.findViewById(R.id.search_genre);
            choice_btn=itemView.findViewById(R.id.choice_btn);
        }

        void onBind(Book item){
            if(item.getBookimage_address()!="") {
                Glide.with(itemView)
                        .load(item.getBookimage_address())
                        .placeholder(R.drawable.icon_search)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(bookimage);
            }
            title.setText(item.getTitle());
            author.setText("작가: "+item.getAuthor());
            publisher.setText("출판사: "+item.getPublisher());
            genre.setText("장르: "+item.getGenre());
            //유저가 선택 버튼을 누르면 transmit_push_to_main()
            choice_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //이미 추가한 책이면 안된다고 알려줘야함
                    ((BookSearchActivity)BookSearchActivity.mContext).transmit_push_to_main(item.getPush());
                }
            });
        }
    }
}

