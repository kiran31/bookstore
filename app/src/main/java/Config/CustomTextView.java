package Config;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import shoparounds.com.R;

public class CustomTextView extends AppCompatTextView {
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context,AttributeSet atrSet)
    {
        TypedArray array = context.obtainStyledAttributes(atrSet, R.styleable.CustomTextView);
        String font_name=array.getString(R.styleable.CustomTextView_fontName);
        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),"Font/"+font_name);
        setTypeface(typeface);
        array.recycle();


    }
}
