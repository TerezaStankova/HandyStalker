package com.example.android.handystalker.ui.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.android.handystalker.model.Contact;
import com.example.android.handystalker.ui.ContactsActivity;
import com.example.android.handystalker.ui.NewContactActivity;
import com.example.android.handystalker.utilities.AppExecutors;

import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

private Context mContext;
private List<Contact> mContacts;
// Member variable for the Database
private AppDatabase mDb;
private final String CONTACTS = "contacts";

//private Task<PlaceBufferResponse> mPlaces;
/**
 * Constructor using the context and the db cursor
 *
 * @param context the calling context/activity
 */
public ContactsAdapter(Context context, List<Contact> contacts) {
        this.mContext = context;
        this.mContacts = contacts;
        }

/**
 * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
 *
 * @param parent   The ViewGroup into which the new View will be added
 * @param viewType The view type of the new View
 * @return A new PlaceViewHolder that holds a View with the item_place_card layout
 */
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

/**
 * Binds the data from a particular position in the cursor to the corresponding view holder
 *
 * @param holder   The PlaceViewHolder instance corresponding to the required position
 * @param position The current position that needs to be loaded with data
 */
@Override
        public void onBindViewHolder(ContactViewHolder holder, final int position) {
        String contactName = mContacts.get(position).getName();
        String contactEmail = mContacts.get(position).getEmail();
        String contactPhone = mContacts.get(position).getPhone();
        holder.nameTextView.setText(contactName);

        if(contactEmail != null){
            holder.emailTextView.setVisibility(View.VISIBLE);
            holder.emailTextView.setText(contactEmail);
        } else {
            holder.emailTextView.setVisibility(View.GONE);
        }

        if(contactPhone != null){
            holder.phoneTextView.setVisibility(View.VISIBLE);
            holder.phoneTextView.setText(contactPhone);
        } else {
            holder.phoneTextView.setVisibility(View.GONE);
        }

        holder.updateIcon.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Contact mContact = mContacts.get(position);
            Intent intent = new Intent(mContext, NewContactActivity.class);
            intent.putExtra(CONTACTS, mContact);
            mContext.startActivity(intent);
        }
    });

        holder.deleteIcon.setOnClickListener(new View.OnClickListener()
        {
                @Override
                public void onClick(View v)
                        {
                final int placeId = mContacts.get(position).getContactId();
                // Delete from database
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                mDb.contactDao().deleteByContactId(placeId);
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
        if(mContacts == null) return 0;
        return mContacts.size();
        }

/**
 * PlaceViewHolder class for the recycler view item
 */
class ContactViewHolder extends RecyclerView.ViewHolder {

    ImageView deleteIcon;
    ImageView updateIcon;
    TextView nameTextView;
    TextView emailTextView;
    TextView phoneTextView;

    public ContactViewHolder(View itemView) {
        super(itemView);
        deleteIcon = (ImageView) itemView.findViewById(R.id.delete_contact_icon);
        updateIcon = (ImageView) itemView.findViewById(R.id.edit_contact_icon);
        nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
        emailTextView = (TextView) itemView.findViewById(R.id.contact_email);
        phoneTextView = (TextView) itemView.findViewById(R.id.contact_phone);
    }
}

    public void setDatabase(AppDatabase myDatabase) {
        mDb = myDatabase;
    }
    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        notifyDataSetChanged();
    }

    public void setContactsFromDatabase(List<ContactsEntry> contactsEntries) {
        if (contactsEntries != null) {
            List<Contact> mContactDatabase = newArrayList();

            for (int i = 0; i < contactsEntries.size(); i++) {
                System.out.println("mPlaceEntry" + i + contactsEntries.get(i).getName());

                String name = contactsEntries.get(i).getName();
                String phone = contactsEntries.get(i).getPhone();
                String email = contactsEntries.get(i).getEmail();
                int id = (int) contactsEntries.get(i).getId();

                Contact newContact = new Contact(id, phone, name, email);
                mContactDatabase.add(newContact);
            }
            setContacts(mContactDatabase);
        }
    }

}
