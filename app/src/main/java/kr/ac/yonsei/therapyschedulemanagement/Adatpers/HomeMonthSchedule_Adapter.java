package kr.ac.yonsei.therapyschedulemanagement.Adatpers;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import kr.ac.yonsei.therapyschedulemanagement.HomeMonth_CardItem;
import kr.ac.yonsei.therapyschedulemanagement.R;

import static kr.ac.yonsei.therapyschedulemanagement.Fragments.Home_Fragment.linear_recycle_block;

public class HomeMonthSchedule_Adapter extends RecyclerView.Adapter<HomeMonthSchedule_Adapter.CustomViewHolder> {

    private ArrayList<HomeMonth_CardItem> cardItemsMonth;

    public HomeMonthSchedule_Adapter(ArrayList<HomeMonth_CardItem> cardItems) {
        this.cardItemsMonth = cardItems;
    }

    @NonNull
    @Override
    public HomeMonthSchedule_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_month_schedule, parent, false);
        HomeMonthSchedule_Adapter.CustomViewHolder customViewHolder = new HomeMonthSchedule_Adapter.CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMonthSchedule_Adapter.CustomViewHolder holder, int position) {
        holder.txt_start_time_month.setText(cardItemsMonth.get(position).getStartTimeMonth());
        holder.txt_end_time_month.setText(cardItemsMonth.get(position).getEndTimeMonth());
        holder.txt_day_month.setText(cardItemsMonth.get(position).getDayMonth());
        holder.itemView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        if (cardItemsMonth.get(position).getTherapyMonth().equals("1")) {
            holder.txt_therapy_month.setText("감각통증");
            holder.img_line.setImageResource(R.drawable.vertical_line_red_icon);
        } else if (cardItemsMonth.get(position).getTherapyMonth().equals("2")) {
            holder.txt_therapy_month.setText("언어치료");
            holder.img_line.setImageResource(R.drawable.vertical_line_blue_icon);
        } else if (cardItemsMonth.get(position).getTherapyMonth().equals("3")) {
            holder.txt_therapy_month.setText("놀이치료");
            holder.img_line.setImageResource(R.drawable.vertical_line_yellow_icon);
        } else if (cardItemsMonth.get(position).getTherapyMonth().equals("4")) {
            holder.txt_therapy_month.setText("물리치료");
            holder.img_line.setImageResource(R.drawable.vertical_line_green_icon);
        } else if (cardItemsMonth.get(position).getTherapyMonth().equals("5")) {
            holder.txt_therapy_month.setText("작업치료");
            holder.img_line.setImageResource(R.drawable.vertical_line_orange_icon);
        }

        if (cardItemsMonth.size() != 0) {
            linear_recycle_block.setVisibility(View.INVISIBLE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            int year = date.getYear() + 2000 - 100;
            int month = date.getMonth() + 1;

            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.fragment1_option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_modify:
                                Toast.makeText(holder.itemView.getContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.item_delete:
                                ArrayList<String> keyList = new ArrayList<>();
                                holder.mDatabase.getReference(holder.mAuth.getCurrentUser().getEmail().replace(".", "_"))
                                        .child("Calendar")
                                        .child(year + "/" + month + "/" + cardItemsMonth.get(position).getDayMonth())
                                        .child("Therapy_schedule")
                                        .child("data_save")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot dataSnapshotKey : dataSnapshot.getChildren()) {
                                                    keyList.add(dataSnapshotKey.getKey());
                                                }


                                                holder.mDatabase.getReference(holder.mAuth.getCurrentUser().getEmail().replace(".", "_"))
                                                        .child("Calendar")
                                                        .child(year + "/" + month + "/" + cardItemsMonth.get(position).getDayMonth())
                                                        .child("Therapy_schedule")
                                                        .child("data_save")
                                                        .child(keyList.get(position))
                                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(holder.itemView.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                        // refreshAdapter();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != cardItemsMonth ? cardItemsMonth.size() : 0);
    }

    public void remove(int pos) {
        try {
            cardItemsMonth.remove(pos);
            notifyItemRemoved(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_therapy_month, txt_start_time_month, txt_end_time_month, txt_day_month;
        private ImageView img_line;
        private FirebaseDatabase mDatabase;
        private FirebaseAuth mAuth;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txt_therapy_month = itemView.findViewById(R.id.txt_therapy_month);
            this.txt_start_time_month = itemView.findViewById(R.id.txt_start_time_month);
            this.txt_end_time_month = itemView.findViewById(R.id.txt_end_time_month);
            this.txt_day_month = itemView.findViewById(R.id.txt_day_month);
            this.img_line = itemView.findViewById(R.id.img_item_line);
            this.mDatabase = FirebaseDatabase.getInstance();
            this.mAuth = FirebaseAuth.getInstance();
        }
    }
}