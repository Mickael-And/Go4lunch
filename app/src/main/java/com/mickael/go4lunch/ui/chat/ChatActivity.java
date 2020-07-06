package com.mickael.go4lunch.ui.chat;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.dao.MessageFirestoreDAO;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.Message;
import com.mickael.go4lunch.data.model.User;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.Listener {
    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.activity_mentor_chat_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_mentor_chat_text_view_recycler_view_empty)
    TextView textViewRecyclerViewEmpty;
    @BindView(R.id.activity_mentor_chat_message_edit_text)
    TextInputEditText editTextMessage;
    @BindView(R.id.chat_toolbar)
    Toolbar toolbar;

    // FOR DATA
    // 2 - Declaring Adapter and data
    private ChatAdapter mentorChatAdapter;
    @Nullable
    private User modelCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        this.configureStatusBar();
        this.configureToolbar();

        this.configureRecyclerView();
        this.getCurrentUserFromFirestore();
    }

    /**
     * Configuration of {@link Toolbar}.
     */
    private void configureToolbar() {
        this.setSupportActionBar(this.toolbar);
    }

    /**
     * Configuration of the statusBar.
     */
    private void configureStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));
        }
    }


    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.activity_mentor_chat_send_button)
    public void onClickSendMessage() {
        Message message = new Message(this.editTextMessage.getText().toString(), this.modelCurrentUser);
        this.createFirestoreMessage(message);
        this.editTextMessage.setText("");
    }

    /**
     * Create the message in firestore.
     *
     * @param message message to create
     */
    private void createFirestoreMessage(Message message) {
        MessageFirestoreDAO.createMessage(message).addOnSuccessListener(aVoid -> {
            Log.d(this.getClass().getSimpleName(), String.format(Locale.getDefault(), "Message %s is written", message.toString()));
        });
    }

    // --------------------
    // REST REQUESTS
    // --------------------
    // 4 - Get Current User from Firestore
    private void getCurrentUserFromFirestore() {
        UserFirestoreDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> modelCurrentUser = documentSnapshot.toObject(User.class));
    }

    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView() {
        //Configure Adapter & RecyclerView
        this.mentorChatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageFirestoreDAO.getAllMessages()), Glide.with(this), this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        mentorChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(mentorChatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.mentorChatAdapter);
    }

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.mentorChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
