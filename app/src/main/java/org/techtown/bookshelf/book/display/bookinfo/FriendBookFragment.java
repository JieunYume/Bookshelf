package org.techtown.bookshelf.book.display.bookinfo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.book.display.BookSelected;
import org.techtown.bookshelf.record.Record;
import org.techtown.bookshelf.record.RecordAdapter;

import java.util.ArrayList;

public class FriendBookFragment extends Fragment {

    BookSelected bookSelected;
    String book_push;
    RecyclerView recordRecyclerView;
    RecordAdapter recordAdapter;

    String FriendId;

    public FriendBookFragment(String FriendId) {
        this.FriendId = FriendId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friendbookinfo, container, false);

            bookSelected = (BookSelected)getArguments().getSerializable("bookSelected");
            book_push = bookSelected.getPush();
            Log.e("책제목",bookSelected.getTitle());

            ImageView bookimage = rootView.findViewById(R.id.bookimage_f);
            TextView title = rootView.findViewById(R.id.list_title_f);
            TextView author = rootView.findViewById(R.id.list_author_f);
            TextView record_num = rootView.findViewById(R.id.list_record_num_f);
            SeekBar readAmountSeekBar = rootView.findViewById(R.id.readAmountSeekBar_f);
            TextView currentPageTV = rootView.findViewById(R.id.currentPageTV_f);
            TextView pagesTV = rootView.findViewById(R.id.pagesTV_f);

            if(bookSelected.getBookimage_address()!="") {
                Glide.with(rootView)
                        .load(bookSelected.getBookimage_address())
                        .placeholder(R.drawable.icon_search)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(bookimage);
            }
            title.setText(bookSelected.getTitle());
            author.setText(bookSelected.getAuthor());
            readAmountSeekBar.setMax(Integer.valueOf(bookSelected.getAmount()));
            pagesTV.setText(bookSelected.getAmount());
            readAmountSeekBar.setProgress(Integer.valueOf(bookSelected.getRead_amount()));
            currentPageTV.setText(bookSelected.getRead_amount());

            readAmountSeekBar.setEnabled(false);
            record_num.setText(bookSelected.getRecord_num());

            recordRecyclerView = rootView.findViewById(R.id.recordRecyclerView_f);
            recordAdapter = new RecordAdapter();
            recordRecyclerView.setAdapter(recordAdapter);
            recordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            //기록 리스트뷰 띄우기
            readRecordData(FriendId);


            return rootView;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }

        public void readRecordData(String id) {
            ArrayList<Record> mrecordItems = new ArrayList<>();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("user").child(id).hasChild("booklist")) {
                        String record_num = snapshot.child("user").child(id).child("booklist").child(book_push).child("record_num").getValue().toString();
                        Log.e("record_num", record_num);
                        if (!record_num.equals("0")) {
                            for (DataSnapshot dataSnapshot : snapshot.child("user").child(id).child("booklist").child(book_push).child("recordlist").getChildren()) {
                                String record_push = dataSnapshot.getKey();
                                String push = snapshot.child("record").child(record_push).child("push").getValue().toString();
                                String page = snapshot.child("record").child(record_push).child("page").getValue().toString();
                                String line = snapshot.child("record").child(record_push).child("line").getValue().toString();
                                String thought = snapshot.child("record").child(record_push).child("thought").getValue().toString();
                                if (thought.equals("")) {
                                    mrecordItems.add(new Record(push, page, line));
                                } else {
                                    mrecordItems.add(new Record(push, page, line, thought));
                                }
                            }
                        }
                        recordAdapter.setRecordList(mrecordItems);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
}
