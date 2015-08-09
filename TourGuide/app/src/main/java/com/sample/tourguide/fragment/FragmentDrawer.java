package com.sample.tourguide.fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sample.tourguide.adapter.NavigationDrawerAdapter;
import com.sample.tourguide.model.NavDrawerItem;
import com.sample.tourguide.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by keyur on 07-08-2015.
 */
public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter mAdapter;
    private View mContainerView;
    private static String[] mFragmentTitles = null;
    private FragmentDrawerListener drawerListener;

    public FragmentDrawer() {
   //Mandatory for Fragments
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();
        // preparing navigation drawer items
        for (int i = 0; i < mFragmentTitles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(mFragmentTitles[i]);
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // drawer labels
        mFragmentTitles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        // attach adapter
        mAdapter = new NavigationDrawerAdapter(getActivity(), getData());
        mRecyclerView.setAdapter (mAdapter);
        mRecyclerView.setLayoutManager (new LinearLayoutManager (getActivity ()));
        mRecyclerView.addOnItemTouchListener (new RecyclerTouchListener (getActivity (), mRecyclerView, new ClickListener () {
            @Override
            public void onClick (View view, int position) {
                drawerListener.onDrawerItemSelected (view, position);
                mDrawerLayout.closeDrawer (mContainerView);
            }

            @Override
            public void onLongClick (View view, int position) {

            }
        }));

        return layout;
    }
    /*
      Function to setup the drawer and toolbar
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        mContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }
   /*
   Interface to communicate with main activity
    */
    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }
   /*
    Inner class for tochlistener
     this will handle touch changes
    */
    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }
    /*
     Interface for Drawer Item selected
     */
    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}
