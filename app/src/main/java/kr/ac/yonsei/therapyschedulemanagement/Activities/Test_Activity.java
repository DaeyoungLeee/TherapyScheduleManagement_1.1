package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import kr.ac.yonsei.therapyschedulemanagement.CalendarDaySchdule_Adapter;
import kr.ac.yonsei.therapyschedulemanagement.CardItem;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Test_Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    FirebaseUser mUser;
    CalendarDaySchdule_Adapter calendarDaySchduleAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activiry);

        recyclerView = findViewById(R.id.recyclerView);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        ArrayList<CardItem> cardItemList = new ArrayList<>();

        calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(linearLayoutManager);

        // 저장되어있는 리스트 가져오기
        mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                .child("Calendar")
                .child("2019/10/12")
                .child("Therapy_schedule").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("email2", "onCreate: " + mUser.getEmail());

                CardItem cardItem = new CardItem(dataSnapshot.getValue().toString(), "-");

                cardItemList.add(cardItem);
                calendarDaySchduleAdapter.notifyDataSetChanged();

                recyclerView.setAdapter(calendarDaySchduleAdapter);
                Log.d("22", "onChildAdded: " + dataSnapshot.getValue());

                //cardItem.setStartTime(startTimeList);
                // 이름정보 가져오기
                mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                        .child("Calendar")
                        .child("2019/11/12")
                        .child("Therapy_schedule")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String endTime = dataSnapshot.getValue(String.class);

                                cardItem.setEndTime(endTime);
                               // calendarDaySchduleAdapter.addItem(cardItem);

                                recyclerView.setAdapter(calendarDaySchduleAdapter);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
