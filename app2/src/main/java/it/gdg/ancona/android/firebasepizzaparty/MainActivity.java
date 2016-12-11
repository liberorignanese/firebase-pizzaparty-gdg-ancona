package it.gdg.ancona.android.firebasepizzaparty;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import it.gdg.ancona.android.firebasepizzaparty.utils.ChatItem;

public class MainActivity extends FireBaseChatActivity {

    private LinearLayoutManager mLayouManager;
    private ChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        mLayouManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(mLayouManager);
        mAdapter = new ChatAdapter();
        recycler.setAdapter(mAdapter);
    }

    @Override
    public void logout() {
        mAdapter.clearAll();
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
            Picasso.with(MainActivity.this)
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
