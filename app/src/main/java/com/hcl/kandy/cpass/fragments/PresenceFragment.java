package com.hcl.kandy.cpass.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.adapters.PresenceAdapter;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.presence.api.FetchAllPresenceListsCallback;
import com.rbbn.cpaas.mobile.presence.api.FetchPresenceListCallback;
import com.rbbn.cpaas.mobile.presence.api.FetchPresenceSourceCallback;
import com.rbbn.cpaas.mobile.presence.api.PresenceActivity;
import com.rbbn.cpaas.mobile.presence.api.PresenceCallback;
import com.rbbn.cpaas.mobile.presence.api.PresenceList;
import com.rbbn.cpaas.mobile.presence.api.PresenceListener;
import com.rbbn.cpaas.mobile.presence.api.PresenceService;
import com.rbbn.cpaas.mobile.presence.api.PresenceSource;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;
import com.rbbn.cpaas.mobile.utilities.services.PresenceEnums;

import java.util.ArrayList;
import java.util.List;

import static com.rbbn.cpaas.mobile.utilities.security.TLSSocketFactory.TAG;

/**
 * Created by Ashish Goel on 2/4/2019.
 */
public class PresenceFragment extends BaseFragment implements PresenceListener, View.OnClickListener {
    private PresenceService mPresenceService;
    private PresenceSource myPresenceSource;
    private List<PresenceList> mPresenceLists = new ArrayList<>();
    private PresenceAdapter adapter;
    private Context mContext;
    private ExpandableListView presenceLists;
    private ImageView mPresenceItemImageView;
    private TextView mPresenceStatusTextView;

    public PresenceFragment() {
    }

    public static PresenceFragment newInstance() {
        return new PresenceFragment();
    }

    public static int getImageResourceForPresence(PresenceActivity presenceActivity) {
        String status = presenceActivity.getStatus();
        PresenceEnums presenceState = presenceActivity.getPresenceState();
        int resource = android.R.drawable.presence_offline;

        switch (status) {
            case "Open":
                switch (presenceState) {
                    case AVAILABLE:
                    case LUNCH:
                    case OTHER:
                        resource = android.R.drawable.presence_online;
                        break;

                    case UNKNOWN:
                        resource = android.R.drawable.presence_invisible;
                        break;
                }
                break;

            case "Closed":
                switch (presenceState) {
                    case BUSY:
                    case ON_THE_PHONE:
                    case UNKNOWN:
                    case OTHER:
                        resource = android.R.drawable.presence_busy;
                        break;

                    case ON_VACATION:
                        resource = android.R.drawable.presence_away;
                        break;
                }
                break;
        }
        return resource;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        if (context != null)
            initPresenceService(context);
    }

    private void initPresenceService(Context context) {
        App applicationContext = (App) context.getApplicationContext();
        CPaaS cpass = applicationContext.getCpass();
        mPresenceService = cpass.getPresenceService();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_presence, container, false);
        adapter = new PresenceAdapter(mPresenceLists, getActivity());
        presenceLists = inflate.findViewById(R.id.presenceLists);
        presenceLists.setAdapter(adapter);

        FloatingActionButton newPresenceButton = inflate.findViewById(R.id.newPresenceButton);
        newPresenceButton.setOnClickListener(this);

        mPresenceItemImageView = inflate.findViewById(R.id.presence_item_image_view);
        mPresenceStatusTextView = inflate.findViewById(R.id.presence_status_text_view);

        View updateStatusLayout = inflate.findViewById(R.id.presence_status_text_view);
        updateStatusLayout.setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onResume() {
        // This gets called when entering the fragment, or when coming back to the fragment from another activity
        super.onResume();

        if (getContext() != null) {
            initPresenceService(getContext());
        }

        if (mPresenceService != null) {
            mPresenceService.setPresenceListener(this);
            fetchAllPresenceLists();
            fetchPresenceByClientCorrelator();
        } else {
            Log.e("PresenceService", "onResume: Presence Service is null");
        }
    }

    protected void fetchAllPresenceLists() {
        mPresenceService.fetchAllPresenceLists(new FetchAllPresenceListsCallback() {
            @Override
            public void onSuccess(List<PresenceList> lists) {
                mPresenceLists = lists;
                adapter.setPresenceLists(mPresenceLists);
                for (PresenceList presenceList : mPresenceLists) {
                    subscribeForPresenceUpdates(presenceList);
                }
            }

            @Override
            public void onFail(MobileError error) {
                Log.d(TAG, error.getErrorMessage());
            }
        });
    }

    protected void fetchPresenceByClientCorrelator() {
        mPresenceService.fetchPresenceByClientCorrelator(new FetchPresenceSourceCallback() {
            @Override
            public void onSuccess(PresenceSource presenceSource) {
                if (presenceSource == null) {
                    createPresenceSource();
                } else {
                    updateMyPresence(presenceSource);
                }
            }

            @Override
            public void onFail(MobileError error) {
                Log.d("HCL", error.getErrorMessage());
            }
        });
    }

