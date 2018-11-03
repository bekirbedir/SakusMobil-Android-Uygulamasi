package bekirbedir.sakusmobilapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bekir on 7.1.2018.
 */

class Otobus {
    public String getOtobusNo() {
        return otobusNo;
    }

    public void setOtobusNo(String otobusNo) {
        this.otobusNo = otobusNo;
    }

    String otobusNo;

    public Otobus(String otobusNo) {
        this.otobusNo = otobusNo;
    }
}
class ViewYerTutucu {
    TextView ulkeButton;


    public ViewYerTutucu(View view) {
        ulkeButton= (TextView) view.findViewById(R.id.btnOtobus);
    }
}

public class OtobusAdapter extends BaseAdapter{
    Context context;
    ArrayList <Otobus> otobusler = new ArrayList<Otobus>();

    public OtobusAdapter(Context c) {
        this.context = c;

        String [] allBusList= { "1" , "2" , "3" , "4" , "5" , "6" , "7" , "9-A" , "9-B" , "12" ,
                "14" ,"15", "17", "18" , "19" , "19-K" , "20" , "20-A" , "21-K" , "21-C" ,"21-D",
                "22-K" ,"22-C" , "22-D","23" ,"24", "24-H" , "24-K" ,"25", "26" , "27" , "28" , "29" , "54-K"} ;
        for (int i = 0 ; i < allBusList.length ; i++)
        {
            otobusler.add( new Otobus( String.valueOf( allBusList[i] ) ));
            if(i == 9)
            {

            }
        }

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
       /* ViewYerTutucu holder;
        if(rowView == null) {

            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             rowView = inf.inflate(R.layout.row_layout, viewGroup, false);
            holder = new ViewYerTutucu(rowView);
            rowView.setTag(holder);

        }
        else{
            holder = (ViewYerTutucu) rowView.getTag();
        } */
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inf.inflate(R.layout.row_layout, viewGroup, false);
        Otobus gecici = otobusler.get(i);

        TextView btnOtobus = rowView.findViewById(R.id.btnOtobus);
        btnOtobus.setText(String.valueOf(otobusler.get(i).otobusNo));

        return  rowView;



    }
}
