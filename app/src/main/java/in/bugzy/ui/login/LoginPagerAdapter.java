package in.bugzy.ui.login;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class LoginPagerAdapter extends FragmentStatePagerAdapter {
    String[] titles = new String[]{"", "", "", ""};

    Fragment[] mFragments = new Fragment[4];

    public LoginPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = mFragments[position];
        if (fragment != null) {
            return fragment;
        }
        switch (position) {
            case 0 :
                fragment = OrganisationFrgment.newInstance();
                break;
            case 1 :
                fragment = CredentialsFragment.newInstance();
                break;
            case 2 :
                fragment = ThemeSelectorFragment.newInstance();
                break;
            case 3 :
                fragment = KnowledgeFragment.newInstance();
                break;
        }
        mFragments[position] = fragment;
        return fragment;
    }
}
