package com.example.android.scanfest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.scanfest.Models.Child;
import com.example.android.scanfest.Models.ChildAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView navigationView;
    private NavigationMenuView navMenuview;
    private List<Child> childList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;


    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        progressBar = findViewById(R.id.progressBar);

        mDrawerLayout =(DrawerLayout) findViewById(R.id.drawer_Layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        navMenuview = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuview.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        loadUsers();

        FloatingActionButton fab = findViewById(R.id.add_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToaddChild();
            }
        });

    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        childList = new ArrayList<>();
        recyclerView = findViewById(R.id.childDisplay);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("StudentRecords");
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.exists()){
                    for (DataSnapshot dsUser: dataSnapshot.getChildren()){
                        Child users = dsUser.getValue(Child.class);
                        childList.add(users);
                    }
                    ChildAdapter adapter = new ChildAdapter(MainActivity.this,childList);
                    recyclerView.setAdapter(adapter);
                }else {
                    Toast.makeText(MainActivity.this, "No Child Added yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToaddChild() {
        Intent mainIntent = new Intent(this,AddChildActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
