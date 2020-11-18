package app.iggy.panicbutton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Config extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "Config";

    private static final String MESSAGE  = "message.txt";

    static String Text;

    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Button btnSave = (Button) findViewById(R.id.save);
        Button btnLoad = (Button) findViewById(R.id.load);
        Button btnContacts = (Button) findViewById(R.id.btnContacts);
        Button btnMyContacts = (Button) findViewById(R.id.btnMyContacts);
        btnSave.setOnClickListener(this);
        btnLoad.setOnClickListener(this);

        activateToolbar(true);

        message = (EditText) findViewById(R.id.message);

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Config.this, Main3Activity.class);
                startActivity(intent);
            }
        });

        btnMyContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Config.this, MainActivity.class));
            }
        });


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

                    message.getText().clear();

                    Toast.makeText(this, getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show();

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
            case R.id.load:

                FileInputStream fis2 = null;

                try {

                    fis2 = openFileInput(MESSAGE);
                    InputStreamReader isr2 = new InputStreamReader(fis2);
                    BufferedReader br2 = new BufferedReader(isr2);
                    StringBuilder sb2 = new StringBuilder();
                    String text2;

                    while ((text2 = br2.readLine()) != null){
                        sb2.append(text2).append("\n");
                    }

                    Log.d(TAG, "onClick: " + Text);

                    message.setText(sb2.toString());

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

                break;

            default:

        }

    }
}
