package com.example.app.Messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;

import java.util.List;

public class ChatAdapter extends SelectableAdapter<ChatAdapter.ViewHolder> {

    private List<Chat> mArrayList;
    private Context mContext;
    private ViewHolder.ClickListener clickListener;



    public ChatAdapter (Context context, List<Chat> arrayList,ViewHolder.ClickListener clickListener) {
        this.mArrayList = arrayList;
        this.mContext = context;
        this.clickListener = clickListener;

    }

    // Create new views
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_merchant, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView,clickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.merchantName.setText(mArrayList.get(position).getName());
//        if (isSelected(position)) {}
//        viewHolder.userPhoto.setImageResource(mArrayList.get(position).getImage());
        if (mArrayList.get(position).isOnline()){
            viewHolder.merchantOpen.setText(mContext.getResources().getString(R.string.open));
            viewHolder.merchantOpen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dot_green, 0, 0, 0);
        }else{
            viewHolder.merchantOpen.setText(mContext.getResources().getString(R.string.closed));
            viewHolder.merchantOpen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dot_red, 0, 0, 0);
        }

//        viewHolder.tvLastChat.setText(mArrayList.get(position).getLastChat());
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {

        public TextView merchantName;
        public TextView merchantAddress;
        public TextView merchantOpen;
        public TextView merchantDistance;
        public TextView showShop;
        public TextView showDirections;

//        public TextView tvTime;
//        public TextView tvLastChat;
//        public ImageView userPhoto;
        public boolean online = false;
//        private final View onlineView;
//        public CheckBox checked;
        private ClickListener listener;
        //private final View selectedOverlay;


        public ViewHolder(View itemLayoutView, ClickListener listener) {
            super(itemLayoutView);

            this.listener = listener;

            merchantName = (TextView) itemLayoutView.findViewById(R.id.name);
            merchantAddress = (TextView) itemLayoutView.findViewById(R.id.address);
            merchantOpen = (TextView) itemLayoutView.findViewById(R.id.open);
            merchantDistance = (TextView) itemLayoutView.findViewById(R.id.distance);
            showShop = (TextView) itemLayoutView.findViewById(R.id.shop);
            showDirections = (TextView) itemLayoutView.findViewById(R.id.direction);

            //selectedOverlay = (View) itemView.findViewById(R.id.selected_overlay);
//            tvTime = (TextView) itemLayoutView.findViewById(R.id.tv_time);
//            tvLastChat = (TextView) itemLayoutView.findViewById(R.id.tv_last_chat);
//            userPhoto = (ImageView) itemLayoutView.findViewById(R.id.iv_user_photo);
//            onlineView = (View) itemLayoutView.findViewById(R.id.online_indicator);
//            checked = (CheckBox) itemLayoutView.findViewById(R.id.chk_list);

            itemLayoutView.setOnClickListener(this);

//            itemLayoutView.setOnLongClickListener (this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition ());
            }
        }

//        @Override
//        public boolean onLongClick (View view) {
//            if (listener != null) {
//                return listener.onItemLongClicked(getAdapterPosition ());
//            }
//            return false;
//        }

        public interface ClickListener {
            public void onItemClicked(int position);

            public boolean onItemLongClicked(int position);

            boolean onCreateOptionsMenu(Menu menu);
        }
    }
}

