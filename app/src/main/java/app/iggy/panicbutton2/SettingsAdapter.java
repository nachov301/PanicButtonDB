package app.iggy.panicbutton2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class SettingsAdapter extends BaseActivity {
    private static final String TAG = "SettingsAdapter";
    private ListView settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        activateToolbar(true);

        settings = (ListView) findViewById(R.id.set_listview);


        Log.d(TAG, "onClick: starts");
        final ArrayList<Item> items = new ArrayList<Item>();


        Item item = new Item(getString(R.string.setada_contacts), getString(R.string.setada_contacts_description), R.drawable.baseline_contacts_black_24);
        items.add(item);

        Item item2 = new Item(getString(R.string.setada_time), getString(R.string.setada_time_description), R.drawable.baseline_timer_black_24);
        items.add(item2);

        Item item3 = new Item(getString(R.string.setada_message), getString(R.string.setada_message_description), R.drawable.baseline_sms_black_24);
        items.add(item3);

        Item item4 = new Item(getString(R.string.setada_call), getString(R.string.setada_call_description), R.drawable.baseline_tty_black_24);
        items.add(item4);


        final ArrayAdapter adapter = new ArrayAdapter(SettingsAdapter.this, R.layout.settings_details, items);
        settings.setAdapter(adapter);

        settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String content = items.get(position).getTitle();
//                Toast.makeText(SettingsAdapter.this, "Item numero: " + position + " Contenido: " + content, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(SettingsAdapter.this, Main2Activity.class);
//                intent.putExtra(PHOTO_TRANSFER, adapter.getItem(position));
//                startActivity(intent);

                switch (position){
                    case 0:
                        startActivity(new Intent(SettingsAdapter.this, MainActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(SettingsAdapter.this, ConfigTime.class));
                        break;
                    case 2:
                        startActivity(new Intent(SettingsAdapter.this, Config.class));
                        break;
                    case 3:
                        startActivity(new Intent(SettingsAdapter.this, SetCall.class));
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_adapter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menusettingsadapter_info:
//                in case my custom setting adapter doesnt work i come back to this one
//                openConfig();
                startActivity(new Intent(SettingsAdapter.this, Pop.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}