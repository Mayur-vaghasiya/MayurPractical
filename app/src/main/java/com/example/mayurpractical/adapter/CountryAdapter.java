package com.example.mayurpractical.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.mayurpractical.R;
import com.example.mayurpractical.model.CountryModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.EmployeeViewHolder> {

    private Context context;
    private ArrayList<CountryModel.Result> countryList = null;
    private ArrayList<CountryModel.Result> listfriOrigin;

    public CountryAdapter(WeakReference<Context> context, ArrayList<CountryModel.Result> countryList) {
        this.context = context.get();
        this.countryList = countryList;
        this.listfriOrigin = new ArrayList<CountryModel.Result>();
        this.listfriOrigin.addAll(countryList);

    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.country_list, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        holder.txtCountryName.setText(countryList.get(position).getName().toString());
        holder.txtCodeOne.setText("Alpha2Code : " + countryList.get(position).getAlpha2Code());
        holder.txtCodeTwo.setText("Alpha3Code : " + countryList.get(position).getAlpha3Code());
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }


    // Filter Class
    public void filters(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        countryList.clear();
        if (charText.length() == 0) {
            countryList.addAll(listfriOrigin);
        } else {
            for (CountryModel.Result wp : listfriOrigin) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getAlpha2Code().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getAlpha3Code().toLowerCase(Locale.getDefault()).contains(charText)) {
                    countryList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView txtCountryName, txtCodeOne, txtCodeTwo;

        EmployeeViewHolder(View itemView) {
            super(itemView);
            txtCountryName = (AppCompatTextView) itemView.findViewById(R.id.actv_name);
            txtCountryName.setSelected(true);
            txtCodeOne = (AppCompatTextView) itemView.findViewById(R.id.actv_codeone);
            txtCodeTwo = (AppCompatTextView) itemView.findViewById(R.id.actv_codetwo);
        }
    }
}