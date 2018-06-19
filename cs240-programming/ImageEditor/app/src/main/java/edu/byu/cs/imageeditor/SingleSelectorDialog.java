package edu.byu.cs.imageeditor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class SingleSelectorDialog extends DialogFragment {

    private static final String TAG = "SingleSelectorDialog";
    private static final String ARGS_INDEX = "index";
    private static final String ARG_ITEMS = "items";
    private ArrayList<String> mItems;
    private int mSelectedIndex = -1;

    public static SingleSelectorDialog newInstance(ArrayList<String> items, int selectedIndex) {
        SingleSelectorDialog f = new SingleSelectorDialog();

        // Supply items input as an argument.
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_ITEMS, items);
        args.putInt(ARGS_INDEX, selectedIndex);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mItems = getArguments().getStringArrayList(ARG_ITEMS);
        mSelectedIndex = getArguments().getInt(ARGS_INDEX);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ArrayAdapter<String> selectionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, mItems);
        builder.setTitle(R.string.pick_file)
                .setSingleChoiceItems(selectionAdapter, mSelectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedIndex = which;

                    }
                })
                .setPositiveButton(R.string.file_selector_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        if (mSelectedIndex != -1) {
                            ((MainActivity) getActivity()).onFileSelected(mItems.get(mSelectedIndex));
                        }
                    }
                });

        return builder.create();
    }
}
