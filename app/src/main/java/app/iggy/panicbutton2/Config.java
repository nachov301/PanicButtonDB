package app.iggy.panicbutton2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static app.iggy.panicbutton2.MainActivity.DIALOG_ID_CANCEL_EDIT;

public class Config extends BaseActivity implements View.OnClickListener, AppDialog.DialogEvents{
    private static final String TAG = "Config";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    private static final String MESSAGE  = "message.txt";

    static String Text;
    private EditText message;
    private EditText time;
    private TextView counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Button btnSave = (Button) findViewById(R.id.save);

        btnSave.setOnClickListener(this);

        activateToolbar(true);

        message = (EditText) findViewById(R.id.message);
        counter = (TextView) findViewById(R.id.conf_counter);

        counter.setText("0/80");
        //for counting characters starts here

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = message.length();
                String convert = String.valueOf(length);
                counter.setText(convert + "/80");
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        //for counting characters finish here


        FileInputStream fis2 = null;

        try {

            fis2 = openFileInput(MESSAGE);
            InputStreamReader isr2 = new InputStreamReader(fis2);
            BufferedReader br2 = new BufferedReader(isr2);
            StringBuilder sb2 = new StringBuilder();
            String text2;

            while ((text2 = br2.readLine()) != null){
                sb2.append(text2);
            }

            Log.d(TAG, "onClick: " + Text);

            message.setText(sb2.toString());

            if (message.length()>0){
                String convert = String.valueOf(message.length());
                counter.setText(convert + "/80");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ((fis2 != null)){
                try {
                    fis2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    public static String getMESSAGE() {
        return MESSAGE;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.save:

                String textMessage = message.getText().toString();


                FileOutputStream fos2 = null;

                try {

                    fos2 = openFileOutput(MESSAGE, MODE_PRIVATE);
                    fos2.write(textMessage.getBytes());


                    Toast.makeText(this, getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show();

                    finish();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if ((fos2 != null)){
                        try {
                            fos2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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
    }
}
