package tw.openedu.www.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class DataUsageDialogFragment extends DialogFragment {

    private static final String TAG = DataUsageDialogFragment.class.getCanonicalName();
    private static final String MESSAGE_ID = TAG + ".title";

    private IDialogCallback callback;

    //TODO don't set callback here!
    public static DataUsageDialogFragment newInstance(int titleID, IDialogCallback callback) {

        DataUsageDialogFragment fragment = new DataUsageDialogFragment();
        fragment.callback = callback;

        Bundle arguments = new Bundle();
        arguments.putInt(MESSAGE_ID, titleID);
        fragment.setArguments(arguments);

        return fragment;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        int messageID = this.getArguments().getInt(MESSAGE_ID);
        builder.setMessage(messageID);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (callback != null) {
                    callback.onPositiveClicked();
                }
                dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder.create();

    }

}
