package com.example.abhisheikh.locationplusalarm.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhisheikh.locationplusalarm.Alarm;
import com.example.abhisheikh.locationplusalarm.R;
import com.example.abhisheikh.locationplusalarm.activity.EditAlarmActivity;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PNRFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PNRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PNRFragment extends Fragment {

    private final static String API_KEY = "t7lo26p3";
    LatLng latLng;
    String locationName;
    String doj;
    Context context;

    EditText enterPnrEditText;
    Button searchButton, addButton;
    LinearLayout linearLayout;
    TextView dateTextView, destinationTextView;
//    TextView dummy;

    private OnFragmentInteractionListener mListener;

    public PNRFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PNRFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PNRFragment newInstance(String param1, String param2) {
        PNRFragment fragment = new PNRFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pnr, container, false);

        context = getActivity();
        enterPnrEditText = (EditText)view.findViewById(R.id.enterPNREditText);
        searchButton = (Button)view.findViewById(R.id.searchPNRButton);
        addButton = (Button)view.findViewById(R.id.addAlarmThroughPNRButton);
        linearLayout = (LinearLayout)view.findViewById(R.id.OverallLinearLayout);
        dateTextView = (TextView)view.findViewById(R.id.dateTextView);
        destinationTextView = (TextView)view.findViewById(R.id.destinationTextView);
//        dummy = (TextView)view.findViewById(R.id.dummyText);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveLocationFromPnr();
            }
        });
        enterPnrEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButton.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm alarm = new Alarm(latLng, locationName);
                Intent intent = new Intent(getContext(),EditAlarmActivity.class);
                intent.putExtra("alarm",alarm);
                startActivityForResult(intent,1);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                Alarm alarm = data.getParcelableExtra("alarm");
                String position = data.getStringExtra("position");
                Intent intent = new Intent();
                intent.putExtra("alarm",alarm);
                intent.putExtra("position",position);
                ((Activity)context).setResult(RESULT_OK,intent);
                ((Activity)context).finish();
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void retrieveLocationFromPnr(){
        String PNR = enterPnrEditText.getText().toString();
        String serverUrl = "http://api.railwayapi.com/pnr_status/pnr/"+PNR+"/apikey/"+API_KEY+"/";
        new FetchPNR().execute(serverUrl);
    }

    private class FetchPNR extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        ProgressDialog dialog;
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element

            dialog = ProgressDialog.show(getContext(), "",
                    "Loading. Please wait...", true);
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method
                HttpGet httpget = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Content = Client.execute(httpget, responseHandler);

            } catch (ClientProtocolException e) {
                Error = e.getMessage();
                cancel(true);
            } catch (IOException e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog

            String location = jsonParse(Content);
            //Toast.makeText(getContext(),location,LENGTH_SHORT).show();
            if(location!=null) {
                String url = "http://maps.google.com/maps/api/geocode/json?address=" + location.replace(" ","%20") + "&sensor=false";
                new FetchLatLng().execute(url);
            }

            dialog.cancel();

        }

        protected String jsonParse(String content) {
//            dummy.setText(content);
            JSONObject baseObject = null;
            String location = null;
            try {
                baseObject = new JSONObject(content);
                int responseCode = baseObject.getInt("response_code");
                if(responseCode!=200){
//                    Toast.makeText(getContext(),R.string.invalid_pnr,LENGTH_SHORT).show();
                    return null;
                }
                //Toast.makeText(getContext(),"Valid",LENGTH_SHORT).show();
                doj = baseObject.getString("doj");
                JSONObject reservationUpto = baseObject.getJSONObject("reservation_upto");
                location = reservationUpto.getString("name");
                locationName = location;
                displayData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return location;
        }

        private void displayData(){
            searchButton.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            dateTextView.setText(doj);
            destinationTextView.setText(locationName);
        }

    }

    private class FetchLatLng extends AsyncTask<String,Void,Void>{
        private HttpClient Client;
        private String Content;
        private String Error = null;

        @Override
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method
                HttpGet httpget = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Client = new DefaultHttpClient();
                Content = Client.execute(httpget, responseHandler);

            } catch (ClientProtocolException e) {
                Error = e.getMessage();
                cancel(true);
            } catch (IOException e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            jsonParse(Content);

        }

        private void jsonParse(String Content){
            try {
                JSONObject baseOject = new JSONObject(Content);
                String status = baseOject.getString("status");
                if(status.equals("OK")){
                    JSONArray results= baseOject.getJSONArray("results");
                    JSONObject resultObj = results.getJSONObject(0);
                    JSONObject loc = resultObj.getJSONObject("geometry").getJSONObject("location");
                    double lat = loc.getDouble("lat");
                    double lng = loc.getDouble("lng");
                    latLng = new LatLng(lat,lng);

//                    Toast.makeText(getContext(),"Lat: "+lat+" Lng: "+lng,LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
