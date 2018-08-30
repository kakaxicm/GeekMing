package com.kakaxicm.geekming.barrage;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kakaxicm.geekming.R;

/**
 * Created by chenming on 2018/8/30
 */
public class ClientBarrageAdapter extends BarrageAdapter<ClientBarrageModel> {

    private Context context;

    public ClientBarrageAdapter(Context c) {
        super();
        context = c;
    }

    @Override
    public int[] getViewTypeArray() {
        int type[] = {0};
        return type;
    }

    @Override
    public int getSingleLineHeight() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_danmu, null);
        //指定行高
        view.measure(View.MeasureSpec.UNSPECIFIED, 0);

        return view.getMeasuredHeight();
    }

    @Override
    public View getView(ClientBarrageModel entry, View convertView) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_danmu, null);
            vh = new ViewHolder();
            vh.tv = convertView.findViewById(R.id.tv_danmu);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.tv.setText(entry.getContent());
        vh.tv.setTextColor(entry.getTextColor());

        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }
}
