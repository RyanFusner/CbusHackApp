package com.example.ryan.cbushackapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListAdapter extends ArrayAdapter<Map<String, String>>
{
    private final Activity context;
    private  final List<Map<String, String>> badges;

    public ListAdapter(Activity context, List<Map<String, String>> badges)
    {
        super(context, R.layout.activity_badge_view, badges);
        this.context = context;
        this.badges = badges;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Takes info from a badge and displays it in the list view
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_badge_view, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.BadgeImage);
        TextView titleTextView = (TextView) rowView.findViewById(R.id.BadgeTitle);
        TextView descriptionTextView = (TextView) rowView.findViewById(R.id.BadgeDescription);

        titleTextView.setText(badges.get(position).get("title"));
        descriptionTextView.setText(badges.get(position).get("description"));

        // If the badge is owned it is given a gold star
        if (Objects.equals(badges.get(position).get("status"), "1"))
        {
            imageView.setImageResource(R.drawable.star);
        }
        else
        {
            imageView.setImageResource((R.drawable.stargray));
        }

        return rowView;
    }
}
