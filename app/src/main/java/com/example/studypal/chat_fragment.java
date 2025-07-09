package com.example.studypal;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;


public class chat_fragment extends Fragment {

    private ChatDatabase db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public chat_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chat_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static chat_fragment newInstance(String param1, String param2) {
        chat_fragment fragment = new chat_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_fragment, container, false);
    }

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private MessageAdapter adapter;
    private List<Message> messageList;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        db = ChatDatabase.getInstance(requireContext());

        // Initialize views
        GeminiService geminiService = new GeminiService();

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);

        // Setup chat
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(adapter);


        new Thread(() -> {
            List<Message> oldMessages = db.messageDao().getAllMessages();
            requireActivity().runOnUiThread(() -> {
                messageList.addAll(oldMessages);
                adapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            });
        }).start();

        sendButton.setOnClickListener(v -> {
            String userMsg = messageInput.getText().toString().trim();
            if (!userMsg.isEmpty()) {
                addMessage(userMsg, Message.TYPE_USER);
                messageInput.setText("");

                geminiService.sendMessage(userMsg, new GeminiService.GeminiCallback() {
                    @Override
                    public void onResponse(String reply) {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                addMessage(reply, Message.TYPE_BOT);
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                addMessage("Error: " + error, Message.TYPE_BOT);
                            });
                        }
                    }
                });
            }
        });
    }


    private void addMessage(String text, int type) {
        if (!isAdded()) return;
        Message message = new Message(text, type);
        messageList.add(message);
        adapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);

        new Thread(() -> db.messageDao().insert(message)).start();
    }

}