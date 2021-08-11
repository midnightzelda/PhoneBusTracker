package rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import rae_dylan_zack_sahnon.cst2335_final_project.R;

public class OCMainFragment extends Fragment {
    private ListView lv;
    private ArrayList<String> feed;
    private String stopNo;

    public OCMainFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View infoView = inflater.inflate(R.layout.oc_frag_stops, container, false);
        Log.i("Fragment", "in Fragment");

        Bundle bundle = getArguments();
        feed = bundle.getStringArrayList("routeNo");
        stopNo = bundle.getString("stopNo");


        if (feed.size() > 0) {
        Log.i("Fragment", feed.get(0));

        OCViewAdapter ocViewAdapter = new OCViewAdapter(getActivity().getApplicationContext());
        lv = infoView.findViewById(R.id.oc_stop_list);
        lv.setAdapter(ocViewAdapter);

        } return infoView;
    }


public class OCViewAdapter extends ArrayAdapter<String> {
    public OCViewAdapter(Context ctx) {
        super(ctx, 0);
    }

    public int getCount() {
        return feed.size();
    }

    public String getItem(int position) {
        return feed.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = getLayoutInflater();
        View result = inflater.inflate(R.layout.oc_route, null);//needs to be a list view in a different activity to show all the info
        final TextView ocItem = result.findViewById(R.id.oc_item);
        final String full = getItem(position);
        Log.d("Adapter",full);
        String forDisplay = "Bus: " + full;
        ocItem.setText(forDisplay); // get the string at position
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OCMainFragment.this.getContext(), OCInfo.class);
                intent.putExtra("busInfo", full);
                intent.putExtra("stopNo", stopNo);
                startActivity(intent);
            }
        });
        return result;
    }

    public long getItemId(int position){
        return position;
    }
}
    }
