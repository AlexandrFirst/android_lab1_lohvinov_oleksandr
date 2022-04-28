package oleksandr.lohvinov.lab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView messageText;
    private KeyBoard keyboard;
    private MathModule mathModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mathModule = new MathModule();

        editText = findViewById(R.id.editText);
        messageText = findViewById(R.id.messageTextView);

        keyboard = findViewById(R.id.keyboard);

        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTextIsSelectable(true);

        editText.setRawInputType(InputType.TYPE_NULL);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setShowSoftInputOnFocus(false);


        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        mathModule.SetOnWrongInput(message -> {
            messageText.setText(message);
        });

        int activeKeyboard = 1;

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            activeKeyboard = KeyBoard.ALL_BOARD;
        } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            activeKeyboard = KeyBoard.BOARD_A;
        }

        keyboard.setupKeyboard(ic,
                () -> {
                    String inputText = String.valueOf(editText.getText());
                    MathModule.PolishNotationTokenizer tokenizedString = mathModule.parseString(inputText);
                    try {
                        double res = mathModule.CalculatePolishNotation(tokenizedString.GetTokenizedString());
                        return String.valueOf(res);
                    } catch (IllegalArgumentException ex) {
                        return inputText;
                    }
                }, this, messageText, activeKeyboard);

        if(savedInstanceState!=null){
            String inputText = savedInstanceState.getString("text");
            keyboard.WriteText(inputText);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_panel_menu, menu);

        menu.findItem(R.id.additionalKeyboard).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                keyboard.changeKeyBoard();
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("text", String.valueOf(editText.getText()));
    }
}