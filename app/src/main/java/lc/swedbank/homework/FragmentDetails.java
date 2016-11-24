package lc.swedbank.homework;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.GONE;

/**
 * Created by leo on 16/11/16.
 */

public class FragmentDetails extends Fragment {
    private static final String ARGUMENT_NAME = "name";
    private static final String ARGUMENT_ADDRESS = "address";
    private static final String ARGUMENT_AVAIL = "avail";
    private static final String ARGUMENT_INFO = "info";
    private static final String ARGUMENT_NCASH = "ncash";
    private static final String ARGUMENT_CS = "cs";
    private static final String ARGUMENT_LAT = "lat";
    private static final String ARGUMENT_LON = "lon";


    public static FragmentDetails newInstance(String name, String address, String avail,
                                              String info, String ncash, String cs, String lat, String lon) {
        Bundle args = new Bundle();
        args.putString(ARGUMENT_NAME, name);
        args.putString(ARGUMENT_ADDRESS, address);
        args.putString(ARGUMENT_AVAIL, avail);
        args.putString(ARGUMENT_INFO, info);
        args.putString(ARGUMENT_NCASH, ncash);
        args.putString(ARGUMENT_CS, cs);
        args.putString(ARGUMENT_LAT, lat);
        args.putString(ARGUMENT_LON, lon);

        final FragmentDetails fd = new FragmentDetails();
        fd.setArguments(args);
        return fd;
    }

    public FragmentDetails() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        final TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        final TextView tvAddress = (TextView) view.findViewById(R.id.tv_address);
        final TextView tvAvail = (TextView) view.findViewById(R.id.tv_availability);
        final TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
        final TextView tvNcash = (TextView) view.findViewById(R.id.tv_ncash);
        final TextView tvCs = (TextView) view.findViewById(R.id.tv_cs);
        final Button button = (Button) view.findViewById(R.id.button_show_map);

        final Bundle args = getArguments();
        tvName.setText(args.getString(ARGUMENT_NAME));
        tvAddress.setText(args.getString(ARGUMENT_ADDRESS));

        //hide blank TextViews

        if (args.getString(ARGUMENT_AVAIL).equals("")) {
            tvAvail.setVisibility(GONE);
        } else {
            tvAvail.setText(args.getString(ARGUMENT_AVAIL));
        }

        if (args.getString(ARGUMENT_INFO).equals("")) {
            tvInfo.setVisibility(GONE);
        } else {
            tvInfo.setText(args.getString(ARGUMENT_INFO));
        }

        if (args.getString(ARGUMENT_NCASH).equals("")) {
            tvNcash.setVisibility(GONE);
        } else {
            tvNcash.setText(args.getString(ARGUMENT_NCASH));
        }

        if (args.getString(ARGUMENT_CS).equals("")) {
            tvCs.setVisibility(GONE);
        } else {
            tvCs.setText(args.getString(ARGUMENT_CS));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = args.getString(ARGUMENT_LAT);
                String longitude = args.getString(ARGUMENT_LON);
                String label = args.getString(ARGUMENT_NAME);
                String name = args.getString(ARGUMENT_ADDRESS);
                String uri = "geo:0,0?q=" + latitude + "," + longitude + "(" + label + ", " + name + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "You have no maps installed on your device",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
