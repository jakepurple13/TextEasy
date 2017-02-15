package app.easy.text.texteasy.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.viethoa.RecyclerViewFastScroller;

import java.util.ArrayList;

import app.easy.text.texteasy.ContactList.ContactAdapter;
import app.easy.text.texteasy.ContactList.Contacts;
import app.easy.text.texteasy.R;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

public class Fastscrollerindex extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    IndexFastScrollRecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastscrollerindex);


        ArrayList<String> als = new ArrayList<>();

        for(int i=0;i<20;i++) {
            als.add(i + " is a number");
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rvView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        //mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ScrollingAdapter(als);
        //mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.GONE);

        rv = (IndexFastScrollRecyclerView) findViewById(R.id.fast_scroller_recycler);

        rv.setAdapter(mAdapter);
        rv.setLayoutManager(mLayoutManager);





    }
}
