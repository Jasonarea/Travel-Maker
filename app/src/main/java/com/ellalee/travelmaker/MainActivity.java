package com.ellalee.travelmaker;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtilLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.widget.Toast.*;
import static com.google.android.gms.auth.api.credentials.CredentialPickerConfig.Prompt.SIGN_IN;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private String[] navItems = {"LogIn", "예산관리"};
    private ListView lvNavList;
    private FrameLayout flContainer;
    private DrawerLayout dlDrawer;
    private ImageButton btn;
    static GoogleAccountCredential mCredential;
   // SQLiteDatabase db;
//    PlanSQLiteHelper helper;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static  com.google.api.services.calendar.Calendar mService = null;
    private static final String PREF_ACCOUNT_NAME = "AccountName";
    private static final String[] SCOPES = { "https://www.googleapis.com/auth/calendar" };
    private String mEmail;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private GoogleApiClient mGoogleApiClient;


 //   SQLiteDatabase db;
    PlanSQLiteHelper db;

    @Override

    public void onBackPressed() {

        if (dlDrawer.isDrawerOpen(lvNavList)) {

            dlDrawer.closeDrawer(lvNavList);

        } else {

            super.onBackPressed();

        }
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        helper = new PlanSQLiteHelper(getApplicationContext());
        db = new PlanSQLiteHelper(getApplicationContext());

        lvNavList = (ListView)findViewById(R.id.lv_activity_main_nav_list);

        flContainer = (FrameLayout)findViewById(R.id.fl_activity_main_container);

        btn = (ImageButton)findViewById(R.id.menu_action_button);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        btn.setOnClickListener(new OnClickListener() {



            @Override

            public void onClick(View v) {
                dlDrawer.openDrawer(lvNavList);
            }

        });

        dlDrawer = (DrawerLayout)findViewById(R.id.dl_activity_main_drawer);

        String email = loadSavedPreferences();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(acct != null){
            navItems[0] = "LogOut";
        }
        else navItems[0] = "LogIn";
        lvNavList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));

        lvNavList.setOnItemClickListener(new MainActivity.DrawerItemClickListener());

    }

    private String loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("email", "EmailStuff");
        return name;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Connection Succeeded", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override

        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

            switch (position) {

                case 0:
                    mCredential = GoogleAccountCredential.usingOAuth2(
                            getApplicationContext(), Arrays.asList(SCOPES))
                            .setBackOff(new ExponentialBackOff());
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    if(acct==null){
                        signIn();
                        navItems[0] = "LogOut";
                        lvNavList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, navItems));
                    }
                    else{
                        signOut();
                    }

                    break;

                case 1:

                    flContainer.setBackgroundColor(Color.parseColor("#5F9EA0"));

                    break;

            }

            dlDrawer.closeDrawer(lvNavList);

        }

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        navItems[0] = "LogIn";lvNavList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, navItems));

                    }
                });
    }
    public void calenderMain(View v) {
        startActivity(new Intent(getApplicationContext(), CalendarMain.class));
    }

    public void mapMain(View v){

        EditText input = findViewById(R.id.EditWhereToGo);
        String city = input.getText().toString();
        LatLng center;
        Plan plan;

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        double latitude, longitude;

        try {
            list = geocoder.getFromLocationName(city, 10);
        } catch (IOException e) {
            e.printStackTrace();
            makeText(MainActivity.this, "I/O Error", LENGTH_SHORT).show();
        }

        if (list != null) {
            if (list.size() == 0) {
                makeText(MainActivity.this, "No matching area info", LENGTH_SHORT).show();
            }
            else {
                plan = new Plan();

                latitude = list.get(0).getLatitude();
                longitude = list.get(0).getLongitude();

                center = new LatLng(latitude, longitude);

                plan.setCentre(center);
                plan.setCity(city);
                plan.setTitle(city); //default title is a city name

                long plan_id = db.createPlan(plan);
/*                db = helper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("KEY_ID",plan_id);
                db.insert("TABLE_PLAN",null,values);
*/
                Intent intent = new Intent(getApplicationContext(),MapMain.class);
                intent.putExtra("plan_id",plan_id);
                //putSerializable("newPlan",plan);
//                intent.putExtras(bundle);

                startActivity(intent);
            }
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case SIGN_IN :
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
        }

    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());

        }
    }
    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    public void showPlanList(View v){
        Intent intent = new Intent(MainActivity.this,planListActivity.class);
        startActivity(intent);
    }
}

