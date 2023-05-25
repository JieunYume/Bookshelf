package org.techtown.bookshelf.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.user.MainActivity;
import org.techtown.bookshelf.user.UserProfileActivity;

import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

public class RegisterActivity extends AppCompatActivity {

    private EditText join_id, join_password, join_name, join_pwck;
    private Button join_button, check_button, back_button;
    private AlertDialog dialog;
    private boolean validate = false;

    //DB연동
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("user");

    FirebaseStorage storage;
    Uri default_image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //아이디값 찾아주기
        join_id = findViewById(R.id.join_id);
        join_password = findViewById(R.id.join_password);
        join_pwck = findViewById(R.id.join_pwck);
        check_button = findViewById(R.id.check_button);
        join_button = findViewById(R.id.join_button);
        back_button = findViewById(R.id.back_button);

        //[check_button]아이디 중복 체크 후 성공하면 DB에 유저 데이터를 추가한다.
        id_check();

        //[join_button]회원가입 버튼 클릭 시 수행
        join();

        //[back_button]
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference submitProfile=storageReference.child("ProfileImage/default_profile.png");
        submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                default_image_uri=uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "디폴트 이미지를 불러오는 데 실패했습니다.", Toast.LENGTH_LONG).show();                                    }
        });

    }

    private void id_check() {
        check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserId = join_id.getText().toString();
                if (validate) {
                    Toast.makeText(getApplicationContext(), "이미 아이디 중복 체크를 완료했어요!", Toast.LENGTH_LONG).show();
                    return; //이미 검증 완료
                }

                //사용자가 아이디를 입력하지 않으면 다이얼로그 경고창을 띄운다.
                if (UserId.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디를 입력해주셔야죵!").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                        while (child.hasNext()) {
                            if (UserId.equals(child.next().getKey())) {
                                Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_LONG).show();
                                return;
                                //리스너 등록을 해제해야 하는가:????? -> 안해도 됨
                            }
                        }
                        validate = true;
                        Toast.makeText(getApplicationContext(), "사용가능한 아이디입니다! 이제 조금만 더 입력하면 돼요!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void join() {
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserId = join_id.getText().toString();
                String UserPwd = join_password.getText().toString();
                String PwdCk = join_pwck.getText().toString();
                //아이디 중복체크 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                //한 칸이라도 입력 안했을 경우
                if (UserId.equals("") || UserPwd.equals("") || PwdCk.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("입력칸을 모두 채워주세요!").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                //비밀번호와 비밀번호 확인이 일치한지 확인 후 DB에 데이터 추가
                if (UserPwd.equals(PwdCk)) {
                    firebaseDatabase.getReference("user").child(UserId).child("id").setValue(UserId);
                    Date joinDate = new Date(System.currentTimeMillis());
                    firebaseDatabase.getReference("user").child(UserId).child("join_date").setValue(joinDate.toString());
                    firebaseDatabase.getReference("user").child(UserId).child("password").setValue(UserPwd);

                    StorageReference storageReference = storage.getReference();
                    StorageReference riversReference = storageReference.child("ProfileImage/"+UserId+"/1.png");
                    UploadTask uploadTask = riversReference.putFile(default_image_uri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "업로드 실패!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(RegisterActivity.this, "업로드 성공!", Toast.LENGTH_LONG).show();

                            Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", UserId), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);

                        }
                    });
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("비밀번호가 동일하지 않습니다.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
            }
        });
    }
}