package com.hcl.kandy.cpass.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hcl.kandy.cpass.R;
import com.rbbn.cpaas.mobile.addressbook.model.Contact;

import java.util.List;

/**
 * Created by Ashish Goel on 2/1/2019.
 */
@SuppressWarnings("ConstantConditions")
public class AddressbookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int CONTACT_TYPE = 1;

    private List<Contact> contact;

    public AddressbookAdapter(List<Contact> contact) {
        this.contact = contact;
    }


    @Override
    public int getItemViewType(int position) {
        return CONTACT_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_chat_me, viewGroup, false);

        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        Contact cont = contact.get(i);

        switch (holder.getItemViewType()) {
            case CONTACT_TYPE:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                myViewHolder.title.setText(cont.getFirstName());
                myViewHolder.message.setText(cont.getEmailAddress());

                break;

        }

    }


    @Override
    public int getItemCount() {
        return contact.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtDestination);
            message = itemView.findViewById(R.id.txtMessage);
        }
    }

}
