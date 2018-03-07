package com.example.iman.gtk.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.iman.gtk.R;
import com.example.iman.gtk.adapters.ReverseFirebaseRecyclerAdapter;
import com.example.iman.gtk.models.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.iman.gtk.util.Constants.FIREBASE_CHILD_CHATS;

public class ChatFragment extends Fragment {

    public static String TAG = "FirebaseUI.chat";

    private DatabaseReference mChatRef;
    private long userId;
    private String mName;
    private String mTime;
    private EditText tvMessageToSend;
    private FloatingActionButton ivSendMessage;
    private RecyclerView mChatRecyclerView;
    private ReverseFirebaseRecyclerAdapter<ChatModel, ChatHolder> mFirebaseRecycleViewAdapter;

    private FirebaseUser mFirebaseUser;

    public static String KEY_NAME = "display_name";

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChatRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_CHILD_CHATS);

        tvMessageToSend = (EditText) view.findViewById(R.id.tvMessageToSend);
        ivSendMessage = (FloatingActionButton) view.findViewById(R.id.ivSendMessage);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            mName =  mFirebaseUser.getDisplayName();
        } else {
            mName = "Anonymous";
        }

        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = tvMessageToSend.getText().toString().trim();
                if (message.length() > 0) {
                    ChatModel chat = new ChatModel(message, mName, userId, System.currentTimeMillis(), mTime);
                    mChatRef.push().setValue(chat)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    } else {

                                    }
                                }
                            });
                    tvMessageToSend.setText("");
                }
            }
        });

        mChatRecyclerView = (RecyclerView) view.findViewById(R.id.rvChat);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(false);

        mChatRecyclerView.setHasFixedSize(false);
        mChatRecyclerView.setLayoutManager(manager);

        Query lastFifty = mChatRef.orderByKey().limitToLast(50);
        mFirebaseRecycleViewAdapter = new ReverseFirebaseRecyclerAdapter<ChatModel, ChatHolder>(
                ChatModel.class, R.layout.item_chat_row, ChatHolder.class, lastFifty) {
            @Override
            protected void populateViewHolder(ChatHolder chatHolder, final ChatModel chatModel, final int i) {
                chatHolder.setName(chatModel.getmName());
                chatHolder.setText(chatModel.getmMessage());
                chatHolder.setTime(chatModel.getFormattedTime());
            }
        };

        mChatRecyclerView.setAdapter(mFirebaseRecycleViewAdapter);
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChatHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView field = (TextView) mView.findViewById(R.id.tvChatName);
            field.setText(name);
        }

        public void setText(String text) {
            TextView field = (TextView) mView.findViewById(R.id.tvChatMessage);
            field.setText(text);
        }

        public void setTime(String time){
            TextView field = (TextView) itemView.findViewById(R.id.tvChatTime);
            field.setText(time);
        }

    }
}
