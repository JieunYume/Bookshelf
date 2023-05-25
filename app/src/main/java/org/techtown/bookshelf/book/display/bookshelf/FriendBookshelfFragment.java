package org.techtown.bookshelf.book.display.bookshelf;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.RoundImageView;
import org.techtown.bookshelf.user.MainActivity;
import org.techtown.bookshelf.book.display.BookSelected;
import org.techtown.bookshelf.book.display.adapter.FriendBookshelfAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendBookshelfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendBookshelfFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String FriendId;
    RoundImageView friendImage;
    //사용자 ID
    TextView userBookshelfTV2;

    FriendBookshelfAdapter bookshelfAdapter;
    RecyclerView bookShelfRecyclerView;
    ArrayList<BookSelected> bookList;

    public FriendBookshelfFragment() {
        // Required empty public constructor
    }

    public static FriendBookshelfFragment newInstance(String FriendId) {
        FriendBookshelfFragment fragment = new FriendBookshelfFragment();
        Bundle args = new Bundle();
        args.putString("FriendId", FriendId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FriendId = getArguments().getString("FriendId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friend_bookshelf, container, false);

        //친구 프로필 설정하기
        dispFriendProfile(rootView);

        // 책꽂이
        bookshelfAdapter=new FriendBookshelfAdapter(FriendId);
        bookShelfRecyclerView = rootView.findViewById(R.id.friendBookshelf);
        bookList = new ArrayList<>();
        bookShelfRecyclerView.setAdapter(bookshelfAdapter);
        bookShelfRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false));
        ((MainActivity)MainActivity.mContext).readBookData(FriendId, bookshelfAdapter);

        return rootView;
}

    //친구 프로필 설정하기
    private void dispFriendProfile(ViewGroup rootView) {
        friendImage=rootView.findViewById(R.id.friendImage);
        userBookshelfTV2 =rootView.findViewById(R.id.userBookshelfTV2);

        loadFriendImage();
        userBookshelfTV2.setText(FriendId+"'s BOOKSHELF");
    }

    public void loadFriendImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("ProfileImage/"+FriendId);
        if (pathReference == null) {
            Toast.makeText(getActivity(), FriendId+"님 저장소에 사진이 없습니다.", Toast.LENGTH_LONG).show();
        } else{
            StorageReference submitProfile=storageReference.child("ProfileImage/"+FriendId+"/1.png");
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(friendImage)
                            .load(uri)
                            .placeholder(R.drawable.icon_search)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(friendImage);
                    friendImage.setRectRadius(20f);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), FriendId+"님 이미지를 불러오는 데 실패했습니다.", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

}
