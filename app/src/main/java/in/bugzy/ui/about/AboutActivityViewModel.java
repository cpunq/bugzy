package in.bugzy.ui.about;


import in.bugzy.utils.SingleLiveEvent;

import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

public class AboutActivityViewModel extends ViewModel {
    SingleLiveEvent<Void> navigateToLibrariesCommand = new SingleLiveEvent<>();

    @Inject
    AboutActivityViewModel() {

    }

    public void navigateToLibraries() {
        navigateToLibrariesCommand.call();
    }

    public SingleLiveEvent<Void> getNavigateToLibrariesCommand() {
        return navigateToLibrariesCommand;
    }
}
