package fr.fusoft.qbooru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import fr.fusoft.qbooru.model.BooruSite;
import fr.fusoft.qbooru.model.SearchFilter;
import fr.fusoft.qbooru.view.SearchFiltersFragment;

public class SearchFiltersActivity extends AppCompatActivity {

    SearchFiltersFragment fragment;
;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filters);
        fragment = (SearchFiltersFragment) getSupportFragmentManager().findFragmentById(R.id.filter_fragment);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.generateFilters();
                //Snackbar.make(view, "Filters : " + fragment.getFilter().generateTags(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                returnFilter();
            }
        });
    }

    protected void onStart(){
        super.onStart();
        fragment.setBooru((BooruSite) getIntent().getExtras().getSerializable("Booru"));
        fragment.setFilter((SearchFilter) getIntent().getExtras().getSerializable("Filter"));
    }

    public void onRadioButtonClicked(View view){
        fragment.onRadioButtonClicked(view);
    }

    public void onCheckBoxChecked(View view){
        fragment.onCheckBoxChecked(view);
    }

    public void returnFilter(){
        Intent intent = new Intent();
        intent.putExtra("Filter", fragment.getFilter());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
