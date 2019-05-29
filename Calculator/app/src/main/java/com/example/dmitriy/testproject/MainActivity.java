package com.example.dmitriy.testproject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

public class MainActivity extends Activity {
    protected final int buttonsNumber = 19;
    protected Button[] buttons = new Button[buttonsNumber];
    protected String expression = "";
    protected String result = "";
    protected EditText editExpression;
    protected TextView resultView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        buttons[0] = findViewById(R.id.buttonDgt0);
        buttons[1] = findViewById(R.id.buttonDgt1);
        buttons[2] = findViewById(R.id.buttonDgt2);
        buttons[3] = findViewById(R.id.buttonDgt3);
        buttons[4] = findViewById(R.id.buttonDgt4);
        buttons[5] = findViewById(R.id.buttonDgt5);
        buttons[6] = findViewById(R.id.buttonDgt6);
        buttons[7] = findViewById(R.id.buttonDgt7);
        buttons[8] = findViewById(R.id.buttonDgt8);
        buttons[9] = findViewById(R.id.buttonDgt9);

        buttons[10] = findViewById(R.id.buttonSumm);
        buttons[11] = findViewById(R.id.buttonDifference);
        buttons[12] = findViewById(R.id.buttonMultiply);
        buttons[13] = findViewById(R.id.buttonDivide);
        buttons[14] = findViewById(R.id.buttonDot);
        buttons[15] = findViewById(R.id.buttonEqual);
        buttons[16] = findViewById(R.id.buttonLeftScope);
        buttons[17] = findViewById(R.id.buttonRightScope);
        buttons[18] = findViewById(R.id.buttonClear);
        editExpression = findViewById(R.id.editExpression);
        resultView = findViewById(R.id.resultView);

        View.OnClickListener onClickButtons = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String inputValue = "";
                switch(v.getId()){
                    case R.id.buttonDgt0:
                        inputValue += "0";
                        break;
                    case R.id.buttonDgt1:
                        inputValue += "1";
                        break;
                    case R.id.buttonDgt2:
                        inputValue += "2";
                        break;
                    case R.id.buttonDgt3:
                        inputValue += "3";
                        break;
                    case R.id.buttonDgt4:
                        inputValue += "4";
                        break;
                    case R.id.buttonDgt5:
                        inputValue += "5";
                        break;
                    case R.id.buttonDgt6:
                        inputValue += "6";
                        break;
                    case R.id.buttonDgt7:
                        inputValue += "7";
                        break;
                    case R.id.buttonDgt8:
                        inputValue += "8";
                        break;
                    case R.id.buttonDgt9:
                        inputValue += "9";
                        break;
                    case R.id.buttonSumm:
                        inputValue += "+";
                        break;
                    case R.id.buttonDifference:
                        inputValue += "-";
                        break;
                    case R.id.buttonDivide:
                        inputValue += "/";
                        break;
                    case R.id.buttonMultiply:
                        inputValue += "*";
                        break;
                    case R.id.buttonDot:
                        inputValue += ".";
                        break;
                    case R.id.buttonEqual:
                        result = Calculator.calculateExpression(expression);
                        resultView.setText(result);
                        break;
                    case R.id.buttonLeftScope:
                        inputValue += "(";
                        break;
                    case R.id.buttonRightScope:
                        inputValue += ")";
                        break;
                    case R.id.buttonClear:
                        expression = "";
                        editExpression.setText("");
                        break;
                }
                int cursorPosition = editExpression.getSelectionStart();
                Editable expressionBefore = editExpression.getText();
                expressionBefore.insert(cursorPosition, inputValue);
                editExpression.setText(expressionBefore);
                editExpression.setSelection(cursorPosition+inputValue.length());
                expression = editExpression.getText().toString();
            }
        };

        for(int i = 0; i < buttonsNumber; i++){
            buttons[i].setOnClickListener(onClickButtons);
        }

        editExpression.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editExpression.setTextIsSelectable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
