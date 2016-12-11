package it.gdg.ancona.android.firebasepizzaparty;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import it.gdg.ancona.android.firebasepizzaparty.utils.ChatItem;

public class MainActivity extends FireBaseChatActivity {

    private EditText mNewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewMessage = (EditText)findViewById(R.id.new_message);
    }

    @Override
    public void logout() {
        super.logout();
        findViewById(R.id.send_layout).setVisibility(View.GONE);
    }

    @Override
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





}
