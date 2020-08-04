package com.example.codeshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.codeshare.adapter.SearchAdapter;
import com.example.codeshare.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_recy)
    RecyclerView searchRecy;
    SearchAdapter searhAdapater;
    List<User> users = new ArrayList<>();

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> user_name;
    ArrayList<String> user_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
            }
        });


        /*
         * Create a array list for each node you want to use
         * */

        user_name = new ArrayList<>();
        user_image = new ArrayList<>();

        searhAdapater = new SearchAdapter(SearchActivity.this, users);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        searchRecy.setLayoutManager(layoutManager);
        searchRecy.setAdapter(searhAdapater);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search1).getActionView();
        searchView.setIconified(false);
        ((EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text)).setTextColor(getResources().getColor(R.color.hint_color));
        ((EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text)).setHintTextColor(getResources().getColor(R.color.hint_color));
        ((ImageView) searchView.findViewById(androidx.appcompat.R.id.search_close_btn)).setImageResource(R.drawable.icon_clear);
        searchView.setQueryHint("Search People ");


        //searchView.setOnQueryTextFocusChangeListener(new SearchView.OnQueryTextListener(){


//}
/*

        private void searchFromDb ( final String query, boolean b){
            databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*
                     * Clear the list for every new search
                     *
                    fullNameList.clear();

                    profilePicList.clear();
                    int counter = 0;

                    /*
                     * Search all users for matching searched string
                     * */
                 /*   for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String uid = snapshot.getKey();
                        String name = snapshot.child("name").getValue(String.class);
                        String image = snapshot.child("image").getValue(String.class);

                        if (name.toLowerCase().contains(query.toLowerCase())) {
                            fullNameList.add(name);
                            profilePicList.add(image);
                            counter++;
                        } else if (name.toLowerCase().contains(query.toLowerCase())) {
                            fullNameList.add(name);
                            profilePicList.add(image);
                            counter++;
                        }

                        /*
                         * Get maximum of 15 searched results only
                         * */
                      /*  if (counter == 15)
                            break;
                    }

                    searhAdapater = new SearchAdapter(SearchActivity.this, fullNameList, profilePicList);
                    searchView.searchFromDb(searhAdapater);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });



        }
    }

                       */
       return true;
    }
}





