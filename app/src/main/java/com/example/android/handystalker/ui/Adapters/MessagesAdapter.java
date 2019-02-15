package com.example.android.handystalker.ui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
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

import com.example.android.handystalker.database.MessagesEntry;
import com.example.android.handystalker.model.Message;

import com.example.android.handystalker.utilities.AppExecutors;

import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ContactViewHolder> {

private Context mContext;
private List<Message> mMessages;
// Member variable for the Database
private AppDatabase mDb;

/**
 * Constructor using the context and list of contacts
 *
 * @param context the calling context/activity
 */
public MessagesAdapter(Context context, List<Message> contacts) {
        this.mContext = context;
        this.mMessages = contacts;
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
        View view = inflater.inflate(R.layout.message_item, parent, false);
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
        String text = mMessages.get(position).getText();
        holder.textTextView.setText(text);


        holder.deleteIcon.setOnClickListener(new View.OnClickListener()
        {
                @Override
                public void onClick(View v)
                        {
                final int messageId = mMessages.get(position).getMessageId();
                // Delete from database
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                mDb.messageDao().deleteByMessageId(messageId);
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
        });
        }


/**
 * Returns the number of items in the list
 *
 * @return Number of items in the list, or 0 if null
 */
@Override
public int getItemCount() {
        if(mMessages == null) return 0;
        return mMessages.size();
        }

/**
 * ContactViewHolder class for the recycler view item
 */
class ContactViewHolder extends RecyclerView.ViewHolder {

    ImageView deleteIcon;
    TextView textTextView;
    

    public ContactViewHolder(View itemView) {
        super(itemView);
        deleteIcon = itemView.findViewById(R.id.delete_message_icon);
        textTextView = itemView.findViewById(R.id.message_text);
    }
}

    /**
     * Setters to set database and list of contacts
     */
    public void setDatabase(AppDatabase myDatabase) {
        mDb = myDatabase;
    }
    public void setMessages(List<Message> messages) {
        mMessages = messages;
        notifyDataSetChanged();
    }

    /**
     * Retrieve List of Message objects from List of MessageEntries
     */
    public void setContactsFromDatabase(List<MessagesEntry> messagesEntries) {
        if (messagesEntries != null) {
            List<Message> mMessagesDatabase = newArrayList();

            for (int i = 0; i < messagesEntries.size(); i++) {
                System.out.println("mMessageEntry" + i + messagesEntries.get(i).getText());

                String text = messagesEntries.get(i).getText();
                int id = messagesEntries.get(i).getId();

                Message newMessage = new Message(id, text);
                mMessagesDatabase.add(newMessage);
            }
            setMessages(mMessagesDatabase);
        }
    }

}
