package com.example.a4paper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class WAdapter extends ArrayAdapter<WordItem> {
    private int resourceID;

    public WAdapter(@NonNull Context context, int resource, @NonNull List<WordItem> objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WordItem wordItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_word);
        checkBox.setText(wordItem.getWord());
        return view;
    }
}
