package getu.app.com.getu.chat.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.model.Chatting;
import getu.app.com.getu.user_side_package.listener.CustomItemClickListener;
import getu.app.com.getu.user_side_package.model.UserList;

/**
 * Created by abc on 02/01/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chatting> chattings;
    private Context mContext;
    private CustomItemClickListener clickListener = null;
    private Session session;
    private Chatting chatting;

    public ChatAdapter(ArrayList<Chatting> chattings, Context mContext) {
        this.chattings = chattings;
        this.mContext = mContext;
        session = new Session(mContext);
    }

    public void setClickListener(CustomItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        chatting = chattings.get(viewType);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_layout, parent, false);
        return new ChatAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ChatAdapter.ViewHolder holder, final int position) {
        chatting = chattings.get(position);

        long timeStamp = (long) chatting.timeStamp;

        DateFormat f = new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ss.mmm'Z'");
        System.out.println(f.format(timeStamp));

        String CurrentString = f.format(timeStamp);
        String date = CurrentString.substring(0, 10);

        int hourOfDay = Integer.parseInt(CurrentString.substring(11, 13));
        int minute = Integer.parseInt(CurrentString.substring(14,16));

        String status, minutes;

        if (hourOfDay > 12) {
            hourOfDay -= 12;
            status = "PM";
        } else if (hourOfDay == 0) {
            hourOfDay += 12;
            status = "AM";
        } else if (hourOfDay == 12) {
            status = "PM";
        } else {
            status = "AM";
        }

        minutes = (minute < 10) ? "0" + minute : String.valueOf(minute);
        String time = hourOfDay + ":" + minutes + " " + status;

        if (chatting.uid.equals(session.getUserID())) {
            if (chatting.message.contains("firebasestorage.googleapis.com/v0/b/getu")){
                holder.layout_for_senderItem.setVisibility(View.GONE);
                holder.layout_for_senderImage.setVisibility(View.VISIBLE);

                if (chatting.message != null && !chatting.message.equals("")) {
                    Picasso.with(mContext).load(chatting.message).placeholder(R.drawable.placeholder_img).into(holder.iv_for_senderImage);
                } else {
                    Picasso.with(mContext).load(R.drawable.placeholder_img).fit().into(holder.iv_for_senderImage);
                }
            }else {
                holder.layout_for_senderImage.setVisibility(View.GONE);
                holder.layout_for_senderItem.setVisibility(View.VISIBLE);
                holder.tv_for_sender.setText(chatting.message);
            }
            holder.tv_for_senderTime.setText(date+" "+time);
            holder.tv_for_senderTimeImage.setText(date+" "+time);
            holder.layout_for_reciver.setVisibility(View.GONE);
            holder.layout_for_sender.setVisibility(View.VISIBLE);

        } else {

            if (chatting.message.contains("firebasestorage.googleapis.com/v0/b/getu")){
                holder.layout_for_reciverItem.setVisibility(View.GONE);
                holder.layout_for_reciverImages.setVisibility(View.VISIBLE);

                if (chatting.message != null && !chatting.message.equals("")) {
                    Picasso.with(mContext).load(chatting.message).placeholder(R.drawable.placeholder_img).into(holder.iv_for_reciverImage);
                } else {
                    Picasso.with(mContext).load(R.drawable.placeholder_img).fit().into(holder.iv_for_reciverImage);
                }
            }else {
                holder.layout_for_reciverImages.setVisibility(View.GONE);
                holder.layout_for_reciverItem.setVisibility(View.VISIBLE);
                holder.tv_for_reciver.setText(chatting.message);
            }

            holder.tv_for_reciverTime.setText(date+" "+time);
            holder.tv_for_reciverTimeImage.setText(date+" "+time);
            holder.layout_for_sender.setVisibility(View.GONE);
            holder.layout_for_reciver.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return chattings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_for_sender, tv_for_reciver,tv_for_reciverTime,tv_for_senderTime,tv_for_reciverTimeImage,tv_for_senderTimeImage;
        private CardView layout_for_reciver, layout_for_sender;
        private ImageView iv_for_senderImage,iv_for_reciverImage;
        private LinearLayout layout_for_senderItem,layout_for_senderImage,layout_for_reciverItem,layout_for_reciverImages;

        ViewHolder(View itemView) {
            super(itemView);

            tv_for_sender = itemView.findViewById(R.id.tv_for_sender);
            tv_for_reciver = itemView.findViewById(R.id.tv_for_reciver);
            layout_for_sender = itemView.findViewById(R.id.layout_for_sender);
            layout_for_reciver = itemView.findViewById(R.id.layout_for_reciver);
            tv_for_reciverTime = itemView.findViewById(R.id.tv_for_reciverTime);
            tv_for_senderTime = itemView.findViewById(R.id.tv_for_senderTime);
            iv_for_senderImage = itemView.findViewById(R.id.iv_for_senderImage);
            iv_for_reciverImage = itemView.findViewById(R.id.iv_for_reciverImage);
            layout_for_senderItem = itemView.findViewById(R.id.layout_for_senderItem);
            layout_for_senderImage = itemView.findViewById(R.id.layout_for_senderImage);
            tv_for_senderTimeImage = itemView.findViewById(R.id.tv_for_senderTimeImage);
            layout_for_reciverItem = itemView.findViewById(R.id.layout_for_reciverItem);
            layout_for_reciverImages = itemView.findViewById(R.id.layout_for_reciverImages);
            tv_for_reciverTimeImage = itemView.findViewById(R.id.tv_for_reciverTimeImage);

            iv_for_senderImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chatDeleteDialog(mContext,getAdapterPosition());
                }
            });
            iv_for_reciverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chatDeleteDialog(mContext,getAdapterPosition());
                }
            });
        }
    }

    private void chatDeleteDialog(Context mContext, int position) {
        final Dialog dialog = new Dialog(this.mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_zoomimage);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        PhotoView iv_for_image = dialog.findViewById(R.id.iv_for_image);

        Chatting chat = chattings.get(position);

        if (chat.message != null && !chat.message.equals("")) {
            Picasso.with(mContext).load(chat.message).placeholder(R.drawable.placeholder_img).into(iv_for_image);
        } else {
            Picasso.with(mContext).load(R.drawable.placeholder_img).fit().into(iv_for_image);
        }

        dialog.show();
    }
}
