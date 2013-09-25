package com.chaseit.fragments;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.chaseit.ParseHelper;
import com.chaseit.R;
import com.chaseit.activities.HuntShowImageActivity;
import com.chaseit.models.CIUser;
import com.chaseit.models.Hunt;
import com.chaseit.models.Location;
import com.chaseit.models.UserHunt;
import com.chaseit.models.wrappers.UserHuntWrapper;
import com.chaseit.util.Constants;
import com.chaseit.util.FragmentFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

public class HuntPlayFragment extends Fragment {

	private static final String tag = "Debug - com.chaseit.fragments.HuntPlayFragment";
	private UserHuntWrapper uHuntWrapper;

	private GoogleMap gMap;
	private ImageView ivHuntImageClue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_hunt_play, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle extras = getArguments();
		uHuntWrapper = (UserHuntWrapper) extras
				.getSerializable(Constants.USER_HUNT_WRAPPER_DATA_NAME);

		ParseHelper.getHuntByObjectId(uHuntWrapper.getHunt().getObjectId(),
				new GetCallback<Hunt>() {
					@Override
					public void done(Hunt hunt, ParseException e) {
						if (e == null) {
							ParseHelper.getLocationsforHunt(hunt,
									new FindCallback<Location>() {
										@Override
										public void done(
												List<Location> objects,
												ParseException e) {
											Log.d(tag, "");
										}
									});
						} else {
							Log.d(tag, e.getMessage());
						}

					}
				});

		LatLng latLongForHunt = getLatLongForHunt(uHuntWrapper.getHunt()
				.getObjectId());

		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		ft.replace(R.id.huntMap,
				FragmentFactory.getHuntMapWithMarkersFragment(null));
		ft.commit();

		setupViews(getView(), latLongForHunt);
	}

	private void setupViews(View view, LatLng latLongForHunt) {
		ivHuntImageClue = (ImageView) view.findViewById(R.id.ivHuntImageClue);
		ivHuntImageClue.setOnClickListener(getHuntImageClueClickListener());
		setupMaps(latLongForHunt);

		Button btnHuntStart = (Button) view
				.findViewById(R.id.btnHuntProgressCheck);
	}

	private OnClickListener getHuntImageClueClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bitmap bitmap = ((BitmapDrawable) ivHuntImageClue.getDrawable())
						.getBitmap();
				Intent in = new Intent(HuntPlayFragment.this.getActivity()
						.getBaseContext(), HuntShowImageActivity.class);
				in.putExtra(Constants.IMAGE_EXTRA, bitmap);
				startActivity(in);
			}
		};
	}

	private void setupMaps(LatLng latLongForHunt) {
		try {
			// Loading map
			initilizeMap();

			Log.d(tag, "adding markers");

			gMap.addMarker(new MarkerOptions()
					.position(latLongForHunt)
					.title("San Francisco")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

			gMap.moveCamera(CameraUpdateFactory.newLatLng(latLongForHunt));

			// Zoom in the Google Map
			gMap.animateCamera(CameraUpdateFactory.zoomTo(15));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * returns the lat and long given the huntid
	 */
	private LatLng getLatLongForHunt(String huntId) {
		return new LatLng(Constants.latUnionSquare, Constants.lngUnionSquare);
	}

	private void addMarkerToMap(LatLng latLng) {

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (gMap == null) {
			gMap = ((SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.huntMap))
					.getMap();

			// check if map is created successfully or not
			if (gMap == null) {
				Log.d(tag, "Sorry! unable to create maps");
			}
		}
	}

	private Hunt getHunt() {
		Hunt hunt = new Hunt();
		hunt.setName("Hunt name");
		hunt.setDetails("Hunt Details");
		hunt.setAvgRating(3.3);
		hunt.setLocality("Union Square");
		hunt.setNumRatings(3);
		hunt.setObjectId("AAAAAAAAA");
		hunt.setStartLocation(new ParseGeoPoint(Constants.latUnionSquare,
				Constants.lngUnionSquare));
		hunt.setTotalDistance(23);
		return hunt;
	}

	private UserHunt getUserHunt() {
		Hunt hunt = getHunt();
		UserHunt userHunt = new UserHunt();
		userHunt.setHunt(hunt);
		userHunt.setUser(CIUser.getCurrentUser());
		Location loc = new Location();
		loc.setLocation(hunt.getStartLocation());
		loc.setHint("first location Hint");
		userHunt.setLastLocation(loc);
		return userHunt;
	}
}
