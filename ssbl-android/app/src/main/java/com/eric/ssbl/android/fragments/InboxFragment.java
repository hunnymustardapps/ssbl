package com.eric.ssbl.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.ConversationActivity;
import com.eric.ssbl.android.pojos.Conversation;

import java.util.List;

public class InboxFragment extends ListFragment {

    private View v;
    private List<Conversation> _conversations;
    private View.OnClickListener _onNewMessageClick;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        _onNewMessageClick = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////                builder.setTitle("Select a user");
////
////                ListView modeList = (ListView)
////                List<User> users = new ArrayList<User>();
////                users.add(DataManager.getCurUser());
////                modeList.setAdapter(new UserListAdapter(getActivity(), users));
////
////                builder.setView(modeList);
////                final Dialog dialog = builder.create();
////
////                dialog.show();
//
//                LayoutInflater li = LayoutInflater.from(getActivity());
//                View userList = li.inflate(R.layout.prompt_select_user, null);
//
//                List<User> users = new ArrayList<User>();
//                users.add(DataManager.getCurUser());
//                ListView ul = (ListView) userList.findViewById(R.id.prompt_list_user);
//                ul.setAdapter(new UserListAdapter(getActivity(), users));
//                ul.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        // create a new conversation and go to conversation activity
//                        Toast.makeText(getActivity(), "clicky", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
//                adb
//                        .setView(userList)
//                        .setTitle("Choose a user")
//                        .setCancelable(true)
//                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//
//                adb.create().show();
//            }
//        };
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        System.out.println("onCreatevie");

        if (v == null)
            v = inflater.inflate(R.layout.fragment_inbox, container, false);
//        ImageButton createMessage = (ImageButton) v.findViewById(R.id.new_message);

//        _conversations = DataManager.getCurUser().getConversations();
//        if (_conversations != null)
//            setListAdapter(new InboxArrayAdapter(getActivity(), _conversations));

//        createMessage.setOnClickListener(_onNewMessageClick);

        return v;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        Intent i = new Intent(getActivity(), ConversationActivity.class);
        i.putExtra("conversation_index", position);
        startActivity(i);
    }
}
