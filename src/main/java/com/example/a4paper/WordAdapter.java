package com.example.a4paper;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.a4paper.R;
import java.util.List;

public class WordAdapter extends ArrayAdapter<word_data> {
/*
    private final int resourceId;
    private final List<word_data> List;
    private final Context context;

    public WordAdapter(Context context, int textViewResourceId, List<word_data> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
*/

/*
    public WordAdapter(Context context,int textViewResourceId, List<word_data> List) {
        this.context = context;
        this.List = List;
        resourceId = textViewResourceId;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
*/

    private int resourceId;
    public WordAdapter(Context context, int textViewResourceId, List<word_data> word_data){
        super(context,textViewResourceId,word_data);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
/*
        String word = (String) getItem(position);//获取实例
*/
/*        int progress = (int) getItem(position);*/
        View view;
        word_data data = getItem(position);
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        }
        else{
            view = convertView;
        }
        NumberProgressView wordProgress = (NumberProgressView) view.findViewById(R.id.np_numberProgressBar);
        TextView text = (TextView) view.findViewById(R.id.word);
        wordProgress.setProgress(data.getTimes());
        text.setText(data.getWord());
        return view;
    }

}
