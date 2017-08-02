package com.myhuanghai.pianokeydemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

public class PianoKeyboard extends View {
    private Context mContext;
    private PianoKeyBoardCallback pianoKeyBoardCallback;
    private int defaultKeyTone = 60;
    public static final String LOG_TAG = "PianoKeyboard";
    public static final int MAX_FINGERS = 5;
    public static final int WHITE_KEYS_COUNT = 7;
    public static final int BLACK_KEYS_COUNT = 5;
    public static final float BLACK_TO_WHITE_WIDTH_RATIO = 0.625f;
    public static final float BLACK_TO_WHITE_HEIGHT_RATIO = 0.54f;
    private Paint mWhiteKeyPaint, mBlackKeyPaint, mBlackKeyHitPaint, mWhiteKeyHitPaint;
    // Support up to five fingers
    private Point[] mFingerPoints = new Point[MAX_FINGERS];
    private int[] mFingerTones = new int[MAX_FINGERS];
    private SoundPool mSoundPool;
    private SparseIntArray mToneToIndexMap = new SparseIntArray();
    private Paint mCKeyPaint, mCSharpKeyPaint, mDKeyPaint,
            mDSharpKeyPaint, mEKeyPaint, mFKeyPaint,
            mFSharpKeyPaint, mGKeyPaint, mGSharpKeyPaint,
            mAKeyPaint, mASharpKeyPaint, mBKeyPaint;
    private Rect mCKey = new Rect(), mCSharpKey = new Rect(),
            mDKey = new Rect(), mDSharpKey = new Rect(),
            mEKey = new Rect(), mFKey = new Rect(),
            mFSharpKey = new Rect(), mGKey = new Rect(),
            mGSharpKey = new Rect(), mAKey = new Rect(),
            mASharpKey = new Rect(), mBKey = new Rect();

    private Bitmap mCKeyImg , mCSharpKeyImg,
            mDKeyImg, mDSharpKeyImg,
            mEKeyImg, mFKeyImg,
            mFSharpKeyImg, mGKeyImg,
            mGSharpKeyImg, mAKeyImg ,
            mASharpKeyImg , mBKeyImg;
    private MotionEvent.PointerCoords mPointerCoords;

    public PianoKeyboard(Context context) {
        super(context);
        this.mContext = context;
    }

