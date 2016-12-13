package it.gdg.ancona.android.firebasepizzaparty;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import it.gdg.ancona.android.firebasepizzaparty.utils.ChatItem;

/**
 * Created by Libero Rignanese.
 * read license file for more informations.
 */


public abstract class FireBaseChatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ChildEventListener {

    protected FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    private GoogleApiClient mGoogleApiClient;
    protected DatabaseReference mReference;
    private LinearLayoutManager mLayouManager;
    private ChatAdapter mAdapter;
    private EditText mNewMessage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        mLayouManager = new LinearLayoutManager(FireBaseChatActivity.this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(mLayouManager);
        mAdapter = new ChatAdapter();
        recycler.setAdapter(mAdapter);

        mNewMessage = (EditText)findViewById(R.id.new_message);

    }

    private void initDatabase(FirebaseUser currentUser){
        if(currentUser !=null){
            mReference = FirebaseDatabase.getInstance().getReference().child("chat");
            mReference.addChildEventListener(FireBaseChatActivity.this);
            mReference.push();
            login();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        menu.findItem(R.id.action_login).setVisible(currentUser == null);
        menu.findItem(R.id.action_logout).setVisible(currentUser != null);
        ((TextView)findViewById(R.id.google_displayname)).setText(
                currentUser == null ?
                        "DISCONNECTED" : currentUser.getDisplayName());
        Picasso.with(FireBaseChatActivity.this)
                .load(currentUser == null ? null : currentUser.getPhotoUrl())
                .into((ImageView) findViewById(R.id.google_image));
        initDatabase(currentUser);
        return super.onPrepareOptionsMenu(menu);
    }

    public void logout() {
        mAdapter.clearAll();
        findViewById(R.id.send_layout).setVisibility(View.GONE);
    }

    public void login() {
        findViewById(R.id.send_layout).setVisibility(View.VISIBLE);
    }

    public void onClickSendMessage(View view) {
        String txt = mNewMessage.getText().toString();
        if(!txt.equals("")){
            ChatItem chatItem = new ChatItem(txt, mAuth.getCurrentUser());
            chatItem.send(mReference);
            mNewMessage.setText("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_login) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else{
            FirebaseAuth.getInstance().signOut();
            invalidateOptionsMenu();
            logout();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            invalidateOptionsMenu();
                        }else{
                            //TODO
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        ChatItem chatItem = ChatItem.loadFromDataSnaphot(dataSnapshot);
        String key = dataSnapshot.getKey();
        mAdapter.addMessage(key, chatItem);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        mAdapter.removeMessage(key);
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

        void clearAll(){
            messages.clear();
            notifyDataSetChanged();
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
            ChatItem chatItem = getItemAt(position);
            holder.username.setText(chatItem.user.displayName);
            holder.message.setText(chatItem.message);
            holder.time.setText(chatItem.getDate());
            Picasso.with(FireBaseChatActivity.this)
                    .load(chatItem.user.photoURL)
                    .into(holder.photo);
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
            return chatItem.itsMe(mAuth.getCurrentUser()) ? ME : NOT_ME;
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        TextView message;
        TextView time;
        ImageView photo;

        ChatViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            message = (TextView) itemView.findViewById(R.id.message);
            time = (TextView) itemView.findViewById(R.id.time);
            photo = (ImageView) itemView.findViewById(R.id.google_image_message);
        }
    }

}
