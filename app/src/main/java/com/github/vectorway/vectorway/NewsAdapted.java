package com.github.vectorway.vectorway;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapted extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<News> objects;

    public NewsAdapted(Context context, ArrayList<News> news) {
        ctx = context;
        objects = news;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void UpdateList(News news){
        objects.add(news);
        notifyDataSetChanged();
    }

    public void UpdateList(ArrayList<News> news){
        objects = news;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView; //Если уже есть созданные, то используем их повторно
        if(view == null){ //Если трюк не удался
            view = lInflater.inflate(R.layout.tem, parent, false);
        }

        News news = (News)getItem(position);

        ((TextView)view.findViewById(R.id.title)).setText(news.title);
        ((TextView)view.findViewById(R.id.info)).setText(news.info);


        TextView tv1 = (view.findViewById(R.id.link));
        tv1.setText(Html.fromHtml("<a href=\""+ news.link + "\">" + "ссылка" + "</a>"));
        tv1.setClickable(true);
        tv1.setMovementMethod (LinkMovementMethod.getInstance());



        return view;
    }
}
