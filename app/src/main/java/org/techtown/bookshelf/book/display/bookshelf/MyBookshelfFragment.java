package org.techtown.bookshelf.book.display.bookshelf;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.RoundImageView;
import org.techtown.bookshelf.user.UserProfileActivity;
import org.techtown.bookshelf.user.MainActivity;
import org.techtown.bookshelf.book.display.adapter.MyBookshelfAdapter;

public class MyBookshelfFragment extends Fragment {
        /* 내 책장 전체 프래그먼트 */

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    RoundImageView myImage;
    //사용자 ID
    TextView userBookshelfTV;
    ImageView edit_myprofile;
    MyBookshelfAdapter myBookshelfAdapter;
    RecyclerView bookShelfRecyclerView;

    private String UserId;

    public MyBookshelfFragment() {
        // Required empty public constructor
    }
    public static MyBookshelfFragment newInstance(String UserId) {
        MyBookshelfFragment fragment = new MyBookshelfFragment();
        Bundle args = new Bundle();
        args.putString("UserId", UserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UserId = getArguments().getString("UserId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_bookshelf, container, false);

        //내 프로필 설정하기
        dispMyProfile(rootView);

        edit_myprofile = rootView.findViewById(R.id.edit_myprofile);
        edit_myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra("UserId", UserId);
                startActivityForResult(intent, 1);
            }
        });

        // 책꽂이
        myBookshelfAdapter =new MyBookshelfAdapter();
        bookShelfRecyclerView = rootView.findViewById(R.id.myBookshelf);
        bookShelfRecyclerView.setAdapter(myBookshelfAdapter);
        bookShelfRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false));
        ((MainActivity) MainActivity.mContext).readBookData(UserId, myBookshelfAdapter);

        return rootView;
    }

    //내 프로필 설정하기
    private void dispMyProfile(ViewGroup rootView) {
        myImage=rootView.findViewById(R.id.myImage);
        userBookshelfTV =rootView.findViewById(R.id.userBookshelfTV);
        loadMyImage();

        userBookshelfTV.setText(UserId+"'s BOOKSHELF");
    }

    public void loadBookData() {
        ((MainActivity) MainActivity.mContext).readBookData(UserId, myBookshelfAdapter);
    }

    public void loadMyImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("ProfileImage/"+UserId);
        if (pathReference == null) {
            Toast.makeText(getActivity(), "저장소에 사진이 없습니다.", Toast.LENGTH_LONG).show();

        } else{
            StorageReference submitProfile=storageReference.child("ProfileImage/"+UserId+"/1.png");
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(myImage)
                            .load(uri)
                            .placeholder(R.drawable.icon_search)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(myImage);
                    myImage.setRectRadius(20f);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "이미지를 불러오는 데 실패했습니다.", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: // 프로필 수정
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getActivity(), "프로필이 수정되었습니다!", Toast.LENGTH_SHORT).show();
                    loadMyImage();

                } else {
                }

        }
    }
}