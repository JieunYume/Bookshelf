package org.techtown.bookshelf.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import org.techtown.bookshelf.book.display.adapter.BookShelfAdapter;
import org.techtown.bookshelf.book.display.bookinfo.FriendBookFragment;
import org.techtown.bookshelf.book.display.bookinfo.MyBookFragment;
import org.techtown.bookshelf.book.display.BookSelected;
import org.techtown.bookshelf.book.display.bookshelf.FriendBookshelfFragment;
import org.techtown.bookshelf.book.display.bookshelf.MyBookshelfFragment;
import org.techtown.bookshelf.book.search.BookSearchActivity;
import org.techtown.bookshelf.friend.FriendAdapter;
import org.techtown.bookshelf.friend.SearchFriendActivity;
import org.techtown.bookshelf.friend.Friend;
import org.techtown.bookshelf.friend.FriendListActivity;
import org.techtown.bookshelf.friend.FriendViewType;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Firebase
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public static Context mContext;
    //책 검색
    ImageView add_book;

    //유저 아이디
    String UserId;
    //책꽂이 기능
    FragmentManager fm;
    FragmentTransaction ft;


    MyBookshelfFragment myBookshelfFragment;
    RecyclerView bookShelfRecyclerView;
    Fragment bookFragment;

    //친구목록
    RecyclerView friendRecyclerView;
    FriendAdapter friendAdapter;
    ImageView add_friend, menu_burger;
    ArrayList<Friend> mfriendItems;


    FirebaseStorage storage;
    public Uri default_image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;

        //LoginAcitivity에서 사용자 아이디 가져오기
        Intent intent = getIntent();
        UserId=intent.getStringExtra("UserId");

        //책검색 아이콘을 누르면 BookSearchActivity열기
        add_book = (ImageView)findViewById(R.id.add_book);
        add_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookSearchActivity.class);
                intent.putExtra("UserId", UserId);
                startActivityForResult(intent, 0);
            }
        });


        myBookshelfFragment=MyBookshelfFragment.newInstance(UserId);
        //책꽂이 fragment 띄우기
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.frame_bookshelf, myBookshelfFragment);
        ft.commitNow();

        //친구목록 뿌리기
        dispFriendProfile();

        //친구목록아이콘
        menu_burger=findViewById(R.id.menu_burger);
        menu_burger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FriendListActivity.class);
                startActivityForResult(intent, 1);

            }
        });
        //친구추가아이콘
        add_friend = findViewById(R.id.add_friend);
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchFriendActivity.class);
                intent.putExtra("UserId", UserId);
                startActivityForResult(intent, 2);
            }
        });

        //디폴트 이미지 가져오기
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


    public void changeFragment(String FriendId) {
        FragmentTransaction ft = fm.beginTransaction(); // FragmentTransaction을 다시 정의해줘야 한다!
        ft.replace(R.id.frame_bookshelf, FriendBookshelfFragment.newInstance(FriendId));
        ft.commit();
        Toast.makeText(this, FriendId, Toast.LENGTH_SHORT).show();
    }
    //친구 데이터 뿌리기(1번레이아웃)
    public void dispFriendProfile() {
        friendRecyclerView = (RecyclerView) findViewById(R.id.friendRecyclerView);
        friendAdapter = new FriendAdapter(); //어답터 초기화
        friendAdapter.setViewType(FriendViewType.HORIZONTAL);
        friendRecyclerView.setAdapter(friendAdapter);//리싸이클러뷰 초기화
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
        mfriendItems=new ArrayList<>();
        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.child("user").child(UserId).child("friendList").getChildren()) {
                        String friend_id = dataSnapshot.getKey();
                        StorageReference storageReference = storage.getReference();
                        StorageReference pathReference = storageReference.child("ProfileImage/" + friend_id);
                        if (pathReference == null) {
                            mfriendItems.add(new Friend(friend_id, default_image_uri));
                            Log.e("friend_id", friend_id);
                            friendAdapter.setFriendList(mfriendItems);
                        } else {
                            StorageReference submitProfile = storageReference.child("ProfileImage/" + friend_id + "/1.png");
                            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mfriendItems.add(new Friend(friend_id, uri));
                                    friendAdapter.setFriendList(mfriendItems);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

            @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //data.getStringExtra("email")
        //requestCode : activity 식별값
        switch (requestCode){
            case 0: //BookSearchActivity-MainActivity
                if(resultCode==RESULT_OK){ // 책 찾기
                    myBookshelfFragment.loadBookData();
                    // 프래그먼트 새로고침
                    Toast.makeText(MainActivity.this, "책이 추가되었습니다!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "책 선택x", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1: // 프로필 수정
                break;
            case 2:
                if(resultCode==RESULT_OK){


                }else{
                }
                break;

        }
    }

    public void readBookData(String id, BookShelfAdapter adapter){
        ArrayList<BookSelected> bookList = new ArrayList<BookSelected>();
        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.child("user").child(id).child("booklist").getChildren()) {
                        String book_push = dataSnapshot.getKey();
                        String title = snapshot.child("book").child(book_push).child("title").getValue().toString();
                        String author = snapshot.child("book").child(book_push).child("author").getValue().toString();
                        String amount = snapshot.child("book").child(book_push).child("amount").getValue().toString();
                        String image_address = snapshot.child("book").child(book_push).child("bookimage_address").getValue().toString();

                        String read_amount = dataSnapshot.child("read_amount").getValue().toString();
                        String record_num = dataSnapshot.child("record_num").getValue().toString();
                        BookSelected bookSelected = new BookSelected(book_push, title, author, amount, read_amount, record_num, image_address);
                        bookList.add(bookSelected);
                    }
                }
                adapter.setBookList(bookList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void dispBookFragment(String who, BookSelected book){
        Bundle bundle = new Bundle();
        bundle.putSerializable("bookSelected", book);
        if(who.equals("my")){
            bookFragment=new MyBookFragment();
            bookFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_bookInfo, bookFragment)
                    .show(bookFragment)
                    .commit();
        } else if (!who.equals("my")) {
            bookFragment = new FriendBookFragment(who);
            bookFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_bookInfo2, bookFragment)
                    .show(bookFragment)
                    .commit();
        }
    }

    public String getUserId(){
        return UserId;
    }

    public FriendAdapter getFriendAdapter(){
        return friendAdapter;
    }
}