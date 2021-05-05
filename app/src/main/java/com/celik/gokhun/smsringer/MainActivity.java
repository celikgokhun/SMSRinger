package com.celik.gokhun.smsringer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String phoneNumberTaken;
    String messageTaken;
    String numberOfMessage;

    EditText phoneNumberEditText;
    EditText messageEditText;
    EditText numberOfMessageEditText;

    ScrollView menu;
    ScrollView info;

    ImageView IBButton;

    boolean isPressed;

    static final int PICK_CONTACT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPressed =  false;
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        messageEditText = findViewById(R.id.messageEditText);
        numberOfMessageEditText = findViewById(R.id.numberOfMessageEditText);

        menu = findViewById(R.id.menu_layout);
        info = findViewById(R.id.info_layout);

        IBButton = findViewById(R.id.info_back_button);

    }

    public void openInfo(View view)
    {
        if (!isPressed)
        {
            menu.setVisibility(View.GONE);
            info.setVisibility(View.VISIBLE);
            isPressed = true;
            IBButton.setImageResource(R.drawable.back);
        }
        else
        {
            menu.setVisibility(View.VISIBLE);
            info.setVisibility(View.GONE);
            isPressed = false;
            IBButton.setImageResource(R.drawable.info);
        }
    }

    public void sendMessageViaContact(View view)
    {
        messageTaken = messageEditText.getText().toString();
        numberOfMessage = numberOfMessageEditText.getText().toString();
        getContactsNumber();
    }



    public void send(View view)
    {
        messageTaken = messageEditText.getText().toString();
        numberOfMessage = numberOfMessageEditText.getText().toString();
        phoneNumberTaken = phoneNumberEditText.getText().toString();


        if (!phoneNumberTaken.isEmpty() && !messageTaken.isEmpty() && Integer.parseInt(numberOfMessage)>0) {
            for (int i = 0; i< Integer.parseInt(numberOfMessage); i++) {
                sendSMS(phoneNumberTaken,messageTaken);
            }
        }
    }

    public void getContactsNumber() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }

        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 10);
            }
        }
    }

    public void sendSMS(String phoneNumber, String message) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            try
            {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
            }
            catch (Exception ErrVar)
            {
                Toast.makeText(getApplicationContext(),ErrVar.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
            }
        }


    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode)
        {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1"))
                        {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();

                            phoneNumberTaken = phones.getString(phones.getColumnIndex("data1"));
                            phoneNumberEditText.setText(phoneNumberTaken);
                            //System.out.println("number is:"+phoneNumberTaken);

                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    }
                }
                break;
        }
    }

}