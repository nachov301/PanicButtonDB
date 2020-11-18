package app.iggy.panicbutton;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

public class Adapter extends ArrayAdapter {
    private static final String TAG = "Adapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<Contacts> contacts;

    public Adapter(@NonNull Context context, int resource, List<Contacts> contacts) {
        super(context, resource);
        Log.d(TAG, "Adapter: starts");
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.contacts = contacts;
        Log.d(TAG, "Adapter: ends");
    }


    @Override
    public int getCount() {
        return contacts.size();
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

        Contacts currentApp = contacts.get(position);

        viewHolder.contactName.setText(currentApp.getContactName());
        viewHolder.phoneNumber.setText(currentApp.getPhoneNumber());

        ColorGenerator generator = ColorGenerator.MATERIAL;

        String contactName = currentApp.getContactName();
        char firstLetter = contactName.charAt(0);

        Drawable round = TextDrawable.builder().buildRound(String.valueOf(firstLetter),generator.getRandomColor());
        viewHolder.contactImg.setImageDrawable(round);

        return convertView;
    }

    public class ViewHolder{
        final TextView contactName;
        final TextView phoneNumber;
        final ImageView contactImg;

        ViewHolder (View v){
            this.contactName = v.findViewById(R.id.contactName);
            this.phoneNumber = v.findViewById(R.id.phoneNumber);
            this.contactImg = v.findViewById(R.id.contactImg);
        }
    }
}

