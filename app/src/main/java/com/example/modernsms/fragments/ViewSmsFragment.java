package com.example.modernsms.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.modernsms.adapters.ViewSmsAdapter;
import com.example.modernsms.databinding.FragmentViewSmsBinding;
import com.example.modernsms.models.SmsModel;
import com.example.modernsms.viewmodels.SmsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ViewSmsFragment extends Fragment implements View.OnClickListener {
private FragmentViewSmsBinding binding;
private ViewSmsAdapter adapter;
private String address;
private SmsViewModel viewModel;

    public ViewSmsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        address = getArguments().getString("address");
        viewModel = new ViewModelProvider(requireActivity()).get(SmsViewModel.class);
        viewModel.init(requireActivity());
        adapter = new ViewSmsAdapter();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewSmsBinding.inflate(inflater,container,false);

        binding.recyclerviewid.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerviewid.setAdapter(adapter);
        viewModel.getSmsByAddress(address).observe(getViewLifecycleOwner(), new Observer<List<SmsModel>>() {
            @Override
            public void onChanged(List<SmsModel> smsModels) {
                adapter.setList(smsModels);
                binding.recyclerviewid.scrollToPosition(adapter.getItemCount()-1);

            }
        });

        //button for sending sms
        binding.linearlayid.setOnClickListener(this);

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

                smsManager.sendTextMessage(address,
                        null, binding.edt.getText().toString(), null, null);
                binding.edt.setText("");
            } else
                Log.d("permisssion", "not granted");
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            //SmsManager smsManager = getContext().getSystemService(SmsManager.class);
            smsManager.sendTextMessage(address,
                    null, binding.edt.getText().toString(), null, null);
            binding.edt.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        if (!binding.edt.getText().toString().isEmpty()) {
            sending();
        }
    }
}