package com.example.iman.gtk.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.iman.gtk.R;
import com.example.iman.gtk.WebviewActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE = 1000;
    private Button btn_server1, btn_server2, btn_server3, btn_server4, btn_server5, btn_server6, btn_server7;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        btn_server1 = (Button) view.findViewById(R.id.btn_server1);
        btn_server2 = (Button) view.findViewById(R.id.btn_server2);
        btn_server3 = (Button) view.findViewById(R.id.btn_server3);
        btn_server4 = (Button) view.findViewById(R.id.btn_server4);
        btn_server5 = (Button) view.findViewById(R.id.btn_server5);
        btn_server6 = (Button) view.findViewById(R.id.btn_server6);
        btn_server7 = (Button) view.findViewById(R.id.btn_server7);
        btn_server1.setOnClickListener(this);
        btn_server2.setOnClickListener(this);
        btn_server3.setOnClickListener(this);
        btn_server4.setOnClickListener(this);
        btn_server5.setOnClickListener(this);
        btn_server6.setOnClickListener(this);
        btn_server7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_server1) {
            WebviewActivity.callWebview(getActivity(), "http://223.27.144.195:2017/index.php?id=aGFsfD18TG9naW5fc2Vrb2xhaC5waHB8fHBhcmFtfD18YldGcllXNGdibUZ6YVE9PQ==;", REQUEST_CODE);
        } else if (id == R.id.btn_server2) {
            WebviewActivity.callWebview(getActivity(), "http://223.27.144.195:7000/index.php?id=aGFsfD18TG9naW5fc2Vrb2xhaC5waHB8fHBhcmFtfD18YldGcllXNGdibUZ6YVE9PQ==;", REQUEST_CODE);
        } else if (id == R.id.btn_server3) {
            WebviewActivity.callWebview(getActivity(), "http://223.27.144.195:212/index.php?id=aGFsfD18TG9naW5fc2Vrb2xhaC5waHB8fHBhcmFtfD18YldGcllXNGdibUZ6YVE9PQ==;", REQUEST_CODE);
        } else if (id == R.id.btn_server4) {
            WebviewActivity.callWebview(getActivity(), "http://223.27.144.195:414/index.php?id=aGFsfD18TG9naW5fc2Vrb2xhaC5waHB8fHBhcmFtfD18YldGcllXNGdibUZ6YVE9PQ==;", REQUEST_CODE);
        } else if (id == R.id.btn_server5) {
            WebviewActivity.callWebview(getActivity(), "http://hadir.gtk.kemdikbud.go.id/index.php?id=aGFsfD18TG9naW5fc2Vrb2xhaC5waHB8fHBhcmFtfD18YldGcllXNGdibUZ6YVE9PQ==;", REQUEST_CODE);
        } else if (id == R.id.btn_server6) {
            WebviewActivity.callWebview(getActivity(), "http://223.27.144.196/index.php?id=aGFsfD18TG9naW5fc2Vrb2xhaC5waHB8fHBhcmFtfD18YldGcllXNGdibUZ6YVE9PQ==;", REQUEST_CODE);
        } else if (id == R.id.btn_server7) {
            WebviewActivity.callWebview(getActivity(), "http://118.98.166.188/index.php?id=aGFsfD18TG9naW5fc2Vrb2xhaC5waHB8fHBhcmFtfD18YldGcllXNGdibUZ6YVE9PQ==;", REQUEST_CODE);
        }
    }

}
