package fr.fusoft.qbooru.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import fr.fusoft.qbooru.adapter.AdapterPictureInfo;
import fr.fusoft.qbooru.R;
import fr.fusoft.qbooru.model.BooruPicture;

/**
 * Created by Florent on 06/03/2016.
 */

public class PictureViewerInfoFragment extends Fragment {

    BooruPicture picture;

    private View myFragmentView;

    public PictureViewerInfoFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picture = (BooruPicture) getArguments().getSerializable("Picture");
    }

    @Override
    public void onStart() {
        super.onStart();
        loadInfo(picture);

        ListView list = (ListView) myFragmentView.findViewById(R.id.listViewPictureInfo);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadAction(position);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                loadLongAction(position);
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_picture_viewer_info, container, false);
        return myFragmentView;
    }

    public void loadInfo(BooruPicture picture) {
        ListView list = (ListView) myFragmentView.findViewById(R.id.listViewPictureInfo);
        AdapterPictureInfo arrayAdapter = new AdapterPictureInfo(getActivity(), picture);
        list.setAdapter(arrayAdapter);

    }

    public void loadAction(int i){

        /*
        0 //ID
        1 //Author
        2 //Creation
        3 //Source
        4 //Size
        5 //Rating
        6 //Score
         */

        switch(i){
            case 0:
                Intent showIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(picture.getShowUrl()));
                startActivity(showIntent);
                break;

            case 1:
                Intent intent = new Intent();
                intent.putExtra("Tag", "user:" + picture.getDataList().get(i));
                intent.putExtra("Reset", true);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
                break;
            case 3:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(picture.getDataList().get(i)));
                startActivity(browserIntent);
                break;
        }
    }

    public void loadLongAction(int i){

        /*
        0 //ID
        1 //Author
        2 //Creation
        3 //Source
        4 //Size
        5 //Rating
        6 //Score
         */
        Toast toast;
        ClipboardManager clipboard;
        ClipData clip;
        String label;
        String text;

        switch(i){
            case 0:
                label = "QBooru " + picture.getFullID();
                text = picture.getShowUrl();
                clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
                clip = ClipData.newPlainText(label, text);
                clipboard.setPrimaryClip(clip);
                toast = Toast.makeText(getActivity(), "Copied post's URL in clipboard", Toast.LENGTH_SHORT);toast.show();
                break;
            case 4:
                label = "QBooru " + picture.getFullID();
                text = picture.getFullUrl();
                clipboard = (ClipboardManager)  getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
                clip = ClipData.newPlainText(label, text);
                clipboard.setPrimaryClip(clip);
                toast = Toast.makeText(getActivity(), "Copied post's full URL in clipboard", Toast.LENGTH_SHORT);toast.show();
                break;
        }
    }
}