    public PianoKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public PianoKeyboard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        Log.d(LOG_TAG, "In onAttachedToWindow");
        super.onAttachedToWindow();
        mPointerCoords = new MotionEvent.PointerCoords();
        Arrays.fill(mFingerPoints, null);
        Arrays.fill(mFingerTones, -1);
        //loadKeySamples(getContext());
        setupPaints();
        setupBitmap();
    }

    private void setupBitmap() {
        mCKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.white_r);
        mCSharpKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.black);
        mDKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.white_m);
        mDSharpKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.black);
        mEKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.white_l);
        mFKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.white_r);
        mFSharpKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.black);
        mGKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.white_m);
        mGSharpKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.black);
        mAKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.white_m);
        mASharpKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.black);
        mBKeyImg = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.white_l);
    }

    @Override

    protected void onDetachedFromWindow() {
        Log.d(LOG_TAG, "In onDetachedFromWindow");
        super.onDetachedFromWindow();
        releaseKeySamples();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(LOG_TAG, "In onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(LOG_TAG, "In onLayout");
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        int whiteKeyWidth = width / WHITE_KEYS_COUNT;
        int blackKeyWidth = (int) (whiteKeyWidth * BLACK_TO_WHITE_WIDTH_RATIO);
        int blackKeyHeight = (int) (height * BLACK_TO_WHITE_HEIGHT_RATIO);
        mCKey.set(0, 0, whiteKeyWidth, height);
        mCSharpKey.set(whiteKeyWidth - (blackKeyWidth / 2), 0,
                whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mDKey.set(whiteKeyWidth, 0, 2 * whiteKeyWidth, height);
        mDSharpKey.set(2 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                2 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mEKey.set(2 * whiteKeyWidth, 0, 3 * whiteKeyWidth, height);
        mFKey.set(3 * whiteKeyWidth, 0, 4 * whiteKeyWidth, height);
        mFSharpKey.set(4 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                4 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mGKey.set(4 * whiteKeyWidth, 0, 5 * whiteKeyWidth, height);
        mGSharpKey.set(5 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                5 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mAKey.set(5 * whiteKeyWidth, 0, 6 * whiteKeyWidth, height);
        mASharpKey.set(6 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                6 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mBKey.set(6 * whiteKeyWidth, 0, 7 * whiteKeyWidth, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d(LOG_TAG, "In onDraw");
        super.onDraw(canvas);

        canvas.drawRect(mCKey, mCKeyPaint);
        //canvas.drawBitmap(mCKeyImg,null, mCKey,mCKeyPaint);
        canvas.drawRect(mDKey, mDKeyPaint);
        canvas.drawRect(mEKey, mEKeyPaint);
        canvas.drawRect(mFKey, mFKeyPaint);
        canvas.drawRect(mGKey, mGKeyPaint);
        canvas.drawRect(mAKey, mAKeyPaint);
        canvas.drawRect(mBKey, mBKeyPaint);

        canvas.drawRect(mCSharpKey, mCSharpKeyPaint);
        canvas.drawRect(mDSharpKey, mDSharpKeyPaint);
        canvas.drawRect(mFSharpKey, mFSharpKeyPaint);
        canvas.drawRect(mGSharpKey, mGSharpKeyPaint);
        canvas.drawRect(mASharpKey, mASharpKeyPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       // Log.d(LOG_TAG, "In onTouchEvent");
        int pointerCount = event.getPointerCount();
       // Log.d(LOG_TAG, "In onTouchEvent pointerCount = " + pointerCount);
        int cappedPointerCount = pointerCount > MAX_FINGERS ? MAX_FINGERS : pointerCount;
       // Log.d(LOG_TAG, "In onTouchEvent cappedPointerCount = " + cappedPointerCount);
        int actionIndex = event.getActionIndex();
      //  Log.d(LOG_TAG, "In onTouchEvent actionIndex = " + actionIndex);
        int action = event.getActionMasked();
      //  Log.d(LOG_TAG, "In onTouchEvent action = " + action);
        int id = event.getPointerId(actionIndex);
       // Log.d(LOG_TAG, "In onTouchEvent id = " + id);

        if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && id < MAX_FINGERS) {
            mFingerPoints[id] = new Point((int) event.getX(actionIndex), (int) event.getY(actionIndex));
        } else if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP) && id < MAX_FINGERS) {
            mFingerPoints[id] = null;
            invalidateKey(mFingerTones[id]);
            Log.i("huanghai","抬起："+mFingerTones[id]);
            mFingerTones[id] = -1;


        }

        for (int i = 0; i < cappedPointerCount; i++) {
            int index = event.findPointerIndex(i);
            if (mFingerPoints[i] != null && index != -1) {
                mFingerPoints[i].set((int) event.getX(index), (int) event.getY(index));
                int tone = getToneForPoint(mFingerPoints[i]);
                //invalidateKey(1);
                if (tone != mFingerTones[i] && tone != -1) {
                    invalidateKey(mFingerTones[i]);
                    if(mFingerTones[id]!=-1){
                        Log.i("huanghai","抬起："+mFingerTones[id]);
                        if(pianoKeyBoardCallback!=null)
                            pianoKeyBoardCallback.onKeyUp(mFingerTones[id]);
                    }

                    mFingerTones[i] = tone;
                    invalidateKey(mFingerTones[i]);
                    if (!isKeyDown(i)) {
                       // int poolIndex = mToneToIndexMap.get(mFingerTones[i]);
                        event.getPointerCoords(index, mPointerCoords);
                        float volume = mPointerCoords.getAxisValue(MotionEvent.AXIS_PRESSURE);
                        volume = volume > 1f ? 1f : volume;
                        Log.i("huanghai","按下："+mFingerTones[i]);
                        if(pianoKeyBoardCallback!=null)
                            pianoKeyBoardCallback.onKeyDown(mFingerTones[i]);
                    }
                }
            }
        }

        updatePaints();

        return true;
    }

    private void setupPaints() {
        mWhiteKeyPaint = new Paint();
        mWhiteKeyPaint.setStyle(Paint.Style.STROKE);
        mWhiteKeyPaint.setColor(Color.BLACK);
        mWhiteKeyPaint.setStrokeWidth(3);
        mWhiteKeyPaint.setAntiAlias(true);
        mCKeyPaint = mWhiteKeyPaint;
        mDKeyPaint = mWhiteKeyPaint;
        mEKeyPaint = mWhiteKeyPaint;
        mFKeyPaint = mWhiteKeyPaint;
        mGKeyPaint = mWhiteKeyPaint;
        mAKeyPaint = mWhiteKeyPaint;
        mBKeyPaint = mWhiteKeyPaint;

        mWhiteKeyHitPaint = new Paint(mWhiteKeyPaint);
        mWhiteKeyHitPaint.setColor(Color.LTGRAY);
        mWhiteKeyHitPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mBlackKeyPaint = new Paint();
        mBlackKeyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBlackKeyPaint.setColor(Color.BLACK);
        mBlackKeyPaint.setAntiAlias(true);
        mCSharpKeyPaint = mBlackKeyPaint;
        mDSharpKeyPaint = mBlackKeyPaint;
        mFSharpKeyPaint = mBlackKeyPaint;
        mGSharpKeyPaint = mBlackKeyPaint;
        mASharpKeyPaint = mBlackKeyPaint;

        mBlackKeyHitPaint = new Paint(mBlackKeyPaint);
        mBlackKeyHitPaint.setColor(Color.DKGRAY);
    }

    private void loadKeySamples(Context context) {
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//        mToneToIndexMap.put(R.raw.c, mSoundPool.load(context, R.raw.c, 1));
//        mToneToIndexMap.put(R.raw.c_sharp, mSoundPool.load(context, R.raw.c_sharp, 1));
//        mToneToIndexMap.put(R.raw.d, mSoundPool.load(context, R.raw.d, 1));
//        mToneToIndexMap.put(R.raw.d_sharp, mSoundPool.load(context, R.raw.d_sharp, 1));
//        mToneToIndexMap.put(R.raw.e, mSoundPool.load(context, R.raw.e, 1));
//        mToneToIndexMap.put(R.raw.f, mSoundPool.load(context, R.raw.f, 1));
//        mToneToIndexMap.put(R.raw.f_sharp, mSoundPool.load(context, R.raw.f_sharp, 1));
//        mToneToIndexMap.put(R.raw.g, mSoundPool.load(context, R.raw.g, 1));
//        mToneToIndexMap.put(R.raw.g_sharp, mSoundPool.load(context, R.raw.g_sharp, 1));
//        mToneToIndexMap.put(R.raw.a, mSoundPool.load(context, R.raw.a, 1));
//        mToneToIndexMap.put(R.raw.a_sharp, mSoundPool.load(context, R.raw.a_sharp, 1));
//        mToneToIndexMap.put(R.raw.b, mSoundPool.load(context, R.raw.b, 1));
    }

    public void releaseKeySamples() {
        mToneToIndexMap.clear();
        mSoundPool.release();
    }

    private boolean isKeyDown(int finger) {
        int key = getToneForPoint(mFingerPoints[finger]);

        for (int i = 0; i < mFingerPoints.length; i++) {
            if (i != finger) {
                Point fingerPoint = mFingerPoints[i];
                if (fingerPoint != null) {
                    int otherKey = getToneForPoint(fingerPoint);
                    if (otherKey == key) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void invalidateKey(int tone) {
       // invalidate(mCKey);
        if(tone==defaultKeyTone){
            invalidate(mCKey);
        }else if(tone==(defaultKeyTone+1)){
            invalidate(mCSharpKey);
        }else if(tone==(defaultKeyTone+2)){
            invalidate(mDKey);
        }else if(tone==(defaultKeyTone+3)){
            invalidate(mDSharpKey);
        }else if(tone==(defaultKeyTone+4)){
            invalidate(mEKey);
        }else if(tone==(defaultKeyTone+5)){
            invalidate(mFKey);
        }else if(tone==(defaultKeyTone+6)){
            invalidate(mFSharpKey);
        }else if(tone==(defaultKeyTone+7)){
            invalidate(mGKey);
        }else if(tone==(defaultKeyTone+8)){
            invalidate(mGSharpKey);
        }else if(tone==(defaultKeyTone+9)){
            invalidate(mAKey);
        }else if(tone==(defaultKeyTone+10)){
            invalidate(mASharpKey);
        }else if(tone==(defaultKeyTone+11)){
            invalidate(mBKey);
        }
    }

    private void updatePaints() {
        mCKeyPaint = mWhiteKeyPaint;
        mDKeyPaint = mWhiteKeyPaint;
        mEKeyPaint = mWhiteKeyPaint;
        mFKeyPaint = mWhiteKeyPaint;
        mGKeyPaint = mWhiteKeyPaint;
        mAKeyPaint = mWhiteKeyPaint;
        mBKeyPaint = mWhiteKeyPaint;
        mCSharpKeyPaint = mBlackKeyPaint;
        mDSharpKeyPaint = mBlackKeyPaint;
        mFSharpKeyPaint = mBlackKeyPaint;
        mGSharpKeyPaint = mBlackKeyPaint;
        mASharpKeyPaint = mBlackKeyPaint;

        for (Point fingerPoint : mFingerPoints) {
            if (fingerPoint != null) {
                if (mCSharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mCSharpKeyPaint = mBlackKeyHitPaint;
                } else if (mDSharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mDSharpKeyPaint = mBlackKeyHitPaint;
                } else if (mFSharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mFSharpKeyPaint = mBlackKeyHitPaint;
                } else if (mGSharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mGSharpKeyPaint = mBlackKeyHitPaint;
                } else if (mASharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mASharpKeyPaint = mBlackKeyHitPaint;
                } else if (mCKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mCKeyPaint = mWhiteKeyHitPaint;
                } else if (mDKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mDKeyPaint = mWhiteKeyHitPaint;
                } else if (mEKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mEKeyPaint = mWhiteKeyHitPaint;
                } else if (mFKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mFKeyPaint = mWhiteKeyHitPaint;
                } else if (mGKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mGKeyPaint = mWhiteKeyHitPaint;
                } else if (mAKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mAKeyPaint = mWhiteKeyHitPaint;
                } else if (mBKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mBKeyPaint = mWhiteKeyHitPaint;
                }
            }
        }
    }

    private int getToneForPoint(Point point) {
        if (mCSharpKey.contains(point.x, point.y))
            return defaultKeyTone + 1;
        if (mDSharpKey.contains(point.x, point.y))
            return defaultKeyTone + 3;
        if (mFSharpKey.contains(point.x, point.y))
            return defaultKeyTone + 6;
        if (mGSharpKey.contains(point.x, point.y))
            return defaultKeyTone + 8;
        if (mASharpKey.contains(point.x, point.y))
            return defaultKeyTone + 10;

        if (mCKey.contains(point.x, point.y))
            return defaultKeyTone;
        if (mDKey.contains(point.x, point.y))
            return defaultKeyTone + 2;
        if (mEKey.contains(point.x, point.y))
            return defaultKeyTone + 4;
        if (mFKey.contains(point.x, point.y))
            return defaultKeyTone + 5;
        if (mGKey.contains(point.x, point.y))
            return defaultKeyTone + 7;
        if (mAKey.contains(point.x, point.y))
            return defaultKeyTone + 9;
        if (mBKey.contains(point.x, point.y))
            return defaultKeyTone + 11;

        return -1;
    }


    public PianoKeyBoardCallback getPianoKeyBoardCallback() {
        return pianoKeyBoardCallback;
    }

    public void setPianoKeyBoardCallback(PianoKeyBoardCallback pianoKeyBoardCallback) {
        this.pianoKeyBoardCallback = pianoKeyBoardCallback;
    }

    public interface PianoKeyBoardCallback {
        void onKeyDown(int tone);

        void onKeyUp(int tone);
    }


    public int getDefaultKeyTone() {
        return defaultKeyTone;
    }

    public void setDefaultKeyTone(int defaultKeyTone) {
        this.defaultKeyTone = defaultKeyTone;
    }
}