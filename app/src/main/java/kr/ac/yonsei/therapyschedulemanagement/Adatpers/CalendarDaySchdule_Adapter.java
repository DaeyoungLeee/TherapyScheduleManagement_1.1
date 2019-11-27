package kr.ac.yonsei.therapyschedulemanagement.Adatpers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import kr.ac.yonsei.therapyschedulemanagement.CardItem;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class CalendarDaySchdule_Adapter extends RecyclerView.Adapter<CalendarDaySchdule_Adapter.CustomViewHolder> {

    private ArrayList<CardItem> cardItems;
    OnItemClickedListener listener;


    public static interface OnItemClickedListener {
        public void onItemClick(CustomViewHolder holder, View view, int position);
        void onDeleteButtonClick(int position);
    }

    public CalendarDaySchdule_Adapter(ArrayList<CardItem> cardItems) {
        this.cardItems = cardItems;
    }

    @NonNull
    @Override
    public CalendarDaySchdule_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_dayschedule, parent,  false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CalendarDaySchdule_Adapter.CustomViewHolder holder, int position) {
        holder.txt_start_time.setText(cardItems.get(position).getStartTime());
        holder.txt_end_time.setText(cardItems.get(position).getEndTime());
        String a = cardItems.get(position).getTherapy();
        if (a.equals("1")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_red_icon);
            holder.txt_therapy.setText("감각통합치료");
        }else if (a.equals("2")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_blue_icon);
            holder.txt_therapy.setText("언어치료");
        }else if (a.equals("3")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_yellow_icon);
            holder.txt_therapy.setText("놀이치료");
        }else if (a.equals("4")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_green_icon);
            holder.txt_therapy.setText("물리치료");
        }else if (a.equals("5")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_orrange_icon);
            holder.txt_therapy.setText("작업치료");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                return true;
            }
        });

        holder.setOnItemClickedListener(listener);

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", "onClick: num" + holder.getAdapterPosition());
                if (listener != null) {
                    listener.onDeleteButtonClick(holder.getAdapterPosition());
                }
            }
        });
    }
    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.listener = listener;

    }
    @Override
    public int getItemCount() {
        return (null != cardItems ? cardItems.size() : 0);
    }

    public void remove(int pos) {
        try {
            cardItems.remove(pos);
            notifyItemRemoved(pos);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_therapy, txt_start_time, txt_end_time;
        private ImageView img_delete;
        private ImageView img_calendar_dot;
        OnItemClickedListener listener;
        private FirebaseDatabase mDatabase;
        private FirebaseAuth mAuth;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txt_therapy = itemView.findViewById(R.id.txt_therapy);
            this.txt_start_time = itemView.findViewById(R.id.txt_start_time);
            this.txt_end_time = itemView.findViewById(R.id.txt_end_time);
            this.img_calendar_dot = itemView.findViewById(R.id.img_calendar_dot);
            this.img_delete = itemView.findViewById(R.id.img_delete);
            this.mAuth = FirebaseAuth.getInstance();
            this.mDatabase = FirebaseDatabase.getInstance();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(CustomViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setOnItemClickedListener(OnItemClickedListener listener) {
            this.listener = listener;
        }
    }


}