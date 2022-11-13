package com.example.modernsms.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.modernsms.adapters.ViewSmsAdapter;
import com.example.modernsms.databinding.FragmentSentSmsBinding;
import com.example.modernsms.models.ContactsModel;
import com.example.modernsms.models.SmsModel;
import com.example.modernsms.viewmodels.SmsViewModel;

import java.util.ArrayList;
import java.util.List;


public class SentSmsFragment extends Fragment implements View.OnClickListener {
    private FragmentSentSmsBinding binding;
    private SmsViewModel viewModel;
    private List<ContactsModel> list;
    private ViewSmsAdapter adapter;
    ActivityResultLauncher<Intent> arl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        list = viewModel.getContacts(result).getValue();
                        binding.contactName.setText(list.get(0).getNumber());
                        uiUpdate();
                    }
                }
            });

    public SentSmsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SmsViewModel.class);
        viewModel.init(requireContext());
        adapter = new ViewSmsAdapter();
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        arl.launch(intent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSentSmsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    void sending() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                //SmsManager smsManager = SmsManager.getDefault();
                SmsManager smsManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    smsManager = getContext().getSystemService(SmsManager.class)
                            .createForSubscriptionId(SubscriptionManager.getDefaultSubscriptionId());
                } else
                    smsManager = SmsManager.getSmsManagerForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId());

                smsManager.sendTextMessage(list.get(0).getNumber(),
                        null, binding.edt.getText().toString(), null, null);
                binding.edt.setText("");
            } else
                Log.d("permisssion", "not granted");
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            //SmsManager smsManager = getContext().getSystemService(SmsManager.class);
            smsManager.sendTextMessage(list.get(0).getNumber(),
                    null, binding.edt.getText().toString(), null, null);
            binding.edt.setText("");
        }


    }
    void uiUpdate(){
        binding.linearlayid.setOnClickListener(this);
        binding.recyclerviewid.setAdapter(adapter);
        binding.recyclerviewid.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter.setCtx(getContext());
        viewModel.getSmsByAddress(list.get(0).getNumber()).observe(getViewLifecycleOwner(), new Observer<List<SmsModel>>() {
            @Override
            public void onChanged(List<SmsModel> smsModels) {
                adapter.setList(smsModels);
                binding.recyclerviewid.scrollToPosition(adapter.getItemCount()-1);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (!binding.edt.getText().toString().isEmpty()) {
            sending();
        }
    }


}