package app.iggy.panicbutton2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ArrayAdapter extends android.widget.ArrayAdapter {
    private static final String TAG = "ArrayAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<Item> mItems;

    public ArrayAdapter(@NonNull Context context, int resource, List<Item> items) {
        super(context, resource);
        Log.d(TAG, "Adapter: starts");
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.mItems = items;
        Log.d(TAG, "Adapter: ends");
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return ((mItems != null) && (mItems.size() != 0) ? mItems.get(position) : null);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Item currentApp = mItems.get(position);

        viewHolder.title.setText(currentApp.getTitle());
        viewHolder.description.setText(currentApp.getDescription());
        viewHolder.mImageView.setImageResource(currentApp.getImage());
//        String title = currentApp.getTitle();
//        char firstLetter = title.charAt(0);
//        ColorGenerator generator = ColorGenerator.MATERIAL;
//
//        Drawable round = TextDrawable.builder().buildRound(String.valueOf(firstLetter), generator.getRandomColor());
//        Drawable round = TextDrawable.builder().buildRound(String.valueOf(firstLetter), generator.getColor(currentApp.getColor()));
//        viewHolder.mImageView.setImageDrawable(round);


        return convertView;
    }

    public class ViewHolder{

    final TextView title;
    final TextView description;
    final ImageView mImageView;

    ViewHolder(View v){
        this.title = v.findViewById(R.id.set_title);
        this.description = v.findViewById(R.id.set_description);
        this.mImageView = v.findViewById(R.id.set_imageview);
    }
    }
}
