package com.kakaxicm.geekming.domain;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by chenming on 2018/8/31
 */
public class Block {
    public float mCenterPointX;//圆心x
    public float mCenterPointY;//圆心y
    public float mBigRadius;//大圆半径
    public float mLittleRadius;//小圆半径
    public BlockSate mState = BlockSate.IDLE;//默认空闲

    public int mId;//索引

    //空闲状态颜色
    public int mIdleBigCircleColor = Color.parseColor("#110000ff");
    public int mIdleLittleCircleColor = Color.parseColor("#0000ff");

    //选中状态颜色
    public int mHittedBigCircleColor = Color.parseColor("#1100ff00");
    public int mHittedLittleCircleColor = Color.parseColor("#00ff00");

    //密码通过的颜色
    public int mSuccessBigCircleColor = Color.parseColor("#1100ff00");
    public int mSuccessdLittleCircleColor = Color.parseColor("#00ff00");

    //密码错误时的颜色
    public int mErroBigCircleColor = Color.parseColor("#11ff0000");
    public int mErroLittleCircleColor = Color.parseColor("#ff0000");

    //三角
    public Path mArrow = new Path();
    //三角指向角度,水平向右为0度，顺时针方向为正
    public double mArrowAngle;

    public void setArrowAngle(double angle){
        mArrowAngle = angle;
    }

    public void drawArrow(Canvas canvas, Paint paint){
        //没有松手，则不画三角
        if(mState != BlockSate.SUCCESS && mState != BlockSate.ERRO){
            return;
        }

        float arrowLen = (mBigRadius - mLittleRadius)*0.5f;
        float arrowLeftX = mCenterPointX + mLittleRadius + (mBigRadius - mLittleRadius - arrowLen)/2;
        float arrowRightX = arrowLeftX + arrowLen;
        float topY = mCenterPointY - arrowLen;
        float bottomY = mCenterPointY + arrowLen;
        mArrow.moveTo(arrowRightX, mCenterPointY);
        mArrow.lineTo(arrowLeftX, topY);
        mArrow.lineTo(arrowLeftX, bottomY);
        mArrow.close();

        canvas.save();
        canvas.rotate((float) mArrowAngle, mCenterPointX, mCenterPointY);
        canvas.drawPath(mArrow, paint);
        canvas.restore();
    }
    public enum BlockSate {
        IDLE,//空闲
        HITTED,//手指触摸
        ERRO,//密码错误
        SUCCESS;//密码正确
    }

}
