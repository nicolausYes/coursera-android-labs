package com.modern.art;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class FragmentRectangles extends Fragment {

    SeekBar mSeekBar;
    View mViewLeftTop;
    View mViewLeftBottom;
    View mViewRightTop;
    View mViewRightMiddle;
    View mViewRightBottom;

    final static int HUE_MAX = 360;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rectangles, container, false);

        // initiate colored views
        mViewLeftTop = rootView.findViewById(R.id.view_left_top);
        mViewLeftBottom = rootView.findViewById(R.id.view_left_bottom);
        mViewRightTop = rootView.findViewById(R.id.view_right_top);
        mViewRightMiddle = rootView.findViewById(R.id.view_right_middle);
        mViewRightBottom = rootView.findViewById(R.id.view_right_bottom);

        mSeekBar = (SeekBar)rootView.findViewById(R.id.seekbar);
        mSeekBar.setMax(HUE_MAX);   // set max to 360. Hue have to be in range [0..360].

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // modify a hue of the views according to the current progress
                modifyViewColor(mViewLeftTop, progress);
                modifyViewColor(mViewLeftBottom, progress);
                modifyViewColor(mViewRightTop, progress);
                modifyViewColor(mViewRightMiddle, progress);
                modifyViewColor(mViewRightBottom, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_rectangles, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_more_information:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    int getViewColor(View view) {
        Drawable background = view.getBackground();
        return background instanceof ColorDrawable ? ((ColorDrawable) background).getColor() : 0;
    }

    void modifyViewColor(View view, int currentProgress) {

        // get current color of the view
        int currentColor = getViewColor(view);

        // convert RGB to HSV and get the hue value
        float[] hsv = new float[3];
        Color.colorToHSV(currentColor, hsv);
        float hue = hsv[0];

        // read initial hue of the view
        // if initial hue value is null (it is the first call of modifyViewColor for this view),
        // then set initial hue
        float initialHue;
        if(view.getTag(R.string.default_hue) == null) {
            initialHue = hue;
            view.setTag(R.string.default_hue, initialHue);
        } else {
            initialHue = (float)view.getTag(R.string.default_hue);
        }

        // change a hue
        hue = initialHue + currentProgress;

        // hue have to be in range [0..360]
        if(hue >= HUE_MAX)
            hue -= HUE_MAX;

        // convert HSV to RGB and set color to view
        hsv[0] = hue;
        setViewColor(view, Color.HSVToColor(hsv));
    }

    void setViewColor(View view, int color) {
        view.setBackgroundColor(color);
    }

    void showDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_text)
                .setPositiveButton(R.string.dialog_visit_moma, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent baseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.moma_url)));
                        Intent chooserIntent = Intent.createChooser(baseIntent, getString(R.string.dialog_view_url));
                        startActivity(chooserIntent);
                    }
                })
                .setNegativeButton(R.string.dialog_not_now, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
