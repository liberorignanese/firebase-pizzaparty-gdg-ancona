package it.gdg.ancona.android.firebasepizzaparty;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.gdg.ancona.android.firebasepizzaparty.utils.ChatItem;

public class MainActivity extends FireBaseChatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                for (String key : getIntent().getExtras().keySet()) {
                    if(key.equals("message")){
                        String message = getIntent().getExtras().getString(key);
                        ChatItem chatItem = new ChatItem(message, currentUser);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chat");
                        chatItem.send(reference);
                    }
                }
            }
        }
    }

}