    private void updateMyPresence(PresenceSource presenceSource) {
        myPresenceSource = presenceSource;
        PresenceActivity presenceActivity = presenceSource.getPresenceActivity();
        mPresenceItemImageView.setImageResource(getImageResourceForPresence(presenceActivity));
        mPresenceStatusTextView.setText(presenceActivity.getActivity());
    }

    private void createPresenceSource() {
        mPresenceService.createPresenceSource(86400, new FetchPresenceSourceCallback() {
            @Override
            public void onSuccess(PresenceSource presenceSource) {
                updateMyPresence(presenceSource);
            }

            @Override
            public void onFail(MobileError error) {
                Log.d("HCL", error.getErrorMessage());
                mPresenceItemImageView.setImageResource(android.R.drawable.presence_offline);
                mPresenceStatusTextView.setText("Offline");

            }
        });
    }

    private void subscribeForPresenceUpdates(PresenceList presenceList) {
        String name = presenceList.getName();
        String key = presenceList.getRealKey();
        mPresenceService.createPresenceListSubscription(key, new PresenceCallback() {
            @Override
            public void onSuccess() {
                String msg = "Subscribed to presence updates for " + name + " (" + key + ")";
                Log.d(TAG, msg);
            }

            @Override
            public void onFail(MobileError error) {
                Log.d(TAG, error.getErrorMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newPresenceButton:
                createNewPresenceList();
                break;

            case R.id.presence_status_text_view:
                updateMyPresenceStatus();
                break;
        }
    }

    private void createNewPresenceList() {
        Context _context = getContext();
        AlertDialog.Builder alert = new AlertDialog.Builder(_context);

        // Set an EditText view to get user input
        final EditText input = new EditText(_context);
        FrameLayout container = new FrameLayout(_context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = params.leftMargin;
        input.setLayoutParams(params);
        input.setHint("Enter the list name");
        container.addView(input);

        alert.setTitle("New Presence List");
        alert.setView(container);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String listID = input.getText().toString();
                Log.d("", "New presence list : " + listID);

                mPresenceService.createPresenceList(listID, new FetchPresenceListCallback() {
                    @Override
                    public void onSuccess(PresenceList presenceList) {
                        mPresenceLists.add(presenceList);
                        adapter.setPresenceLists(mPresenceLists);
                        subscribeForPresenceUpdates(presenceList);
                    }

                    @Override
                    public void onFail(MobileError error) {
                        Log.d(TAG, error.getErrorMessage());
                    }
                });
                return;
            }
        });

        alert.setNegativeButton("Cancel",
                (dialog, which) -> {
                });

        alert.show();
    }

    private void updateMyPresenceStatus() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        LinearLayout layout = new LinearLayout(mContext);
        View v = LayoutInflater.from(mContext).inflate(R.layout.update_presence_status_dialog, null);
        layout.addView(v);

        Spinner spinner = v.findViewById(R.id.presence_status_spinner);
        Spinner availabilitySpinner = v.findViewById(R.id.presence_availability_spinner);
        EditText otherMessageEditText = v.findViewById(R.id.other_status_message);

        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, PresenceEnums.values());
        spinner.setAdapter(spinnerAdapter);

        if (myPresenceSource != null) {
            PresenceEnums presenceEnums = myPresenceSource.getPresenceActivity().getPresenceState();
            spinner.setSelection(presenceEnums.ordinal(), false);
            availabilitySpinner.setEnabled(presenceEnums.canOverrideWillingness());
            otherMessageEditText.setEnabled(presenceEnums == PresenceEnums.OTHER);
        }

        spinner.setOnItemSelectedListener(new ExpandableListView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PresenceEnums presenceEnum = (PresenceEnums) adapterView.getItemAtPosition(i);
                availabilitySpinner.setEnabled(presenceEnum.canOverrideWillingness());
                otherMessageEditText.setEnabled(presenceEnum == PresenceEnums.OTHER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                mContext, R.array.presence_availability, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(adapter);

        alert.setTitle("Update Status");
        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (myPresenceSource == null) {
                    createPresenceSource();
                    return;
                }

                String sourceID = myPresenceSource.getSourceId();
                int duration = 86400;
                PresenceEnums activity = (PresenceEnums) spinner.getSelectedItem();
                String status = (String) availabilitySpinner.getSelectedItem();
                String other = otherMessageEditText.getText().toString();

                mPresenceService.updatePresenceSource(sourceID, duration, activity, status, other,
                        new FetchPresenceSourceCallback() {
                            @Override
                            public void onSuccess(PresenceSource presenceSource) {
                                updateMyPresence(presenceSource);
                            }

                            @Override
                            public void onFail(MobileError error) {
                                Log.d(TAG, error.getErrorMessage());
                            }
                        }
                );
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

        alert.show();
    }

    @Override
    public void presenceNotification(String s, PresenceActivity presenceActivity) {

    }

    @Override
    public void presenceListNotification(String s, PresenceList presenceList) {

    }

}
