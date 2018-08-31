package com.kakaxicm.geekming.frameworks.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.kakaxicm.geekming.domain.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenming on 2018/8/31
 */
public class GestureLockView extends View {
    private float mSize;//w=h

    private final float MBIGRADIUSFRACTION = 40/300.0f;
    private final float MLITTLERADIUSFRACTION = 15/300.0f;

    private float mLittleRadius;//小圆半径
    private float mBigRadius;//大圆半径

    private List<Block> mBaseBlocks = new ArrayList<>();
    private List<Integer> mSelectedIds = new ArrayList<>();

    private Paint mBigCirclePaint;
    private Paint mSmallCirclePaint;
    private Paint mLinePaint;//滑动过程中的折线和指引线paint

    private Path mPath;//滑动过程中的折线
    private float mNodeLineX;//折线的节点位置
    private float mNodeLineY;
    private float mLineTmpX;//指引线的终点
    private float mLineTmpY;

    String mAnswer = "012543678";//预设密码

    /**
     * 手势锁回调
     */
    public interface OnGestureLockListener{
        void onBlockHitted(int index);//block被触摸到
        void onGestureLockSuccess(String password);
        void onGestureLockFail();
    }

    private OnGestureLockListener mGestureLockListener;

    public void setmGestureLockListener(OnGestureLockListener listener){
        mGestureLockListener = listener;
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.GRAY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int resultWidth = wSize;
        int resultHeight = hSize;
        Resources r = Resources.getSystem();
        //lp = wrapcontent时 指定默认值
        if(wMode == MeasureSpec.AT_MOST){
            resultWidth =  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
        }
        if(hMode == MeasureSpec.AT_MOST){
            resultHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
        }
        int size = resultWidth>resultHeight?resultHeight:resultWidth;
        setMeasuredDimension(size, size);
        initParams();
    }

