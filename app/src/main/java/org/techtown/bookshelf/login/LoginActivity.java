package org.techtown.bookshelf.login;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.user.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
    DataSnapshot dataSnapshot;
    private AlertDialog dialog;
    private EditText login_id, login_password;
    private Button login_button, join_button;

    String UserId, UserPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        login_id = findViewById( R.id.login_id);
        login_password = findViewById( R.id.login_password );
        join_button = findViewById( R.id.join_button );
        login_button = findViewById( R.id.login_button );

        join_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( LoginActivity.this, RegisterActivity.class );
                startActivity(intent);
            }
        });

        login();
    }

    private void login() {
        login_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserId = login_id.getText().toString();
                UserPwd = login_password.getText().toString();

                if (UserId.equals("") || UserPwd.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    dialog = builder.setMessage("아이디 또는 비밀번호를 입력해주셔야죵!").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if(dataSnapshot.getKey().equals(UserId)){
                                if(dataSnapshot.child("password").getValue().equals(UserPwd)) {
                                    Toast.makeText(getApplicationContext(), "로그인에 성공했습니다.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("UserId", UserId);
                                    startActivity(intent);
                                    return;
                                }else{
                                    Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }
                        Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }


}