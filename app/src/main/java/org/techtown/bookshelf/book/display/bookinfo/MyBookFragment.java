package org.techtown.bookshelf.book.display.bookinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.bookshelf.R;
import org.techtown.bookshelf.MainActivity;
import org.techtown.bookshelf.book.display.BookSelected;
import org.techtown.bookshelf.camera.CameraActivity;
import org.techtown.bookshelf.record.Record;
import org.techtown.bookshelf.record.RecordAdapter;

import java.util.ArrayList;

public class MyBookFragment extends Fragment {
    /* 책기록 정보를 띄워줌 */
    BookSelected bookSelected;
    String book_push;
    RecyclerView recordRecyclerView;
    RecordAdapter recordAdapter;

    String UserId;
    InputMethodManager imm;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mybookinfo, container, false);

        bookSelected = (BookSelected)getArguments().getSerializable("bookSelected");
        book_push = bookSelected.getPush();
        Log.e("책제목",bookSelected.getTitle());
        UserId=((MainActivity) MainActivity.mContext).getUserId();
        ImageView bookimage = rootView.findViewById(R.id.bookimage);
        TextView title = rootView.findViewById(R.id.list_title);
        TextView author = rootView.findViewById(R.id.list_author);
        Button write_record_button = rootView.findViewById(R.id.write_record_button);
        TextView record_num = rootView.findViewById(R.id.record_num);
        ConstraintLayout layout_add_record = rootView.findViewById(R.id.layout_add_record);
        SeekBar readAmountSeekBar = rootView.findViewById(R.id.readAmountSeekBar);
        TextView currentPageTV = rootView.findViewById(R.id.currentPageTV);
        TextView pagesTV = rootView.findViewById(R.id.pagesTV);

        layout_add_record.setVisibility(View.GONE);
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
        readAmountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentPageTV.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bookSelected.setRead_amount(String.valueOf(seekBar.getProgress()));
            }
        });

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        record_num.setText(bookSelected.getRecord_num());
        //기록추가 버튼을 눌렀을 때
        write_record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_add_record.setVisibility(View.VISIBLE);
                EditText record_page=rootView.findViewById(R.id.wRecord_page);
                EditText record_content=rootView.findViewById(R.id.wRecord_content);
                EditText record_thought=rootView.findViewById(R.id.wRecord_thought);
                Button record_save_button=rootView.findViewById(R.id.wRecord_save_button);
                Button record_back_button=rootView.findViewById(R.id.wRecord_cancel_button);
                ImageView camera = rootView.findViewById(R.id.camera);

                //editText에 포커스주고 키보드 열기
                record_content.requestFocus();
                imm.showSoftInput(record_content, InputMethodManager.SHOW_IMPLICIT);

                //기록추가-저장 버튼을 눌렀을 때
                record_save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String page = record_page.getText().toString();
                        String content = record_content.getText().toString();
                        String thought = record_thought.getText().toString();
                        if(!page.equals("") && !content.equals("") && !thought.equals("")){
                            Record record = writeRecordData(record_page.getText().toString(), record_content.getText().toString(), record_thought.getText().toString(), bookSelected.getRecord_num());
                            recordAdapter.addItem(record);
                            layout_add_record.setVisibility(View.GONE);
                            record_page.setText(null);
                            record_content.setText(null);
                            record_thought.setText(null);
                        }else{
                            Toast.makeText(getActivity(), "기록을 모두 입력해주세요!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CameraActivity.class);
                        startActivityForResult(intent, 300);
                    }
                });


                record_back_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout_add_record.setVisibility(View.GONE);
                    }
                });
            }
        });

        recordRecyclerView = rootView.findViewById(R.id.recordRecyclerView);
        recordAdapter = new RecordAdapter();
        recordRecyclerView.setAdapter(recordAdapter);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //기록 리스트뷰 띄우기
        readRecordData();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private Record writeRecordData(String page, String line, String thought, String record_num) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //DatabaseReference databaseReference = firebaseDatabase.getReference();

        //[DB]record에 기록 데이터 추가
        String record_push = firebaseDatabase.getReference("record").push().getKey();
        Record record = new Record(record_push, page, line, thought);
        firebaseDatabase.getReference("record").child(record_push).setValue(record)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getActivity(), "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });



        //[DB]user-book-record 연결
        Log.e("푸시값(책, 기록)", book_push+", "+record_push);

        firebaseDatabase.getReference("user").child(UserId).child("booklist").child(book_push).child("recordlist").child(record_push).setValue(record_push)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed

                    }
                });

        String record_num_save=((Integer) (Integer.parseInt(record_num)+1)).toString();
        firebaseDatabase.getReference("user").child(UserId).child("booklist").child(book_push).child("record_num").setValue(record_num_save)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful
                        Toast.makeText(getActivity(), "저장을 완료했습니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getActivity(), "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

        return record;

    }

    public void readRecordData(){
        ArrayList<Record> mrecordItems=new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.child("user").child(UserId).child("booklist").child(book_push).child("recordlist").getChildren()) {
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
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(context.getApplicationContext(), "기록 데이터를 받아오는데 실패", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
