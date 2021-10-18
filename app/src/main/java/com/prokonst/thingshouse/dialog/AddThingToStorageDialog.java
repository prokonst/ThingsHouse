package com.prokonst.thingshouse.dialog;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.prokonst.thingshouse.R;

public class AddThingToStorageDialog {

    private Context context;
    private ChangeValueInterface.GetValueCallback<String> getValueCallback;
    private ChangeValueInterface.ChangeValueCallbackWithAction<String> changeValueCallback;
    private EditText nameEditText;
    private ButtonsType buttonsType;

    public AddThingToStorageDialog(Context context, ButtonsType buttonsType,
                                  ChangeValueInterface.GetValueCallback<String> getValueCallback,
                                  ChangeValueInterface.ChangeValueCallbackWithAction<String> changeValueCallback) {
        this.buttonsType = buttonsType;
        this.context = context;
        this.getValueCallback = getValueCallback;
        this.changeValueCallback = changeValueCallback;
    }

    public void show() {
        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context.getApplicationContext());

        final View viewEditThing = layoutInflaterAndroid.inflate(R.layout.layout_edit_thing, null);

        final TextView thingTitleTextView = viewEditThing.findViewById(R.id.thingTitle);
        thingTitleTextView.setText("Add thing to storage");

        nameEditText = viewEditThing.findViewById(R.id.nameEditText);
        nameEditText.setText(getValueCallback.onGetValue());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setView(viewEditThing)
                .setCancelable(false);
        if(this.buttonsType == ButtonsType.ScanAndSelect) {
            alertDialogBuilder.setPositiveButton("Select to", (dialogBox, id) -> {
                                    onClickButton(dialogBox, ChangeValueInterface.ActionType.Select);
                                })
                                .setNegativeButton("Scan to", (dialogBox, id) -> {
                                    onClickButton(dialogBox, ChangeValueInterface.ActionType.Scan);
                                });
        }
        else if(this.buttonsType == ButtonsType.Ok) {
            alertDialogBuilder.setPositiveButton("Ok", (dialogBox, id) -> {
                                                    onClickButton(dialogBox, ChangeValueInterface.ActionType.Unknown);
                                                });
        }

        alertDialogBuilder.setNeutralButton("Cancel", (dialogBox, id) -> {
                                dialogBox.dismiss();
                                Toast.makeText(context, "Command cancelled by user", Toast.LENGTH_LONG).show();
                            });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void onClickButton(DialogInterface dialogBox, ChangeValueInterface.ActionType actionType) {
        String newValue = nameEditText.getText().toString().trim();
        try {
            newValue = newValue.replace(',', '.');
            double newValDouble = Double.parseDouble(newValue);
            dialogBox.dismiss();
            changeValueCallback.onChangeValue(newValue, actionType);
        } catch (Exception ex) {
            Toast.makeText(context, "Enter numeric", Toast.LENGTH_LONG).show();
        }
    }

    public enum ButtonsType {
        ScanAndSelect,
        Ok
    }
}
