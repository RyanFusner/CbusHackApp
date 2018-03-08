package com.example.ryan.cbushackapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class HomeScreen extends FragmentActivity implements OnMapReadyCallback {
    public Context context;
    private GoogleMap mMap;
    private LatLng userLocation;
    private boolean canUseLocation;
    private Integer MIN_REQ_DISTANCE = 50;
    private List<Integer> ids = new ArrayList<>();
    private List<LatLng> locations = new ArrayList<>();
    private List<String> statuses = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        canUseLocation = false;
        context = this;
        createListeners();
        getBadgeInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        // Turns on location if permission is granted
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    canUseLocation = true;
                    mMap.setMyLocationEnabled(true);
                }
                else
                {
                    canUseLocation = false;
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        // Coordinates for map setup
        LatLng middle = new LatLng(39.965372184806796, -82.99029350280762);
        LatLngBounds discoveryDistrict = new LatLngBounds(new LatLng(39.95545789880199, -83.00033569335937), new LatLng(39.96963481889702, -82.98068046569824));

        // Adds markers to the map
        getBadgeInfo();
        for (Integer i = 0; i < locations.size(); i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(locations.get(i))
                    .title(titles.get(i))
                    .snippet(descriptions.get(i))
            );
        }

        // Sets up the map settings
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(15);
        mMap.setLatLngBoundsForCameraTarget(discoveryDistrict);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(middle));

        // Checks for permission to use location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
        // Turns on location if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            canUseLocation = true;
        }

    }

    private void createListeners()
    {
        // Creates listeners for buttons
        Button goToBadgeScreenButton = (Button) findViewById(R.id.BadgeScreenButton);
        goToBadgeScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Changes screen to badge screen
                Intent intent = new Intent(context, BadgeScreen.class);
                if (intent.resolveActivity(getPackageManager()) != null)
                {
                    startActivity(intent);
                }
            }
        });

        Button getBadgeButton = (Button) findViewById(R.id.GetBadgeButton);
        getBadgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checks for nearby badges if the user has given permission
                if (canUseLocation)
                {
                    checkForBadges();
                }
            }
        });
    }

    public void getBadgeInfo()
    {
        // Gets badge info from database
        BadgesDbHelper mDbHelper = new BadgesDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BadgesContract.BadgeEntry._ID,
                BadgesContract.BadgeEntry.COLUMN_NAME_TITLE,
                BadgesContract.BadgeEntry.COLUMN_NAME_DESCRIPTION,
                BadgesContract.BadgeEntry.COLUMN_NAME_LAT,
                BadgesContract.BadgeEntry.COLUMN_NAME_LNG,
                BadgesContract.BadgeEntry.COLUMN_NAME_STATUS
        };

        Cursor cursor = db.query(
                BadgesContract.BadgeEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                                  // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                     // The sort order
        );

        ids = new ArrayList<>();
        locations = new ArrayList<>();
        statuses = new ArrayList<>();
        titles = new ArrayList<>();
        descriptions = new ArrayList<>();

        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(cursor.getColumnIndex(BadgesContract.BadgeEntry._ID));
            String title = cursor.getString(cursor.getColumnIndex(BadgesContract.BadgeEntry.COLUMN_NAME_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(BadgesContract.BadgeEntry.COLUMN_NAME_DESCRIPTION));
            String lat = cursor.getString(cursor.getColumnIndex(BadgesContract.BadgeEntry.COLUMN_NAME_LAT));
            String lng = cursor.getString(cursor.getColumnIndex(BadgesContract.BadgeEntry.COLUMN_NAME_LNG));
            String status = cursor.getString(cursor.getColumnIndex(BadgesContract.BadgeEntry.COLUMN_NAME_STATUS));

            ids.add(id);
            titles.add(title);
            descriptions.add(description);
            locations.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
            statuses.add(status);
        }
        cursor.close();
        mDbHelper.close();
    }

    private void checkForBadges()
    {
        getBadgeInfo();
        // Tries to get location from user
        try
        {
            userLocation = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
        }
        catch(NullPointerException e)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("Could not get Location.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        Integer badgesInRange = 0;

        // Checks for nearby badges
        for (Integer i = 0; i < ids.size(); i++) {
            if (isInRange(userLocation, locations.get(i)) && Objects.equals(statuses.get(i), "0"))
            {
                // If badge is avaliable, give badge
                giveBadge(ids.get(i));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setMessage("You got a new badge!: " + titles.get(i))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                badgesInRange++;
            } else if (isInRange(userLocation, locations.get(i)) && Objects.equals(statuses.get(i), "1"))
            {
                // If badge is already own, notify user
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setMessage("You already have that badge.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                badgesInRange++;
            }
        }

        // If no badges in range, notify user
        if (badgesInRange == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("No badges in range.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public boolean isInRange(LatLng user, LatLng point)
    {
        // Checks if the user is in range of the badge
        boolean output = false;
        float[] results = new float[1];
        Location.distanceBetween(user.latitude, user.longitude, point.latitude, point.longitude, results);

        if (results[0] < MIN_REQ_DISTANCE) {
            output = true;
        }

        return output;
    }

    public void giveBadge(Integer rowId)
    {
        // Updates the status of the badge to give the user the badge
        BadgesDbHelper mDbHelper = new BadgesDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ContentValues args = new ContentValues();
        args.put("status", "1");
        db.update(BadgesContract.BadgeEntry.TABLE_NAME, args, "_id=" + rowId, null);
    }

}