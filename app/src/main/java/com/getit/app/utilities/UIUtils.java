package com.getit.app.utilities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;

import com.getit.app.Constants;
import com.getit.app.CustomApplication;
import com.getit.app.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UIUtils {

    private static final int RANDOM_COLOR_START_RANGE = 0;
    private static final int RANDOM_COLOR_END_RANGE = 9;

    private static final int COLOR_MAX_VALUE = 255;
    private static final float COLOR_ALPHA = 0.8f;
    private static Map<Integer, Integer> colorsMap = new HashMap<>();

    private static final Random random = new Random();
    private static int previousColor;

    private UIUtils() {
    }

    public static String getAccountType(int type) {
        switch (type) {
            case Constants.USER_TYPE_ADMIN:
                return CustomApplication.getApplication().getApplicationContext().getString(R.string.str_account_type_admin);
            case Constants.USER_TYPE_TEACHER:
                return CustomApplication.getApplication().getApplicationContext().getString(R.string.str_account_type_teacher);
            case Constants.USER_TYPE_STUDENT:
                return CustomApplication.getApplication().getApplicationContext().getString(R.string.str_account_type_student);
        }
        return "N/A";
    }

    public static Drawable getRandomColorCircleDrawable() {
        return getColoredCircleDrawable(getRandomCircleColor());
    }

    public static Drawable getColorCircleDrawable(int colorPosition) {
        return getColoredCircleDrawable(getCircleColor(colorPosition % RANDOM_COLOR_END_RANGE));
    }

    public static Drawable getColoredCircleDrawable(@ColorInt int color) {
        GradientDrawable drawable = (GradientDrawable) CustomApplication.getApplication().getResources().getDrawable(R.drawable.shape_circle);
        drawable.setColor(color);
        return drawable;
    }

    public static int getRandomCircleColor() {
        int randomNumber = random.nextInt(RANDOM_COLOR_END_RANGE) + 1;

        int generatedColor = getCircleColor(randomNumber);
        if (generatedColor != previousColor) {
            previousColor = generatedColor;
            return generatedColor;
        } else {
            do {
                generatedColor = getRandomCircleColor();
            } while (generatedColor != previousColor);
        }
        return previousColor;
    }

    public static int getCircleColor(@IntRange(from = RANDOM_COLOR_START_RANGE, to = RANDOM_COLOR_END_RANGE) int colorPosition) {
        String colorIdName = String.format("random_color_%d", colorPosition + 1);
        int colorId = CustomApplication.getApplication().getResources()
                .getIdentifier(colorIdName, "color", CustomApplication.getApplication().getPackageName());

        return CustomApplication.getApplication().getResources().getColor(colorId);
    }

    public static int getRandomTextColorById(Integer senderId) {
        if (colorsMap.get(senderId) != null) {
            return colorsMap.get(senderId);
        } else {
            int colorValue = getRandomColor();
            colorsMap.put(senderId, colorValue);
            return colorsMap.get(senderId);
        }
    }

    public static int getRandomColor() {
        float[] hsv = new float[3];
        int color = Color.argb(COLOR_MAX_VALUE, random.nextInt(COLOR_MAX_VALUE), random.nextInt(
                COLOR_MAX_VALUE), random.nextInt(COLOR_MAX_VALUE));
        Color.colorToHSV(color, hsv);
        hsv[2] *= COLOR_ALPHA;
        color = Color.HSVToColor(hsv);
        return color;
    }
}