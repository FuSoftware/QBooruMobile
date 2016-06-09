package fr.fusoft.qbooru.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.fusoft.qbooru.BuildConfig;
import fr.fusoft.qbooru.QBooruUtils;
import fr.fusoft.qbooru.R;
import fr.fusoft.qbooru.model.BooruPicture;
import fr.fusoft.qbooru.model.BooruRating;
import fr.fusoft.qbooru.model.BooruSite;
import fr.fusoft.qbooru.model.SearchFilter;

/**
 * Created by Florent on 31/03/2016.
 */
public class SearchFiltersFragment extends Fragment {

    private View myFragmentView;

    //Inputs
    EditText textH;
    EditText textW;

    CheckBox screenRes;

    RadioButton radioSortingDate;
    RadioButton radioSortingDateAsc;
    RadioButton radioSortingScore;

    RadioButton radioScoreSup;
    RadioButton radioScoreInf;
    RadioButton radioScoreEqu;

    RadioButton radioDateAny;
    RadioButton radioDateSelect;

    RadioButton radioRatingAny;
    RadioButton radioRatingSafe;
    RadioButton radioRatingQuest;
    RadioButton radioRatingExp;

    RadioGroup radioGroupSorting;
    RadioGroup radioGroupScore;
    RadioGroup radioGroupDate;
    RadioGroup radioGroupRating;

    EditText score;

    private BooruSite site;
    private SearchFilter filter;

    @Override
    public void onStart() {
        super.onStart();

        //UI
        textH = (EditText) myFragmentView.findViewById(R.id.editTextSearchH);
        textW = (EditText) myFragmentView.findViewById(R.id.editTextSearchW);

        score = (EditText) myFragmentView.findViewById(R.id.editTextSearchScore);

        screenRes = (CheckBox) myFragmentView.findViewById(R.id.checkBoxSearchScreenRes);

        radioSortingDate = (RadioButton) myFragmentView.findViewById(R.id.radioButtonSortingDate);
        radioSortingDateAsc = (RadioButton) myFragmentView.findViewById(R.id.radioButtonSortingDateR);
        radioSortingScore = (RadioButton) myFragmentView.findViewById(R.id.radioButtonSortingScore);

        radioScoreSup = (RadioButton) myFragmentView.findViewById(R.id.radioButtonSearchScoreSup);
        radioScoreInf = (RadioButton) myFragmentView.findViewById(R.id.radioButtonSearchScoreInf);
        radioScoreEqu = (RadioButton) myFragmentView.findViewById(R.id.radioButtonSearchScoreEq);

        radioDateAny = (RadioButton) myFragmentView.findViewById(R.id.radioButtonSearchDateAnytime);
        radioRatingSafe = (RadioButton) myFragmentView.findViewById(R.id.radioButtonSearchDateDay);

        radioRatingAny = (RadioButton) myFragmentView.findViewById(R.id.radioButtonFilterRatingAny);
        radioRatingSafe = (RadioButton) myFragmentView.findViewById(R.id.radioButtonFilterRatingSafe);
        radioRatingQuest = (RadioButton) myFragmentView.findViewById(R.id.radioButtonFilterRatingQuest);
        radioRatingExp = (RadioButton) myFragmentView.findViewById(R.id.radioButtonFilterRatingExp);

        radioGroupSorting = (RadioGroup) myFragmentView.findViewById(R.id.radioGroupFilterSorting);
        radioGroupDate = (RadioGroup) myFragmentView.findViewById(R.id.radioGroupFilterDate);
        radioGroupScore = (RadioGroup) myFragmentView.findViewById(R.id.radioGroupFilterScore);
        radioGroupRating = (RadioGroup) myFragmentView.findViewById(R.id.radioGroupFilterRating);

        TextView labelRating = (TextView) myFragmentView.findViewById(R.id.textViewFilterRating);

        //Init
        radioSortingDate.setChecked(true);
        radioDateAny.setChecked(true);

        if(BuildConfig.SAFE_BUILD){
            radioGroupRating.setVisibility(View.GONE);
            labelRating.setVisibility(View.GONE);
            //radioRatingSafe.setChecked(true);
        }else{
            radioRatingAny.setChecked(true);
        }


        if(getArguments() != null) {
            setBooru((BooruSite) getArguments().getSerializable("Booru"));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_search_filters, container, false);
        return myFragmentView;
    }

    public void setBooru(BooruSite site){
        this.site = site;

        if (site.getType() == BooruSite.SiteType.GELBOORU) {
            for (int i = 0; i < radioGroupSorting.getChildCount(); i++) {
                (radioGroupSorting.getChildAt(i)).setEnabled(false);
            }

            for (int i = 0; i < radioGroupDate.getChildCount(); i++) {
                (radioGroupDate.getChildAt(i)).setEnabled(false);
            }
        }

        filter = new SearchFilter(site);

    }

    public void setFilter(SearchFilter filter){this.filter = filter;}

