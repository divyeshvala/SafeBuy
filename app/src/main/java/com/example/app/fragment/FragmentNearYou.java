package com.example.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Merchant;
import com.example.app.model.ModelTestData;

import java.util.ArrayList;

public class FragmentNearYou extends Fragment {

    private RecyclerView recyclerViewNearYou;
    private ListAdapter mListadapter;

    public static FragmentNearYou newInstance() {
        return new FragmentNearYou();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_you, container, false);

        recyclerViewNearYou = (RecyclerView) view.findViewById(R.id.recyclerViewNearYou);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewNearYou.setLayoutManager(layoutManager);


        ArrayList data = new ArrayList<ModelTestData>();
        for (int i = 0; i < ModelTestData.nameArray.length; i++) {
            data.add(
                    new Merchant
                            (
                                    ModelTestData.nameArray[i],
                                    ModelTestData.addressArray[i],
                                    ModelTestData.openArray[i],
                                    ModelTestData.distanceArray[i]
                            ));
        }

        mListadapter = new ListAdapter(data);
        recyclerViewNearYou.setAdapter(mListadapter);
        return view;
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private ArrayList<Merchant> dataList;

        public ListAdapter(ArrayList<Merchant> data) {
            this.dataList = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            TextView textViewAddress;
            TextView textViewOpen;
            TextView textViewDistance;

            public ViewHolder(View itemView) {
                super(itemView);
                this.textViewName = (TextView) itemView.findViewById(R.id.name);
                this.textViewAddress = (TextView) itemView.findViewById(R.id.address);
                this.textViewOpen = (TextView) itemView.findViewById(R.id.open);
                this.textViewDistance = (TextView) itemView.findViewById(R.id.distance);
            }
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_merchant, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position) {
            holder.textViewName.setText(dataList.get(position).getName());
            holder.textViewAddress.setText(dataList.get(position).getAddress());
            holder.textViewOpen.setText(dataList.get(position).isOpen() ? "true" : "false");
            holder.textViewDistance.setText(dataList.get(position).getDistance());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}