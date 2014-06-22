package Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blacktowhite.R;

import Internal.Grid;
import Internal.Panel;

/**
 * Justin Harjanto and Daniel Fang
 * Created 6/21/2014
 * BlackToWhite Fragment
 */
public class BlackToWhiteBoardFragment extends Fragment implements View.OnClickListener{
    private Grid g;
    private boolean DEBUG_MODE = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_black_to_white, container, false);
        g = new Grid();
        initializeGrid(v);

        Button solve = (Button) v.findViewById(R.id.solvebutton);
        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g.solve();
            }
        });

        Button undo = (Button) v.findViewById(R.id.undobutton);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g.undo();
            }
        });

        Button reset = (Button) v.findViewById(R.id.resetbutton);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g.generateBoard();
            }
        });

        if (!DEBUG_MODE) {
            solve.setVisibility(View.INVISIBLE);
            undo.setVisibility(View.INVISIBLE);
        }

        return v;
    }


    /**
     * Initializes the grid with Panels.
     * @param layout Layout containing all of the Buttons to construct panels.
     */
    private void initializeGrid(View layout){
        Button[] allButtons = new Button[]{(Button) layout.findViewById(R.id.panel1),
                (Button) layout.findViewById(R.id.panel2), (Button) layout.findViewById(R.id
                .panel3), (Button) layout.findViewById(R.id.panel4),
                (Button) layout.findViewById(R.id.panel5), (Button) layout.findViewById(R.id
                .panel6), (Button) layout.findViewById(R.id.panel7),
                (Button) layout.findViewById(R.id.panel8), (Button) layout.findViewById(R.id
                .panel9)};
        for(int i = 0; i < allButtons.length; i++){
            int row = i / g.getGrid().length;
            int col = i % g.getGrid().length;
            g.getGrid()[row][col] = new Panel(allButtons[i]);
            allButtons[i].setOnClickListener(this);
        }
        g.generateBoard();
    }

    @Override
    public void onClick(View v) {
        g.changePanels(v);
    }

}
