package edu.udg.exit.heartrate.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.udg.exit.heartrate.R;

public class ExpandItem extends LinearLayout {

    ///////////////
    // Variables //
    ///////////////

    // Components
    private RelativeLayout label = null;
    private LinearLayout collapsibleComponent = null;

    // State
    private Boolean isCollapsed = true;

    // Callback
    private Runnable onCollapseCallback = null;

    // Label Attributes
    private String labelText = null;
    private String labelValue = null;

    ///////////////////////
    // Lifecycle Methods //
    ///////////////////////

    /**
     * Constructor by default.
     * @param context - Context of the component.
     */
    public ExpandItem(Context context) {
        super(context);
        initializeViews(context);
    }

    /**
     * Constructor with attributes.
     * @param context - Context of the component.
     * @param attrs - Attributes of the component.
     */
    public ExpandItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStyledAttributes(context,attrs);
        initializeViews(context);
    }

    /**
     * Constructor with defined style.
     * @param context - Context of the component.
     * @param attrs - Attributes of the component.
     * @param defStyle - Defined style.
     */
    public ExpandItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setStyledAttributes(context,attrs);
        initializeViews(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Get child that acts as label
        label = (RelativeLayout) findViewById(R.id.label);

        // Set label attributes
        setLabelText(this.labelText);
        setLabelValue(this.labelValue);

        // Get child that acts as collapsible component and its index
        collapsibleComponent = (LinearLayout) findViewById(R.id.collapsible_component);
        int index = indexOfChild(collapsibleComponent);

        // Move all children after the collapsibleComponent inside the collapsibleComponent
        while(index+1 < getChildCount()){
            View child = getChildAt(index+1);
            removeViewAt(index+1);
            collapsibleComponent.addView(child);
        }
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        label.setOnClickListener(l);
    }

    /**
     * Sets a callback to be triggered when the item is collapsed.
     * @param callback - Callback that will be triggered
     */
    public void setOnCollapseCallback(Runnable callback) {
        this.onCollapseCallback = callback;
    }

    /**
     * Gets current state (collapsed/expanded) of the item.
     * @return True when the item is collapsed, false otherwise.
     */
    public boolean isCollapsed() {
        return isCollapsed();
    }

    /**
     * Collapses the item.
     */
    public void collapse() {
        if(onCollapseCallback != null) onCollapseCallback.run();
        collapsibleComponent.setVisibility(View.GONE);
        isCollapsed = true;
    }

    /**
     * Expands the item.
     */
    public void expand() {
        collapsibleComponent.setVisibility(View.VISIBLE);
        isCollapsed = false;
    }

    /**
     * Sets the text of the label.
     * @param text - Text of the label.
     */
    public void setLabelText(String text) {
        TextView labelText = (TextView) findViewById(R.id.label_text);
        labelText.setText(text);
    }

    /**
     * Sets the value of the label.
     * @param value - Value of the label.
     */
    public void setLabelValue(String value) {
        TextView labelValue = (TextView) findViewById(R.id.label_value);
        labelValue.setText(value);
    }



    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Set styled attributes of the component.
     * @param context - Context of the component
     * @param attrs - Array of attributes
     */
    private void setStyledAttributes(Context context, AttributeSet attrs) {
        // Obtain the attributes
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandItem);

        // Set the attributes
        labelText = (String) typedArray.getText(R.styleable.ExpandItem_labelText);
        labelValue = (String) typedArray.getText(R.styleable.ExpandItem_labelValue);

        // Clean the array of attributes
        typedArray.recycle();
    }

    /**
     * Inflates the views in the layout.
     * @param context - the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_expand, this);
    }



}
