package com.example.android.handystalker.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.handystalker.R;

public class PlaceNameFragment extends DialogFragment {

    String mAddress;
    String mPlaceId;
    String mPlaceName;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PlaceNameListener {
        public void onDialogPositiveClick(DialogFragment dialog, String placeId, String name);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    PlaceNameListener mListener;


    // Override onAttach to to instantiate the PlaceNameListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mListener = (PlaceNameListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement PlaceNameListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View placeLayout = inflater.inflate(R.layout.place_name_dialog, null);
        TextView address = (TextView) placeLayout.findViewById(R.id.address_textView);
        final EditText nameEdit= (EditText) placeLayout.findViewById(R.id.my_place_name);
        address.setText(mAddress);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Set name of your place")
                .setView(placeLayout)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mPlaceName = nameEdit.getText().toString();
                        mListener.onDialogPositiveClick(PlaceNameFragment.this, mPlaceId, mPlaceName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(PlaceNameFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    // Setter methods for keeping track of the steps and ingredients of current recipe
    public void setAddress(String adress) {
        mAddress = adress;
    }
    public void setmPlaceId(String placeId) {
        mPlaceId = placeId;
    }

}
