package rae_dylan_zack_sahnon.cst2335_final_project.Movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import rae_dylan_zack_sahnon.cst2335_final_project.R;

public class MovieFragment extends Fragment {
    private ListView daList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState){

        View theView = (View) inflater.inflate(R.layout.moviefrag, container,false);
        daList = theView.findViewById(R.id.listId);


        return inflater.inflate(R.layout.moviefrag,container,false);
    }
}
