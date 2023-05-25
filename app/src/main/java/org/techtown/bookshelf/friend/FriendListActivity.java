package org.techtown.bookshelf.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.user.MainActivity;

import java.util.ArrayList;

public class FriendListActivity extends AppCompatActivity {
    RecyclerView friendListRecyclerView;
    FriendAdapter friendAdapter;
    ArrayList<Friend> mfriendItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        friendListRecyclerView = findViewById(R.id.friendlistRecyclerView);
        friendAdapter = ((MainActivity)MainActivity.mContext).getFriendAdapter(); //어답터 초기화
        friendAdapter.setViewType(FriendViewType.VERTICAL);
        friendListRecyclerView.setAdapter(friendAdapter);//리싸이클러뷰 초기화
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));

        mfriendItems = new ArrayList<>(); //아이템배열 정의

    }
}