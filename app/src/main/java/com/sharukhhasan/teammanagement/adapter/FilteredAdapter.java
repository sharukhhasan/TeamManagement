package com.sharukhhasan.teammanagement.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.sharukhhasan.teammanagement.model.FilteredAdapterInterface;
import com.sharukhhasan.teammanagement.model.ModelKeyInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharukhhasan on 5/7/16.
 */
public abstract class FilteredAdapter<T> extends BaseAdapter {
    private Query mRef;
    private Class<T> mModelClass;
    private int mLayout;
    private LayoutInflater mInflater;
    private List<T> mModels;
    private List<String> mKeys;
    private ChildEventListener mListener;

    public FilteredAdapter(Query mRef, Class<T> mModelClass, int mLayout, Activity activity, FilteredAdapterInterface filter) {
        final FilteredAdapterInterface filter2 = filter;
        this.mRef = mRef;
        this.mModelClass = mModelClass;
        this.mLayout = mLayout;
        mInflater = activity.getLayoutInflater();
        mModels = new ArrayList<T>();
        mKeys = new ArrayList<String>();

        mListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName)
            {

                T model = dataSnapshot.getValue(FilteredAdapter.this.mModelClass);
                ((ModelKeyInterface)model).setKey(dataSnapshot.getKey());
                String key = dataSnapshot.getKey();
                if(!filter2.allowObject(model))
                    return;

                insertChild(model, key, previousChildName);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName)
            {
                String key = dataSnapshot.getKey();
                T newModel = dataSnapshot.getValue(FilteredAdapter.this.mModelClass);
                ((ModelKeyInterface)newModel).setKey(dataSnapshot.getKey());

                if(!filter2.allowObject(newModel))
                {
                    if(mKeys.contains(key))
                    {
                        int index = mKeys.indexOf(key);
                        mKeys.remove(index);
                        mModels.remove(index);

                        notifyDataSetChanged();
                    }
                    return;
                }
                else if(!mKeys.contains(key))
                {
                    insertChild(newModel, key, previousChildName);
                }

                int index = mKeys.indexOf(key);

                mModels.set(index, newModel);

                notifyDataSetChanged();
            }

            private void insertChild(T model, String key, String previousChildName)
            {
                if(previousChildName == null)
                {
                    mModels.add(0, model);
                    mKeys.add(0, key);
                }
                else
                {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if(nextIndex == mModels.size())
                    {
                        mModels.add(model);
                        mKeys.add(key);
                    }
                    else
                    {
                        mModels.add(nextIndex, model);
                        mKeys.add(nextIndex, key);
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                if(!filter2.allowObject(dataSnapshot.getValue(FilteredAdapter.this.mModelClass)))
                    return;

                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                mKeys.remove(index);
                mModels.remove(index);

                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName)
            {
                String key = dataSnapshot.getKey();
                T newModel = dataSnapshot.getValue(FilteredAdapter.this.mModelClass);
                ((ModelKeyInterface)newModel).setKey(dataSnapshot.getKey());

                if(!filter2.allowObject(newModel))
                    return;

                int index = mKeys.indexOf(key);
                mModels.remove(index);
                mKeys.remove(index);

                if(previousChildName == null)
                {
                    mModels.add(0, newModel);
                    mKeys.add(0, key);
                }
                else
                {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;

                    if(nextIndex == mModels.size())
                    {
                        mModels.add(newModel);
                        mKeys.add(key);
                    }
                    else
                    {
                        mModels.add(nextIndex, newModel);
                        mKeys.add(nextIndex, key);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError)
            {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }

    public void cleanup()
    {
        mRef.removeEventListener(mListener);
        mModels.clear();
        mKeys.clear();
    }

    @Override
    public int getCount()
    {
        return mModels.size();
    }

    @Override
    public Object getItem(int i)
    {
        return mModels.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if(view == null)
        {
            view = mInflater.inflate(mLayout, viewGroup, false);
        }

        T model = mModels.get(i);
        populateView(view, model);
        return view;
    }

    protected abstract void populateView(View v, T model);
}
