package fr.fusoft.qbooru.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import fr.fusoft.qbooru.R;
import fr.fusoft.qbooru.model.BooruPicture;

/**
 * Created by Florent on 06/03/2016.
 */
public class PictureViewerTagsFragment extends Fragment {

    BooruPicture picture;
    private View myFragmentView;

    public PictureViewerTagsFragment() {
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

        ListView list = (ListView) myFragmentView.findViewById(R.id.listViewPictureTags);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("Tag", picture.getTagsArray().get(position));
                intent.putExtra("Reset", false);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_picture_viewer_tags, container, false);
        return myFragmentView;
    }

    public void loadInfo(BooruPicture picture) {
        ListView list = (ListView) myFragmentView.findViewById(R.id.listViewPictureTags);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.image_tags_item,
                R.id.imageTag,
                picture.getTagsArray() );

        list.setAdapter(arrayAdapter);

    }
}
