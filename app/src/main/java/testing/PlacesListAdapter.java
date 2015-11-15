package testing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalfriends.com.aroundme.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by anup on 11/10/15.
 */
public class PlacesListAdapter extends ArrayAdapter<Places> {
    Context mContext;
    Places[] mPlaces;
    ImageLoader mImageLoader;

    private static class ViewHolder {
        public ImageView thumbnailImage;
        public TextView placeName;
        public TextView vicinity;
    }

    public PlacesListAdapter(Context context, Places[] objects) {
        super(context, R.layout.layout_places_item, objects);
        mContext = context;
        mPlaces = objects;
        mImageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context)
                .build();
        mImageLoader.init(imageLoaderConfiguration);
    }

    /*
    @Override
    public int getCount() {
        return mPlaces.length;
    }

    @Override
    public Object getItem(int position) {
        return mPlaces[position];
    }

    @Override
    public long getItemId(int position) {

        return position;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_places_item, null);

            viewHolder = new ViewHolder();
            viewHolder.thumbnailImage = (ImageView) convertView.findViewById(R.id.thumbnail_image);
            viewHolder.placeName = (TextView) convertView.findViewById(R.id.place_name);
            viewHolder.vicinity = (TextView) convertView.findViewById(R.id.vincinity);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Places place = (Places) getItem(position);

        viewHolder.placeName.setText(place.getName());
        viewHolder.vicinity.setText(place.getVicinity());
        mImageLoader.displayImage(place.getIcon(), viewHolder.thumbnailImage);

        return convertView;
    }
}
