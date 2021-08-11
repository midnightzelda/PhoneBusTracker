package rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import rae_dylan_zack_sahnon.cst2335_final_project.R;

/**
 * Fragment class that inflates the food_item_details.xml to display details passed in the bundle
 */
public class FoodDBFragment extends Fragment {

    /**
     * empty constructor
     */
    public FoodDBFragment(){    }

    /**
     * create view for fragment
     * @param inflater - LayoutInflater object
     * @param container - View container
     * @param savedInstanceState - Bundle passed to the method
     * @return view displaying the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.food_item_details, container, false);

        //list of text views and set their texts from bundle
        TextView itemText = (TextView) view.findViewById(R.id.itemValue);
        itemText.setText(getArguments().getString("label"));

        TextView brandText = (TextView) view.findViewById(R.id.brandValue);
        brandText.setText(getArguments().getString("brand"));

        TextView energyText = (TextView) view.findViewById(R.id.kcalValue);
        energyText.setText(Double.toString(getArguments().getDouble("energy")));

        TextView carbsText = (TextView) view.findViewById(R.id.carbValue);
        carbsText.setText(Double.toString(getArguments().getDouble("carbs")));

        TextView proteinText = (TextView) view.findViewById(R.id.proteinValue);
        proteinText.setText(Double.toString(getArguments().getDouble("protein")));

        TextView fatText = (TextView) view.findViewById(R.id.fatValue);
        fatText.setText(Double.toString(getArguments().getDouble("fat")));

        TextView fiberText = (TextView) view.findViewById(R.id.fiberValue);
        fiberText.setText(Double.toString(getArguments().getDouble("fiber")));

        //bundle strings
        Bundle passInfo = new Bundle(getArguments());

        final Intent intent = new Intent(getActivity(), FoodItemFavorites.class);
        intent.putExtras(passInfo);

        TextView tagValue = (TextView) view.findViewById(R.id.tagValue);
        TextView tagLabel = (TextView) view.findViewById(R.id.tagLabel);

        Button favoriteButton = (Button) view.findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                intent.putExtra("function", new Integer(5));
                startActivity(intent);
            }
        });

        //initializing Button from layout and adding functionality
        Button tagButton = (Button) view.findViewById(R.id.tagButton);
        if(passInfo.getBoolean("fromFavorites")) {
            tagButton.setVisibility(View.VISIBLE);
            favoriteButton.setText(R.string.food_remove_favorites);
            tagLabel.setVisibility(View.VISIBLE);
            tagValue.setVisibility(View.VISIBLE);

            tagValue.setText(passInfo.getString("tag"));
        }

        //dialog created n tag button press to display alert with text input
        tagButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                AlertDialog.Builder imageBuilder = new AlertDialog.Builder(getActivity());

                imageBuilder.setView(R.layout.food_tag_alert);


                imageBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Dialog getText = (Dialog) dialog;

                        EditText viewText = (EditText) getText.findViewById(R.id.edit_query);

                        String tagInput = viewText.getText().toString();

                        intent.putExtra("tag", tagInput);
                        intent.putExtra("function", new Integer(1));
                        startActivity(intent);
                    }
                });
                imageBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                // Create the AlertDialog
                AlertDialog imageDialog = imageBuilder.create();
                imageDialog.show();
            }
        });

        return view;
    }
}
