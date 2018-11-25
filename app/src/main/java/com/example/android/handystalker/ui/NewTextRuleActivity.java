package com.example.android.handystalker.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.example.android.handystalker.R;
import com.example.android.handystalker.ui.Adapters.TextRulesPagerAdapter;

public class NewTextRuleActivity extends AppCompatActivity {

    //final ActionBar actionBar = getActionBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_text_rule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TextRulesPagerAdapter adapter = new TextRulesPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Specify that tabs should be displayed in the action bar.
        //assert actionBar != null;
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}