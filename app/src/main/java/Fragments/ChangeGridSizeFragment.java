package Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.blacktowhite.R;

import Internal.Grid;

/**
 * Created by Justin on 7/6/2014.
 */
public class ChangeGridSizeFragment extends Fragment {
    private Button mOkay;
    private NumberPicker mChangeGridSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.change_grid_size, container, false);
        findViews(v);
        return v;
    }

    /**
     * Finds and sets the views.
     * @param v Layout containing all the views.
     */
    private void findViews(View v){
        mOkay = (Button) v.findViewById(R.id.confirm_grid);
        mChangeGridSize = (NumberPicker) v.findViewById(R.id.pick_grid_size);
        mChangeGridSize.setMinValue(2);
        mChangeGridSize.setMaxValue(25);
        mChangeGridSize.setValue(3);
        mOkay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Grid.GRID_SIZE = mChangeGridSize.getValue();
                FragmentTransaction f = getFragmentManager().beginTransaction();
                f.replace(R.id.container, new BlackToWhiteBoardFragment());
                f.commit();
            }
        });
    }

}
