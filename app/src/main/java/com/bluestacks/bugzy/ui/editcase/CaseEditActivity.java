package com.bluestacks.bugzy.ui.editcase;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.CaseEvent;
import com.bluestacks.bugzy.data.model.CaseStatus;
import com.bluestacks.bugzy.data.model.Category;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.data.model.Priority;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.ui.BaseActivity;
import com.bluestacks.bugzy.ui.caseevents.CaseEventsAdapter;
import static com.bluestacks.bugzy.ui.editcase.CaseEditViewModel.PropType.*;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CaseEditActivity extends BaseActivity {
    public static final String TAG = CaseEditActivity.class.getName();
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;
    public static final int MODE_ASSIGN = 2;
    public static final int MODE_RESOLVE = 3;
    public static final int MODE_REOPEN = 4;
    public static final int MODE_REACTIVATE = 5;
    public static final int MODE_CLOSE = 6;

    public static final String PARAM_CASE_ID    = "case_id";
    public static final String PARAM_MODE       = "mode";

    private CaseEditViewModel mCaseEditViewModel;
    private int mMode;
    private int  mCaseId;
    private CaseEventsAdapter mAdapter;

    @Inject
    ViewModelProvider.Factory mFactory;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.et_case_title)
    EditText mCaseTitle;

    @BindView(R.id.et_tags)
    EditText mTagsView;

    @BindView(R.id.spinner_project)
    Spinner mProjectSpinner;

    @BindView(R.id.spinner_area)
    Spinner mAreaSpinner;

    @BindView(R.id.spinner_milestone)
    Spinner mMileStoneSpinner;

    @BindView(R.id.spinner_category)
    Spinner mCategorySpinner;

    @BindView(R.id.spinner_assigned_to)
    Spinner mAssignedToSpinner;

    @BindView(R.id.spinner_status)
    Spinner mStatusesSpinner;

    @BindView(R.id.spinner_priority)
    Spinner mPrioritySpinner;

    @BindView(R.id.et_found_in)
    EditText mFoundInView;

    @BindView(R.id.et_verified_in)
    EditText mVerifiedInView;

    @BindView(R.id.et_fixed_in)
    EditText mFixedInView;

    @BindView(R.id.recycler_view_events)
    RecyclerView mEventsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter_slide_up, 0);
        setContentView(R.layout.activity_case_edit);
        ButterKnife.bind(this);
        parseArgs(getIntent());
        setupViews();
        mCaseEditViewModel = ViewModelProviders.of(this, mFactory).get(CaseEditViewModel.class);
        mCaseEditViewModel.setParams(mMode, mCaseId);
        subscribeToViewModel();
        setupEventsRecyclerView();
    }

    private void setupEventsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mEventsRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CaseEventsAdapter(this);
        mEventsRecyclerView.setAdapter(mAdapter);
        mEventsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void parseArgs(Intent intent) {
        mMode = intent.getIntExtra(PARAM_MODE, MODE_NEW);
        if (mMode != MODE_NEW) {
            mCaseId = intent.getIntExtra(PARAM_CASE_ID, -1);
            if (mCaseId == -1) {
                throw new RuntimeException("PARAM_CASE_ID not sent");
            }
        }
    }

    public void subscribeToViewModel() {
        mCaseEditViewModel.getMilestones().observe(this, value -> {
            if (value != null && value.data != null) {
                showMilestones(value.data);
            }
        });
        mCaseEditViewModel.getAreas().observe(this, value -> {
            if (value != null && value.data != null) {
                showAreas(value.data);
            }
        });
        mCaseEditViewModel.getProjects().observe(this, value -> {
            if (value.data != null) {
                showProjects(value.data);
            }
        });
        mCaseEditViewModel.getPersons().observe(this, value -> {
            if (value.data != null) {
                showPeople(value.data);
            }
        });
        mCaseEditViewModel.getPriorities().observe(this, value -> {
            if (value.data != null) {
                showPriorities(value.data);
            }
        });
        mCaseEditViewModel.getCategories().observe(this, value -> {
            if (value.data != null) {
                showCategories(value.data);
            }
        });
        mCaseEditViewModel.getStatuses().observe(this, value -> {
            if (value.data != null) {
                showStatuses(value.data);
            }
        });
        mCaseEditViewModel.getToken().observe(this, token -> {
            mAdapter.setToken(token);
        });
        mCaseEditViewModel.getCaseLiveData().observe(this, value ->  {
            if (value == null) {
                return;
            }
            if (value.data != null) {
                showCaseDetails(value.data);
            }
            if (value.status == Status.LOADING) {
                // Show working somewhere
                return;
            }
        });

        mCaseEditViewModel.getDefaultPropSelectionLiveData().observe(this, map -> {
            mProjectSpinner.setSelection(map.get(PROJECT));
            mAreaSpinner.setSelection(map.get(AREA));
            mMileStoneSpinner.setSelection(map.get(MILESTONE));
            mCategorySpinner.setSelection(map.get(CATEGORY));
            mStatusesSpinner.setSelection(map.get(STATUS));
            mAssignedToSpinner.setSelection(map.get(ASSIGNEDTO));
            mPrioritySpinner.setSelection(map.get(PRIORITY));
        });
    }

    @OnClick(R.id.container_project_spinner)
    void onProjectSpinnerClicked() {
        mProjectSpinner.performClick();
    }

    public void showCaseDetails(Case kase) {
        mCaseTitle.setText(kase.getTitle());
        getSupportActionBar().setTitle(kase.getIxBug() + "");
        mTagsView.setText(getTagsString(kase.getTags()));

        mVerifiedInView.setText(kase.getVerifiedIn());
        mFixedInView.setText(kase.getFixedIn());
        mFoundInView.setText(kase.getFoundIn());

        List<CaseEvent> evs = kase.getCaseevents();
        if (evs != null) {
            mAdapter.setData(evs);
            mAdapter.notifyDataSetChanged();
        }
    }
    private String getTagsString(List<String> tags) {
        StringBuilder tagStringBuilder = new StringBuilder();
        for (String tag : tags) {
            tagStringBuilder.append(tag.toString() + ", ");
        }
        if (tags.size() > 0) {
            tagStringBuilder.replace(tagStringBuilder.length() - 2, tagStringBuilder.length(), "");
        }
        return tagStringBuilder.toString();
    }

    public void showMilestones(List<Milestone> milestones) {
        ArrayAdapter<Milestone> dataAdapter = new ArrayAdapter<Milestone>(this,
                android.R.layout.simple_spinner_item, milestones);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMileStoneSpinner.setAdapter(dataAdapter);
    }

    public void showAreas(List<Area> areas) {
        ArrayAdapter<Area> dataAdapter = new ArrayAdapter<Area>(this,
                android.R.layout.simple_spinner_item, areas);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAreaSpinner.setAdapter(dataAdapter);
    }

    List<Project> mProjects;

    public void showProjects(List<Project> projects) {
        mProjects = projects;
        ArrayAdapter<Project> dataAdapter = new ArrayAdapter<Project>(this,
                android.R.layout.simple_spinner_item, projects);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProjectSpinner.setAdapter(dataAdapter);
    }

    public void showPeople(List<Person> list) {
        ArrayAdapter<Person> dataAdapter = new ArrayAdapter<Person>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAssignedToSpinner.setAdapter(dataAdapter);
    }

    public void showStatuses(List<CaseStatus> list) {
        ArrayAdapter<CaseStatus> dataAdapter = new ArrayAdapter<CaseStatus>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatusesSpinner.setAdapter(dataAdapter);

    }

    public void showCategories(List<Category> categories) {
        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<Category>(this,
                android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(dataAdapter);
    }

    public void showPriorities(List<Priority> priorities) {
        ArrayAdapter<Priority> dataAdapter = new ArrayAdapter<Priority>(this,
                android.R.layout.simple_spinner_item, priorities);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPrioritySpinner.setAdapter(dataAdapter);
    }

    public void setupViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24px);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCaseEditViewModel.projectSelected(mProjects.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Emulate a back press
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.enter_slide_up, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.exit_slide_down);
    }
}
