package gmedia.net.id.psplocationfinder.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.psplocationfinder.R;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListPelangganAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListPelangganAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_pelanggan, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_pelanggan, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem2());
        holder.tvItem2.setText(itemSelected.getItem3());
        holder.tvItem3.setText(itemSelected.getItem4()+" / "+itemSelected.getItem5());
        holder.tvItem4.setText((itemSelected.getItem6().length()>0) ? itemSelected.getItem6()+" ; "+itemSelected.getItem7(): "");

        return convertView;

    }
}
