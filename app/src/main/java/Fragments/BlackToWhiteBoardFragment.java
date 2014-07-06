package Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.blacktowhite.BlackToWhiteActivity;
import com.blacktowhite.R;

import Internal.Grid;
import Internal.Panel;

/**
 * Justin Harjanto and Daniel Fang
 * Created 6/21/2014
 * BlackToWhite Fragment
 */
public class BlackToWhiteBoardFragment extends Fragment {
    private Grid g;
    private Switch solve;
    private SeekBar mSpeedOfSolve;
    private Button undo;
    private Button reset;
    private Button itr;
    private Button changeGridSize;
    private EditText loadGrid;

    private final boolean SHOW_MOVES = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_black_to_white, container, false);
        g = new Grid();
        Panel.panelNumber = 0;
        initializeGrid(v);

        loadGrid = (EditText) v.findViewById(R.id.loadboardedittext);
        mSpeedOfSolve = (SeekBar) v.findViewById(R.id.speedofseekbar);
        changeGridSize = (Button) v.findViewById(R.id.changegridsize);
        mSpeedOfSolve.setProgress(50);
        mSpeedOfSolve.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int delay, boolean b) {
                g.setSolveDelay(delay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        changeGridSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction f = getFragmentManager().beginTransaction();
                f.replace(R.id.container, new ChangeGridSizeFragment());
                f.commit();
            }
        });
        solve = (Switch) v.findViewById(R.id.solvebutton);
        solve.setChecked(false);
        solve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    g.solve(SHOW_MOVES);
                } else {
                    g.stopSolving();
                }
            }
        });

        itr = (Button) v.findViewById(R.id.itrbutton);
        itr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g.solveIter();
            }
        });

        undo = (Button) v.findViewById(R.id.undobutton);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g.undo();
            }
        });

        reset = (Button) v.findViewById(R.id.resetbutton);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solve.setChecked(false);
                mSpeedOfSolve.setProgress(50);
//                String id = loadGrid.getText().toString();
//                if (id != "") {
//                    g.generateBoard(id);
//                    loadGrid.setText("");
//                } else {
//                    g.generateBoard();
//                }
                g.resetToBlack();
                g.randomTilePresses(100);
            }
        });

        if (!BlackToWhiteActivity.DEBUG_MODE) {
            solve.setVisibility(View.INVISIBLE);
            undo.setVisibility(View.INVISIBLE);
            loadGrid.setVisibility(View.INVISIBLE);
            itr.setVisibility(View.GONE);
            reset.setVisibility(View.INVISIBLE);
        }

        return v;
    }





    /**
     * Initializes the grid with Panels.
     *
     * @param layout Layout containing all of the Buttons to construct panels.
     */
    private void initializeGrid(View layout) {
        final LinearLayout gridLayout = (LinearLayout) layout.findViewById((R.id.grid_view));
        final TableLayout grid = (TableLayout) layout.findViewById(R.id.grid);
        ViewTreeObserver observe = gridLayout.getViewTreeObserver();
        observe.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int totalMarginHorizontal = (int) (gridLayout.getMeasuredWidth() * Grid.MARGIN_PERCENT);
                int totalMarginVertical = (int) (gridLayout.getMeasuredHeight() * Grid.MARGIN_PERCENT);
                int gridWidth = gridLayout.getMeasuredWidth(); // for debug
                int gridHeight = gridLayout.getMeasuredHeight();
                int singlePanelWidth = gridLayout.getMeasuredWidth() / Grid.GRID_SIZE - totalMarginHorizontal;
                int singlePanelHeight = gridLayout.getMeasuredHeight() / Grid.GRID_SIZE - totalMarginVertical;
                for (int i = 0; i < Grid.GRID_SIZE; i++) {
                    TableRow row = new TableRow(getActivity());
                    row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams
                            .MATCH_PARENT));
                    row.setGravity(Gravity.CENTER);
                    for (int j = 0; j < Grid.GRID_SIZE; j++) {
                        Button panel = new Button(getActivity());
                        g.getGrid()[i][j] = new Panel(panel);
                        panel.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         g.changePanels(v);
                                                     }
                                                 });
                        TableRow.LayoutParams panelParams = new TableRow.LayoutParams(singlePanelWidth,
                                singlePanelHeight);
                        panelParams.setMargins(totalMarginHorizontal / 2,
                                totalMarginVertical / 2, totalMarginHorizontal / 2,
                                totalMarginVertical / 2);
                        panel.setLayoutParams(panelParams);
//                        panel.setHeight(singlePanelHeight);
//                        panel.setWidth(singlePanelWidth);
                        row.addView(panel);
                    }
                    grid.addView(row);
                }
                grid.requestLayout();
                g.generateBoard();
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

}
