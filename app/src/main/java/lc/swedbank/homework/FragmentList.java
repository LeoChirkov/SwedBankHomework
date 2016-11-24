package lc.swedbank.homework;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FragmentList extends ListFragment {
    private static final String ARGUMENT_URL = "url";
    private static final String ARGUMENT_PATH = "path";
    private static final String COOKIE = "Swedbank-Embedded=android-app";

    private String entryName;
    private String entryAddress;
    private String entryAvail;
    private String entryInfo;
    private String entryNcash;
    private String entryCs;
    private String entryLat;
    private String entryLon;

    private static String FROM_URL = "";
    private static String FILE_PATH = "";
    private static ArrayList<EntryItem> data;
    private static ArrayList<EntryItem> dataWithHeaders;
    private ListView mListView;
    OnEntryItemSelected listener = null;

    public static FragmentList newInstance(String url, String path) {
        final Bundle args = new Bundle();
        args.putString(ARGUMENT_URL, url);
        args.putString(ARGUMENT_PATH, path);
        final FragmentList fl = new FragmentList();
        fl.setArguments(args);

        return fl;
    }

    public FragmentList() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentButtons.OnFragmentButtonSelected) {
            listener = (FragmentList.OnEntryItemSelected) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnEntryItemSelected");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        final Bundle args = getArguments();
        FROM_URL = args.getString(ARGUMENT_URL);
        FILE_PATH = args.getString(ARGUMENT_PATH);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        mListView = (ListView) view.findViewById(R.id.list_view);

        File file = new File(FILE_PATH);

        // Check if file exists and isn't older than 60 min
        // or file exists but there's no Internet connection
        if ((file.exists() && file.lastModified() + 3600000 > System.currentTimeMillis())
                || (file.exists() && !MainActivity.IS_CONNECTED)) {
            // generate data from existing file and pass it to ListView adapter
            dataWithHeaders = createArrayFromFile(FILE_PATH);
            LocationsAdapter adapter = new LocationsAdapter(getActivity(), dataWithHeaders);
            mListView.setAdapter(adapter);
        } else {
            // download new file if it doesn't exist or too old
            new DownloadFileWithCookie().execute();
        }
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        entryName = dataWithHeaders.get(position).name;
        entryAddress = dataWithHeaders.get(position).address;
        entryAvail = dataWithHeaders.get(position).avail;
        entryInfo = dataWithHeaders.get(position).info;
        entryNcash = dataWithHeaders.get(position).ncash;
        entryCs = dataWithHeaders.get(position).cs;
        entryLat = dataWithHeaders.get(position).lat;
        entryLon = dataWithHeaders.get(position).lon;

        l.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        l.setSelector(android.R.color.darker_gray);

        listener.onEntryItemSelected(entryName, entryAddress, entryAvail, entryInfo,
                entryNcash, entryCs, entryLat, entryLon);
    }

    public class DownloadFileWithCookie extends AsyncTask<Void, Void, ArrayList<EntryItem>> {
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading data...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        protected ArrayList<EntryItem> doInBackground(Void... params) {
            // download file to external storage /data/data/package.name/files
            downloadFile(FROM_URL, FILE_PATH);

            return createArrayFromFile(FILE_PATH);
        }

        protected void onPostExecute(ArrayList<EntryItem> result) {
            mProgressDialog.dismiss();

            // pass previously generated data to ListView adapter
            LocationsAdapter adapter = new LocationsAdapter(getActivity(), result);
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (MainActivity.IS_CONNECTED)
            inflater.inflate(R.menu.activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_menu_item:
                new DownloadFileWithCookie().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String downloadFile(String fromURL, String toFile) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        CookieManager cookieManager = new CookieManager(null, null);
        CookieHandler.setDefault(cookieManager);
        try {
            URL url = new URL(fromURL);
            connection = (HttpURLConnection) url.openConnection();

            // set cookie for current connection
            connection.setRequestProperty("Cookie", COOKIE);
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(toFile);

            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    public ArrayList<EntryItem> createArrayFromFile(String filePath) {
        data = new ArrayList<>();
        dataWithHeaders = new ArrayList<>();

        // create a set of regions, which allows only unique values
        Set<String> regions = new HashSet<>();

        File file = new File(filePath);
        StringBuilder result = new StringBuilder();
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONArray jArray = new JSONArray(result.toString());
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                EntryItem item = new EntryItem();
                if (json_data.has("r")) {
                    regions.add(json_data.get("r").toString());
                    item.region = json_data.getString("r");
                }

                item.lat = json_data.getString("lat");
                item.lon = json_data.getString("lon");
                item.address = json_data.getString("a");
                item.type = json_data.getString("t");

                // merge type and name to show in ListView
                switch(item.type) {
                    case "0":
                        item.name = json_data.getString("n") + ", Branch";
                        break;
                    case "1":
                        item.name = json_data.getString("n") + ", ATM";
                        break;
                    case "2":
                        item.name = json_data.getString("n") + ", BNA";
                        break;
                }

                // check the existence of various parameters and pass their value or blank value
                if (json_data.has("av")) {
                    item.avail = json_data.getString("av");
                } else {
                    item.avail = "";
                }
                if (json_data.has("i")) {
                    item.info = json_data.getString("i");
                } else {
                    item.info = "";
                }
                if (json_data.has("ncash") && json_data.getString("ncash").equals("true")) {
                    item.ncash = "No cash";
                } else {
                    item.ncash = "";
                }
                if (json_data.has("cs") && json_data.getString("cs").equals("true")) {
                    item.cs = "Has a coin station";
                } else {
                    item.cs = "";
                }

                data.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // convert set regions to array
        List<String> regionsList = new ArrayList<>(regions);

        // sort regions alphabetically
        Collections.sort(regionsList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        // sort all items by region and address
        Collections.sort(data, new ObjectComparator());


        // create new sorted array with sections and items
        for (int i = 0; i < regionsList.size(); i++) {
            EntryItem section = new EntryItem();
            section.region = regionsList.get(i);
            section.isSection = true;
            dataWithHeaders.add(section);
            for (int j = 0; j < data.size(); j++) {
                EntryItem newItem = data.get(j);
                if (section.region.equals(newItem.region))
                    dataWithHeaders.add(newItem);
            }
        }

        return dataWithHeaders;
    }

    public class ObjectComparator implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {
            EntryItem item1 = (EntryItem) lhs;
            EntryItem item2 = (EntryItem) rhs;

            int stringResult = item1.region.compareTo(item2.region);
            if (stringResult == 0) {
                // strings are equal, sort by address
                return item1.address.compareTo(item2.address);
            } else {
                return stringResult;
            }
        }
    }

    public interface OnEntryItemSelected {
        void onEntryItemSelected(String name, String address, String avail,
                                 String info, String ncash, String cs, String lat, String lon);
    }
}
