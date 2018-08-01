package com.example.android.handystalker.ui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.ContactsEntry;
import com.example.android.handystalker.database.RuleEntry;
import com.example.android.handystalker.model.Contact;
import com.example.android.handystalker.model.Rule;
import com.example.android.handystalker.utilities.AppExecutors;

import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class RulesAdapter extends RecyclerView.Adapter<RulesAdapter.RuleViewHolder> {

    private Context mContext;
    private List<Rule> mRules;
    // Member variable for the Database
    private AppDatabase mDb;

//private Task<PlaceBufferResponse> mPlaces;
    /**
     * Constructor using the context and the db cursor
     *
     * @param context the calling context/activity
     */
    public RulesAdapter(Context context, List<Rule> rules) {
        this.mContext = context;
        this.mRules = rules;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new PlaceViewHolder that holds a View with the item_place_card layout
     */
    @Override
    public RulesAdapter.RuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rule_item, parent, false);
        return new RulesAdapter.RuleViewHolder(view);
    }

    /**
     * Binds the data from a particular position in the cursor to the corresponding view holder
     *
     * @param holder   The PlaceViewHolder instance corresponding to the required position
     * @param position The current position that needs to be loaded with data
     */
    @Override
    public void onBindViewHolder(RulesAdapter.RuleViewHolder holder, final int position) {
        String contactName = mRules.get(position).getName();
        String arrivalPlace = mRules.get(position).getArrivalPlace();
        String departurePlace = mRules.get(position).getDeparturePlace();
        holder.nameTextView.setText(contactName);

        if(arrivalPlace != null){
            holder.arriveTextView.setVisibility(View.VISIBLE);
            holder.arriveTextView.setText(arrivalPlace);
        } else {
            holder.arriveTextView.setVisibility(View.GONE);
        }

        if(departurePlace != null){
            holder.departTextView.setVisibility(View.VISIBLE);
            holder.departTextView.setText(departurePlace);
        } else {
            holder.departTextView.setVisibility(View.GONE);
        }

        holder.deleteIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final int ruleId = mRules.get(position).getRuleId();
                // Delete from database
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.ruleDao().deleteByRuleId(ruleId);
                        Log.d("delete task","deleted task: ");
                    }
                });
            }
        });
    }


    /**
     * Returns the number of items in the cursor
     *
     * @return Number of items in the cursor, or 0 if null
     */
    @Override
    public int getItemCount() {
        if(mRules == null) return 0;
        return mRules.size();
    }

    /**
     * PlaceViewHolder class for the recycler view item
     */
    class RuleViewHolder extends RecyclerView.ViewHolder {

        ImageView deleteIcon;
        TextView nameTextView;
        TextView arriveTextView;
        TextView departTextView;

        public RuleViewHolder(View itemView) {
            super(itemView);
            deleteIcon = (ImageView) itemView.findViewById(R.id.delete_rule_icon);
            nameTextView = (TextView) itemView.findViewById(R.id.rule_contact_name);
            arriveTextView = (TextView) itemView.findViewById(R.id.rule_arrival);
            departTextView = (TextView) itemView.findViewById(R.id.rule_depart);
        }
    }

    public void setDatabase(AppDatabase myDatabase) {
        mDb = myDatabase;
    }
    public void setRules(List<Rule> rules) {
        mRules = rules;
        notifyDataSetChanged();
    }

    /*public void setRulesFromDatabase(List<RuleEntry> ruleEntries) {
        if (ruleEntries != null) {
            List<Rule> mContactDatabase = newArrayList();

            for (int i = 0; i < ruleEntries.size(); i++) {
                System.out.println("mRuleEntry" + i + ruleEntries.get(i).getContactId());

                final int id = ruleEntries.get(i).getId();

                final String arrival;
                final String departure;

                final int idContact = ruleEntries.get(i).getContactId();
                int idArrival = ruleEntries.get(i).getArrivalId();
                int idDeparture = ruleEntries.get(i).getDepartureId();

                // Select from database
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        String name = mDb.contactDao().findNameForContactId(idContact);
                        Log.d("collect contact data","contact: ");
                    }
                });

                //Rule newRule = new Rule(id, arrival, name, departure);
                //mContactDatabase.add(newRule);
            }
            setRules(mContactDatabase);
        }
    }*/

}