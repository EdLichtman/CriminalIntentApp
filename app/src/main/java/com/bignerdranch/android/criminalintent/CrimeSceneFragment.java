package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by EdwardLichtman on 4/3/16.
 */
public class CrimeSceneFragment extends DialogFragment {

    public static final String ARG_CRIME_FILE = "Crime_Scene_Photo";

    public static CrimeSceneFragment newInstance(File crimeSceneFile) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_FILE, crimeSceneFile);

        CrimeSceneFragment crimeScene = new CrimeSceneFragment();
        crimeScene.setArguments(args);
        return crimeScene;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        File crimeSceneFile = (File) getArguments().getSerializable(ARG_CRIME_FILE);

        Bitmap imageFile = PictureUtils.getScaledBitmap(crimeSceneFile.getPath(), getActivity());

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_crime_scene, null);

        ImageView imageView = (ImageView) v.findViewById(R.id.dialog_crime_scene);
        imageView.setImageBitmap(imageFile);

        return new AlertDialog.Builder(getActivity())
                .setView(imageView)
                .setTitle(R.string.crime_scene_photo)
                .create();
    }
}
