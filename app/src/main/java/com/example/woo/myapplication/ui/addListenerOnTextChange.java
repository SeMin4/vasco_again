package com.example.woo.myapplication.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class addListenerOnTextChange implements TextWatcher {
    private Context mContext;
    private EditText mEditText;
    private int sDrawable;
    private int bDrawable;

    public addListenerOnTextChange(Context context, EditText editText, int selector, int burgundy) {
        super();
        this.mContext = context;
        this.mEditText= editText;
        this.sDrawable = selector;
        this.bDrawable = burgundy;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(mEditText.getText().length() > 0){
            mEditText.setCompoundDrawablesWithIntrinsicBounds(bDrawable,0,0,0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        mEditText.setCompoundDrawablesWithIntrinsicBounds(sDrawable,0,0,0);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //What you want to do
    }

}
