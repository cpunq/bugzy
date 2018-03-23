package com.bluestacks.bugzy.ui.editcase;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.CaseStatus;
import com.bluestacks.bugzy.data.model.Category;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.data.model.Priority;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.ui.BaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaseEditActivity extends BaseActivity {
    public static final String TAG = CaseEditActivity.class.getName();
    private CaseEditViewModel mCaseEditViewModel;
    @Inject
    ViewModelProvider.Factory mFactory;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter_slide_up, 0);
        setContentView(R.layout.activity_case_edit);
        ButterKnife.bind(this);
        setupViews();
        mCaseEditViewModel = ViewModelProviders.of(this, mFactory).get(CaseEditViewModel.class);
        subscribeToViewModel();
    }

    public void subscribeToViewModel() {
        mCaseEditViewModel.getMilestones().observe(this, value -> {
            if (value.data != null) {
                showMilestones(value.data);
            }
        });
        mCaseEditViewModel.getAreas().observe(this, value -> {
            if (value.data != null) {
                showAreas(value.data);
            }
        });
        mCaseEditViewModel.getProjects().observe(this, value -> {
            if (value.data != null) {
                showProjects(value.data);
            }
        });
        mCaseEditViewModel.getPeople().observe(this, value -> {
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

    public void showProjects(List<Project> projects) {
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24px);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
