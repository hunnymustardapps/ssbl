package com.eric.ssbl.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.eric.ssbl.R;
import com.eric.ssbl.android.fragments.ChartFragment;
import com.eric.ssbl.android.fragments.EventListFragment;
import com.eric.ssbl.android.fragments.InboxFragment;
import com.eric.ssbl.android.fragments.ProfileFragment;
import com.eric.ssbl.android.fragments.SettingsFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MainActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {
        this.disableLearningPattern();
        MaterialAccount account = new MaterialAccount(this.getResources(),"Sanic Weedhog","gotta.go.fast@sanic.com", R.drawable.honey, R.drawable.md_tangents);
        this.addAccount(account);

        MaterialSection map = newSection(getString(R.string.map), new ChartFragment());
        MaterialSection inbox = newSection(getString(R.string.inbox), new InboxFragment());
        MaterialSection events = newSection(getString(R.string.events), new EventListFragment());
        MaterialSection profile = newSection(getString(R.string.profile), new ProfileFragment());
        // divisor
        MaterialSection settings = newSection(getString(R.string.settings), new SettingsFragment());

        this.addSection(map);
        this.addSection(inbox);
        this.addSection(events);
        this.addSection(profile);

        this.addDivisor();

        this.addSection(settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_log_out:
                // Clean ModelManager data
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}