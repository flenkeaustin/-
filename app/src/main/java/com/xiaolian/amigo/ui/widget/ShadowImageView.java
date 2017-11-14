package com.xiaolian.amigo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
//import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xiaolian.amigo.R;

/**
 * <p>
 * Created by zcd on 10/10/17.
 */

public class ShadowImageView extends RelativeLayout {

    private int shadowRound = 0;

    private boolean mInvalidat;

    public ShadowImageView(Context context) {
        this(context, null);
    }

    public ShadowImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setPadding(80, 40, 80, 120);
        setGravity(Gravity.CENTER);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        int imageresource;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowImageView);
            imageresource = a.getResourceId(R.styleable.ShadowImageView_shadowSrc, -1);
            shadowRound = a.getDimensionPixelSize(R.styleable.ShadowImageView_shadowRound, shadowRound);
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            shadowRound = (int) (shadowRound * density);
            imageresource = -1;
        }

        RoundImageView roundImageView = new RoundImageView(context);
        roundImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (imageresource == -1) {
            roundImageView.setImageResource(android.R.color.transparent);
        } else {
            roundImageView.setImageResource(imageresource);
        }

        addView(roundImageView);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int N = getChildCount();
                for (int i = 0; i < N; i++) {
                    View view = getChildAt(i);
                    if (i != 0) {
                        removeView(view);
                        getChildCount();
                        continue;
                    }
                    N = getChildCount();
                }

                ((RoundImageView) getChildAt(0)).setRound(shadowRound);
                mInvalidat = true;
            }
        });
    }

    public void setImageResource(int resId) {
        ((RoundImageView) getChildAt(0)).setImageResource(resId);
        invalidate();
        mInvalidat = true;
    }

    public void setImageDrawable(Drawable drawable) {
        ((RoundImageView) getChildAt(0)).setImageDrawable(drawable);
        invalidate();
        mInvalidat = true;
    }

    public void setImageBitmap(Bitmap bitmap) {
        ((RoundImageView) getChildAt(0)).setImageBitmap(bitmap);
        invalidate();
        mInvalidat = true;
    }

    public void setImageRadius(int radius) {
        if (radius > getChildAt(0).getWidth() / 2 || radius > getChildAt(0).getHeight() / 2) {
            if (getChildAt(0).getWidth() > getChildAt(0).getHeight()) {
                radius = getChildAt(0).getHeight() / 2;
            } else {
                radius = getChildAt(0).getWidth() / 2;
            }
        }

        this.shadowRound = radius;
        ((RoundImageView) getChildAt(0)).setRound(shadowRound);
        invalidate();
        mInvalidat = true;
    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//
//        if (mInvalidat) {
//
//            mInvalidat = false;
//
//            View view = getChildAt(0);
//
//            Paint shadowPaint = new Paint();
//
//            shadowPaint.setColor(Color.WHITE);
//            shadowPaint.setStyle(Paint.Style.FILL);
//            shadowPaint.setAntiAlias(true);
//
//            int radius = view.getHeight() / 12 > 40 ? 40 : view.getHeight() / 12;
//            int shadowColor = view.getHeight() / 16 > 28 ? 28 : view.getHeight() / 16;
//
//            Bitmap bitmap;
//            int rgb;
//
//            if (((ImageView) view).getDrawable() instanceof ColorDrawable) {
//                rgb = ((ColorDrawable) ((ImageView) view).getDrawable()).getColor();
//                shadowPaint.setShadowLayer(40, 0, 28, getDarkerColor(rgb));
//            } else if (((ImageView) view).getDrawable() instanceof BitmapDrawable) {
//                bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
//                Palette.Swatch mSwatch = Palette.from(bitmap).generate().getDominantSwatch();
//
//                if (null != mSwatch) {
//                    rgb = mSwatch.getRgb();
//                } else {
//                    rgb = Color.parseColor("#8D8D8D");
//                }
//
//                shadowPaint.setShadowLayer(radius, 0, shadowColor, getDarkerColor(rgb));
//                Bitmap bitmapT = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 4 * 3,
//                        bitmap.getWidth(), bitmap.getHeight() / 4);
//
//                if (null != Palette.from(bitmapT).generate().getDominantSwatch()) {
//                    rgb = Palette.from(bitmapT).generate().getDominantSwatch().getRgb();
//                    shadowPaint.setShadowLayer(radius, 0, shadowColor, rgb);
//                }
//            } else if (((ImageView) view).getDrawable() instanceof GradientDrawable) {
////                GradientDrawable drawable = ((GradientDrawable) ((ImageView) view).getDrawable());
////                bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
////                Canvas canvas1 = new Canvas(bitmap);
////                drawable.draw(canvas1);
////
////                Palette.Swatch mSwatch = Palette.from(bitmap).generate().getDominantSwatch();
////
////                if (null != mSwatch) {
////                    rgb = mSwatch.getRgb();
////                } else {
////                    rgb = Color.parseColor("#8D8D8D");
////                }
////
////                shadowPaint.setShadowLayer(radius, 0, shadowColor, getDarkerColor(rgb));
////                Bitmap bitmapT = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 4 * 3,
////                        bitmap.getWidth(), bitmap.getHeight() / 4);
////
////                if (null != Palette.from(bitmapT).generate().getDominantSwatch()) {
////                    rgb = Palette.from(bitmapT).generate().getDominantSwatch().getRgb();
////                    shadowPaint.setShadowLayer(radius, 0, shadowColor, rgb);
////                }
//                rgb = Color.parseColor("#ff9a64");
//                shadowPaint.setShadowLayer(radius, 0, shadowColor, rgb);
//
//            } else {
//                rgb = Color.parseColor("#8D8D8D");
//                shadowPaint.setShadowLayer(radius, 0, shadowColor, getDarkerColor(rgb));
//            }
//
//            RectF rectF = new RectF(view.getX() + (view.getWidth() / 20), view.getY(), view.getX() + view.getWidth() - (view.getWidth() / 20), view.getY() + view.getHeight() - ((view.getHeight() / 40)));
//
//            canvas.drawRoundRect(rectF, shadowRound, shadowRound, shadowPaint);
//
//            canvas.save();
//        }
//        super.dispatchDraw(canvas);
//    }


    public int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] + 0.1f;
        hsv[2] = hsv[2] - 0.1f;
        int darkerColor = Color.HSVToColor(hsv);
        return darkerColor;
    }
}
