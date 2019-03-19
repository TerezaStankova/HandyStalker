package com.example.android.handystalker.ui.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.utilities.AppExecutors;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    private Context mContext;
    //private PlaceBufferResponse mPlaces;
    // Member variable for the Database
    private AppDatabase mDb;
    List<String> mNames;
    List<String> mPlaceIds;

    /**
     * Constructor using the context and PlaceBufferResponse places
     *
     * @param context the calling context/activity
     */
    public PlacesAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new PlaceViewHolder that holds a View with the item_place_card layout
     */
    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.place_item, parent, false);
        return new PlaceViewHolder(view);
    }

    /**
     * Binds the data from a particular position in the cursor to the corresponding view holder
     * The delete icon deletes data from database for the specific place
     *
     * @param holder   The PlaceViewHolder instance corresponding to the required position
     * @param position The current position that needs to be loaded with data
     */
    @Override
    public void onBindViewHolder(PlaceViewHolder holder, final int position) {
        String placeName = mNames.get(position);
        //String placeAddress = mPlaces.get(position).getAddress().toString();
        holder.nameTextView.setText(placeName);
        //holder.addressTextView.setText(placeAddress);
        holder.deleteIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String placeId = mPlaceIds.get(position);
                // Delete from database
                openDeleteDialog(placeId);
            }
        });
    }

    private void openDeleteDialog(final String placeId) {
        // Ask the user if the deletion should continue.
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(R.string.delete)
                .setMessage(R.string.delete_place)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity

                        // Delete from database
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mDb.placeDao().deleteByPlaceId(placeId);
                                Log.d("delete task","deleted task: ");

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, mContext.getString(R.string.item_deleted), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    public void refreshPlaces(List<String> placeIds, List<String> names){
        mNames = names;
        mPlaceIds = placeIds;
        if (mPlaceIds != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    /**
     * Returns the number of places
     *
     * @return Number of places, or 0 if null
     */
    @Override
    public int getItemCount() {
        if(mPlaceIds==null) return 0;
        return mPlaceIds.size();
    }

    /**
     * PlaceViewHolder class for the recycler view item
     */
    class PlaceViewHolder extends RecyclerView.ViewHolder {

        ImageView deleteIcon;
        TextView nameTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
            nameTextView = itemView.findViewById(R.id.place_name);
        }

    }

    //Set the database
    public void setDatabase(AppDatabase myDatabase) {
        mDb = myDatabase;
    }

}
