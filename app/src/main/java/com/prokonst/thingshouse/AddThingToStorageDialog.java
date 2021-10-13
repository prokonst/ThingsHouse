package com.prokonst.thingshouse;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.text.TextUtils;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.navigation.NavDirections;
        import androidx.navigation.fragment.NavHostFragment;

        import java.util.Locale;

public class AddThingToStorageDialog {

    private Context context;
    private ChangeValueInterface.GetValueCallback<String> getValueCallback;
    private ChangeValueInterface.ChangeValueCallbackWithAction<String> changeValueCallback;
    private EditText nameEditText;

    public AddThingToStorageDialog(Context context,
                                  ChangeValueInterface.GetValueCallback<String> getValueCallback,
                                  ChangeValueInterface.ChangeValueCallbackWithAction<String> changeValueCallback) {
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

        final AlertDialog alertDialog = (new AlertDialog.Builder(context))
                .setView(viewEditThing)
                .setCancelable(false)
                .setPositiveButton("Select to", (dialogBox, id) -> {
                    onClickButton(dialogBox, "Select");
                })
                .setNegativeButton("Scan to", (dialogBox, id) -> {
                    onClickButton(dialogBox, "Scan");
                })
                .setNeutralButton("Cancel", (dialogBox, id) -> {
                    dialogBox.dismiss();
                    Toast.makeText(context, "Command cancelled by user", Toast.LENGTH_LONG).show();
                })
                .create();

        alertDialog.show();
    }

    private void onClickButton(DialogInterface dialogBox, String actionNameKey) {
        String newValue = nameEditText.getText().toString().trim();
        try {
            newValue = newValue.replace(',', '.');
            double newValDouble = Double.parseDouble(newValue);
            dialogBox.dismiss();
            changeValueCallback.onChangeValue(newValue, actionNameKey);
        } catch (Exception ex) {
            Toast.makeText(context, "Enter numeric", Toast.LENGTH_LONG).show();
        }
    }
}
