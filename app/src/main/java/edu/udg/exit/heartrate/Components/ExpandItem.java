package edu.udg.exit.heartrate.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import edu.udg.exit.heartrate.R;

public class ExpandItem extends LinearLayout {

    public ExpandItem(Context context) {
        super(context);
        initializeViews(context);
    }
    public ExpandItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }
    public ExpandItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
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

        LinearLayout hiddenComponent = (LinearLayout) findViewById(R.id.hidden_component);
        int index = indexOfChild(hiddenComponent);

        while(index+1 < getChildCount()){
            View child = getChildAt(index+1);
            removeViewAt(index+1);
            hiddenComponent.addView(child);
        }
    }

}
