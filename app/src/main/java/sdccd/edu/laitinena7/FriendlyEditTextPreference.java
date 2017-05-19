package sdccd.edu.laitinena7;

/**
 * Created by Tuulikki Laitinen on 5/18/2017.
 */

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

public class FriendlyEditTextPreference extends EditTextPreference {

    public FriendlyEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FriendlyEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FriendlyEditTextPreference(Context context) {
        super(context);
    }

    // According to ListPreference implementation
    @Override
    public CharSequence getSummary() {
        String text = getText();
        if (TextUtils.isEmpty(text)) {
            CharSequence hint = getEditText().getHint();
            if (!TextUtils.isEmpty(hint)) {
                return hint;
            } else {
                return super.getSummary();
            }
        } else {
            CharSequence summary = super.getSummary();
            if (!TextUtils.isEmpty(summary)) {
                return String.format(summary.toString(), text);
            } else {
                return summary;
            }
        }
    }
}