package org.techtown.bookshelf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    //Firebase DB 가져오기
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    EditText book_search_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        book_search_et = (EditText)findViewById(R.id.book_search);
        book_search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookSearchActivity.class);
                startActivityForResult(intent, 0);


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //data.getStringExtra("email")
        //requestCode : activity 식별값

        if(requestCode==0 && resultCode==RESULT_OK){
            Toast.makeText(MainActivity.this, data.getStringExtra("book_push"), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "result cancle!", Toast.LENGTH_SHORT).show();
        }
    }
}