package getu.app.com.getu.user_side_package.adapter;

import android.content.Context;
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
import getu.app.com.getu.user_side_package.listener.CustomItemClickListener;
import getu.app.com.getu.user_side_package.model.UserList;

/**
 * Created by abc on 01/11/2017.
 */

public class MapUserAdapter extends RecyclerView.Adapter<MapUserAdapter.ViewHolder> {
    private List<UserList> mapUserLists;
    private Context mContext;
    private CustomItemClickListener clickListener = null;

    public MapUserAdapter(ArrayList<UserList> mapUserLists, Context mContext) {
        this.mapUserLists = mapUserLists;
        this.mContext = mContext;
    }

    public void setClickListener(CustomItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public MapUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_user_list, parent, false);
        return new MapUserAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MapUserAdapter.ViewHolder holder, final int position) {
        UserList userList = mapUserLists.get(position);

        if (userList.profileimage != null && !userList.profileimage.equals("")) {
            Picasso.with(mContext).load(userList.profileimage).placeholder(R.drawable.user).into(holder.profile_image);
        }else{
            Picasso.with(mContext).load(R.drawable.user).fit().into(holder.profile_image);}

        holder.tv_for_fullName.setText(userList.fullName);
        holder.tv_for_category.setText(userList.category);
        holder.tv_for_address.setText(userList.address);
        holder.tv_for_distance.setText(userList.distance);
        if (userList.onlineStatus.equals("0")){
            holder.iamge.setImageResource(R.drawable.unlike_ico);
        }else {
            holder.iamge.setImageResource(R.drawable.like_icon);
        }
    }

    @Override
    public int getItemCount() {
        return mapUserLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView profile_image,iamge;
        TextView tv_for_fullName, tv_for_category, tv_for_address, tv_for_distance;

         ViewHolder(View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            tv_for_fullName = itemView.findViewById(R.id.tv_for_fullName);
            tv_for_category = itemView.findViewById(R.id.tv_for_category);
            tv_for_address = itemView.findViewById(R.id.tv_for_address);
            tv_for_distance = itemView.findViewById(R.id.tv_for_distance);
            iamge = itemView.findViewById(R.id.iamge);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            UserList mapUserList = mapUserLists.get(getAdapterPosition());
            if (clickListener != null){
                clickListener.callback(getAdapterPosition(),mapUserList);
            }
        }
    }

}
