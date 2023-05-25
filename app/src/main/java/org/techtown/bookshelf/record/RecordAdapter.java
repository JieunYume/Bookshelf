package org.techtown.bookshelf.record;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.bookshelf.R;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private ArrayList<Record> recordList = new ArrayList<Record>();
    private Context context;
    //파이어베이스 가져오기
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("book");



    @NonNull
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, int position) {
        holder.onBind(recordList.get(position));
    }

    public void setRecordList(ArrayList<Record> list){
        this.recordList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public Record getItem(int position){
        return recordList.get(position);
    }

    private void writeThoughtData(int position, String thought) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //[DB]record에 생각 추가
        String record_push = getItem(position).getPush();
        firebaseDatabase.getReference("record").child(record_push).child("thought").setValue(thought)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful
                        Log.e("생각저장", "생각 저장 성공");
                        getItem(position).setThought(thought);
                        Log.e("생각", getItem(position).getThought());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.e("생각저장", "생각 저장 실패");
                        return;
                    }
                });
    }

    public void addItem(Record item){
        recordList.add(item);
        notifyDataSetChanged();
    }

    public void arrayClear(){
        recordList.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView page;
        TextView line;
        TextView thought;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            page = itemView.findViewById(R.id.rRecord_page);
            line = itemView.findViewById(R.id.rRecord_line);
            thought = itemView.findViewById(R.id.thoughtTV);
        }

        void onBind(Record record){
            page.setText(record.getPage()+"p");
            line.setText(record.getLine());
            thought.setText(record.getThought());
        }
    }

}

