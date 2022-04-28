package oleksandr.lohvinov.lab1;

import android.content.Context;
import android.text.TextUtils;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.zip.Inflater;

public class KeyBoard extends GridLayout implements View.OnClickListener {

    public static final int BOARD_A = 1;
    public static final int BOARD_B = 2;
    public static final int ALL_BOARD = 3;

    public KeyBoard(Context context) {
        this(context, null, 0);
    }

    public KeyBoard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButton0;
    private Button mButtonDel;
    private Button mButtonClr;
    private Button mButtonEq;
    private Button mButtonMul;
    private Button mButtonDiv;
    private Button mButtonAdd;
    private Button mButtonSub;
    private Button mButtonSqrt;
    private Button mButtonDot;
    private Button mButtonE;
    private Button mButtonPow;
    private Button mButtonLeftParentheses;
    private Button mButtonRightParentheses;
    private Button mButtonSin;
    private Button mButtonCos;


    private SparseArray<String> keyValues = new SparseArray<>();
    private InputConnection inputConnection;

    private ViewGroup mainKeyboardScene;
    private Scene keyboardSceneA;
    private Scene keyboardSceneB;
    private AppCompatActivity rootActivity;
    private TextView messageText;

    private OnEqBtnClicked clickEqBtnListener;

    private boolean keyboardA_active = true;
    private Transition fadeTransition;

    private Context context;
    private String currentString = "";

    private void init(Context context, AttributeSet attrs) {
        this.context = context;

        initBoardA(context);

        keyValues.put(R.id.btn1, "1");
        keyValues.put(R.id.btn2, "2");
        keyValues.put(R.id.btn3, "3");
        keyValues.put(R.id.btn4, "4");
        keyValues.put(R.id.btn5, "5");
        keyValues.put(R.id.btn6, "6");
        keyValues.put(R.id.btn7, "7");
        keyValues.put(R.id.btn8, "8");
        keyValues.put(R.id.btn9, "9");
        keyValues.put(R.id.btn0, "0");
        keyValues.put(R.id.btnDot, ".");
        keyValues.put(R.id.btnAdd, "+");
        keyValues.put(R.id.btnSub, "-");
        keyValues.put(R.id.btnDiv, "/");
        keyValues.put(R.id.btnMul, "*");
        keyValues.put(R.id.btnSqrt, "√");
        keyValues.put(R.id.btnE, "2.718");
        keyValues.put(R.id.btnPow, "^");
        keyValues.put(R.id.btnLeftParentheses, "(");
        keyValues.put(R.id.btnRightParentheses, ")");
        keyValues.put(R.id.btnSin, "sin(");
        keyValues.put(R.id.btnCos, "cos(");
    }

    private void initBoardA(Context context) {
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);

        mButton1 = findViewById(R.id.btn1);
        mButton2 = findViewById(R.id.btn2);
        mButton3 = findViewById(R.id.btn3);
        mButton4 = findViewById(R.id.btn4);
        mButton5 = findViewById(R.id.btn5);
        mButton6 = findViewById(R.id.btn6);
        mButton7 = findViewById(R.id.btn7);
        mButton8 = findViewById(R.id.btn8);
        mButton9 = findViewById(R.id.btn9);
        mButton0 = findViewById(R.id.btn0);
        mButtonDel = findViewById(R.id.btnDel);
        mButtonClr = findViewById(R.id.btnClr);
        mButtonEq = findViewById(R.id.btnEq);
        mButtonMul = findViewById(R.id.btnMul);
        mButtonDiv = findViewById(R.id.btnDiv);
        mButtonAdd = findViewById(R.id.btnAdd);
        mButtonSub = findViewById(R.id.btnSub);
        mButtonSqrt = findViewById(R.id.btnSqrt);
        mButtonDot = findViewById(R.id.btnDot);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);
        mButtonDel.setOnClickListener(this);
        mButtonClr.setOnClickListener(this);
        mButtonEq.setOnClickListener(this);
        mButtonMul.setOnClickListener(this);
        mButtonDiv.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
        mButtonSub.setOnClickListener(this);
        mButtonSqrt.setOnClickListener(this);
        mButtonDot.setOnClickListener(this);
    }

    private void initBoardB(Context context) {
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);

        mButtonE = findViewById(R.id.btnE);
        mButtonPow = findViewById(R.id.btnPow);
        mButtonLeftParentheses = findViewById(R.id.btnLeftParentheses);
        mButtonRightParentheses = findViewById(R.id.btnRightParentheses);
        mButtonSin = findViewById(R.id.btnSin);
        mButtonCos = findViewById(R.id.btnCos);


        mButtonE.setOnClickListener(this);
        mButtonPow.setOnClickListener(this);
        mButtonLeftParentheses.setOnClickListener(this);
        mButtonRightParentheses.setOnClickListener(this);
        mButtonSin.setOnClickListener(this);
        mButtonCos.setOnClickListener(this);
    }

    public void initBoard(int boardNum) {
        if (boardNum == BOARD_A) {
            initBoardA(context);
        } else if (boardNum == BOARD_B) {
            initBoardB(context);
        } else if (boardNum == ALL_BOARD) {
            initBoardA(context);
            initBoardB(context);
        }
    }

    public void WriteText(String text){
        if(!text.equals("")){
            inputConnection.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
            inputConnection.commitText(text, 1);
        }
    }

    @Override
    public void onClick(View v) {
        if (inputConnection == null) return;

        messageText.setText("");

        if (v.getId() == R.id.btnDel) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText("", 1);
            }
        } else if (v.getId() == R.id.btnClr) {
            inputConnection.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
            currentString = "";
        } else if (v.getId() == R.id.btnEq) {
            String res = clickEqBtnListener.CalculateInputExpression();
            inputConnection.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
            inputConnection.commitText(res, 1);
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }
    }

    public void setupKeyboard(
            InputConnection ic,
            OnEqBtnClicked clickEqBtnListener,
            AppCompatActivity rootActivity,
            TextView messageText,
            int keyboardToActivate) {
        this.inputConnection = ic;
        this.clickEqBtnListener = clickEqBtnListener;
        this.rootActivity = rootActivity;
        this.messageText = messageText;


        mainKeyboardScene = findViewById(R.id.keyboardRoot);

        if(keyboardToActivate == BOARD_A){
            initBoardA(context);
        } else if (keyboardToActivate == BOARD_B){
            initBoardB(context);
        } else if(keyboardToActivate == ALL_BOARD){
            initBoardA(context);
            initBoardB(context);
        }

        if (mainKeyboardScene != null) {
            keyboardSceneA = Scene.getSceneForLayout(mainKeyboardScene, R.layout.keyboard_a, rootActivity);
            keyboardSceneB = Scene.getSceneForLayout(mainKeyboardScene, R.layout.keyboard_b, rootActivity);

            fadeTransition =
                    TransitionInflater.from(rootActivity).
                            inflateTransition(R.transition.fade_transition);
        }
    }

    public void changeKeyBoard() {

        if (keyboardA_active) {
            TransitionManager.go(keyboardSceneB, fadeTransition);
            keyboardA_active = false;
            initBoardB(context);
        } else {
            TransitionManager.go(keyboardSceneA, fadeTransition);
            keyboardA_active = true;
            initBoardA(context);
        }

    }

    public interface OnEqBtnClicked {
        String CalculateInputExpression();
    }

}
