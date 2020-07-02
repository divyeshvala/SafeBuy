package com.example.app.Messaging.Customer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.app.Messaging.ConversationRecyclerView;
import com.example.app.R;
import com.example.app.Utilities.Communication;
import com.example.app.Utilities.PaymentHandler;
import com.example.app.Utilities.util;
import com.example.app.fragment.AddListBottomSheetFragment;
import com.example.app.fragment.FragmentATM;
import com.example.app.model.ATMObject;
import com.example.app.model.MessageObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerConversationActivity extends AppCompatActivity implements AddListBottomSheetFragment.BottomSheetListener {
    public static String TAG = "CustomerConverstionActivity";
    public RecyclerView mRecyclerView;
    public ConversationRecyclerView mAdapter;
    private EditText messageText;
    private String merchantId;
    private FirebaseUser currentUser;
    public List<MessageObject> messagesList = new ArrayList<>();
    private Communication communication;
    AddListBottomSheetFragment addListBottomSheetFragment;
    private final int MODE_CHAT = 0;
    private final int MODE_PAY = 1;
    private int flagMode = MODE_PAY;
    public static String pay_message = "";
    Context mContext;
    private SharedPreferences sharedPreferences;
    private String receiverPAN;
    private ProgressBar payProgress;
    private PaymentHandler paymentHandler;

    private RecyclerView recyclerViewSuggestions;
    private ListAdapter mSuggestionAdapter;
    private ArrayList<String> suggestionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_conversation);
        mContext = this;

        String merchantName = getIntent().getStringExtra("merchantName");
        merchantId = getIntent().getStringExtra("merchantId");
        String chatId = getIntent().getStringExtra("chatId");
        receiverPAN = getIntent().getStringExtra("merchantPan");


