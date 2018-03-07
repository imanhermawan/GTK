package com.example.iman.gtk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Iman on 02/03/2018.
 */
public abstract class ReverseFirebaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{
    private static String TAG="RevFireRecyAdap";

    Class<T> mModelClass;
    protected int mModelLayout;
    Class<VH> mViewHolderClass;
    ReverseFirebaseArray mSnapshots;

    /**
     * @param modelClass Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                   combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>
     */
    public ReverseFirebaseRecyclerAdapter(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, Query ref) {
        mModelClass = modelClass;
        mModelLayout = modelLayout;
        mViewHolderClass = viewHolderClass;
        mSnapshots = new ReverseFirebaseArray(ref);

        mSnapshots.setOnChangedListener(new ReverseFirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                switch (type) {
                    case Added:
                        //Log.i(TAG, "zz setOnChangedListener Added index="+index);
                        //notifyItemInserted(index);
                        notifyItemInserted(-1);
                        break;
                    case Changed:
                        //Log.i(TAG, "zz setOnChangedListener Changed index="+index);
                        notifyItemChanged(index);
                        break;
                    case Removed:
                        //Log.i(TAG, "zz setOnChangedListener Removed index="+index);
                        //notifyItemRemoved(index);
                        notifyItemRemoved(mSnapshots.getCount());
                        break;
                    case Moved:
                        //Log.i(TAG, "zz setOnChangedListener Moved index="+index);
                        notifyItemMoved(oldIndex, index);
                        break;
                    default:
                        throw new IllegalStateException("Incomplete case statement");
                }
            }
        });
    }

    /**
     * @param modelClass Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                   combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>
     */
    public ReverseFirebaseRecyclerAdapter(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, DatabaseReference ref) {
        this(modelClass, modelLayout, viewHolderClass, (Query) ref);
    }

    public void cleanup() {
        mSnapshots.cleanup();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.getCount();
    }

    public T getItem(int position) {
        position=getItemCount()-(position+1);
        return parseSnapshot(mSnapshots.getItem(position));
    }

    /**
     * This method parses the DataSnapshot into the requested type. You can override it in subclasses
     * to do custom parsing.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    protected T parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(mModelClass);
    }

    public DatabaseReference getRef(int position) {
        position=getItemCount()-(position+1);
        return mSnapshots.getItem(position).getRef();
    }

    @Override
    public long getItemId(int position) {
        position=getItemCount()-(position+1);
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots.getItem(position).getKey().hashCode();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        try {
            Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        //position=getItemCount()-(position+1);
        T model = getItem(position);
        populateViewHolder(viewHolder, model, position);
    }

    @Override
    public int getItemViewType(int position) {
        return mModelLayout;
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param viewHolder The view to populate
     * @param model      The object containing the data used to populate the view
     * @param position  The position in the list of the view being populated
     */
    abstract protected void populateViewHolder(VH viewHolder, T model, int position);
}
