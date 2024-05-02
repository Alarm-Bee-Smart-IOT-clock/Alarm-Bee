package com.example.arlambee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DeleteBottomSheetFragment extends BottomSheetDialogFragment {
    private String mItemId;
    private OnDeleteClickListener mListener;
    public interface OnDeleteClickListener {
        void onDeleteClick(String itemId);

        void onDeleteClick();
    }


    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        mListener = listener;
    }

    public static DeleteBottomSheetFragment newInstance(String itemId) {
        DeleteBottomSheetFragment fragment = new DeleteBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("itemId", itemId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemId = getArguments().getString("itemId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_delete, container, false);

        view.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDeleteClick();
                }
                dismiss(); // Dismiss the Bottom Sheet after delete button click
            }
        });

        return view;
    }
}
