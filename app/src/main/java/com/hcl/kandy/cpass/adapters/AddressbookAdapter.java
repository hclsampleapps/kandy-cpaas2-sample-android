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
    private final AddressbookListner addressbookListner;

    private List<Contact> contact;

    public AddressbookAdapter(List<Contact> contact, AddressbookListner addressbookListner) {
        this.contact = contact;
        this.addressbookListner = addressbookListner;
    }


    @Override
    public int getItemViewType(int position) {
        return CONTACT_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_addressbook, viewGroup, false);

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
                myViewHolder.addressbookContainer.setTag(R.id.addressbookContainer, cont);
                myViewHolder.deleteButton.setTag(R.id.addressbookContainer, cont);
                break;

        }

    }


    @Override
    public int getItemCount() {
        return contact.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message;
        View addressbookContainer, deleteButton;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            message = itemView.findViewById(R.id.txtMessage);
            addressbookContainer = itemView.findViewById(R.id.addressbookContainer);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            addressbookContainer.setOnClickListener(v -> {
                Contact contact = (Contact) v.getTag(R.id.addressbookContainer);
                addressbookListner.onClickAddressBook(contact);
            });

            deleteButton.setOnClickListener(v -> {
                Contact contact = (Contact) v.getTag(R.id.addressbookContainer);
                addressbookListner.onDeleteAddressBook(contact);
            });
        }

    }

    public interface AddressbookListner {
        void onClickAddressBook(Contact contact);
        void onDeleteAddressBook(Contact contact);
    }

}
