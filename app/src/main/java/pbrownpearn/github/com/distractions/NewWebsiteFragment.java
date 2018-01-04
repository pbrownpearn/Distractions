package pbrownpearn.github.com.distractions;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NewWebsiteFragment extends DialogFragment {

    private static final String LOG_TAG = NewWebsiteFragment.class.getSimpleName();

    public static final String EXTRA_URL = "pbrownpearn.github.com.distractions.url";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View viewAddWebsite = LayoutInflater.from(getContext()).inflate(R.layout.new_website_dialog, (ViewGroup) getView(), false);

        final EditText editTextWebsite = (EditText) viewAddWebsite.findViewById(R.id.edit_text_website_URL);

        return new AlertDialog.Builder(getActivity())
                .setView(viewAddWebsite)
                .setTitle(R.string.new_website)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = editTextWebsite.getText().toString();
                        if (validateUrl(url)) {
                            sendResult(Activity.RESULT_OK, url);
                        } else {
                            sendResult(Activity.RESULT_CANCELED, null);
                        }
                    }
                })
                .create();
        }

    private boolean validateUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    private void sendResult(int resultCode, String url) {
            if (getTargetFragment() == null) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_URL, url);

            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        }



}
