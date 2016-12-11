package it.gdg.ancona.android.firebasepizzaparty;

import android.os.Bundle;
import android.view.Menu;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.gdg.ancona.android.firebasepizzaparty.utils.ChatItem;

public class MainActivity extends FireBaseChatActivity implements ChildEventListener {

    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        initDatabase();
        return true;
    }

    private void initDatabase(){
        if(mAuth.getCurrentUser() !=null){
            mReference = FirebaseDatabase.getInstance().getReference().child("chat");
            mReference.addChildEventListener(this);
            mReference.push();
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        ChatItem chatItem = ChatItem.loadFromDataSnaphot(dataSnapshot);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        int i = 0;
        i++;

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        int i = 0;
        i++;

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        int i = 0;
        i++;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

        int i = 0;
        i++;
    }
}
