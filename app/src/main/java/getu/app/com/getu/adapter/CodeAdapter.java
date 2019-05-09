package getu.app.com.getu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import getu.app.com.getu.R;
import getu.app.com.getu.model.CodeInfo;

import java.util.List;

/**
 * Created by abc on 26/10/2017.
 */

public class CodeAdapter extends RecyclerView.Adapter<CodeAdapter.ViewHolder> {

    private List<CodeInfo> codeList;
    private Context mContext;

    public CodeAdapter(List<CodeInfo> codeList, Context mContext) {
        this.codeList = codeList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.code,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CodeInfo code = codeList.get(position);

        holder.tv_for_code.setText(code.phone_code);
        holder.tv_for_country.setText(code.country_name);

    }

    @Override
    public int getItemCount() {
        return codeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_for_code,tv_for_country;

        ViewHolder(View itemView)  {
            super(itemView);
            tv_for_code = itemView.findViewById(R.id.tv_for_code);
            tv_for_country = itemView.findViewById(R.id.tv_for_country);
        }
    }
}