    /**
     * 绘制涉及参量的初始化操作
     */
    private void initParams(){
        mSize = getMeasuredWidth();
        mBigRadius = MBIGRADIUSFRACTION*mSize;
        mLittleRadius = MLITTLERADIUSFRACTION*mSize;

        mBigCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeWidth(mLittleRadius*2);
        mLinePaint.setColor(Color.parseColor("#4400ff00"));

        mPath = new Path();
        //blocks初始化
        if(mBaseBlocks.size() == 0){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    //构建3*3 block
                    Block block = new Block();
                    float centerX = mSize*(1+j*2)/6;
                    float centerY = mSize*(1+i*2)/6;
                    block.mCenterPointX = centerX;
                    block.mCenterPointY = centerY;
                    block.mBigRadius = mBigRadius;
                    block.mLittleRadius = mLittleRadius;
                    block.mId = i*3+j;
                    mBaseBlocks.add(block);
                }
            }
        }

    }

    /**
     * 绘制blocks、折线、指引线
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        for(int i = 0; i < mBaseBlocks.size(); i++){
            Block block = mBaseBlocks.get(i);
            drawBlock(canvas, block);
        }
        //绘制折线和指引线
        canvas.drawPath(mPath, mLinePaint);
        if(mSelectedIds.size()>0){
            canvas.drawLine(mNodeLineX, mNodeLineY, mLineTmpX, mLineTmpY, mLinePaint);
        }

    }

    /**
     * 绘制基本单元
     * 1.大、小圆
     * 2.三角指示
     * @param canvas
     * @param block
     */
    private void drawBlock(Canvas canvas, Block block){
        if(block.mState == Block.BlockSate.IDLE){
            mBigCirclePaint.setColor(block.mIdleBigCircleColor);
            mSmallCirclePaint.setColor(block.mIdleLittleCircleColor);
        }else if(block.mState == Block.BlockSate.HITTED){
            mBigCirclePaint.setColor(block.mHittedBigCircleColor);
            mSmallCirclePaint.setColor(block.mHittedLittleCircleColor);
        }else if(block.mState == Block.BlockSate.SUCCESS){
            mBigCirclePaint.setColor(block.mSuccessBigCircleColor);
            mSmallCirclePaint.setColor(block.mSuccessdLittleCircleColor);
        }else if(block.mState == Block.BlockSate.ERRO){
            mBigCirclePaint.setColor(block.mErroBigCircleColor);
            mSmallCirclePaint.setColor(block.mErroLittleCircleColor);
        }

        canvas.drawCircle(block.mCenterPointX,block.mCenterPointY, block.mBigRadius, mBigCirclePaint);
        canvas.drawCircle(block.mCenterPointX,block.mCenterPointY, block.mLittleRadius, mSmallCirclePaint);

        //画三角指示符
        if(mSelectedIds.size() > 0){
            if(block.mId != mSelectedIds.get(mSelectedIds.size()-1)){//最后一个不画三角
                block.drawArrow(canvas,mSmallCirclePaint);
            }
        }
    }

    /**
     * 核心代码，控制手势监听的逻辑
     * step1:ACTION_DOWN 做复位操作
     * setp2:ACTION_MOVE 监测手指滑到哪个block，同时更新block状态、指引线及折线
     * step3:ACTION_UP 校验密码、更新选中的block状态、设置选中的block三角角度
     * srep4:前三步都会更改绘制涉及的参数，需要重绘操作
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                Block block = checkHitBlock(x, y);
                //探测未选中的block
                if(block != null && !mSelectedIds.contains(block.mId)){//探测到
                    if(mGestureLockListener != null){
                        mGestureLockListener.onBlockHitted(block.mId);
                    }
                    //手指触摸到block,作以下处理:
                    //1.block状态处理
                    //2.path的节点设置为block的中心
                    //3.指引线的终点设为节点位置
                    block.mState = Block.BlockSate.HITTED;
                    mSelectedIds.add(block.mId);
                    mNodeLineX = block.mCenterPointX;
                    mNodeLineY = block.mCenterPointY;//折线变为block的圆心

                    if(mSelectedIds.size() == 1){//手指第一次选中block
                        mPath.moveTo(mNodeLineX, mNodeLineY);
                    }else{
                        mPath.lineTo(mNodeLineX, mNodeLineY);
                    }
                    mLineTmpX = mNodeLineX;
                    mLineTmpY = mNodeLineY;
                }else{//未探测到
                    //手指未触摸到block,则只需要设置指引线终点即可
                    mLineTmpX = x;
                    mLineTmpY = y;
                }

                break;
            case MotionEvent.ACTION_UP:
                //选中的block 改为error/success状态
                changeReleaseBlockState();
                //折线处理,终点回退到节点,实现取消指引线的效果
                mLineTmpX = mNodeLineX;
                mLineTmpY = mNodeLineY;
                //三角角度设置
                configBlockArrowAngles();
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 手指松开时，根据选中的block，设置三角的角度
     */
    private void configBlockArrowAngles(){
        for(int i = 0; i < mSelectedIds.size()-1; i++){
            int index = mSelectedIds.get(i);
            int nextIndex = mSelectedIds.get(i+1);
            Block curBlock = mBaseBlocks.get(index);
            Block nextBlock = mBaseBlocks.get(nextIndex);

            float offsetX = nextBlock.mCenterPointX - curBlock.mCenterPointX;
            float offsetY = nextBlock.mCenterPointY - curBlock.mCenterPointY;
            double angle = Math.toDegrees(Math.atan2(offsetY,offsetX));
            curBlock.setArrowAngle(angle);
            Log.e("ANGLES",angle+"");
        }
    }

    /**
     * 松手时，检测结果，修改选中的block状态
     */
    private void changeReleaseBlockState(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i < mSelectedIds.size();i++){
            sb.append(mSelectedIds.get(i));
        }

        boolean isSuccess = TextUtils.equals(mAnswer, sb.toString());

        if(mGestureLockListener != null){
            if(isSuccess){
                mGestureLockListener.onGestureLockSuccess(mAnswer);
            }else {
                mGestureLockListener.onGestureLockFail();
            }
        }
        //设置选中的block的状态
        for(int i = 0; i < mBaseBlocks.size(); i++){
            Block block = mBaseBlocks.get(i);
            if(mSelectedIds.contains(block.mId)){
                if(isSuccess){
                    block.mState = Block.BlockSate.SUCCESS;
                }else{
                    block.mState = Block.BlockSate.ERRO;
                }
            }
        }
    }


    /**
     * 复位所有block、path、指引线状态
     */
    private void reset(){
        mPath.reset();
        mSelectedIds.clear();
        for(int i = 0; i < mBaseBlocks.size(); i++){
            Block block = mBaseBlocks.get(i);
            block.mState = Block.BlockSate.IDLE;
            block.mArrowAngle = 0;
        }

        mLineTmpX = 0;
        mLineTmpY = 0;
        mNodeLineX = 0;
        mNodeLineY = 0;
    }

    /**
     * 检测位置落在哪个block上
     * @param x
     * @param y
     * @return
     */
    private Block checkHitBlock(float x, float y){
        for(int i = 0; i < mBaseBlocks.size(); i++){
            Block block = mBaseBlocks.get(i);
            float startX = block.mCenterPointX - block.mBigRadius;
            float endX = block.mCenterPointX + block.mBigRadius;
            float startY = block.mCenterPointY - block.mBigRadius;
            float endY = block.mCenterPointY + block.mBigRadius;

            if(x >= startX && x <=endX && y >= startY && y <= endY){
                return block;
            }
        }
        return null;
    }

}
