package com.app.android.handystalker.ui.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.app.android.handystalker.R;
import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.database.ContactsEntry;
import com.app.android.handystalker.model.Contact;
import com.app.android.handystalker.ui.NewContactActivity;
import com.app.android.handystalker.utilities.AppExecutors;

import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

private Context mContext;
private List<Contact> mContacts;
// Member variable for the Database
private AppDatabase mDb;
private final String CONTACTS = "contacts";

/**
 * Constructor using the context and list of contacts
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
 * @return A new PlaceViewHolder that holds a View with the contact_item layout
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
 * @param holder   The ContactViewHolder instance corresponding to the required position
 * @param position The current position that needs to be loaded with data
 */
@Override
        public void onBindViewHolder(ContactViewHolder holder, final int position) {
        String contactName = mContacts.get(position).getName();
        String contactPhone = mContacts.get(position).getPhone();
        holder.nameTextView.setText(contactName);

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
                final int contactId = mContacts.get(position).getContactId();
                // Delete from database
                openDeleteDialog(contactId);
                }
        });
        }

    private void openDeleteDialog(final int contactId) {
        // Ask the user if the deletion should continue.
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(R.string.delete)
                .setMessage(R.string.delete_contact)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity

                        // Delete from database
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mDb.contactDao().deleteByContactId(contactId);
                                Log.d("delete task","deleted task: ");

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "The item was successfully deleted", Toast.LENGTH_LONG).show();
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


/**
 * Returns the number of items in the list
 *
 * @return Number of items in the list, or 0 if null
 */
@Override
public int getItemCount() {
        if(mContacts == null) return 0;
        return mContacts.size();
        }

/**
 * ContactViewHolder class for the recycler view item
 */
class ContactViewHolder extends RecyclerView.ViewHolder {

    ImageView deleteIcon;
    ImageView updateIcon;
    TextView nameTextView;
    TextView phoneTextView;

    public ContactViewHolder(View itemView) {
        super(itemView);
        deleteIcon = itemView.findViewById(R.id.delete_contact_icon);
        updateIcon = itemView.findViewById(R.id.edit_contact_icon);
        nameTextView = itemView.findViewById(R.id.contact_name);
        phoneTextView = itemView.findViewById(R.id.contact_phone);
    }
}

    /**
     * Setters to set database and list of contacts
     */
    public void setDatabase(AppDatabase myDatabase) {
        mDb = myDatabase;
    }
    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        notifyDataSetChanged();
    }


    /**
     * Retrieve List of Contact objects from List of ContactEntries
     */
    public void setContactsFromDatabase(List<ContactsEntry> contactsEntries) {
        if (contactsEntries != null) {
            List<Contact> mContactDatabase = newArrayList();

            for (int i = 0; i < contactsEntries.size(); i++) {
                System.out.println("mPlaceEntry" + i + contactsEntries.get(i).getName());

                String name = contactsEntries.get(i).getName();
                String phone = contactsEntries.get(i).getPhone();
                int id = contactsEntries.get(i).getId();

                Contact newContact = new Contact(id, phone, name);
                mContactDatabase.add(newContact);
            }
            setContacts(mContactDatabase);
        }
    }

}
