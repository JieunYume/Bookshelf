package org.techtown.bookshelf.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchFriendActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private EditText searchFriendET;
    private RecyclerView searchFriendRecyclerView;
    private ArrayList<Friend> userList, filteredList, friendList;
    private SearchFriendAdapter searchFriendAdapter;
    String UserId;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);

        Intent intent = getIntent();
        UserId = intent.getStringExtra("UserId");
        Log.e("UserId", UserId);
        searchFriendET = findViewById(R.id.searchFriendET);
        searchFriendRecyclerView = findViewById(R.id.searchFriendRecyclerView);

        userList = new ArrayList<>();
        filteredList = new ArrayList<>();
        friendList = ((MainActivity) MainActivity.mContext).getFriendAdapter().getFriendList();
        searchFriendAdapter = new SearchFriendAdapter();
        getFriendList(); //friendList에 유저 목록 불러옴
        searchFriendRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        searchFriendRecyclerView.setAdapter(searchFriendAdapter);

        searchFriendET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = searchFriendET.getText().toString();
                searchFilter(searchText);

            }
        });
        storage = FirebaseStorage.getInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void searchFilter(String searchText) {
        filteredList.clear();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(userList.get(i));
            }
        }
        searchFriendAdapter.setFilteredList(filteredList);
    }

    //DB에서 유저목록 불러오기
    private void getFriendList() {
        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.child("user").getChildren()) {
                        String user_id = dataSnapshot.child("id").getValue().toString();
                        Log.e("UserId", user_id);
                        //본인 계정은 패스하기
                        if (user_id.equals(UserId)) {
                            continue;
                        }
                        //이미 친구인 사람들 제외하기
                        if (!isFriend(user_id)) {
                            StorageReference storageReference = storage.getReference();
                            StorageReference pathReference = storageReference.child("ProfileImage/"+user_id);
                            if (pathReference == null) {
                                userList.add(new Friend(user_id, ((MainActivity)MainActivity.mContext).default_image_uri));

                            } else{
                                StorageReference submitProfile=storageReference.child("ProfileImage/"+user_id+"/1.png");
                                submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        userList.add(new Friend(user_id, uri));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "이미지를 불러오는 데 실패했습니다.", Toast.LENGTH_LONG).show();
                                        userList.add(new Friend(user_id, ((MainActivity)MainActivity.mContext).default_image_uri));
                                    }
                                });
                            }
                        }
                    }
                    searchFriendAdapter.setFilteredList(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //친구인지 아닌지 확인하는 함수
    private boolean isFriend(String user_id) {
        Iterator<Friend> it = friendList.iterator();
        while (it.hasNext()) {
            String friend_id = it.next().getId();
            if (user_id.equals(friend_id)) {
                return true;
            }
        }
        return false;
    }

    public void loadMyImage() {
    }
}