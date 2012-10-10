package mx.israelbuitron.githubjobs4a;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AboutDialog extends SherlockDialogFragment implements OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.about_dialog, null);
        AlertDialog.Builder about = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.about_dialog_title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(view)
                .setNeutralButton(
                        R.string.about_dialog_accept_button_label, this);
        return about.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}
