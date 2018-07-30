package com.briatka.pavol.favouriteplaces.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.fragments.FavPlacesFragment;
import com.briatka.pavol.favouriteplaces.fragments.TripListFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    Context mContext;

    public FragmentAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.mContext = context;


    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TripListFragment();
        } else {
            return new FavPlacesFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.tab_one_text);
        } else {
            return mContext.getString(R.string.tab_two_text);
        }
    }
}
