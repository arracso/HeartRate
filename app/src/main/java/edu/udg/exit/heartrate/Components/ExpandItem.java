package edu.udg.exit.heartrate.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import edu.udg.exit.heartrate.R;

public class ExpandItem extends LinearLayout {

    public ExpandItem(Context context) {
        super(context);
    }
    public ExpandItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ExpandItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Inflates the views in the layout.
     * @param context - the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_expand, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

}
