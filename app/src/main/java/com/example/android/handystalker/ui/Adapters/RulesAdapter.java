package com.example.android.handystalker.ui.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.RuleEntry;
import com.example.android.handystalker.model.Rule;
import com.example.android.handystalker.utilities.AppExecutors;

import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class RulesAdapter extends RecyclerView.Adapter<RulesAdapter.RuleViewHolder> {

    private Context mContext;
    private List<Rule> mRules;
    private List<RuleEntry> mRulesEntries;
    // Member variable for the Database
    private AppDatabase mDb;

    //Boolean to distinguish between handy and stalking rules
    private boolean handy = false;

    /*
     *Constructor using the context and list of Rules
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
     * @return A new RuleViewHolder that holds a View with the rule_item layout
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
     * @param holder   The RuleViewHolder instance corresponding to the required position
     * @param position The current position that needs to be loaded with data
     */
    @Override
    public void onBindViewHolder(RulesAdapter.RuleViewHolder holder, final int position) {

        String arrivalPlace = mRules.get(position).getArrivalPlace();
        String typeRule = mRules.get(position).getType();

        //Set the View for stalking rules
        if (!handy) {
        String contactName = mRules.get(position).getName();
        String departurePlace = mRules.get(position).getDeparturePlace();
        String myStalker = mContext.getString(R.string.my_stalker) + " " + contactName;
        holder.nameTextView.setText(myStalker);

        if(arrivalPlace != null){
            holder.arriveTextView.setVisibility(View.VISIBLE);
            String myArrival = mContext.getString(R.string.arrival_to) +" "+ arrivalPlace;
            holder.arriveTextView.setText(myArrival);
        } else {
            holder.arriveTextView.setVisibility(View.GONE);
        }

        if(departurePlace != null){
            holder.departTextView.setVisibility(View.VISIBLE);
            String myDeparture = mContext.getString(R.string.departure_to) + " " + departurePlace;
            holder.departTextView.setText(myDeparture);
        } else {
            holder.departTextView.setVisibility(View.GONE);
        }

        if(typeRule.equals("sms")){
            holder.typeIcon.setImageResource(R.drawable.ic_sms_green_24dp);
        } else {
            holder.typeIcon.setImageResource(R.drawable.ic_notifications_active_green_24dp);
        }
        }
        //Set the View for handy rules
        else if (handy) {
            String myPlace = mContext.getString(R.string.place) +" "+ arrivalPlace;
            holder.nameTextView.setText(myPlace);
            holder.arriveTextView.setVisibility(View.VISIBLE);
            String NET = "net";
            String NETOFF = "netoff";
            String SOUND = "sound";
            String SOUNDOFF = "soundoff";
            String WIFI = "wifi";
            String WIFIOFF = "wifioff";
            if(typeRule.equals(WIFI) || typeRule.equals(WIFIOFF)){
                holder.typeIcon.setImageResource(R.drawable.ic_wifi_green_24dp);
                if(typeRule.equals(WIFI)){
                    holder.arriveTextView.setText(R.string.wifi_on_turn);
                }else{
                    holder.arriveTextView.setText(R.string.wifi_of_turn);
                }
            } else if(typeRule.equals(SOUND) || typeRule.equals(SOUNDOFF)) {
                holder.typeIcon.setImageResource(R.drawable.ic_volume_up_green_24dp);
                if(typeRule.equals(WIFI)){
                    holder.arriveTextView.setText(R.string.sound_on_turn);
                }else{
                    holder.arriveTextView.setText(R.string.sound_off_turn);
                }
            } else if(typeRule.equals(NET) || typeRule.equals(NETOFF)) {
                holder.typeIcon.setImageResource(R.drawable.ic_net_green_24dp);
                if(typeRule.equals(WIFI)){
                    holder.arriveTextView.setText(R.string.data_on_turn);
                }else{
                    holder.arriveTextView.setText(R.string.data_off_turn);
                }
            }

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
     * Returns the number of rules
     *
     * @return Number of rules, or 0 if null
     */
    @Override
    public int getItemCount() {
        if(mRules == null) return 0;
        return mRules.size();
    }

    /**
     * RuleViewHolder class for the recycler view item
     */
    class RuleViewHolder extends RecyclerView.ViewHolder {

        ImageView deleteIcon;
        ImageView typeIcon;
        TextView nameTextView;
        TextView arriveTextView;
        TextView departTextView;

        public RuleViewHolder(View itemView) {
            super(itemView);
            deleteIcon = itemView.findViewById(R.id.delete_rule_icon);
            typeIcon = itemView.findViewById(R.id.type_icon);
            nameTextView = itemView.findViewById(R.id.rule_contact_name);
            arriveTextView = itemView.findViewById(R.id.rule_arrival);
            departTextView = itemView.findViewById(R.id.rule_depart);
        }
    }


    //Setters to set the database, handy boolean and List of Rules
    public void setDatabase(AppDatabase myDatabase) {
        mDb = myDatabase;
    }
    public void setHandy(boolean mHandy) {
        handy = mHandy;
    }

    public void setRules(List<Rule> rules) {
        mRules = rules;
        notifyDataSetChanged();
    }


    //List of Rules is retrieved from the database using AsyncTask
    public void setRulesFromDatabase(final List<RuleEntry> ruleEntries) {
        if (ruleEntries != null) {
        mRulesEntries = ruleEntries;
        new FetchRulesTask().execute();
        }
    }

        class FetchRulesTask extends AsyncTask<String, Void, List<Rule>> {

            @Override
        protected List<Rule> doInBackground(String... params){

            final List<Rule> mRuleDatabase = newArrayList();
            final List<RuleEntry> ruleEntries = mRulesEntries;
            // Select from database

                    for (int i = 0; i < ruleEntries.size(); i++) {
                        System.out.println("mRuleEntry" + i + ruleEntries.get(i).getContactId());

                        final int id = ruleEntries.get(i).getId();

                        final Integer idContact = ruleEntries.get(i).getContactId();
                        final Integer idArrival = ruleEntries.get(i).getArrivalId();
                        final Integer idDeparture = ruleEntries.get(i).getDepartureId();

                        final String name = mDb.contactDao().findNameForContactId(idContact);
                        final String arrival;
                        final String departure;
                        if (idArrival != null) {
                            arrival = mDb.placeDao().findPlaceNameById(idArrival);
                        } else {
                            arrival = null;
                        }

                        if (idDeparture != null) {
                            departure = mDb.placeDao().findPlaceNameById(idDeparture);
                        } else {
                            departure = null;
                        }

                        final String type = mDb.ruleDao().findTypeByRuleId(id);
                        Log.d("collect rule data", "contact: ");

                        Rule newRule = new Rule(id, arrival, name, departure, type);

                        mRuleDatabase.add(newRule);
                        Log.d("collect rule data", "contact: " + mRuleDatabase.get(i).getArrivalPlace());
                    }

            return mRuleDatabase;
        }

        @Override
        protected void onPostExecute(List<Rule> mRuleDatabase) {

            Log.d("collect rule data","contact: " + mRuleDatabase.size());

            if (mRuleDatabase != null && mRuleDatabase.size() != 0) {
                setRules(mRuleDatabase);
            }
        }
}
}

