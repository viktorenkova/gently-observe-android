package com.gentleobserver.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gentleobserver.app.GentleObserverApp;
import com.gentleobserver.app.R;

/**
 * Onboarding Activity - first-time user introduction
 * Uses ViewPager for swipeable screens
 */
public class OnboardingActivity extends FragmentActivity {
    
    private ViewPager viewPager;
    private OnboardingPagerAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        
        viewPager = findViewById(R.id.viewPager);
        adapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }
    
    private void finishOnboarding() {
        GentleObserverApp.getInstance().setFirstRunComplete();
        // Go directly to CheckInActivity instead of MainActivity
        Intent intent = new Intent(this, CheckInActivity.class);
        startActivity(intent);
        finish();
    }
    
    public void goToNextPage() {
        int currentPosition = viewPager.getCurrentItem();
        if (currentPosition < 3) {
            viewPager.setCurrentItem(currentPosition + 1);
        } else {
            finishOnboarding();
        }
    }
    
    public class OnboardingPagerAdapter extends FragmentPagerAdapter {
        
        public OnboardingPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        
        @Override
        public Fragment getItem(int position) {
            return OnboardingFragment.newInstance(position);
        }
        
        @Override
        public int getCount() {
            return 4;
        }
    }
    
    public static class OnboardingFragment extends Fragment {
        
        private static final String ARG_POSITION = "position";
        
        public static OnboardingFragment newInstance(int position) {
            OnboardingFragment fragment = new OnboardingFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_onboarding, container, false);
            
            int position = getArguments().getInt(ARG_POSITION);
            
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            TextView tvText = view.findViewById(R.id.tvText);
            Button btnAction = view.findViewById(R.id.btnAction);
            
            switch (position) {
                case 0:
                    tvTitle.setText("Добро пожаловать");
                    tvText.setText("Это приложение поможет вам отслеживать самочувствие и готовиться к визиту к врачу");
                    btnAction.setText("Далее");
                    break;
                case 1:
                    tvTitle.setText("Как это работает");
                    tvText.setText("Отвечайте на вопросы о самочувствии, питании и эмоциях");
                    btnAction.setText("Продолжить");
                    break;
                case 2:
                    tvTitle.setText("Напоминания");
                    tvText.setText("Разрешите уведомления, чтобы не забывать о чек-инах");
                    btnAction.setText("Разрешить");
                    break;
                case 3:
                    tvTitle.setText("Готовы начать?");
                    tvText.setText("Начните первый чек-ин прямо сейчас");
                    btnAction.setText("Начать чек-ин");
                    break;
            }
            
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnboardingActivity activity = (OnboardingActivity) getActivity();
                    if (activity != null) {
                        activity.goToNextPage();
                    }
                }
            });
            
            return view;
        }
    }
}
