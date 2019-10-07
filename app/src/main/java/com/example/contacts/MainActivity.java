package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.contacts.adapter.ContactAdapter;
import com.example.contacts.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Contact> arrContact;
    private ContactAdapter adapter;
    private EditText etName;
    private EditText etNumber;
    private RadioButton rbtnMale;
    private RadioButton rbtnFemale;
    private Button btnAddContact;
    private ListView lvContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWidget();

        arrContact = new ArrayList<>();
        adapter = new ContactAdapter(this, R.layout.item_contact, arrContact);

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String number = etNumber.getText().toString().trim();
                boolean isMale = true;
                if (rbtnMale.isChecked()) {
                    isMale = true;
                } else {
                    isMale = false;
                }
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(number)) {
                    Toast.makeText(MainActivity.this, "Please input all", Toast.LENGTH_SHORT).show();
                } else {
                    Contact contact = new Contact(isMale, name, number);
                    arrContact.add(contact);

                    etName.setText("");
                    etNumber.setText("");
                    etName.requestFocus();
                }
                adapter.notifyDataSetChanged();
            }
        });
        lvContact.setAdapter(adapter);

        CheckAndRequestPermission();

        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogConfirm(position);
            }
        });
    }

    //Cấp quyền cho android 6.0 trở lên
    private void CheckAndRequestPermission() {
        String[] permissions = new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS
        };
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission);
            }
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), 1);
        }
    }

    //
    public void setWidget() {
        etName = (EditText) findViewById(R.id.et_name);
        etNumber = (EditText) findViewById(R.id.et_number);
        rbtnMale = (RadioButton) findViewById(R.id.rbtn_male);
        rbtnFemale = (RadioButton) findViewById(R.id.rbtn_female);
        btnAddContact = (Button) findViewById(R.id.btn_add_contact);
        lvContact = (ListView) findViewById(R.id.lv_contacts);
    }

    public void showDialogConfirm(final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.setCancelable(false);
        Button btnCall = dialog.findViewById(R.id.btn_call);
        Button btnSendMessage = dialog.findViewById(R.id.btn_send_message);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Call", Toast.LENGTH_SHORT).show();
                intentCall(position);
                dialog.cancel();
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Send Message", Toast.LENGTH_SHORT).show();
                intentMessage(position);
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void intentMessage(int position) {
        //Viết tắc, trong khởi tạo Intent có sẵn setAction, setData
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms: " + arrContact.get(position).getmNumber()));
        startActivity(intent);
    }

    private void intentCall(int position) {
        //Viết cách 2
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel: " + arrContact.get(position).getmNumber()));
        startActivity(intent);
    }
}
