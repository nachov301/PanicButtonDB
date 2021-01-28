package app.iggy.panicbutton2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static app.iggy.panicbutton2.MainActivity.DIALOG_ID_CANCEL_EDIT;

public class ConfigTime extends BaseActivity implements View.OnClickListener, AppDialog.DialogEvents {
    private static final String TAG = "ConfigTime";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    private static final String MESSAGE  = "message.txt";

    static String Text;
    private EditText message;
    private EditText time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_time);

        activateToolbar(true);

        Button btnSave = (Button) findViewById(R.id.btn_savetime);
        btnSave.setOnClickListener(this);

        time = (EditText) findViewById(R.id.minutes);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(TEXT, "");
        time.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_savetime:

                String min = time.getText().toString();
                int min2 = Integer.valueOf(min);
                if (min2<20){
                    Toast.makeText(this, R.string.minimum_time, Toast.LENGTH_LONG).show();
                }else{
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(TEXT, min);
                    editor.commit();

                    Toast.makeText(this, getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show();

                    finish();
                }

                break;


            default:

        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        finish();
    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp: here");
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: starts");
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);
//        super.onBackPressed();
    }
}