package lc.swedbank.homework;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements FragmentButtons.OnFragmentButtonSelected, FragmentList.OnEntryItemSelected {

    private static String FILE_PATH = "";
    public static boolean IS_CONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        FILE_PATH = this.getFilesDir() + "/";
        updateConnectedFlag();

        FragmentButtons fb = new FragmentButtons();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root_layout, fb)
                    .commit();
        }
    }

    @Override
    public void onFragmentButtonSelected(String url, String fileName) {
        String path = FILE_PATH + fileName;
        File file = new File(path);
        final FragmentList fl = FragmentList.newInstance(url, path);
        updateConnectedFlag();

        if(getResources().getBoolean(R.bool.largeMode) || getResources().getBoolean(R.bool.twoPaneMode)) {
            if (IS_CONNECTED || file.exists()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.second_pane, fl, null)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (IS_CONNECTED || file.exists()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_layout, fl, null)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        }




    }

    @Override
    public void onEntryItemSelected(String name, String address, String avail,
                                    String info, String ncash, String cs, String lat, String lon) {
        if (!name.equals("")) {
            final FragmentDetails fd = FragmentDetails.newInstance(name, address, avail, info, ncash, cs, lat, lon);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_layout, fd, null)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void updateConnectedFlag() {
        NetworkInfo activeInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            IS_CONNECTED = activeInfo.isConnected();
        } else {
            IS_CONNECTED = false;
        }

        Log.i("INTERNET_IS_CONNECTED ", String.valueOf(IS_CONNECTED));
    }
}

