package com.example.modernsms.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.modernsms.R;
import com.example.modernsms.adapters.ListSmsAdapter;
import com.example.modernsms.databinding.FragmentSmsListBinding;
import com.example.modernsms.interfaces.OnRecyclerItemClick;
import com.example.modernsms.models.SmsModel;
import com.example.modernsms.viewmodels.SmsViewModel;

import java.util.ArrayList;
import java.util.List;

public class SmsListFragment extends Fragment implements View.OnClickListener, OnRecyclerItemClick {
    FragmentSmsListBinding binding;
    ListSmsAdapter listSmsAdapter;
    SmsViewModel viewModel;
    Handler handler;

    public SmsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SmsViewModel.class);
        viewModel.init(getActivity());
        listSmsAdapter = new ListSmsAdapter();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentSmsListBinding.inflate(inflater, container, false);
        binding.recyclerid.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView head = (TextView) getActivity().findViewById(R.id.textView3);
        head.setText("Messages");


        handler = new Handler();


        listSmsAdapter.setContext(getContext());
        listSmsAdapter.setOnRecyclerItemClick(this);

        binding.recyclerid.setAdapter(listSmsAdapter);

        viewModel.getAllSms().observe(getViewLifecycleOwner(), new Observer<List<SmsModel>>() {
            @Override
            public void onChanged(List<SmsModel> smsModels) {
                listSmsAdapter.setList(smsModels);
            }
        });



        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty())
                    listSmsAdapter.setList(viewModel.getAllSms().getValue());
                else {
                    binding.recyclerid.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchMatchLists(s);
                        }
                    }, 800);
                }
                return true;
            }

        });

        binding.floatingActionButton4.setOnClickListener(this);

        return binding.getRoot();
    }

    private void searchMatchLists(String s) {
        if (!s.isEmpty()) {
            ArrayList<SmsModel> arrayList = new ArrayList<>();
            for (SmsModel smsModel : viewModel.getAllSms().getValue()) {
                if (smsModel.getBody().toLowerCase().contains(s) || smsModel.getAddress().toLowerCase().contains(s))
                    arrayList.add(smsModel);
            }
            if (!arrayList.isEmpty())
                listSmsAdapter.setList(arrayList);
        } else
            listSmsAdapter.setList(viewModel.getAllSms().getValue());

        binding.recyclerid.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.floatingActionButton4) {
            TextView head = (TextView) getActivity().findViewById(R.id.textView3);
            head.setText("Write Sms");
            loadFrag(new SentSmsFragment(), 1);
        }
    }

    @Override
    public void onItemClick(String address) {
        Bundle bundle = new Bundle();
        bundle.putString("address", address);
        ViewSmsFragment viewSmsFragment = new ViewSmsFragment();
        viewSmsFragment.setArguments(bundle);
        loadFrag(viewSmsFragment, 1);
    }

    private void loadFrag(Fragment fragment, int flag) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out  // popExit
        );
        if (flag == 1) {
            ft.addToBackStack("first");
            ft.replace(R.id.frameLayout, fragment);
        }
        ft.commit();
    }


}