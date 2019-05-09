package getu.app.com.getu.freelancer_side.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import getu.app.com.getu.R;
import getu.app.com.getu.common_activity.NotificationUserDetailActivity;
import getu.app.com.getu.freelancer_side.model.NotificationListModel;
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.user_side_package.fragment.ChatProfileFragment;
import getu.app.com.getu.user_side_package.listener.CustomItemClickListener;
import getu.app.com.getu.user_side_package.model.MapUserList;
import getu.app.com.getu.user_side_package.model.UserList;

/**
 * Created by abc on 30/12/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationListModel> notificationListModels;
    private Context mContext;
    private CustomItemClickListener clickListener = null;

    public NotificationAdapter(ArrayList<NotificationListModel> notificationListModels, Context mContext) {
        this.notificationListModels = notificationListModels;
        this.mContext = mContext;
    }

    public void setClickListener(CustomItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list, parent, false);
        return new NotificationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder holder, final int position) {
        NotificationListModel notificationListModel = notificationListModels.get(position);

        if (notificationListModel.profileImage != null && !notificationListModel.profileImage.equals("")) {
            Picasso.with(mContext).load(notificationListModel.profileImage).placeholder(R.drawable.user).into(holder.profile_image);
        }else{
            Picasso.with(mContext).load(R.drawable.user).fit().into(holder.profile_image);}

        holder.tv_for_name.setText(notificationListModel.fullName);
        holder.tv_for_notificationTime.setText(notificationListModel.notificationTime);
        holder.tv_for_message.setText(notificationListModel.message);
    }

    @Override
    public int getItemCount() {
        return notificationListModels.size();
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
                    NotificationListModel notificationListModel = notificationListModels.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, NotificationUserDetailActivity.class);
                    intent.putExtra("USER_NOTIFICATION",notificationListModel.userId);
                    mContext.startActivities(new Intent[]{intent});
                }
            });

        }
    }

}
