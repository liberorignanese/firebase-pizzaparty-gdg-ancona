package it.gdg.ancona.android.firebasepizzaparty;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import it.gdg.ancona.android.firebasepizzaparty.utils.ChatItem;

public class ChatActivity extends AppCompatActivity implements ChildEventListener, OnCompleteListener<Void> {

    private ChatAdapter mAdapter;
    private LinearLayoutManager mLayouManager;
    private DatabaseReference mReference;
    private EditText mNewMessage;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        mNewMessage = (EditText)findViewById(R.id.new_message);

        mLayouManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(mLayouManager);
        mAdapter = new ChatAdapter();
        recycler.setAdapter(mAdapter);
        FirebaseCrash.log("ChatActivity_created");

        mReference = FirebaseDatabase.getInstance().getReference().child("chat");
        mReference.addChildEventListener(this);
        mReference.push();

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchRemoteConfigValuesValues();

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                if(key.equals("message")){
                    String message = getIntent().getExtras().getString(key);
                    ChatItem.send(mReference, "RINO", message);
                }
            }
        }

    }


    // Remote Config keys
    private static final String LOADING_PHRASE_CONFIG_KEY = "loading_phrase";
    private static final String STATUS_BAR_COLOR_KEY = "status_bar_color";
    private void fetchRemoteConfigValuesValues() {
        String loading_phrase = mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY);
        Toast.makeText(ChatActivity.this, loading_phrase, Toast.LENGTH_SHORT).show();

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(this, this);
        // [END fetch_config_with_callback]
    }

    private void setStatusBarColor() {
        String status_bar_color_value = mFirebaseRemoteConfig.getString(STATUS_BAR_COLOR_KEY);
        int status_bar_color = Color.parseColor(status_bar_color_value);
        getWindow().setStatusBarColor(status_bar_color);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        ChatItem chatItem = ChatItem.loadFromDataSnaphot(dataSnapshot);
        String key = dataSnapshot.getKey();
        mAdapter.addMessage(key, chatItem);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        mAdapter.removeMessage(key);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void onClickSendMessage(View view) {
        String txt = mNewMessage.getText().toString();
        if(!txt.equals("")){
            ChatItem.send(mReference, "RINO", txt);
            mNewMessage.setText("");
        }
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            Toast.makeText(ChatActivity.this, "Fetch Succeeded",
                    Toast.LENGTH_SHORT).show();
            // Once the config is successfully fetched it must be activated before newly fetched
            // values are returned.
            mFirebaseRemoteConfig.activateFetched();
        } else {
            Toast.makeText(ChatActivity.this, "Fetch Failed",
                    Toast.LENGTH_SHORT).show();
        }
        setStatusBarColor();
    }

    class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

        LinkedHashMap<String, ChatItem> messages;

        ChatAdapter(){
            super();
            messages = new LinkedHashMap<>();
        }

        int getItemPosition(String key){
            ArrayList<String> list = new ArrayList<>(messages.keySet());
            return list.indexOf(key);
        }

        ChatItem getItemAt(int position){
            ArrayList<String> list = new ArrayList<>(messages.keySet());
            String key = list.get(position);
            return messages.get(key);
        }

        void addMessage(String key, ChatItem chatItem){
            messages.put(key, chatItem);
            int index = getItemPosition(key);
            notifyItemInserted(index);
            mLayouManager.scrollToPosition(index);
        }

        void removeMessage(String key){
            int index = getItemPosition(key);
            if(index > -1){
                messages.remove(key);
                notifyItemRemoved(index);
            }
        }

        @Override
        public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if(viewType == ME){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout_me, parent, false);
            }else{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout_not_me, parent, false);
            }
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatViewHolder holder, int position) {
            ChatItem message = getItemAt(position);
            holder.username.setText(message.username);
            holder.message.setText(message.message);
            holder.time.setText(message.getDate());
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        final int ME = 0;
        final int NOT_ME = 1;

        @Override
        public int getItemViewType(int position) {
            ChatItem chatItem = getItemAt(position);
            return chatItem.itsMe() ? ME : NOT_ME;
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        TextView message;
        TextView time;

        ChatViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            message = (TextView) itemView.findViewById(R.id.message);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }

}
