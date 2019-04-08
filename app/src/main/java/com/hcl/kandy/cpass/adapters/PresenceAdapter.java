package com.hcl.kandy.cpass.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.fragments.PresenceFragment;
import com.rbbn.cpaas.mobile.presence.api.PresenceActivity;
import com.rbbn.cpaas.mobile.presence.api.PresenceList;
import com.rbbn.cpaas.mobile.presence.api.Presentity;

import java.util.List;

public class PresenceAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private List<PresenceList> presenceLists;

    public PresenceAdapter(List<PresenceList> presenceLists, Activity context) {
        this.context = context;
        this.presenceLists = presenceLists;
    }

    public void setPresenceLists(List<PresenceList> list) {
        presenceLists.clear();
        presenceLists.addAll(list);
        context.runOnUiThread(this::notifyDataSetChanged);
    }

    public void update() {
        context.runOnUiThread(this::notifyDataSetChanged);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        PresenceList presenceList = presenceLists.get(groupPosition);
        List<Presentity> presentityList = presenceList.getPresentities();
        return presentityList.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Presentity presentity = (Presentity) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.presentity_item, null);
        }

        PresenceActivity presenceActivity = presentity.getActivity();
        String userId = presentity.getUserId();
        String parts[] = userId.split("@");
        String activityString = presenceActivity.getActivity();
        String displayString = parts[0] + " - " + activityString;

        TextView presentityIdTextView = convertView.findViewById(R.id.presentity_user_id);
        presentityIdTextView.setText(displayString);

        ImageView presentityItemImageView = convertView.findViewById(R.id.presentity_item_image_view);
        int status = PresenceFragment.getImageResourceForPresence(presenceActivity);
        presentityItemImageView.setImageResource(status);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        PresenceList presenceList = this.presenceLists.get(groupPosition);
        List<Presentity> presentities = presenceList.getPresentities();
        return presentities != null ? presentities.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.presenceLists.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.presenceLists.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        PresenceList presenceList = (PresenceList) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.presence_lists_item, null);
        }

        String name = presenceList.getName();
        List<Presentity> presentities = presenceList.getPresentities();
        int count = presentities != null ? presentities.size() : 0;

        TextView lblListHeader = convertView.findViewById(R.id.participant_text_view);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(name + " (" + count + ")");

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
