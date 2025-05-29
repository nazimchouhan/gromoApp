package com.example.gromoapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.genius.gromo.fragments.CoursesFragment;
import com.genius.gromo.fragments.TrainingFragment;


public class LearnPagerAdapter extends FragmentStateAdapter {

    public LearnPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TrainingFragment();
            case 1:
                return new CoursesFragment();
            default:
                return new TrainingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
} 