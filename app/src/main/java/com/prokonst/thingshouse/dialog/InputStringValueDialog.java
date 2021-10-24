package com.prokonst.thingshouse.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prokonst.thingshouse.R;

import java.util.Locale;

public class InputStringValueDialog {

    private Context context;
    private String title;
    private String nameValue;
    private ChangeValueInterface.GetValueCallback<String> getValueCallback;
    private ChangeValueInterface.ChangeValueCallback<String> changeValueCallback;
    private boolean isPassword;

    public InputStringValueDialog(Context context, String title, String nameValue, boolean isPassword,
                                  ChangeValueInterface.GetValueCallback<String> getValueCallback,
                                  ChangeValueInterface.ChangeValueCallback<String> changeValueCallback) {

        this.context = context;
        this.title = title;
        this.nameValue = nameValue;
        this.isPassword = isPassword;
        this.getValueCallback = getValueCallback;
        this.changeValueCallback = changeValueCallback;
    }

    public void show(){
        this.show("Change");
    }

    public void show(String prefixPositiveButton) {
        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context.getApplicationContext());

        final View viewEditThing = layoutInflaterAndroid.inflate(R.layout.layout_edit_thing, null);

        final TextView thingTitleTextView = viewEditThing.findViewById(R.id.thingTitle);
        thingTitleTextView.setText(title);

        final EditText nameEditText = viewEditThing.findViewById(R.id.nameEditText);
        nameEditText.setText(getValueCallback.onGetValue());
        if(this.isPassword) {
            nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            nameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        }


        final AlertDialog alertDialog = (new AlertDialog.Builder(context))
                .setView(viewEditThing)
                .setCancelable(false)
                .setPositiveButton(prefixPositiveButton + " " + nameValue, (dialogBox, id) -> {
                    String newValue = nameEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(newValue)) {
                        Toast.makeText(context, "Enter thing " + nameValue.toLowerCase(Locale.ROOT) + "!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        dialogBox.dismiss();
                    }

                    changeValueCallback.onChangeValue(newValue);
                })
                .setNegativeButton("Cancel", (dialogBox, id) -> {
                    Toast.makeText(context, "Command cancelled by user", Toast.LENGTH_SHORT).show();
                })
                .create();

        alertDialog.show();
    }
}
