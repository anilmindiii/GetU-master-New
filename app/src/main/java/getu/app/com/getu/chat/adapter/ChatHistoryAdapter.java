package getu.app.com.getu.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import getu.app.com.getu.R;
import getu.app.com.getu.chat.activity.ChatActivity;
import getu.app.com.getu.chat.model.ChatHistory;
import getu.app.com.getu.common_activity.NotificationUserDetailActivity;
import getu.app.com.getu.freelancer_side.model.NotificationListModel;
import getu.app.com.getu.user_side_package.listener.CustomItemClickListener;

/**
 * Created by abc on 02/01/2018.
 */

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {
    private List<ChatHistory> chatHistories;
    private Context mContext;
    private CustomItemClickListener clickListener = null;

    public ChatHistoryAdapter(ArrayList<ChatHistory> chatHistories, Context mContext) {
        this.chatHistories = chatHistories;
        this.mContext = mContext;
    }

    public void setClickListener(CustomItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ChatHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list, parent, false);
        return new ChatHistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ChatHistoryAdapter.ViewHolder holder, final int position) {
        ChatHistory chatHistory = chatHistories.get(position);

        if (chatHistory.profilePic != null && !chatHistory.profilePic.equals("")) {
            Picasso.with(mContext).load(chatHistory.profilePic).placeholder(R.drawable.user).into(holder.profile_image);
        } else {
            Picasso.with(mContext).load(R.drawable.user).fit().into(holder.profile_image);
        }

        holder.tv_for_name.setText(chatHistory.name);
        long timeStamp = (long) chatHistory.timeStamp;

        DateFormat f = new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ss.mmm'Z'");
        System.out.println(f.format(timeStamp));

        String CurrentString = f.format(timeStamp);
        String date = CurrentString.substring(0, 10);

        holder.tv_for_notificationTime.setText(date);
        if (chatHistory.message.contains("firebasestorage.googleapis.com/v0/b/getu")) {
            holder.tv_for_message.setText("Image");
        }else {
            holder.tv_for_message.setText(chatHistory.message);
        }
    }

    @Override
    public int getItemCount() {
        return chatHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profile_image;
        private TextView tv_for_name, tv_for_notificationTime, tv_for_message;

        ViewHolder(View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            tv_for_name = itemView.findViewById(R.id.tv_for_name);
            tv_for_notificationTime = itemView.findViewById(R.id.tv_for_notificationTime);
            tv_for_message = itemView.findViewById(R.id.tv_for_message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatHistory chatHistory = chatHistories.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("USER_ID", chatHistory.uid);
                    intent.putExtra("FULLNAME", chatHistory.name);
                    intent.putExtra("PROFILE_PIC", chatHistory.profilePic);
                    mContext.startActivities(new Intent[]{intent});
                }
            });

        }
    }

}