    public void loadFilter(SearchFilter filter){
        //Resolution
        if(filter.getH() > 0){textH.setText(Integer.toString(filter.getH()));}
        if(filter.getW() > 0){textW.setText(Integer.toString(filter.getW()));}

        //Sorting
        switch(filter.getSortingFilter()){
            case SearchFilter.SORT_FILTER_DATE:
                radioSortingDate.setChecked(true);
                break;
            case SearchFilter.SORT_FILTER_DATE_REVERSE:
                radioSortingDateAsc.setChecked(true);
                break;
            case SearchFilter.SORT_FILTER_SCORE:
                radioSortingScore.setChecked(true);
                break;
        }

        //Score
        switch(filter.getScore()){
            case SearchFilter.SCORE_FILTER_SUP:
                radioScoreSup.setChecked(true);
                break;
            case SearchFilter.SCORE_FILTER_INF:
                radioScoreInf.setChecked(true);
                break;
            case SearchFilter.SCORE_FILTER_EQU:
                radioScoreEqu.setChecked(true);
                break;
        }

        if(filter.getScore() > 0){score.setText(Integer.toString(filter.getScore()));}

        //Date
        switch(filter.getDateFilter()){
            case SearchFilter.DATE_FILTER_ANY:
                radioDateAny.setChecked(true);
                break;
            case SearchFilter.DATE_FILTER_SELECT:
                radioDateSelect.setChecked(true);
                break;
        }

        //Rating
        if(filter.getR() == BooruRating.ALL){
            radioRatingAny.setChecked(true);
        }else if(filter.getR() == BooruRating.SAFE){
            radioRatingSafe.setChecked(true);
        }else if(filter.getR() == BooruRating.QUESTIONABLE){
            radioRatingQuest.setChecked(true);
        }else if(filter.getR() == BooruRating.EXPLICIT){
            radioRatingExp.setChecked(true);
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            /*** Score ***/
            case R.id.radioButtonSearchScoreSup:
                if (checked) {filter.setScoreFilter(SearchFilter.SCORE_FILTER_SUP);}
                break;
            case R.id.radioButtonSearchScoreInf:
                if (checked) {filter.setScoreFilter(SearchFilter.SCORE_FILTER_INF);}
                break;
            case R.id.radioButtonSearchScoreEq:
                if (checked) {filter.setScoreFilter(SearchFilter.SCORE_FILTER_EQU);}
                break;
            /*** Order ***/
            case R.id.radioButtonSortingDate:
                if (checked) {filter.setSortingFilter(SearchFilter.SORT_FILTER_DATE);}
                break;
            case R.id.radioButtonSortingDateR:
                if (checked) {filter.setSortingFilter(SearchFilter.SORT_FILTER_DATE_REVERSE);}
                break;
            case R.id.radioButtonSortingScore:
                if (checked) {filter.setSortingFilter(SearchFilter.SORT_FILTER_SCORE);}
                break;
            /*** Date ***/
            case R.id.radioButtonSearchDateAnytime:
                if (checked) {filter.setDateFilter(SearchFilter.DATE_FILTER_ANY);}
                radioDateSelect.setText(R.string.select_date);
                break;
            case R.id.radioButtonSearchDateDay:
                if (checked) {filter.setDateFilter(SearchFilter.DATE_FILTER_SELECT);}
                Dialog d = createDatePicker();
                d.show();
                break;
            /*** Rating ***/
            case R.id.radioButtonFilterRatingAny:
                if (checked) {filter.setRating(BooruRating.ALL);}
                break;
            case R.id.radioButtonFilterRatingSafe:
                if (checked) {filter.setRating(BooruRating.SAFE);}
                break;
            case R.id.radioButtonFilterRatingQuest:
                if (checked) {filter.setRating(BooruRating.QUESTIONABLE);}
                break;
            case R.id.radioButtonFilterRatingExp:
                if (checked) {filter.setRating(BooruRating.EXPLICIT);}
                break;
        }
    }

    public void onCheckBoxChecked(View view){
        // Is the button now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.checkBoxSearchScreenRes:
                setupResolution(checked);
                break;
        }
    }

    public SearchFilter getFilter(){
        return this.filter;
    }

    public void setupResolution(boolean useDevice){
        if(useDevice){
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            textH.setText(Integer.toString(size.y));
            textW.setText(Integer.toString(size.x));
        }else{
            textH.setText("");
            textW.setText("");
        }

        textH.setEnabled(!useDevice);
        textW.setEnabled(!useDevice);
    }

    public void generateFilters(){
        if(!score.getText().toString().isEmpty()) {
            filter.setScore(Integer.parseInt(score.getText().toString()));
        }else{
            filter.setScore(0);
        }

        int h,w;

        if(!textW.getText().toString().isEmpty()) {
            w = Integer.parseInt(textW.getText().toString());
        }else{
            w = 0;
        }

        if(!textH.getText().toString().isEmpty()) {
            h = Integer.parseInt(textH.getText().toString());
        }else{
            h = 0;
        }

        filter.setSize(w,h);
    }

    protected Dialog createDatePicker() {
        // TODO Auto-generated method stub
        Calendar calendar = Calendar.getInstance();
        return new DatePickerDialog(getContext(), mDateFilterListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    private DatePickerDialog.OnDateSetListener mDateFilterListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int y, int m, int d) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            Date date = QBooruUtils.getDate(y, m, d);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            filter.setDate(date);
            radioDateSelect.setText("Before " + formatter.format(date) );
        }
    };
}
