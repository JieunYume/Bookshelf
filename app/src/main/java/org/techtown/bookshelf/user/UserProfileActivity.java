package org.techtown.bookshelf.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.bookshelf.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.Proxy;

public class UserProfileActivity extends AppCompatActivity {

    ImageView myImage_update;
    TextView userId_TV;
    String UserId;
    Button save_profile;

    private FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        UserId = getIntent().getStringExtra("UserId");
        storage = FirebaseStorage.getInstance();

        userId_TV = findViewById(R.id.userId);
        myImage_update = findViewById(R.id.myImage_update);
        save_profile = findViewById(R.id.save_profile);
        save_profile.setVisibility(View.INVISIBLE);

        userId_TV.setText("id: "+UserId);
        myImage_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);

            }
        });

        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Uri file = data.getData();
                StorageReference storageReference = storage.getReference();
                StorageReference riversReference = storageReference.child("ProfileImage/"+UserId+"/1.png");
                UploadTask uploadTask = riversReference.putFile(file);

                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    Glide.with(getApplicationContext()).load(data.getData()).into(myImage_update);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileActivity.this, "업로드 실패!", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(UserProfileActivity.this, "업로드 성공!", Toast.LENGTH_LONG).show();
                        save_profile.setVisibility(View.VISIBLE);
                    }
                });


            }
        }

    }
}