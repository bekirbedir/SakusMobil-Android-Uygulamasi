package bekirbedir.sakusmobilapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bekir on 16.1.2018.
 */

public class FavoriteBus extends BaseAdapter {
    Context context;
    ArrayList<Otobus> otobusler = new ArrayList<Otobus>();

    public FavoriteBus(Context context, ArrayList<Otobus> otobusler) {
        this.context = context;
        this.otobusler = otobusler;
    }

    @Override
    public int getCount() {
        return otobusler.size();
    }

    @Override
    public Object getItem(int i) {
        return otobusler.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inf.inflate(R.layout.fav_row_layout, viewGroup, false);
        Otobus gecici = otobusler.get(i);

        TextView btnOtobus = rowView.findViewById(R.id.btnOtobus);
        btnOtobus.setText(String.valueOf(otobusler.get(i).otobusNo));

        return  rowView;
    }

}
