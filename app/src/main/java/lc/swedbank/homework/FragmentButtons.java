package lc.swedbank.homework;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentButtons extends Fragment {
    private static final String URL_LT = "https://ib.swedbank.lt/finder.json";
    private static final String URL_LV = "https://ib.swedbank.lv/finder.json";
    private static final String URL_EE = "https://www.swedbank.ee/finder.json";

    private static final String FILE_LT = "lt.json";
    private static final String FILE_LV = "lv.json";
    private static final String FILE_EE = "ee.json";

    private OnFragmentButtonSelected listener;

    public FragmentButtons() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentButtonSelected) {
            listener = (OnFragmentButtonSelected) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentButtonSelected");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_buttons, container, false);

        final Button buttonLT = (Button) view.findViewById(R.id.button_lt);
        buttonLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFragmentButtonSelected(URL_LT, FILE_LT);
            }
        });

        final Button buttonLV = (Button) view.findViewById(R.id.button_lv);
        buttonLV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFragmentButtonSelected(URL_LV, FILE_LV);
            }
        });

        final Button buttonEE = (Button) view.findViewById(R.id.button_est);
        buttonEE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFragmentButtonSelected(URL_EE, FILE_EE);
            }
        });

        return view;
    }

    public interface OnFragmentButtonSelected {
        void onFragmentButtonSelected(String url, String path);
    }
}
