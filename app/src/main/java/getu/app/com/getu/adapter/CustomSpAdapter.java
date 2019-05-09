package getu.app.com.getu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import getu.app.com.getu.R;
import getu.app.com.getu.model.Category;

/**
 * Created by abc on 09/12/2017.
 */

public class CustomSpAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Category> arrayList;

    public CustomSpAdapter(Context context, ArrayList<Category> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_sp, parent, false);

            holder = new ViewHolder();
            holder.tv_VFname = row.findViewById(R.id.text1);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Category item = arrayList.get(position);
        holder.tv_VFname.setText(item.cName);
        return row;
    }
    class ViewHolder {
        TextView tv_VFname;
    }
}
