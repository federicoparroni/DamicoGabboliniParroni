package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.IdentityManager;
import com.example.gabdampar.travlendar.Controller.MappingServiceAPIWrapper;
import com.example.gabdampar.travlendar.Controller.Synchronizer;
import com.example.gabdampar.travlendar.Model.User;
import com.example.gabdampar.travlendar.Controller.ViewController.Fragment.AppointmentsListFragment;
import com.example.gabdampar.travlendar.Controller.ViewController.Fragment.HomeFragment;
import com.example.gabdampar.travlendar.Controller.ViewController.Fragment.ScheduleListFragment;
import com.example.gabdampar.travlendar.Controller.ViewController.Fragment.UserProfileFragment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity{

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtUsername, txtUserDetails;
    private Toolbar toolbar;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    public static final String TAG_HOME = "home";
    private static final String TAG_APPOINTMENTS = "appts";
    private static final String TAG_SCHEDULES = "schedules";
    private static final  String TAG_PROFILE = "profile";
    //private static final String TAG_NOTIFICATIONS = "notifications";

    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** synchronize appointment (load from file) */
        Synchronizer.GetInstance().Synchronize(this);

        /**
         * the first time the user settings will be initialized to the default values
         * take care to initialize with the same default values of the database
         */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtUsername = navHeader.findViewById(R.id.header_user_name_field);
        txtUserDetails = navHeader.findViewById(R.id.header_user_detail_field);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.main_menu_items_array);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        /*MappingServiceAPIWrapper.getInstance().getStopDistance(new MappingServiceAPIWrapper.StopServiceCallbackListener() {
            @Override
            public void StopServiceCallback(LatLng latLng) {
            }
        }, TravelMeanEnum.BUS, new LatLng(45.484075,9.241673), 2000);*/

        //AppointmentManager.GetInstance().CreateDummyAppointments();
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        IdentityManager.GetUserProfile(new IdentityManager.UserProfileListener() {
            @Override
            public void UserProfileCallback(boolean success, User user) {
                if(success) {
                    txtUsername.setText(user.email);
                    // sync user
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    IdentityManager.GetInstance().user.hasCar = sharedPref.getBoolean("has_car", false);
                    IdentityManager.GetInstance().user.hasBike = sharedPref.getBoolean("has_bike", false);
                    IdentityManager.GetInstance().user.hasPass = sharedPref.getBoolean("has_pass", false);

                } else {
                    txtUserDetails.setText("null");
                    Log.e("Error", "Cannot load user profile");
            }
        }
    });

    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    public void loadHomeFragment() {
        // set toolbar title
        setToolbarTitle();

        if (getFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, with fragment with huge data, screen seems hanging when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getCurrentFragment();

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getCurrentFragment() {
        switch (navItemIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                AppointmentsListFragment appointmentListFragment = new AppointmentsListFragment();
                return appointmentListFragment;
            case 2:
                ScheduleListFragment scheduleListFragment = new ScheduleListFragment();
                return scheduleListFragment;
            case 3:
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                return userProfileFragment;
            /*case 3:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            case 4:

                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;*/
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    /*private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }*/

    private void cleanMenuItems(){
        for(int i=0; i<navigationView.getMenu().size(); i++)
            navigationView.getMenu().getItem(i).setChecked(false);

    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                cleanMenuItems();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        changeFragment(TAG_HOME,0);
                        break;
                    case R.id.nav_appointments:
                        changeFragment(TAG_APPOINTMENTS,1);
                        break;
                    case R.id.nav_schedules:
                        changeFragment(TAG_SCHEDULES,2);
                        break;
                    case R.id.nav_logout:
                        IdentityManager.Logout();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("calling-activity", 1);
                        startActivity(intent);
                        return true;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_user_profile:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PROFILE;
                        loadHomeFragment();
                        break;
                    default:
                        navItemIndex = 0;
                }
                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    //===== build right menu in action bar
    //@Override
    /*public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            //getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if(id == R.id.nav_user_profile) {
            navItemIndex = 3;
            CURRENT_TAG = TAG_PROFILE;
            loadHomeFragment();
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeFragment(String fragmentTag, int index){
        navItemIndex = index;
        CURRENT_TAG = fragmentTag;
        navigationView.getMenu().getItem(index).setChecked(true);
        loadHomeFragment();
    }
}