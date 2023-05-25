package org.techtown.bookshelf.book.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.bookshelf.R;

import java.util.ArrayList;

public class BookSearchActivity extends AppCompatActivity {
    //Firebase DB 가져오기
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("book");

    public static Context mContext;
    public String a;
    //유저 아이디
    String UserId;

    RecyclerView bookRecyclerView;
    BookSearchAdapter bookAdapter;
    ImageView back_IV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        UserId = getIntent().getStringExtra("UserId");
        /*
        writeBookData("데일 카네기 인간관계론", "데일 카네기", "현대지성", "자기계발", "http://image.yes24.com/goods/79297023/L");
        writeBookData("아몬드", "손원평", "창비", "소설", "https://image.yes24.com/goods/37300128/L");
        writeBookData("언어의 온도", "이기주", "말글터", "에세이", "http://image.yes24.com/goods/30387696/L");
         */
        back_IV = findViewById(R.id.back_IV);
        back_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        bookRecyclerView =findViewById(R.id.bookRecyclerView);
        bookAdapter=new BookSearchAdapter();
        bookRecyclerView.setAdapter(bookAdapter);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        readOriginalBookData();

        mContext=this;

    }


    //DB에 데이터 쓰기(클래스로 저장되는게 아니라 String으로 저장됨!!)
    private void writeBookData(String title, String author, String publisher, String genre, String amount, String bookimage_address) {
        String push = databaseReference.push().getKey();
        Book book = new Book(push, title, author, publisher, genre, amount, bookimage_address);
        firebaseDatabase.getReference("book").child(push).setValue(book)
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
    private void readOriginalBookData(){
        ArrayList<Book> bookList = new ArrayList<>();
        firebaseDatabase.getReference("book").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String push = dataSnapshot.getKey();
                    String title = dataSnapshot.child("title").getValue().toString();
                    String author = dataSnapshot.child("author").getValue().toString();
                    String publisher = dataSnapshot.child("publisher").getValue().toString();
                    String genre = dataSnapshot.child("genre").getValue().toString();
                    String amount = dataSnapshot.child("amount").getValue().toString();
                    String image_address = dataSnapshot.child("bookimage_address").getValue().toString();

                    Book book = new Book(push, title, author, publisher, genre, amount, image_address);
                    bookList.add(book);
                }
                bookAdapter.setBookList(bookList);
                Log.e("책 데이터", "책 데이터 다 받아옴");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Warning!! : ", "데이터를 받아오는 데에 실패했습니다!");
            }
        });
    }

    //MainActivity로 책 푸시값 넘겨주고 BookSerachActivity 종료하기
    public void transmit_push_to_main(String book_push){

        Toast.makeText(this, book_push, Toast.LENGTH_SHORT).show();
        firebaseDatabase.getReference("user").child(UserId).child("booklist").child(book_push).child("push").setValue(book_push);
        firebaseDatabase.getReference("user").child(UserId).child("booklist").child(book_push).child("read_amount").setValue("0");
        firebaseDatabase.getReference("user").child(UserId).child("booklist").child(book_push).child("record_num").setValue("0");

        setResult(RESULT_OK);
        finish();
    }

}