package com.example.a5502;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class spinner_adapter extends ArrayAdapter<String> {
    private final Context mContext;
    private String[] mValues;
    private int[] mImageIds;

    public spinner_adapter(Context context, int textViewResourceId, String[] values, int[] imageIds) {
        super(context, textViewResourceId, values);
        this.mContext = context;
        this.mValues = values;
        this.mImageIds = imageIds;
    }

    public void updateData(String[] newData, int[] newImageIds) {
        mValues = newData;
        mImageIds = newImageIds;
        notifyDataSetChanged(); // 更新spinner
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //獲取mContext的實例，從而使應用程序能使用它們
        View itemView = inflater.inflate(R.layout.spinner, parent, false); //加載要顯示的布局文件
        //                               要加載的布局文件,    外層的父視圖,         是否立即加載到父視圖內

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView imageView = itemView.findViewById(R.id.imageView);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = itemView.findViewById(R.id.textView);

        imageView.setImageResource(mImageIds[position]);
        textView.setText(mValues[position]);

        return itemView;
    }
}
