package lc.swedbank.homework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<EntryItem> item;

    private String entryName;
    private String entryAddress;

    public LocationsAdapter(Context context, ArrayList<EntryItem> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (item.get(position).isSection) {
            convertView = inflater.inflate(R.layout.listview_section, parent, false);
            TextView tvSectionTitle = (TextView) convertView.findViewById(R.id.textSeparator);
            tvSectionTitle.setText(item.get(position).region);
        } else {
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
            TextView tvItemName = (TextView) convertView.findViewById(R.id.item_name);
            TextView tvItemAddress = (TextView) convertView.findViewById(R.id.item_address);

            entryName = item.get(position).name;
            tvItemName.setText(entryName);
            entryAddress = item.get(position).address;
            tvItemAddress.setText(entryAddress);
        }

        return convertView;
    }
}
