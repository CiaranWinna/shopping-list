package com.example.shopping_list;

// class that implements an array adapter for our custom list items
// imports
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
// class definition
public class CustomArrayAdapter extends BaseAdapter {

    // constructor for the class that takes in references to a context and
    // an array list

    /**
     * This is the constructor of the customArrayAdapter, it will take in a Context and an ArrayList of type CustomItem.
     * @param c
     * @param al
     */
    public CustomArrayAdapter(Context c, ArrayList<CustomItem> al) {
        context = c;
        al_items = al;
    }

    /**
     * This method will:
     * Genrate a view holder which will be used to store the data gathered by the adapter while running through the passed arraylist
     * The class will set the layout of each list item to the layout specified in the custom_item_layout XML file
     *
     * @param position
     * @param convert_view
     * @param parent
     * @return
     */
    // overridden method that will construct a View for the listview out of the
    // item at the given position
    public View getView(int position, View convert_view, ViewGroup parent) {

        // view holder to save us from requesting references to items over and over again
        ViewHolder holder;

        // if we do not have a view to recycle then inflate the layout and fix up the view holder
        if(convert_view == null) {
            holder = new ViewHolder();

            // get access to the layout infaltor service
            LayoutInflater inflator = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // inflate the XML custom item layout into a view to which we can add data
            convert_view = inflator.inflate(R.layout.custom_item_layout, parent, false);

            // pull all the items from the XML so we can modify them
            holder.tv_name = (TextView) convert_view.findViewById(R.id.tv_name);
            holder.iv_image = (ImageView) convert_view.findViewById(R.id.iv_image);

            // set the view holder as a tag on this convert view in case it needs to be
            // recycled
            convert_view.setTag(holder);
        }
        else {
            holder = (ViewHolder) convert_view.getTag();
        }

        // set all the data on the fields before returning it
        holder.iv_image.setImageResource(R.mipmap.ic_launcher);
        holder.tv_name.setText(al_items.get(position).getName());

        // return the constructed view
        return convert_view;

    }
    // overridden method that will tell the listview how many items of data there is
    // to be displayed
    public int getCount() { return al_items.size(); }

    // returns the rowid of the item at the given position. Given that we are using an
    // array list the rowid will be equal to the index of the item
    public long getItemId(int position) { return position; }

    // overridden method that will return the item at the given position in the list
    public Object getItem(int position) { return al_items.get(position); }

    // private fields of the class we need a copy of the context in order to
    // update the list view properly and a link to the array list in order to
    // provide data for generating list items
    private Context context;
    private ArrayList<CustomItem> al_items;
    static class ViewHolder {
        public TextView tv_name;
        public ImageView iv_image;
    }
}