package in.noobgames.apitest;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    EditText searchEdit;
    ListView searchListView;
    ArrayList<Product> productList = new ArrayList<>();
    SearchAdapter searchAdapter;
    FrameLayout progressLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_search, container, false);
        searchEdit = (EditText) parent.findViewById(R.id.search_bar);
        progressLayout = (FrameLayout) parent.findViewById(R.id.progressLayout);
        searchListView = (ListView) parent.findViewById(R.id.search_list_view);
        searchAdapter = new SearchAdapter(getActivity(), R.layout.item_search);

        searchListView.setAdapter(searchAdapter);

        return parent;
    }

    public void onSearchButtonClick() {
        progressLayout.setVisibility(View.VISIBLE);
        String searchTerm = searchEdit.getText().toString();

        SearchTask task = new SearchTask();
        task.execute(searchTerm);
    }

    class SearchAdapter extends ArrayAdapter<View> {
        int mResource;

        public SearchAdapter(Context context, int resource) {
            super(context, resource);
            mResource = resource;
        }

        @Override
        public int getCount() {
            return productList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(mResource, null);
            }
            TextView nameText = (TextView) convertView.findViewById(R.id.name);
            TextView companyText = (TextView) convertView.findViewById(R.id.company);
            TextView typeText = (TextView) convertView.findViewById(R.id.type);

            Product product = productList.get(position);

            nameText.setText(product.getName());
            companyText.setText(product.getCompany());
            typeText.setText(product.getType());

            return convertView;
        }
    }

    class SearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... search) {
            String searchTerm = search[0];
            String result = null;
            try {
                result = callAPI(searchTerm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //call API
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("API Result", result);
                productList.clear();
                //Parsing
                try {
                    JSONObject root = new JSONObject(result);
                    JSONObject resultRoot = root.getJSONObject("Result");
                    String searchTerm = resultRoot.getString("SearchTerm");
                    JSONArray resultSet = resultRoot.getJSONArray("ResultSet");
                    for (int i = 0; i < resultSet.length(); i++) {
                        JSONObject productObject = resultSet.getJSONObject(i);
                        Product product = new Product();
                        product.setId(productObject.getInt("Product_ID"));
                        product.setName(productObject.getString("ProductName"));
                        product.setCompany(productObject.getString("CompanyName"));
                        product.setForm(productObject.getString("Form"));
                        product.setType(productObject.getString("Type"));
                        product.setPacking(productObject.getString("Packing"));

                        productList.add(product);
                    }
                    searchAdapter.notifyDataSetChanged();
                    progressLayout.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else
                Log.d("API Results", "Null Result");
        }

        private String callAPI(String searchTerm) throws IOException {
            String originalUrl = "<Your URL here>";
            String finalUrl = originalUrl + "?ProductName=" + searchTerm;
            URL url = new URL(finalUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder str = new StringBuilder();

            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                str.append(line + "\n");
            }
            return str.toString();
        }

    }
}