//        setupToolbarWithUpNav(R.id.toolbar, merchantName, R.drawable.ic_action_back);

        TextView textViewTitle = findViewById(R.id.textViewTitle);
        textViewTitle.setText(merchantName);

        payProgress = findViewById(R.id.id_payment_progressbar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationRecyclerView(this, messagesList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if(mAdapter.getItemCount()>0)
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        }, 1000);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        communication = new Communication(CustomerConversationActivity.this,
                messagesList, mAdapter, mRecyclerView, chatId, "customer", merchantName);

        communication.getMessages();

        IntentFilter intentFilter = new IntentFilter("GOT_PAYMENT_RESPONSE");
        registerReceiver(paymentResponseReceiver, intentFilter);

        if(util.listItems.length() > 0){
            communication.sendMessage("My List:\n=======\n"+util.listItems);
            util.listItems = "";
            Toast.makeText(this, "Your List has been Sent", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            //Uncomment the below code to Set the message and title from the strings.xml file
            builder.setMessage("Do you want it delivered ?") .setTitle("");
            //Setting message manually and performing action on button click
            builder.setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(),"Action for delivery",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(),"Pickup Chosen",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.show();
        }

        final FloatingActionButton fab = findViewById(R.id.bt_send);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagMode == MODE_CHAT) {
                    if (!messageText.getText().toString().equals("")) {
                        communication.sendMessage(messageText.getText().toString());
                        messageText.setText("");
                    }
                } else if (flagMode == MODE_PAY)
                {
                    pay_message = "";

                    LayoutInflater li = LayoutInflater.from(mContext);
                    View promptsView = li.inflate(R.layout.dialog_pay, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final String[] currency = new String[] {"USD", "INR"};
                    ArrayAdapter<String> distance_adapter =
                            new ArrayAdapter<>(
                                    CustomerConversationActivity.this,
                                    R.layout.dropdown_menu_popup_item,
                                    currency);
                    final AutoCompleteTextView currencyValue =
                            promptsView.findViewById(R.id.currency_dropdown);
                    currencyValue.setText(currency[0]);
                    currencyValue.setAdapter(distance_adapter);

                    final EditText userInput = promptsView
                            .findViewById(R.id.editTextAmount);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Pay",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            sharedPreferences = mContext.getSharedPreferences
                                                    ("MySharedPref", Context.MODE_PRIVATE);

                                            String senderPAN = sharedPreferences.getString("cardNumber", "-1");
                                            senderPAN = senderPAN.replace(" ", "");
                                            Log.i(TAG, senderPAN);

                                            if(senderPAN.equals("-1"))
                                            {
                                                Toast.makeText(mContext, "Please add your card details in your profile",
                                                        Toast.LENGTH_SHORT).show();

                                                dialog.dismiss();
                                                return;
                                            }

                                            if(userInput.getText().toString().equals("0.00"))
                                            {
                                                Toast.makeText(mContext, "Amount should be greater than 0", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            Log.i(TAG, "sender pan not equal to -1");

                                            payProgress.setVisibility(View.VISIBLE);
                                            paymentHandler = new PaymentHandler(CustomerConversationActivity.this,
                                                    senderPAN, receiverPAN, userInput.getText().toString(),
                                                    currencyValue.getText().toString());

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    paymentHandler.getTransactionStatus();
                                                }
                                            }).start();

                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }

            }
        });

        messageText = (EditText) findViewById(R.id.et_message);
        messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mAdapter.getItemCount()>0)
                            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    }
                }, 500);
            }
        });

        /*
        * Dynamic Floating Action Button:
        * If the number of characters in the text box is 0 then: payment button
        * If the number of characters in the text box is > 0 then: send message button
        * */
        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = messageText.length();
                if(length > 0) {
                    fab.setImageResource(R.drawable.ic_send_white);
                    flagMode = MODE_CHAT;
                }
                else {
                    fab.setImageResource(R.drawable.ic_card_white);
                    flagMode = MODE_PAY;
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        recyclerViewSuggestions = findViewById(R.id.id_suggestionsRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewSuggestions.setLayoutManager(layoutManager);
        suggestionsList = new ArrayList<>();
        mSuggestionAdapter = new ListAdapter(suggestionsList);
        recyclerViewSuggestions.setAdapter(mSuggestionAdapter);

        addSuggestions();
    }

    public class ViewDialog
    {
        public void showDialog(Activity activity)
        {}
    }

    private String formatPaymentMessage(String pay_message) {
        String temp_pay="";
        int left_hyphen = (int)Math.floor((18 - pay_message.length())/2.0f);
        int right_hyphen = (int)Math.ceil((18 - pay_message.length())/2.0f);

        for(int i=0; i < left_hyphen; i++)  temp_pay = temp_pay.concat("─");
        temp_pay = temp_pay.concat(pay_message);
        for(int i=0; i < right_hyphen; i++)  temp_pay = temp_pay.concat("─");

        return "┌───── •✧✧• ─────┐\n " + temp_pay + "\n" +
                "└───── •✧✧• ─────┘";
    }

    /*
    * When the customer clicks the plus button to add list(say grocery) to send to the customer
    * */
    public void addList(View view) {
        addListBottomSheetFragment = new AddListBottomSheetFragment();
        addListBottomSheetFragment.show(getSupportFragmentManager(), addListBottomSheetFragment.getTag());
    }

    @Override
    public void onButtonClicked(ArrayList<String> list) {
        Log.i("Customer Chat", "Total Items: " + list.size());
        String final_list = "My List \n======\n";
        for(int i = 0; i< list.size(); i++){
            final_list = final_list.concat(list.get(i));
            if(i!=list.size()-1)
                final_list = final_list.concat("\n");
        }
        if (!final_list.equals(""))
        {
            communication.sendMessage(final_list);
        }

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage("Do you want it delivered ?") .setTitle("");
        //Setting message manually and performing action on button click
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(),"Action for delivery",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(),"Pickup Chosen",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        //alert.setTitle("AlertDialogExample");
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        addListBottomSheetFragment.dismiss();
    }

    private final BroadcastReceiver paymentResponseReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Log.i("Payments", "inside payment handler1");
            if(intent!=null && intent.getAction().equals("GOT_PAYMENT_RESPONSE"))
            {
                if(intent.getStringExtra("status").equals("failed"))
                {
                    payProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(CustomerConversationActivity.this, "Payment failed. Please try again.",Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i("Payments", "inside payment handler2");
                String response = intent.getStringExtra("response");
                if(response.equals("Approved and completed successfully"))
                {
                    Toast.makeText(CustomerConversationActivity.this, "Payment successful",Toast.LENGTH_LONG).show();
                    pay_message = pay_message.concat(intent.getStringExtra("amount"));
                    communication.sendMessage(formatPaymentMessage("PAID:" + pay_message));
                }
                else
                {
                    Toast.makeText(CustomerConversationActivity.this, "Payment failed. Please try again.",Toast.LENGTH_LONG).show();
                }
            }
            payProgress.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(paymentResponseReceiver);
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private ArrayList<String> list;

        private ListAdapter(ArrayList<String> data) {
            this.list = data;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            private ViewHolder(View itemView) {
                super(itemView);
                this.textView = itemView.findViewById(R.id.item_suggestion);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.textView.setText(list.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    communication.sendMessage(list.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    private void addSuggestions()
    {
        suggestionsList.add("Hello there!");
        suggestionsList.add("I want to order following items.");
        suggestionsList.add("Can you deliver?");
        suggestionsList.add("I want to pickup following items from your store.");
        suggestionsList.add("Where is my order?");
        mSuggestionAdapter.notifyDataSetChanged();
    }
}
