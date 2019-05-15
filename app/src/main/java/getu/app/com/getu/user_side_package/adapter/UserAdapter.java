package getu.app.com.getu.user_side_package.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import getu.app.com.getu.user_side_package.acrivity.UserMainActivity;
import getu.app.com.getu.user_side_package.fragment.ChatProfileFragment;
import getu.app.com.getu.user_side_package.fragment.UserHomeFragment;
import getu.app.com.getu.user_side_package.fragment.UserMapFragment;
import getu.app.com.getu.user_side_package.model.UserList;

/**
 * Created by abc on 01/11/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserList> userLists;
    private Context mContext;
    private String userID;

    public UserAdapter(ArrayList<UserList> userLists, Context mContext) {
        this.userLists = userLists;
        this.mContext = mContext;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false);
        return new UserAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, final int position) {
        UserList userList = userLists.get(position);

        if (userList.profileimage != null && !userList.profileimage.equals("")) {
            Picasso.with(mContext).load(userList.profileimage).fit().
                    placeholder(R.drawable.user).into(holder.profile_image);
        }else {
            Picasso.with(mContext).load(R.drawable.user).fit().into(holder.profile_image);}

        holder.tv_for_fullName.setText(userList.fullName);
        holder.tv_for_category.setText(userList.category);
        holder.tv_for_address.setText(userList.address);
        if (userList.onlineStatus.equals("0")){
            holder.iamge.setImageResource(R.drawable.unlike_ico);
        }else {
            holder.iamge.setImageResource(R.drawable.like_icon);
        }
        userID = userList.userId;
    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profile_image,iamge;
        TextView tv_for_fullName, tv_for_category, tv_for_address;

        ViewHolder(View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            tv_for_fullName = itemView.findViewById(R.id.tv_for_fullName);
            tv_for_category = itemView.findViewById(R.id.tv_for_category);
            tv_for_address = itemView.findViewById(R.id.tv_for_address);
            iamge = itemView.findViewById(R.id.iamge);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    UserList userList = userLists.get(getAdapterPosition());
                    ((UserMainActivity) mContext).addFragment(ChatProfileFragment.newInstance(userList,""), true, R.id.framlayout);
                }
            });
        }
    }


}
