package com.globalfriends.com.aroundme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.globalfriends.com.aroundme.R;

import testing.MainActivity;

/**
 * Created by vishal on 11/8/2015.
 */
public class Launcher extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, SelectionFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_launcher);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateFragment(new SelectionFragment(), true, false);
    }


    private void updateFragment(final Fragment fragment, final boolean first,
                                boolean isBackStack) {
        if (findViewById(R.id.fragment_container) != null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            if (first) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment).commit();
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
            if (isBackStack)
                getSupportFragmentManager().beginTransaction().addToBackStack(null);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_my_location:
                updateFragment(new MyLocationFragment(), false, true);
                break;
            case R.id.drawer_recent:
                updateFragment(new RecentFragment(), false, true);
                break;
            case R.id.drawer_favorite:
                updateFragment(new FavoriteFragment(), false, true);
                break;
            case R.id.drawer_maps:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.drawer_feedback:
                Toast.makeText(this, "feedback", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_send:
                Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_share:
                Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
                break;
            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String id) {

    }
}
