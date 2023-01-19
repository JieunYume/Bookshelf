package org.techtown.bookshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookSearchActivity extends AppCompatActivity {
    //Firebase DB 가져오기
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("book");

    public static Context mContext;
    public String a;

    ListView bookListView;
    BookAdapter bookAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        /*
        writeBookData("데일 카네기 인간관계론", "데일 카네기", "현대지성", "자기계발", "http://image.yes24.com/goods/79297023/L");
        writeBookData("아몬드", "손원평", "창비", "소설", "https://image.yes24.com/goods/37300128/L");
        writeBookData("언어의 온도", "이기주", "말글터", "에세이", "http://image.yes24.com/goods/30387696/L");
         */
        bookListView=findViewById(R.id.bookListView);
        bookAdapter=new BookAdapter();
        bookListView.setAdapter(bookAdapter);

        readBookData();

        mContext=this;


        Intent intent_position = getIntent();
        if(intent_position.getStringExtra("book_position") != null) {
            int book_position = Integer.valueOf(intent_position.getStringExtra("book_position"));
            Book book = (Book) bookAdapter.getItem(book_position);
            Log.e("bookdata", "데이터를 받아와써용"+book_position);
        }


    }


    //DB에 데이터 쓰기(클래스로 저장되는게 아니라 String으로 저장됨!!)
    private void writeBookData(String title, String author, String publisher, String genre, String bookimage_address) {
        String push = databaseReference.push().getKey();
        Book book = new Book(push, title, author, publisher, genre, bookimage_address);
        databaseReference.child(push).setValue(book)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful
                        //bread.setBread_push(bread_push);
                        Toast.makeText(BookSearchActivity.this, "저장을 완료했습니다. push:"+push, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(BookSearchActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //DB에서 getValue()로 데이터 받아오기
    private void readBookData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookAdapter.items.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String push = dataSnapshot.getKey();
                    String title = dataSnapshot.child("title").getValue().toString();
                    String author = dataSnapshot.child("author").getValue().toString();
                    String publisher = dataSnapshot.child("publisher").getValue().toString();
                    String genre = dataSnapshot.child("genre").getValue().toString();
                    String image_address = dataSnapshot.child("bookimage_address").getValue().toString();

                    Book book = new Book(push, title, author, publisher, genre, image_address);
                    bookAdapter.addItem(book);
                }
                bookAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Warning!! : ", "데이터를 받아오는 데에 실패했습니다!");
            }
        });
    }

    //Main액티비티로 책 푸시값 넘겨주고 BookSerach액티비티 종료하기
    public void transmit_push_to_main(int position){
        //mainactivity로 전환(책푸시값 넘겨주기)
        String book_push = ((Book)bookAdapter.getItem(position)).getPush();
        Intent intent = new Intent();
        intent.putExtra("book_push", book_push);
        setResult(RESULT_OK, intent);

        finish();
    }

}